
package ElBuenSabor.ProyectoFinal.Service;

import ElBuenSabor.ProyectoFinal.DTO.*;
import ElBuenSabor.ProyectoFinal.Entities.*;
import ElBuenSabor.ProyectoFinal.Entities.Estado;
import ElBuenSabor.ProyectoFinal.Entities.FormaPago;

import ElBuenSabor.ProyectoFinal.Exceptions.ResourceNotFoundException;
import ElBuenSabor.ProyectoFinal.Repositories.*;
import com.mercadopago.client.payment.PaymentClient;
import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.exceptions.MPException;
import com.mercadopago.resources.payment.Payment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import java.util.HashSet;

import java.util.Set;


@Service
public class PedidoServiceImpl extends BaseServiceImpl<Pedido, Long> implements PedidoService {

    // Necesitamos los repositorios para resolver las relaciones
    private final ArticuloInsumoRepository articuloInsumoRepository;
    private final ArticuloManufacturadoRepository articuloManufacturadoRepository;
    private final ClienteRepository clienteRepository;
    private final DomicilioRepository domicilioRepository;
    private final SucursalRepository sucursalRepository;
    private final UsuarioRepository usuarioRepository;
    private final ArticuloRepository articuloRepository; // No se usa en los métodos provistos, pero si es una dependencia general se mantiene

    public PedidoServiceImpl(
            PedidoRepository pedidoRepository,
            ArticuloInsumoRepository articuloInsumoRepository,
            ArticuloManufacturadoRepository articuloManufacturadoRepository,
            ClienteRepository clienteRepository,
            DomicilioRepository domicilioRepository,
            SucursalRepository sucursalRepository,
            UsuarioRepository usuarioRepository,
            ArticuloRepository articuloRepository) {
        super(pedidoRepository); // Llama al constructor de la clase base
        this.articuloInsumoRepository = articuloInsumoRepository;
        this.articuloManufacturadoRepository = articuloManufacturadoRepository;
        this.clienteRepository = clienteRepository;
        this.domicilioRepository = domicilioRepository;
        this.sucursalRepository = sucursalRepository;
        this.usuarioRepository = usuarioRepository;
        this.articuloRepository = articuloRepository;
    }

    // Los métodos findAll, findById, save, update, deleteById, toggleBaja
    // se heredan de BaseServiceImpl.

    // Nuevo método para crear un pedido antes de generar la preferencia de MP
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

            // 🧾 Factura (la factura de MP se completará después, pero se crea un placeholder)
            if (dto.getFactura() != null) {
                FacturaCreateDTO f = dto.getFactura();
                Factura factura = Factura.builder()
                        .fechaFacturacion(f.getFechaFacturacion())
                        // Los IDs de MP se llenarán después de la confirmación
                        .mpPaymentId(null)
                        .mpMerchantOrderId(null)
                        .mpPreferenceId(null)
                        .mpPaymentType(null)
                        .formaPago(FormaPago.MERCADO_PAGO) // Asumimos que esta es la forma de pago si se usa este método
                        .totalVenta(f.getTotalVenta())
                        .build();
                pedido.setFactura(factura);
            }

            // 🧾 Detalles del pedido
            if (dto.getDetalles() != null) {
                Set<DetallePedido> detalles = new HashSet<>();
                for (DetallePedidoCreateDTO detalleDTO : dto.getDetalles()) {
                    DetallePedido detalle = new DetallePedido();
                    detalle.setCantidad(detalleDTO.getCantidad());
                    detalle.setSubTotal(detalleDTO.getSubTotal());

                    // Resolver el tipo de artículo
                    ArticuloInsumo insumo = articuloInsumoRepository.findById(detalleDTO.getArticuloId()).orElse(null);
                    if (insumo != null) {
                        detalle.setArticuloInsumo(insumo);
                    } else {
                        ArticuloManufacturado manufacturado = articuloManufacturadoRepository.findById(detalleDTO.getArticuloId())
                                .orElseThrow(() -> new ResourceNotFoundException("Artículo no encontrado"));
                        detalle.setArticuloManufacturado(manufacturado);
                    }

                    detalle.setPedido(pedido); // Establecer la relación inversa
                    detalles.add(detalle);
                }
                pedido.setDetallesPedidos(detalles);
            }

            // Establecer estado inicial del pedido (antes del pago)
            pedido.setEstado(Estado.A_CONFIRMAR); // O un nuevo estado como PENDIENTE_PAGO si lo defines
            pedido.setFechaPedido(LocalDate.now()); // Establecer la fecha actual del pedido
            pedido.setBaja(false); // Por defecto, el pedido no está dado de baja

            return baseRepository.save(pedido); // Guarda el pedido en la BD
        } catch (Exception e) {
            throw new Exception("Error al crear el pedido para Mercado Pago: " + e.getMessage());
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
            System.out.println("Payment ID (MP): " + payment.getId());
            // El external_reference que enviamos al crear la preferencia es el ID de nuestro Pedido
            Long pedidoId = Long.valueOf(payment.getExternalReference());
            Pedido pedido = findById(pedidoId);

            if (pedido == null) {
                System.err.println("Notificación: Pedido no encontrado en la BD con externalReference: " + payment.getExternalReference());
                return;
            }

            // Actualizar el estado del Pedido según el estado del pago de Mercado Pago
            switch (payment.getStatus()) {
                case "approved":
                    pedido.setEstado(Estado.PAGADO);
                    break;
                case "pending":
                    pedido.setEstado(Estado.A_CONFIRMAR);
                    break;
                case "in_process":
                    pedido.setEstado(Estado.EN_COCINA);
                    break;
                case "rejected":
                    pedido.setEstado(Estado.RECHAZADO);
                    break;
                case "cancelled":
                    pedido.setEstado(Estado.CANCELADO);
                    break;
                case "refunded":
                    pedido.setEstado(Estado.DEVOLUCION);
                    break;
                case "list":
                    pedido.setEstado(Estado.LISTO);
                    break;
                case "delivery":
                    pedido.setEstado(Estado.EN_DELIVERY);
                    break;
                case "dedicated":
                    pedido.setEstado(Estado.ENTREGADO);
                    break;
                case "charged_back":
                    pedido.setEstado(Estado.CANCELADO);
                    break;
                default:
                    System.out.println("Estado de pago de MP desconocido: " + payment.getStatus());
            }

            // Actualizar la Factura asociada al Pedido con los datos de Mercado Pago
            Factura factura = pedido.getFactura();
            if (factura == null) {
                factura = new Factura();
                pedido.setFactura(factura);
            }

            // CORRECCIÓN: Usar los métodos correctos del objeto Payment
            factura.setMpPaymentId(payment.getId() != null ? payment.getId().intValue() : null);


            if (payment.getOrder() != null && payment.getOrder().getId() != null) {
                factura.setMpMerchantOrderId(payment.getOrder().getId().intValue());
            }else {
                // Si el Order objeto es null, asegura que el campo en factura también sea null o un valor por defecto.
                factura.setMpMerchantOrderId(null);
            }


            // OPCIÓN 3: Si necesitas obtener estos datos, puedes hacer consultas separadas
            // usando MerchantOrderClient para obtener la orden del comerciante

            factura.setMpPaymentType(payment.getPaymentTypeId());
            factura.setFormaPago(FormaPago.MERCADO_PAGO);
            factura.setTotalVenta(payment.getTransactionAmount() != null ? payment.getTransactionAmount().doubleValue() : null);

            // Convertir fecha de MP a LocalDate
            if (payment.getDateApproved() != null) {
                LocalDate fechaFacturacion = payment.getDateApproved().toLocalDate();
                factura.setFechaFacturacion(fechaFacturacion);
            } else if (payment.getDateCreated() != null) {
                LocalDate fechaFacturacion = payment.getDateCreated().toLocalDate();
                factura.setFechaFacturacion(fechaFacturacion);
            }

            // Guardar el pedido actualizado
            baseRepository.save(pedido);

            System.out.println("Pedido " + pedido.getId() + " actualizado a estado: " + pedido.getEstado() + " por notificación de MP.");

        } catch (MPException | MPApiException e) {
            System.err.println("Error SDK Mercado Pago al procesar notificación de pago: " + e.getMessage());
            throw new Exception("Error al obtener detalles del pago de Mercado Pago", e);
        } catch (Exception e) {
            System.err.println("Error general al procesar notificación de pago: " + e.getMessage());
            throw new Exception("Error al procesar la notificación de pago", e);
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

            // Actualizar Factura
            if (updatedPedido.getFactura() != null) {
                Factura updatedFactura = updatedPedido.getFactura();
                Factura existingFactura = actual.getFactura();

                if (existingFactura == null) {
                    existingFactura = new Factura();
                    actual.setFactura(existingFactura);
                }
                // Copiar propiedades de la factura actualizada a la existente
                existingFactura.setFechaFacturacion(updatedFactura.getFechaFacturacion());
                existingFactura.setMpPaymentId(updatedFactura.getMpPaymentId());
                existingFactura.setMpMerchantOrderId(updatedFactura.getMpMerchantOrderId());
                existingFactura.setMpPreferenceId(updatedFactura.getMpPreferenceId());
                existingFactura.setMpPaymentType(updatedFactura.getMpPaymentType());
                existingFactura.setFormaPago(updatedFactura.getFormaPago());
                existingFactura.setTotalVenta(updatedFactura.getTotalVenta());
            } else {
                actual.setFactura(null); // Si la factura ya no viene, se elimina la relación
            }

            // Sincronizar la colección de Detalles del Pedido
            if (updatedPedido.getDetallesPedidos() != null) {
                actual.getDetallesPedidos().clear();
                for (DetallePedido detalle : updatedPedido.getDetallesPedidos()) {
                    detalle.setPedido(actual); // Asegura la relación inversa
                    actual.getDetallesPedidos().add(detalle);
                }
            } else {
                actual.getDetallesPedidos().clear();
            }

            return baseRepository.save(actual);
        } catch (Exception e) {
            throw new Exception("Error al actualizar el pedido: " + e.getMessage());
        }
    }

}