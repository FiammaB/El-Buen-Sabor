package ElBuenSabor.ProyectoFinal.Service;

import java.io.ByteArrayOutputStream;
import ElBuenSabor.ProyectoFinal.DTO.*;
import ElBuenSabor.ProyectoFinal.Entities.*;
import ElBuenSabor.ProyectoFinal.Exceptions.ResourceNotFoundException;
import ElBuenSabor.ProyectoFinal.Repositories.*;
import com.itextpdf.text.log.SysoCounter;
import com.mercadopago.client.payment.PaymentClient;
import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.exceptions.MPException;
import com.mercadopago.resources.payment.Payment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

@Service
public class PedidoServiceImpl extends BaseServiceImpl<Pedido, Long> implements PedidoService {

    private final PedidoRepository pedidoRepository; // Mantener esta referencia si necesitas llamar a métodos específicos no en BaseService

    // Repositorios para resolver relaciones
    private final ArticuloInsumoRepository articuloInsumoRepository;
    private final ArticuloManufacturadoRepository articuloManufacturadoRepository;
    private final PersonaRepository personaRepository;
    private final DomicilioRepository domicilioRepository;
    private final SucursalRepository sucursalRepository;
    private final UsuarioRepository usuarioRepository;
    private final ArticuloRepository articuloRepository;
    private final FacturaService facturaService;
    private final EmailService emailService;
    private final CloudinaryService cloudinaryService;
    // Nuevos servicios para NotaCredito y RegistroAnulacion
    private final NotaCreditoService notaCreditoService;
    private final RegistroAnulacionService registroAnulacionService;
    private final ArticuloInsumoService articuloInsumoService;
    private final DetallePedidoRepository detallePedidoRepository;
    private final PromocionRepository promocionRepository;

    public PedidoServiceImpl(
            PedidoRepository pedidoRepository,
            ArticuloInsumoRepository articuloInsumoRepository,
            ArticuloManufacturadoRepository articuloManufacturadoRepository,
            PersonaRepository personaRepository,
            DomicilioRepository domicilioRepository,
            SucursalRepository sucursalRepository,
            UsuarioRepository usuarioRepository,
            ArticuloRepository articuloRepository,
            FacturaService facturaService,
            EmailService emailService,
            CloudinaryService cloudinaryService,
            NotaCreditoService notaCreditoService,
            RegistroAnulacionService registroAnulacionService,
            ArticuloInsumoService articuloInsumoService,
            DetallePedidoRepository detallePedidoRepository,
            PromocionRepository promocionRepository) {
        super(pedidoRepository); // Llama al constructor de la clase base
        this.pedidoRepository = pedidoRepository;
        this.articuloInsumoRepository = articuloInsumoRepository;
        this.articuloManufacturadoRepository = articuloManufacturadoRepository;
        this.personaRepository = personaRepository;
        this.domicilioRepository = domicilioRepository;
        this.sucursalRepository = sucursalRepository;
        this.usuarioRepository = usuarioRepository;
        this.articuloRepository = articuloRepository;
        this.facturaService = facturaService;
        this.emailService = emailService;
        this.cloudinaryService = cloudinaryService;
        this.notaCreditoService = notaCreditoService;
        this.registroAnulacionService = registroAnulacionService;
        this.articuloInsumoService = articuloInsumoService;
        this.detallePedidoRepository = detallePedidoRepository;
        this.promocionRepository = promocionRepository;
    }

    // Método para crear un pedido antes de generar la preferencia de MP
    @Override
    @Transactional
    public Pedido crearPedidoPreferenciaMP(PedidoCreateDTO dto) throws Exception {
        try {
            Pedido pedido = new Pedido();
            System.out.println("DEBUG Pedido: Creando instancia de Pedido para Mercado Pago: " + pedido.getFormaPago());
            // 🧩 Asignación de relaciones obligatorias
            pedido.setPersona(personaRepository.findById(dto.getPersonaId())
                    .orElseThrow(() -> new ResourceNotFoundException("Persona no encontrado")));

            if (dto.getDomicilioId() != null) {
                pedido.setDomicilioEntrega(domicilioRepository.findById(dto.getDomicilioId())
                        .orElseThrow(() -> new ResourceNotFoundException("Domicilio no encontrado")));
            }

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

                    // PRIORIZA PROMO PRIMERO
                    if (detalleDTO.getPromocionId() != null) {
                        Promocion promo = promocionRepository.findById(detalleDTO.getPromocionId())
                                .orElseThrow(() -> new ResourceNotFoundException("Promoción no encontrada con id " + detalleDTO.getPromocionId()));
                        detalle.setPromocion(promo);
                        detalle.setSubTotal(promo.getPrecioPromocional() * detalle.getCantidad());

                    } else if (detalleDTO.getArticuloId() != null) {
                        // Buscá primero como insumo
                        ArticuloInsumo insumo = articuloInsumoRepository.findById(detalleDTO.getArticuloId()).orElse(null);
                        if (insumo != null) {
                            detalle.setArticuloInsumo(insumo);
                            detalle.setSubTotal(insumo.getPrecioVenta() * detalle.getCantidad());
                        } else {
                            // Si no es insumo, es manufacturado
                            ArticuloManufacturado manufacturado = articuloManufacturadoRepository.findById(detalleDTO.getArticuloId())
                                    .orElseThrow(() -> new ResourceNotFoundException("Artículo manufacturado no encontrado con id " + detalleDTO.getArticuloId()));
                            detalle.setArticuloManufacturado(manufacturado);
                            detalle.setSubTotal(detalleDTO.getSubTotal());
                        }

                    } else {
                        throw new RuntimeException("El detalle del pedido debe tener articuloId o promocionId");
                    }
                    detalle.setPedido(pedido);
                    detalles.add(detalle);
                }

                pedido.setDetallesPedidos(detalles);
            }
            // --- ¡NUEVO!: CALCULAR Y ASIGNAR EL totalCosto AQUÍ ---
            Double costoCalculado = calcularTotalCostoPedido(pedido); // Llama al nuevo método
            pedido.setTotalCosto(costoCalculado); // <-- Asignar el totalCosto al pedido
            System.out.println("DEBUG Costo: Total Costo del Pedido asignado: " + costoCalculado);
            // ----------------------------------------------------

            // --- LÓGICA: APLICAR DESCUENTO DEL 10% SI ES RETIRO EN LOCAL ---
            Double totalCalculado = dto.getTotal(); // Usamos el total que viene del DTO inicialmente

            if (dto.getTipoEnvio() == TipoEnvio.RETIRO_EN_LOCAL) { // Verificar si es retiro en local
                double descuento = totalCalculado * 0.10; // Calcular el 10% de descuento
                totalCalculado = totalCalculado - descuento; // Aplicar el descuento
                System.out.println("DEBUG Descuento: Aplicado 10% de descuento por Retiro en Local. Total Original: " + dto.getTotal() + ", Descuento: " + descuento + ", Total Final: " + totalCalculado);
            } else {
                System.out.println("DEBUG Descuento: No aplica descuento por Retiro en Local. Tipo de Envío: " + dto.getTipoEnvio());
            }

            pedido.setTotal(totalCalculado); // <-- Asignar el total ya con el descuento (si aplica)
            pedido.setTipoEnvio(dto.getTipoEnvio());
            // Establecer estado inicial del pedido (antes del pago)
            pedido.setEstado(Estado.A_CONFIRMAR);
            pedido.setFechaPedido(LocalDate.now());
            pedido.setBaja(false);
            pedido.setTotal(dto.getTotal());
            // --- ¡CALCULAR Y ASIGNAR LA HORA ESTIMADA DE FINALIZACIÓN AQUÍ! ---
            LocalTime horaEstimada = calcularTiempoEstimadoFinalizacion(pedido);
            pedido.setHoraEstimadaFinalizacion(horaEstimada); // <-- ¡Asignar aquí!
            System.out.println("DEBUG Tiempo Estimado: Hora estimada finalización asignada: " + horaEstimada);

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
            System.out.println("procesar not mp" + pedido.getFormaPago());//aca tambien me da null
            if (pedido == null) {
                System.err.println("Notificación: Pedido no encontrado en la BD con externalReference: " + payment.getExternalReference());
                return;
            }
            // --- FIN DE LA CORRECCIÓN DE POSICIÓN ---

            // --- PASO CLAVE: OBTENER LA INSTANCIA DE FACTURA UNA SOLA VEZ AL PRINCIPIO ---
            // Se asume que la Factura ya fue creada y asociada al Pedido en crearPedidoPreferenciaMP
            Factura factura = pedido.getFactura(); // <-- 'factura' ahora se obtiene aquí
            if (factura == null) {
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
                    // --- LÓGICA DE DESCUENTO DE STOCK ACTUALIZADA ---
                    System.out.println("DEBUG Stock (Descuento): Iniciando descuento de stock para Pedido ID: " + pedido.getId());
                    descontarInsumosDelStock(pedido);
                    System.out.println("DEBUG Stock (Descuento): Finalizado descuento de stock.");
                    // --- FIN LÓGICA DE DESCUENTO DE STOCK ACTUALIZADA ---

                    try {
                        ByteArrayOutputStream pdfBytes = (ByteArrayOutputStream) facturaService.generarFacturaPdf(pedido);
                        String filePathLocal = "factura_" + pedido.getId() + ".pdf";
                        Files.write(Paths.get(filePathLocal), pdfBytes.toByteArray());
                        System.out.println("PDF de factura generado para Pedido " + pedido.getId() + ". Guardado LOCALMENTE en: " + filePathLocal);

                        String cloudinaryPublicId = "factura_pedido_" + pedido.getId();
                        generatedPdfUrl = cloudinaryService.uploadByteArray(pdfBytes, cloudinaryPublicId);
                        System.out.println("PDF subido a Cloudinary: " + generatedPdfUrl);

                        factura.setUrlPdf(generatedPdfUrl); // <-- ¡ASIGNA LA URL A LA INSTANCIA ÚNICA DE FACTURA!

                        String recipientEmail = pedido.getPersona().getUsuario().getEmail();
                        String subject = "Factura de tu pedido #" + pedido.getId() + " - El Buen Sabor";
                        String body = "¡Gracias por tu compra, " + pedido.getPersona().getUsuario().getUsername() + "! Adjuntamos la factura de tu pedido #" + pedido.getId();
                        String attachmentFilename = "factura_" + pedido.getId() + ".pdf"; // ✅ línea agregada

                        emailService.sendEmail(recipientEmail, subject, body, pdfBytes, attachmentFilename);
                        System.out.println("Correo con factura enviado a " + recipientEmail);

                    } catch (Exception uploadMailEx) {
                        System.err.println("ERROR al generar PDF, subirlo y/o enviar correo para Pedido " + pedido.getId() + ": " + uploadMailEx.getMessage());
                        uploadMailEx.printStackTrace();
                    }

                    break;


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

            if (factura.getFormaPago() != null) { // Asegurarse de que no sea null antes de asignar
                pedido.setFormaPago(factura.getFormaPago());
            } else {
                // En caso de que factura.getFormaPago() sea null (no debería pasar si se setea arriba)
                pedido.setFormaPago(FormaPago.MERCADO_PAGO); // Establecer por defecto si es una confirmación de MP
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

            boolean estadoCambioAPagado = (updatedPedido.getEstado() == Estado.PAGADO && updatedPedido.getFormaPago() == FormaPago.EFECTIVO);

            actual.setFechaPedido(updatedPedido.getFechaPedido());
            actual.setHoraEstimadaFinalizacion(updatedPedido.getHoraEstimadaFinalizacion());
            actual.setTotal(updatedPedido.getTotal());
            actual.setTotalCosto(updatedPedido.getTotalCosto());
            actual.setEstado(updatedPedido.getEstado());
            actual.setTipoEnvio(updatedPedido.getTipoEnvio());
            actual.setFormaPago(updatedPedido.getFormaPago());

            actual.setPersona(updatedPedido.getPersona());
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

            // --- TU LÓGICA EXISTENTE PARA MANEJAR DETALLES DE PEDIDO ---
            if (updatedPedido.getDetallesPedidos() != null) {
                for (DetallePedido detalle : updatedPedido.getDetallesPedidos()) {
                    // --- DEBUG PRINTS QUE SOLICITASTE ---
                    if (detalle.getArticuloInsumo() != null) {
                        System.out.println("DEBUG: Detalle Pedido ID: " + detalle.getId() + " intentando asociar ArticuloInsumo ID: " + detalle.getArticuloInsumo().getId());
                    } else if (detalle.getArticuloManufacturado() != null) {
                        System.out.println("DEBUG: Detalle Pedido ID: " + detalle.getId() + " intentando asociar ArticuloManufacturado ID: " + detalle.getArticuloManufacturado().getId());
                    } else {
                        System.out.println("DEBUG: Detalle Pedido ID: " + detalle.getId() + " no tiene ArticuloInsumo ni ArticuloManufacturado asignado.");
                    }
                    // --- FIN DEBUG PRINTS ---

                    detalle.setPedido(actual); // Asegura la relación inversa
                    actual.getDetallesPedidos().add(detalle);
                }
            } else {
                actual.getDetallesPedidos().clear();
            }
            // --- FIN DE TU LÓGICA EXISTENTE PARA MANEJAR DETALLES DE PEDIDO ---

            System.out.println("DEBUG Comparación Estados: actual=" + actual.getEstado() + " | nuevo=" + updatedPedido.getEstado());

            if (estadoCambioAPagado) {
                System.out.println("DEBUG Update: Pedido " + id + " cambió a estado PAGADO y FormaPago EFECTIVO.");

                // --- LÓGICA DE DESCUENTO DE STOCK INICIAR AQUÍ ---
                System.out.println("DEBUG Stock (Descuento): Iniciando descuento de stock para Pedido ID: " + actual.getId());
                descontarInsumosDelStock(actual); // Llama a la lógica de descuento de stock
                System.out.println("DEBUG Stock (Descuento): Finalizado descuento de stock.");
                // --- FIN LÓGICA DE DESCUENTO DE STOCK ---

                try {
                    ByteArrayOutputStream pdfBytes = (ByteArrayOutputStream) facturaService.generarFacturaPdf(actual);
                    String filePathLocal = "factura_" + actual.getId() + ".pdf";
                    Files.write(Paths.get(filePathLocal), pdfBytes.toByteArray());
                    System.out.println("PDF de factura generado para Pedido " + actual.getId() + ". Guardado en: " + filePathLocal);

                    String cloudinaryPublicId = "factura_pedido_" + actual.getId();
                    String generatedPdfUrl = cloudinaryService.uploadByteArray(pdfBytes, cloudinaryPublicId);
                    System.out.println("PDF subido a Cloudinary: " + generatedPdfUrl);

                    Factura factura = actual.getFactura();
                    if (factura == null) {
                        factura = new Factura();
                        actual.setFactura(factura);
                    }
                    factura.setUrlPdf(generatedPdfUrl);

                    String recipientEmail = actual.getPersona().getUsuario().getEmail();
                    String subject = "Factura de tu pedido #" + actual.getId() + " - El Buen Sabor";
                    String body = "¡Gracias por tu compra, " + actual.getPersona().getUsuario().getUsername()
                            + "! Adjuntamos la factura de tu pedido #" + actual.getId();
                    String attachmentFilename = "factura_" + actual.getId() + ".pdf";

                    emailService.sendEmail(recipientEmail, subject, body, pdfBytes, attachmentFilename);
                    System.out.println("Correo con factura enviado a " + recipientEmail);
                } catch (Exception e) {
                    System.err.println("ERROR al generar y enviar la factura para el pedido: " + actual.getId());
                    e.printStackTrace();
                    throw new RuntimeException("Error en la generación de factura o envío de email: " + e.getMessage(), e);
                }
            }

            return baseRepository.save(actual);
        } catch (Exception e) {
            throw new RuntimeException("Error al actualizar el pedido: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<Pedido> findPedidosByClienteId(Long personaId) throws Exception {
        try {
            return pedidoRepository.findByPersonaId(personaId);
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }


    @Override
    @Transactional // Esta operación debe ser transaccional
    public NotaCredito anularFacturaYGenerarNotaCredito(Long pedidoId, String motivoAnulacion, Usuario usuarioAnulador) throws Exception {
        try {
            // 1. Obtener el pedido y la factura original
            Pedido pedido = findById(pedidoId);
            if (pedido == null) {
                throw new ResourceNotFoundException("Pedido no encontrado con ID: " + pedidoId);
            }

            Factura facturaAnulada = pedido.getFactura();
            if (facturaAnulada == null) {
                throw new ResourceNotFoundException("Factura no encontrada para el pedido con ID: " + pedidoId);
            }
            if (facturaAnulada.isAnulada()) { // Asumiendo que has añadido 'private boolean anulada = false;' a Factura
                throw new Exception("La factura ya ha sido anulada.");
            }

            // 2. Marcar la factura original como anulada
            facturaAnulada.setAnulada(true); // Actualiza la bandera de anulada
            // baseRepository.save(pedido); // Se guardará al final de la transacción

            // 3. Crear la Nota de Crédito con los mismos ítems e importes
            NotaCredito notaCredito = NotaCredito.builder()
                    .fechaEmision(LocalDate.now())
                    .total(facturaAnulada.getTotalVenta()) // Mismo importe total
                    .motivo(motivoAnulacion)
                    .facturaAnulada(facturaAnulada) // Referencia a la factura que anula
                    .pedidoOriginal(pedido) // Referencia al pedido original
                    .persona(pedido.getPersona()) // Referencia al persona del pedido
                    .build();

            // Copiar detalles del pedido original a la Nota de Crédito
            Set<DetallePedido> detallesNotaCredito = new HashSet<>();
            if (pedido.getDetallesPedidos() != null) {
                for (DetallePedido detalleOriginal : pedido.getDetallesPedidos()) {
                    // Crear una nueva instancia de DetallePedido para la Nota de Crédito
                    // Importante: No es el mismo objeto DetallePedido de la BD, es una copia
                    DetallePedido nuevoDetalleNC = DetallePedido.builder()
                            .cantidad(detalleOriginal.getCantidad())
                            .subTotal(detalleOriginal.getSubTotal())
                            .articuloInsumo(detalleOriginal.getArticuloInsumo()) // Copia la referencia
                            .articuloManufacturado(detalleOriginal.getArticuloManufacturado()) // Copia la referencia
                            .build();
                    // El nuevoDetalleNC.setPedido(null) o .setNotaCredito(notaCredito) dependiendo de la relación
                    // En NotaCredito, DetallePedido tiene @JoinColumn(name = "nota_credito_id")
                    nuevoDetalleNC.setNotaCredito(notaCredito); // Establecer la relación inversa
                    detallesNotaCredito.add(nuevoDetalleNC);
                }
            }
            notaCredito.setDetalles(detallesNotaCredito); // Asignar los detalles copiados
            notaCredito = notaCreditoService.save(notaCredito); // Guardar la Nota de Crédito


            // 4. Reponer el stock de ingredientes
         /* if (pedido.getDetallesPedidos() != null) {
                for (DetallePedido detalle : pedido.getDetallesPedidos()) {
                    // Reponer ingredientes solo si son artículos manufacturados
                    if (detalle.getArticuloManufacturado() != null) {
                        ArticuloManufacturado am = detalle.getArticuloManufacturado();
                        if (am.getDetalles() != null) { // Detalles del AM que son los insumos
                            for (ArticuloManufacturadoDetalle amd : am.getDetalles()) {
                                ArticuloInsumo insumo = amd.getArticuloInsumo();
                                if (insumo != null && insumo.getEsParaElaborar()) {
                                    Double cantidadAReponer = amd.getCantidad() * detalle.getCantidad(); // Cantidad de insumo por cantidad de AM
                                    insumo.setStockActual(insumo.getStockActual() + cantidadAReponer);
                                    articuloInsumoService.save(insumo); // Guardar el insumo con stock repuesto
                                }
                            }
                        }
                    }
                }
            }*/

            // 5. Registrar la anulación
            RegistroAnulacion registroAnulacion = RegistroAnulacion.builder()
                    .fechaHoraAnulacion(LocalDateTime.now())
                    .motivoAnulacion(motivoAnulacion)
                    .usuarioAnulador(usuarioAnulador)
                    .facturaAnulada(facturaAnulada)
                    .notaCreditoGenerada(notaCredito)
                    .build();
            registroAnulacionService.save(registroAnulacion); // Guardar el registro de anulación

            // 6. Opcional: Marcar el pedido como anulado o cambiar su estado
            pedido.setAnulado(true); // Asumiendo que añades 'anulado' a Pedido
            pedido.setEstado(Estado.CANCELADO); // O un nuevo estado como 'ANULADO_CON_NC'
            baseRepository.save(pedido); // Guarda el pedido con la factura y el estado actualizado

            System.out.println("Factura " + facturaAnulada.getId() + " anulada exitosamente. Nota de Crédito " + notaCredito.getId() + " generada.");
            // --- NUEVA LÓGICA: Generar PDF de Nota de Crédito y Subir a Cloudinary ---
            try {
                ByteArrayOutputStream pdfNotaCreditoBytes = facturaService.generarNotaCreditoPdf(notaCredito);

                String filePathLocalNC = "nota_credito_" + notaCredito.getId() + ".pdf";
                Files.write(Paths.get(filePathLocalNC), pdfNotaCreditoBytes.toByteArray());
                System.out.println("PDF de Nota de Crédito generado para NC " + notaCredito.getId() + ". Guardado LOCALMENTE en: " + filePathLocalNC);

                String cloudinaryPublicIdNC = "nota_credito_" + notaCredito.getId(); // ID único para Cloudinary
                String pdfUrlNC = cloudinaryService.uploadByteArray(pdfNotaCreditoBytes, cloudinaryPublicIdNC);
                System.out.println("PDF de Nota de Crédito subido a Cloudinary: " + pdfUrlNC);

                notaCredito.setUrlPdfNotaCredito(pdfUrlNC); // <-- ¡Guardar la URL en la NotaCredito!
                notaCreditoService.save(notaCredito); // <-- Persistir la URL

                // ✅ Corrección: acceder al usuario del persona
                String recipientEmail = notaCredito.getPersona().getUsuario().getEmail();
                String subject = "Nota de Crédito de tu pedido #" + pedido.getId() + " - El Buen Sabor";
                String body = "Estimado/a " + notaCredito.getPersona().getUsuario().getUsername() + ", \n\n" +
                        "Adjuntamos la Nota de Crédito N° " + notaCredito.getId() + " emitida por la anulación de tu factura del pedido #" + pedido.getId() + ".\n" +
                        "Motivo de la anulación: " + notaCredito.getMotivo() + "\n\n" +
                        "Puedes descargarla también desde: " + pdfUrlNC + "\n\n" +
                        "Gracias por tu comprensión.\n\n" +
                        "Atentamente,\n" +
                        "El equipo de El Buen Sabor";
                String attachmentFilenameNC = "nota_credito_" + notaCredito.getId() + ".pdf";

                emailService.sendEmail(recipientEmail, subject, body, pdfNotaCreditoBytes, attachmentFilenameNC);
                System.out.println("Correo con Nota de Crédito enviado a " + recipientEmail);

            } catch (Exception pdfUploadNCEx) {
                System.err.println("ERROR al generar PDF o subir Nota de Crédito " + notaCredito.getId() + " a Cloudinary: " + pdfUploadNCEx.getMessage());
                pdfUploadNCEx.printStackTrace();
            }

            // --- FIN NUEVA LÓGICA ---
            System.out.println("Factura " + facturaAnulada.getId() + " anulada exitosamente. Nota de Crédito " + notaCredito.getId() + " generada.");
            return notaCredito;

        } catch (Exception e) {
            System.err.println("Error al anular factura y generar nota de crédito para pedido " + pedidoId + ": " + e.getMessage());
            e.printStackTrace();
            throw new Exception("Error en el proceso de anulación de factura: " + e.getMessage(), e);
        }
    }

    // --- Nuevo método para calcular el tiempo estimado de finalización ---
    @Override // Implementa el método de la interfaz
    public LocalTime calcularTiempoEstimadoFinalizacion(Pedido pedido) throws Exception {
        long tiempoTotalMinutos = 0;

        // 1. Del tiempo estimado de cada uno de los artículos pedidos por el persona se elige el mayor
        long maxTiempoArticulosPedido = 0;
        if (pedido.getDetallesPedidos() != null) {
            maxTiempoArticulosPedido = pedido.getDetallesPedidos().stream()
                    .filter(dp -> dp.getArticuloManufacturado() != null && dp.getArticuloManufacturado().getTiempoEstimadoMinutos() != null)
                    .mapToLong(dp -> dp.getArticuloManufacturado().getTiempoEstimadoMinutos())
                    .max() // Obtiene el valor máximo de Integer
                    .orElse(0L); // Si no hay AMs o tiempos, el máximo es 0
        }
        tiempoTotalMinutos += maxTiempoArticulosPedido;
        System.out.println("DEBUG Tiempo Estimado: Max tiempo de artículos en este pedido: " + maxTiempoArticulosPedido + " min.");


        // 2. De los pedidos que se encuentran en cocina, el artículo con el mayor tiempo estimado
        // Necesitamos una consulta para obtener pedidos en estado EN_COCINA
        List<Pedido> pedidosEnCocina = pedidoRepository.findByEstado(Estado.EN_COCINA); // <-- Asegúrate que findByEstado(Estado) existe en PedidoRepository
        long maxTiempoCocina = 0;
        if (pedidosEnCocina != null && !pedidosEnCocina.isEmpty()) {
            maxTiempoCocina = pedidosEnCocina.stream()
                    .flatMap(p -> p.getDetallesPedidos().stream()) // Obtener todos los detalles de todos los pedidos en cocina
                    .filter(dp -> dp.getArticuloManufacturado() != null && dp.getArticuloManufacturado().getTiempoEstimadoMinutos() != null)
                    .mapToLong(dp -> dp.getArticuloManufacturado().getTiempoEstimadoMinutos())
                    .max()
                    .orElse(0L);
        }
        tiempoTotalMinutos += maxTiempoCocina;
        System.out.println("DEBUG Tiempo Estimado: Max tiempo de artículos en pedidos en cocina: " + maxTiempoCocina + " min.");


        // 3. 10 minutos de entrega por delivery (solo si el persona eligió dicha opción)
        if (pedido.getTipoEnvio() == TipoEnvio.DELIVERY) { // <-- Asumo que TipoEnvio ya está seteado en el pedido
            tiempoTotalMinutos += 10;
            System.out.println("DEBUG Tiempo Estimado: Añadidos 10 min por DELIVERY.");
        }

        System.out.println("DEBUG Tiempo Estimado: Tiempo total estimado: " + tiempoTotalMinutos + " min.");

        // Convertir minutos a LocalTime (a partir de la hora actual)
        LocalTime horaActual = LocalTime.now();
        return horaActual.plusMinutes(tiempoTotalMinutos);

    }

    @Override
    @Transactional(readOnly = true) // <-- Implementación del método findPedidosByEstado
    public List<Pedido> findPedidosByEstados(List<Estado> estados) throws Exception {
        try {
            return pedidoRepository.findByEstadoIn(estados);
        } catch (Exception e) {
            throw new Exception("Error al buscar pedidos por estado: " + e.getMessage());
        }
    }

    //------------------------REPORTE CLIENTES-----------------------------
    @Override
    public List<PersonaReporteDTO> obtenerReporteClientes(LocalDate desde, LocalDate hasta, String orden) {
        return pedidoRepository.obtenerReporteClientes(desde, hasta, orden);
    }

    //-----------------------------------------------------------------------------------
    @Override
    public List<ProductoRankingDTO> obtenerRankingProductosMasVendidos(LocalDate desde, LocalDate hasta) {
        return detallePedidoRepository.rankingProductosMasVendidos(desde, hasta);
    }

    @Transactional
    public Double calcularTotalCostoPedido(Pedido pedido) {
        double totalCosto = 0.0;
        if (pedido.getDetallesPedidos() == null) return 0.0;

        for (DetallePedido detalle : pedido.getDetallesPedidos()) {
            double costoDetalle = 0.0;
            // CASO 1: ArticuloInsumo directo
            if (detalle.getArticuloInsumo() != null) {
                if (detalle.getArticuloInsumo().getPrecioCompra() != null) {
                    costoDetalle = detalle.getArticuloInsumo().getPrecioCompra() * detalle.getCantidad();
                }
            }
            // CASO 2: ArticuloManufacturado directo
            else if (detalle.getArticuloManufacturado() != null) {
                ArticuloManufacturado am = detalle.getArticuloManufacturado();
                double costoManufacturadoUnitario = 0.0;
                if (am.getDetalles() != null) {
                    for (ArticuloManufacturadoDetalle amd : am.getDetalles()) {
                        if (amd.getArticuloInsumo() != null && amd.getArticuloInsumo().getPrecioCompra() != null) {
                            costoManufacturadoUnitario += amd.getArticuloInsumo().getPrecioCompra() * amd.getCantidad();
                        }
                    }
                }
                costoDetalle = costoManufacturadoUnitario * detalle.getCantidad();
            }
            // CASO 3: Promoción
            else if (detalle.getPromocion() != null) {
                Promocion promo = detalle.getPromocion();
                double costoPromoUnitario = 0.0;

                // CORREGIDO: Sumar costos de ArticulosManufacturados desde promocionDetalles
                if (promo.getPromocionDetalles() != null) {
                    for (PromocionDetalle promoDetalle : promo.getPromocionDetalles()) {
                        ArticuloManufacturado amPromo = promoDetalle.getArticuloManufacturado();
                        Integer cantidadEnPromo = promoDetalle.getCantidad();
                        if (amPromo != null && amPromo.getDetalles() != null && cantidadEnPromo != null) {
                            double costoAMUnitario = 0.0;
                            for (ArticuloManufacturadoDetalle detalleReceta : amPromo.getDetalles()) {
                                if (detalleReceta.getArticuloInsumo() != null && detalleReceta.getArticuloInsumo().getPrecioCompra() != null) {
                                    costoAMUnitario += detalleReceta.getArticuloInsumo().getPrecioCompra() * detalleReceta.getCantidad();
                                }
                            }
                            costoPromoUnitario += (costoAMUnitario * cantidadEnPromo);
                        }
                    }
                }

                // CORREGIDO: Sumar costos de ArticulosInsumos directos de la promoción
                if (promo.getArticulosInsumos() != null) {
                    for (ArticuloInsumo aiPromo : promo.getArticulosInsumos()) {
                        if (aiPromo.getPrecioCompra() != null) {
                            costoPromoUnitario += aiPromo.getPrecioCompra();
                        }
                    }
                }
                costoDetalle = costoPromoUnitario * detalle.getCantidad();
            }
            totalCosto += costoDetalle;
        }
        return totalCosto;
    }
    @Transactional
    public void descontarInsumosDelStock(Pedido pedido) {
        // Set para recolectar ArticuloManufacturado que necesitan ser marcados como baja
        Set<ArticuloManufacturado> articulosManufacturadosToUpdate = new HashSet<>();

        // Primera pasada: realizar las deducciones de stock para todos los ítems
        for (DetallePedido detalle : pedido.getDetallesPedidos()) {
            // 🟠 CASO 1: Artículo Manufacturado (directamente en el detalle de pedido)
            ArticuloManufacturado manufacturadoDelDetalle = detalle.getArticuloManufacturado();
            if (manufacturadoDelDetalle != null) {
                // Obtener el estado más reciente de ArticuloManufacturado y sus detalles (receta)
                // Esto es crucial si los detalles no se cargan eager o si el objeto está desasociado
                ArticuloManufacturado fullAm = articuloManufacturadoRepository.findById(manufacturadoDelDetalle.getId())
                        .orElseThrow(() -> new ResourceNotFoundException("ArticuloManufacturado no encontrado con ID: " + manufacturadoDelDetalle.getId()));
                for (ArticuloManufacturadoDetalle det : fullAm.getDetalles()) {
                    ArticuloInsumo insumo = det.getArticuloInsumo();
                    if (insumo != null) {
                        double cantidadAReducir = (double) detalle.getCantidad() * det.getCantidad();
                        performStockDeductionForInsumo(insumo, cantidadAReducir, fullAm, articulosManufacturadosToUpdate);
                    }
                }
            }

            // 🟢 CASO 2: Artículo Insumo directo (directamente en el detalle de pedido)
            ArticuloInsumo insumoDirecto = detalle.getArticuloInsumo();
            if (insumoDirecto != null) {
                // Obtener el estado más reciente de ArticuloInsumo
                ArticuloInsumo fullInsumoDirecto = articuloInsumoRepository.findById(insumoDirecto.getId())
                        .orElseThrow(() -> new ResourceNotFoundException("ArticuloInsumo no encontrado con ID: " + insumoDirecto.getId()));
                // Solo se descuentan insumos directos que NO son 'para elaborar' (ya que esos se manejan por AMs)
                if (!fullInsumoDirecto.getEsParaElaborar()) {
                    double cantidadAReducir = detalle.getCantidad();
                    performStockDeductionForInsumo(fullInsumoDirecto, cantidadAReducir, null, articulosManufacturadosToUpdate);
                } else {
                    System.out.println("DEBUG Stock (Descuento): ArticuloInsumo '" + fullInsumoDirecto.getDenominacion() + "' (ID: " + fullInsumoDirecto.getId() + ") es para elaborar. No se descuenta directamente de stock de venta.");
                }
            }

            // 🔵 CASO 3: Promoción (si el detalle de pedido hace referencia a una promoción)
            Promocion promocion = detalle.getPromocion();
            if (promocion != null) {
                Promocion fullPromo = promocionRepository.findById(promocion.getId())
                        .orElseThrow(() -> new ResourceNotFoundException("Promocion no encontrada con ID: " + promocion.getId()));

                // --- Procesar ArticulosManufacturados con cantidad desde PromocionDetalle ---
                if (fullPromo.getPromocionDetalles() != null) {
                    for (PromocionDetalle promoDetalle : fullPromo.getPromocionDetalles()) {
                        ArticuloManufacturado amPromo = promoDetalle.getArticuloManufacturado();
                        Integer cantidadEnPromo = promoDetalle.getCantidad();

                        if (amPromo.getDetalles() != null) {
                            for (ArticuloManufacturadoDetalle detalleReceta : amPromo.getDetalles()) {
                                ArticuloInsumo insumoReceta = detalleReceta.getArticuloInsumo();
                                if (insumoReceta != null) {
                                    double cantidadAReducir = (double) detalle.getCantidad() * cantidadEnPromo * detalleReceta.getCantidad();
                                    performStockDeductionForInsumo(insumoReceta, cantidadAReducir, amPromo, articulosManufacturadosToUpdate);
                                }
                            }
                        }
                    }
                }

                // --- Procesar ArticulosInsumos directos de la promoción ---
                if (fullPromo.getArticulosInsumos() != null) {
                    for (ArticuloInsumo aiPromo : fullPromo.getArticulosInsumos()) {
                        if (!aiPromo.getEsParaElaborar()) {
                            double cantidadAReducir = detalle.getCantidad();
                            performStockDeductionForInsumo(aiPromo, cantidadAReducir, null, articulosManufacturadosToUpdate);
                        }
                    }
                }
            }
        }
        // Segunda pasada: determinar si las promociones deben ser dadas de baja
        Set<Promocion> promocionesToSetBaja = new HashSet<>(); // Para guardar Promociones que irán a baja

        for (DetallePedido detalle : pedido.getDetallesPedidos()) {
            if (detalle.getPromocion() != null) {
                Promocion fullPromo = promocionRepository.findById(detalle.getPromocion().getId())
                        .orElseThrow(() -> new ResourceNotFoundException("Promocion no encontrada con ID: " + detalle.getPromocion().getId()));

                boolean promoShouldBeBaja = false;

                // Verificar ArticuloManufacturado en la promoción
                if (fullPromo.getPromocionDetalles() != null) {
                    for (PromocionDetalle promoDetalle : fullPromo.getPromocionDetalles()) {
                        if (promoDetalle.getArticuloManufacturado().getBaja()) {
                            promoShouldBeBaja = true;
                            break;
                        }
                    }
                }


                // Verificar si algún ArticuloInsumo directo en la promoción está de baja
                if (!promoShouldBeBaja && fullPromo.getArticulosInsumos() != null) {
                    for (ArticuloInsumo aiPromo : fullPromo.getArticulosInsumos()) {
                        if (aiPromo.getBaja()) {
                            promoShouldBeBaja = true;
                            break;
                        }
                    }
                }

                /*-------------------------------------------------------------------------------------------------
                // Verificar si algún insumo de los detalles propios de la promoción está de baja
                // Solo si la promoción aún no ha sido marcada para baja
                if (!promoShouldBeBaja && fullPromo.getDetalles() != null) {
                    for (ArticuloManufacturadoDetalle amdPromoDetalle : fullPromo.getDetalles()) {
                        ArticuloInsumo insumoDetalle = amdPromoDetalle.getArticuloInsumo();
                        if (insumoDetalle != null) {
                            // Cargar el estado más reciente del insumo para ver si está de baja
                            ArticuloInsumo currentInsumoDetalle = articuloInsumoRepository.findById(insumoDetalle.getId())
                                    .orElseThrow(() -> new ResourceNotFoundException("Insumo en detalle de promocion no encontrado: " + insumoDetalle.getId()));
                            if (currentInsumoDetalle.getBaja()) {
                                promoShouldBeBaja = true;
                                break; // Si uno está de baja, la promo completa debe ir a baja
                            }
                        }
                    }
                }
---------------------------------------------------------------------------------------------------------------------------------------------------------*/
                // Si la promoción debe ser dada de baja y aún no lo está, la añadimos al set
                if (promoShouldBeBaja && !fullPromo.getBaja()) {
                    fullPromo.setBaja(true);
                    promocionesToSetBaja.add(fullPromo);
                }
            }
        }

        // Guardar cualquier ArticuloManufacturado que necesite actualización (ya marcado como baja en performStockDeductionForInsumo)
        for (ArticuloManufacturado am : articulosManufacturadosToUpdate) {
            articuloManufacturadoRepository.save(am);
            System.out.println("DEBUG Estado: Artículo Manufacturado '" + am.getDenominacion() +
                    "' (ID: " + am.getId() + ") DADO DE BAJA porque un insumo vital se agotó (actualizado fuera del bucle).");
        }

        // Guardar cualquier Promoción que necesite ser dada de baja
        for (Promocion promo : promocionesToSetBaja) {
            promocionRepository.save(promo);
            System.out.println("DEBUG Estado: Promoción '" + promo.getDenominacion() +
                    "' (ID: " + promo.getId() + ") DADO DE BAJA porque un componente se agotó.");
        }
    }

    @Transactional
    public void performStockDeductionForInsumo(ArticuloInsumo insumo, double cantidadAReducir,
                                               ArticuloManufacturado articuloManufacturado,
                                               Set<ArticuloManufacturado> articulosManufacturadosToUpdate) {
        double stockAntes = insumo.getStockActual();
        double nuevoStock = insumo.getStockActual() - cantidadAReducir;
        insumo.setStockActual(nuevoStock);

        boolean insumoDadoDeBaja = false;
        // Si el stock actual es menor que el stock mínimo, se marca el insumo como de baja
        if (insumo.getStockActual() < insumo.getStockMinimo()) {
            insumo.setBaja(true);
            insumoDadoDeBaja = true;
            System.out.println("DEBUG Estado: Insumo '" + insumo.getDenominacion() + "' (ID: " + insumo.getId() + ") DADO DE BAJA por bajo stock.");

            // Si hay un ArticuloManufacturado asociado y el insumo se da de baja,
            // se marca el ArticuloManufacturado para ser dado de baja (se añadirá al set).
            if (articuloManufacturado != null) {
                if (!articuloManufacturado.getBaja()) { // Solo si no está ya dado de baja
                    articuloManufacturado.setBaja(true);
                    articulosManufacturadosToUpdate.add(articuloManufacturado); // Añadir al set para actualización posterior
                }
            }
        }

        articuloInsumoRepository.save(insumo); // Siempre guarda la actualización del insumo inmediatamente.

        System.out.println("DEBUG Stock (Descuento): Descontado insumo '" + insumo.getDenominacion() +
                "' (ID: " + insumo.getId() + ") para pedido. Cant: " + cantidadAReducir +
                ". Stock Antes: " + stockAntes + ". Stock Después: " + insumo.getStockActual() +
                (insumoDadoDeBaja ? " (INSUMO DADO DE BAJA)" : ""));
    }
    @Transactional
    public boolean verificarStockParaPedido(Pedido pedido) {
        // Usamos un mapa para llevar la cuenta de las cantidades totales de cada insumo
        // que se necesitarían para el pedido, para evitar múltiples consultas o cálculos.
        Map<Long, Double> insumosNecesarios = new HashMap<>();

        for (DetallePedido detalle : pedido.getDetallesPedidos()) {
            // 🟠 CASO 1: Artículo Manufacturado
            ArticuloManufacturado manufacturado = detalle.getArticuloManufacturado();
            if (manufacturado != null) {
                for (ArticuloManufacturadoDetalle det : manufacturado.getDetalles()) {
                    ArticuloInsumo insumo = det.getArticuloInsumo();
                    if (insumo != null) {
                        double cantidadNecesaria = detalle.getCantidad() * det.getCantidad();
                        insumosNecesarios.merge(insumo.getId(), cantidadNecesaria, Double::sum);
                    }
                }
            }

            // 🟢 CASO 2: Artículo Insumo directo
            ArticuloInsumo insumoDirecto = detalle.getArticuloInsumo();
            if (insumoDirecto != null) {
                // Solo considera insumos directos que no son 'para elaborar'
                if (!insumoDirecto.getEsParaElaborar()) {
                    double cantidadNecesaria = detalle.getCantidad();
                    insumosNecesarios.merge(insumoDirecto.getId(), cantidadNecesaria, Double::sum);
                }
            }

            // 🔵 CASO 3: Promoción (CORREGIDO)
            Promocion promocion = detalle.getPromocion();
            if (promocion != null) {
                // Sumar insumos de ArticulosManufacturados dentro de la promoción
                if (promocion.getPromocionDetalles() != null) {
                    for (PromocionDetalle promoDetalle : promocion.getPromocionDetalles()) {
                        ArticuloManufacturado am = promoDetalle.getArticuloManufacturado();
                        Integer cantidadEnPromo = promoDetalle.getCantidad();
                        if (am.getDetalles() != null) {
                            for (ArticuloManufacturadoDetalle detalleReceta : am.getDetalles()) {
                                ArticuloInsumo insumo = detalleReceta.getArticuloInsumo();
                                if (insumo != null) {
                                    double cantidadNecesaria = (double) detalle.getCantidad() * cantidadEnPromo * detalleReceta.getCantidad();
                                    insumosNecesarios.merge(insumo.getId(), cantidadNecesaria, Double::sum);
                                }
                            }
                        }
                    }
                }
                // Sumar ArticulosInsumos directos dentro de la promoción (NUEVO)
                if (promocion.getArticulosInsumos() != null) {
                    for (ArticuloInsumo aiPromo : promocion.getArticulosInsumos()) {
                        if (!aiPromo.getEsParaElaborar()) {
                            double cantidadNecesaria = detalle.getCantidad(); // Se asume que es 1 unidad de insumo por cada promo pedida
                            insumosNecesarios.merge(aiPromo.getId(), cantidadNecesaria, Double::sum);
                        }
                    }
                }
                /*--------------------------------------------------------------------------------------------
                // Sumar insumos definidos en los detalles propios de la promoción (NUEVO)
                if (promocion.getDetalles() != null) {
                    for (ArticuloManufacturadoDetalle amdPromoDetalle : promocion.getDetalles()) {
                        ArticuloInsumo insumoDetalle = amdPromoDetalle.getArticuloInsumo();
                        if (insumoDetalle != null) {
                            double cantidadNecesaria = detalle.getCantidad() * amdPromoDetalle.getCantidad();
                            insumosNecesarios.merge(insumoDetalle.getId(), cantidadNecesaria, Double::sum);
                        }
                    }
                }*/
            }
        }

        // Ahora, recorremos el mapa de insumos necesarios y comparamos con el stock actual
        for (Map.Entry<Long, Double> entry : insumosNecesarios.entrySet()) {
            Long insumoId = entry.getKey();
            Double cantidadRequerida = entry.getValue();

            // Buscar el insumo por ID (podrías tener un mapa de insumos cargados previamente para optimizar)
            // Es crucial cargar el insumo directamente de la base de datos para obtener el stock más reciente
            ArticuloInsumo insumo = articuloInsumoRepository.findById(insumoId)
                    .orElseThrow(() -> new RuntimeException("Insumo no encontrado: " + insumoId));

            // Si el insumo está dado de baja o el stock es insuficiente, el pedido no es viable
            if (insumo.getBaja() || insumo.getStockActual() < cantidadRequerida) {
                System.out.println("DEBUG Verificación Stock: Insuficiente stock para '" + insumo.getDenominacion() +
                        "'. Requerido: " + cantidadRequerida + ", Disponible: " + insumo.getStockActual() +
                        (insumo.getBaja() ? " (El insumo está dado de baja)" : ""));
                return false; // No hay stock, retorna false inmediatamente
            }
        }

        return true; // Hay stock para todos los insumos
    }

    @Override
    @Transactional
    public Pedido marcarPedidoComoPagadoYFacturar(Long pedidoId) throws Exception {
        Pedido pedido = findById(pedidoId);

        if (pedido.getEstado() != Estado.A_CONFIRMAR) {
            throw new IllegalStateException("Solo se puede marcar como PAGADO un pedido en estado A_CONFIRMAR");
        }
        pedido.setEstado(Estado.PAGADO);
        System.out.println("DEBUG Estado: Pedido '" + pedido.getEstado());

        // Descontar stock (puede estar en otro método según tu estructura)
        descontarInsumosDelStock(pedido);

        // Generar PDF de la factura
        ByteArrayOutputStream pdfBytes = facturaService.generarFacturaPdf(pedido);

        // Subir a Cloudinary (la carpeta la maneja CloudinaryService)
        String publicId = "factura_pedido_" + pedido.getId();
        String urlPdf = cloudinaryService.uploadByteArray(pdfBytes, publicId);

        // Actualizar/crear Factura asociada
        Factura factura = pedido.getFactura();
        if (factura == null) {
            factura = new Factura();
            pedido.setFactura(factura);
        }
        factura.setUrlPdf(urlPdf);
        factura.setFechaFacturacion(LocalDate.now());
        factura.setFormaPago(pedido.getFormaPago());
        factura.setTotalVenta(pedido.getTotal());

        // Enviar email al cliente
        String destinatario = pedido.getPersona().getUsuario().getEmail();
        String subject = "Factura de tu pedido #" + pedido.getId() + " - El Buen Sabor";
        String body = "¡Gracias por tu compra! Adjuntamos la factura de tu pedido #" + pedido.getId() + ".";
        String attachment = "factura_" + pedido.getId() + ".pdf";
        emailService.sendEmail(destinatario, subject, body, pdfBytes, attachment);

        return save(pedido);
    }



}