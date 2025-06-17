package ElBuenSabor.ProyectoFinal.Service;

import com.itextpdf.io.source.ByteArrayOutputStream;
import ElBuenSabor.ProyectoFinal.DTO.*;
import ElBuenSabor.ProyectoFinal.Entities.*;
import ElBuenSabor.ProyectoFinal.Exceptions.ResourceNotFoundException;
import ElBuenSabor.ProyectoFinal.Repositories.*;
import com.mercadopago.client.payment.PaymentClient;
import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.exceptions.MPException;
import com.mercadopago.resources.payment.Payment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class PedidoServiceImpl extends BaseServiceImpl<Pedido, Long> implements PedidoService {

    private final PedidoRepository pedidoRepository; // Mantener esta referencia si necesitas llamar a métodos específicos no en BaseService

    // Repositorios para resolver relaciones
    private final ArticuloInsumoRepository articuloInsumoRepository;
    private final ArticuloManufacturadoRepository articuloManufacturadoRepository;
    private final ClienteRepository clienteRepository;
    private final DomicilioRepository domicilioRepository;
    private final SucursalRepository sucursalRepository;
    private final UsuarioRepository usuarioRepository;
    private final ArticuloRepository articuloRepository;
    private final FacturaService facturaService;
    private final EmailService emailService;
    private final CloudinaryService cloudinaryService;
    public PedidoServiceImpl(
            PedidoRepository pedidoRepository,
            ArticuloInsumoRepository articuloInsumoRepository,
            ArticuloManufacturadoRepository articuloManufacturadoRepository,
            ClienteRepository clienteRepository,
            DomicilioRepository domicilioRepository,
            SucursalRepository sucursalRepository,
            UsuarioRepository usuarioRepository,
            ArticuloRepository articuloRepository,
            FacturaService facturaService,
            EmailService emailService,
            CloudinaryService cloudinaryService) {
        super(pedidoRepository); // Llama al constructor de la clase base
        this.pedidoRepository = pedidoRepository;
        this.articuloInsumoRepository = articuloInsumoRepository;
        this.articuloManufacturadoRepository = articuloManufacturadoRepository;
        this.clienteRepository = clienteRepository;
        this.domicilioRepository = domicilioRepository;
        this.sucursalRepository = sucursalRepository;
        this.usuarioRepository = usuarioRepository;
        this.articuloRepository = articuloRepository;
        this.facturaService = facturaService;
        this.emailService = emailService;
        this.cloudinaryService = cloudinaryService;

    }

    // Método para crear un pedido antes de generar la preferencia de MP
    @Override
    @Transactional
    public Pedido crearPedidoPreferenciaMP(PedidoCreateDTO dto) throws Exception {
        try {
            Pedido pedido = new Pedido();

            // 🧩 Asignación de relaciones obligatorias
            pedido.setCliente(clienteRepository.findById(dto.getClienteId())
                    .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado")));

            pedido.setDomicilioEntrega(domicilioRepository.findById(dto.getDomicilioId())
                    .orElseThrow(() -> new ResourceNotFoundException("Domicilio no encontrado")));

            // 🧩 Relaciones opcionales
            if (dto.getSucursalId() != null) {
                pedido.setSucursal(sucursalRepository.findById(dto.getSucursalId()).orElse(null));
            }

            if (dto.getEmpleadoId() != null) {
                pedido.setEmpleado(usuarioRepository.findById(dto.getEmpleadoId()).orElse(null));
            }

            // 🧾 Factura (se crea un placeholder, los IDs de MP se llenarán después de la confirmación)
            Factura factura;
            if (dto.getFactura() != null) {
                // Si el DTO trae datos de factura, úsalos
                FacturaCreateDTO f = dto.getFactura();
                factura = Factura.builder()
                        .fechaFacturacion(f.getFechaFacturacion())
                        .mpPaymentId(null)
                        .mpMerchantOrderId(null)
                        .mpPreferenceId(null) // Se seteará en MPController
                        .mpPaymentType(null)
                        .formaPago(FormaPago.MERCADO_PAGO)
                        .totalVenta(f.getTotalVenta())
                        .build();
            } else {
                // Si el DTO no trae datos de factura, crea una instancia básica
                factura = Factura.builder()
                        .fechaFacturacion(LocalDate.now()) // Fecha actual por defecto
                        .formaPago(FormaPago.MERCADO_PAGO)
                        .totalVenta(dto.getTotal()) // O 0.0, según tu lógica de negocio
                        .build();
            }
            pedido.setFactura(factura);

            // 🧾 Detalles del pedido
            if (dto.getDetalles() != null) {
                Set<DetallePedido> detalles = new HashSet<>();
                for (DetallePedidoCreateDTO detalleDTO : dto.getDetalles()) {
                    DetallePedido detalle = new DetallePedido();
                    detalle.setCantidad(detalleDTO.getCantidad());
                    detalle.setSubTotal(detalleDTO.getSubTotal());

                    ArticuloInsumo insumo = articuloInsumoRepository.findById(detalleDTO.getArticuloId()).orElse(null);
                    if (insumo != null) {
                        detalle.setArticuloInsumo(insumo);
                    } else {
                        ArticuloManufacturado manufacturado = articuloManufacturadoRepository.findById(detalleDTO.getArticuloId())
                                .orElseThrow(() -> new ResourceNotFoundException("Artículo no encontrado"));
                        detalle.setArticuloManufacturado(manufacturado);
                    }

                    detalle.setPedido(pedido);
                    detalles.add(detalle);
                }
                pedido.setDetallesPedidos(detalles);
            }

            // Establecer estado inicial del pedido (antes del pago)
            pedido.setEstado(Estado.A_CONFIRMAR);
            pedido.setFechaPedido(LocalDate.now());
            pedido.setBaja(false);
            pedido.setTotal(dto.getTotal());

            return baseRepository.save(pedido); // Guarda el pedido en la BD
        } catch (Exception e) {
            throw new RuntimeException("Error al crear el pedido para Mercado Pago: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    public void procesarNotificacionPagoMercadoPago(String paymentId) throws Exception {
        try {
            PaymentClient paymentClient = new PaymentClient();
            Payment payment = paymentClient.get(Long.valueOf(paymentId));

            if (payment == null) {
                System.err.println("Notificación: Pago no encontrado en Mercado Pago con ID: " + paymentId);
                return;
            }

            // --- CORRECCIÓN: MOVER LA DEFINICIÓN DE PEDIDO AQUÍ ---
            Long pedidoId = Long.valueOf(payment.getExternalReference());
            Pedido pedido = findById(pedidoId); // <-- 'pedido' se define aquí

            if (pedido == null) {
                System.err.println("Notificación: Pedido no encontrado en la BD con externalReference: " + payment.getExternalReference());
                return;
            }
            // --- FIN DE LA CORRECCIÓN DE POSICIÓN ---

            // --- PASO CLAVE: OBTENER LA INSTANCIA DE FACTURA UNA SOLA VEZ AL PRINCIPIO ---
            // Se asume que la Factura ya fue creada y asociada al Pedido en crearPedidoPreferenciaMP
            Factura factura = pedido.getFactura(); // <-- 'factura' ahora se obtiene aquí
            if(factura == null) {
                // Esto no debería pasar si crearPedidoPreferenciaMP funciona correctamente,
                // pero lo manejamos por seguridad, creando y asociando una nueva.
                factura = new Factura();
                pedido.setFactura(factura);
                System.out.println("ADVERTENCIA: Factura era null en webhook. Se creó una nueva instancia.");
            }

            // --- LÍNEAS DE DEBUG ---
            System.out.println("--- DEBUG MP NOTIFICATION ---");
            System.out.println("Payment ID (MP): " + payment.getId());
            System.out.println("Payment Status: " + payment.getStatus());
            System.out.println("External Reference (Pedido ID): " + payment.getExternalReference());
            System.out.println("Transaction Amount: " + payment.getTransactionAmount());
            System.out.println("Payment Type ID: " + payment.getPaymentTypeId());
            System.out.println("--- FIN DEBUG ---");

            switch (payment.getStatus()) {
                case "approved":
                    pedido.setEstado(Estado.PAGADO);
                    String generatedPdfUrl = null; // Declaramos aquí para usarla más adelante
                    try {
                        ByteArrayOutputStream pdfBytes = (ByteArrayOutputStream) facturaService.generarFacturaPdf(pedido);
                        String filePathLocal = "factura_" + pedido.getId() + ".pdf";
                        Files.write(Paths.get(filePathLocal), pdfBytes.toByteArray());
                        System.out.println("PDF de factura generado para Pedido " + pedido.getId() + ". Guardado LOCALMENTE en: " + filePathLocal);

                        String cloudinaryPublicId = "factura_pedido_" + pedido.getId();
                        generatedPdfUrl = cloudinaryService.uploadByteArray(pdfBytes, cloudinaryPublicId);
                        System.out.println("PDF subido a Cloudinary: " + generatedPdfUrl);

                        factura.setUrlPdf(generatedPdfUrl); // <-- ¡ASIGNA LA URL A LA INSTANCIA ÚNICA DE FACTURA!

                        String recipientEmail = pedido.getCliente().getEmail();
                        String subject = "Factura de tu pedido #" + pedido.getId() + " - El Buen Sabor";
                        String body = "¡Gracias por tu compra, " + pedido.getCliente().getNombre() + "! Adjuntamos la factura de tu pedido #" + pedido.getId() ;
                        String attachmentFilename = "factura_" + pedido.getId() + ".pdf";

                        emailService.sendEmail(recipientEmail, subject, body, pdfBytes, attachmentFilename);
                        System.out.println("Correo con factura enviado a " + recipientEmail);

                    } catch (Exception uploadMailEx) {
                        System.err.println("ERROR al generar PDF, subirlo y/o enviar correo para Pedido " + pedido.getId() + ": " + uploadMailEx.getMessage());
                        uploadMailEx.printStackTrace();
                    }
                    break;
                // ... (resto de los casos del switch) ...
            }

            // --- LÓGICA: Actualizar campos de FACTURA desde el Webhook (SOBRE LA MISMA INSTANCIA DE FACTURA) ---
            // Estos campos también se actualizan sobre la misma instancia de 'factura'
            factura.setMpPaymentId(payment.getId() != null ? payment.getId().intValue() : null);
            if (payment.getOrder() != null && payment.getOrder().getId() != null) {
                factura.setMpMerchantOrderId(payment.getOrder().getId().intValue());
            } else {
                factura.setMpMerchantOrderId(null);
            }
            // mpPreferenceId NO se toca aquí, ya se asignó en MPController
            factura.setMpPaymentType(payment.getPaymentTypeId());
            factura.setFormaPago(FormaPago.MERCADO_PAGO); // Confirmar o actualizar FormaPago
            factura.setTotalVenta(payment.getTransactionAmount() != null ? payment.getTransactionAmount().doubleValue() : null);

            if (payment.getDateApproved() != null) {
                factura.setFechaFacturacion(payment.getDateApproved().toLocalDate());
            } else if (payment.getDateCreated() != null) {
                factura.setFechaFacturacion(payment.getDateCreated().toLocalDate());
            }

            // --- DEBUG: Confirmar el valor de urlPdf justo antes del save final ---
            System.out.println("DEBUG FINAL: Valor de urlPdf en factura antes del save: " + (factura != null ? factura.getUrlPdf() : "FACTURA ES NULL"));
            System.out.println("DEBUG FINAL: Llamando a baseRepository.save(pedido) para Pedido ID: " + pedido.getId());
            baseRepository.save(pedido);
            System.out.println("DEBUG FINAL: baseRepository.save(pedido) completado.");

            System.out.println("Pedido " + pedido.getId() + " actualizado a estado: " + pedido.getEstado() + " por notificación de MP.");

        } catch (MPException | MPApiException e) {
            System.err.println("Error SDK Mercado Pago al procesar notificación de pago: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Error al obtener detalles del pago de Mercado Pago", e);
        } catch (Exception e) {
            System.err.println("Error general al procesar notificación de pago: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Error general al procesar notificación de pago: " + e.getMessage(), e);
        }
    }



    @Override
    @Transactional
    public Pedido update(Long id, Pedido updatedPedido) throws Exception {
        try {
            Pedido actual = findById(id);

            actual.setFechaPedido(updatedPedido.getFechaPedido());
            actual.setHoraEstimadaFinalizacion(updatedPedido.getHoraEstimadaFinalizacion());
            actual.setTotal(updatedPedido.getTotal());
            actual.setTotalCosto(updatedPedido.getTotalCosto());
            actual.setEstado(updatedPedido.getEstado());
            actual.setTipoEnvio(updatedPedido.getTipoEnvio());
            actual.setFormaPago(updatedPedido.getFormaPago());

            actual.setCliente(updatedPedido.getCliente());
            actual.setEmpleado(updatedPedido.getEmpleado());
            actual.setSucursal(updatedPedido.getSucursal());
            actual.setDomicilioEntrega(updatedPedido.getDomicilioEntrega());

            if (updatedPedido.getFactura() != null) {
                Factura updatedFactura = updatedPedido.getFactura();
                Factura existingFactura = actual.getFactura();

                if (existingFactura == null) {
                    existingFactura = new Factura();
                    actual.setFactura(existingFactura);
                }
                existingFactura.setFechaFacturacion(updatedFactura.getFechaFacturacion());
                existingFactura.setMpPaymentId(updatedFactura.getMpPaymentId());
                existingFactura.setMpMerchantOrderId(updatedFactura.getMpMerchantOrderId());
                existingFactura.setMpPreferenceId(updatedFactura.getMpPreferenceId());
                existingFactura.setMpPaymentType(updatedFactura.getMpPaymentType());
                existingFactura.setFormaPago(updatedFactura.getFormaPago());
                existingFactura.setTotalVenta(updatedFactura.getTotalVenta());
            } else {
                actual.setFactura(null);
            }

            if (updatedPedido.getDetallesPedidos() != null) {
                actual.getDetallesPedidos().clear();
                for (DetallePedido detalle : updatedPedido.getDetallesPedidos()) {
                    detalle.setPedido(actual);
                    actual.getDetallesPedidos().add(detalle);
                }
            } else {
                actual.getDetallesPedidos().clear();
            }

            return baseRepository.save(actual);
        } catch (Exception e) {
            throw new RuntimeException("Error al actualizar el pedido: " + e.getMessage(), e);
        }
    }

    // Implementaciones de los nuevos métodos de consulta (asumimos que ya están implementados)
    // List<Pedido> findPedidosByClienteId(Long clienteId) throws Exception;
    // List<Pedido> findPedidosByEstado(Estado estado) throws Exception;
    // List<Pedido> findPedidosBetweenFechas(LocalDate fechaInicio, LocalDate fechaFin) throws Exception;
    // List<Pedido> findPedidosByClienteIdAndEstado(Long clienteId, Estado estado) throws Exception;
    // List<Pedido> findPedidosByFechaAndEstado(LocalDate fechaInicio, LocalDate fechaFin, Estado estado) throws Exception;
    // List<Pedido> findPedidosByEstadoOrderByFechaPedidoDesc(Estado estado) throws Exception;
    // List<Pedido> findPedidosByClienteExcludingEstado(Long clienteId, Estado estadoExcluido) throws Exception;
    // long countPedidosByEstado(Estado estado) throws Exception;
    // List<Pedido> findPedidosBySucursalIdAndFechaBetween(Long sucursalId, LocalDate fechaInicio, LocalDate fechaFin) throws Exception;
    // List<Pedido> findPedidosByArticuloManufacturadoId(Long articuloManufacturadoId) throws Exception;
}