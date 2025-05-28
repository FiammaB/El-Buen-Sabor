package ElBuenSabor.ProyectoFinal.Service;

import ElBuenSabor.ProyectoFinal.DTO.*;
import ElBuenSabor.ProyectoFinal.Entities.*;
import ElBuenSabor.ProyectoFinal.Repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value; // Para leer de application.properties
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class PedidoServiceImpl extends BaseServiceImpl<Pedido, Long> implements PedidoService {

    private final PedidoRepository pedidoRepository;
    private final ClienteRepository clienteRepository;
    private final DomicilioRepository domicilioRepository;
    private final SucursalRepository sucursalRepository;
    private final ArticuloInsumoRepository articuloInsumoRepository;
    private final ArticuloManufacturadoRepository articuloManufacturadoRepository;
    // private final DetallePedidoRepository detallePedidoRepository; // No se usa directamente si Pedido maneja la cascada
    private final FacturaRepository facturaRepository;
    private final ArticuloService articuloService; // Para calcularMaximoProducible

    @Value("${app.config.cantidad-cocineros:1}") // Leer de application.properties, con default 1
    private int cantidadCocineros;

    @Autowired
    public PedidoServiceImpl(PedidoRepository pedidoRepository,
                             ClienteRepository clienteRepository,
                             DomicilioRepository domicilioRepository,
                             SucursalRepository sucursalRepository,
                             ArticuloInsumoRepository articuloInsumoRepository,
                             ArticuloManufacturadoRepository articuloManufacturadoRepository,
                             // DetallePedidoRepository detallePedidoRepository,
                             FacturaRepository facturaRepository,
                             ArticuloService articuloService ) {
        super(pedidoRepository);
        this.pedidoRepository = pedidoRepository;
        this.clienteRepository = clienteRepository;
        this.domicilioRepository = domicilioRepository;
        this.sucursalRepository = sucursalRepository;
        this.articuloInsumoRepository = articuloInsumoRepository;
        this.articuloManufacturadoRepository = articuloManufacturadoRepository;
        // this.detallePedidoRepository = detallePedidoRepository;
        this.facturaRepository = facturaRepository;
        this.articuloService = articuloService;
    }

    @Override
    @Transactional
    public PedidoResponseDTO crearPedido(PedidoCreateDTO dto, Usuario actorCliente) throws Exception {
        if (!(actorCliente instanceof Cliente)) {
            throw new Exception("El actor que crea el pedido debe ser un Cliente.");
        }
        Cliente cliente = (Cliente) actorCliente;

        if (cliente.isBaja()){ // HU#65
            throw new Exception("El cliente está dado de baja y no puede realizar pedidos.");
        }

        Sucursal sucursal = sucursalRepository.findById(dto.getSucursalId())
                .orElseThrow(() -> new Exception("Sucursal activa no encontrada con ID: " + dto.getSucursalId()));
        if(sucursal.isBaja()){
            throw new Exception("La sucursal seleccionada está dada de baja.");
        }

        // HU#35, HU#67: Verificar horario de atención de la sucursal
        if (!isSucursalAbierta(sucursal, LocalDate.now(), LocalTime.now())) {
            throw new Exception("El local se encuentra cerrado en este momento. No se pueden realizar pedidos.");
        }

        Domicilio domicilioEntregaValidado = null;
        if (dto.getTipoEnvio() == TipoEnvio.DELIVERY) {
            if (dto.getDomicilioEntregaId() == null) {
                throw new Exception("Se requiere domicilio de entrega para el tipo de envío DELIVERY.");
            }
            domicilioEntregaValidado = domicilioRepository.findById(dto.getDomicilioEntregaId())
                    .orElseThrow(() -> new Exception("Domicilio de entrega activo no encontrado con ID: " + dto.getDomicilioEntregaId()));
            if (domicilioEntregaValidado.isBaja()){
                throw new Exception("El domicilio de entrega seleccionado está dado de baja.");
            }
            // Validar que el domicilio pertenezca al cliente
            final Domicilio finalDomicilioEntrega = domicilioEntregaValidado;
            boolean domicilioValido = cliente.getDomicilios().stream()
                    .anyMatch(d -> !d.isBaja() && d.getId().equals(finalDomicilioEntrega.getId()));
            if (!domicilioValido) {
                throw new Exception("El domicilio de entrega no pertenece al cliente o está dado de baja.");
            }
        }

        // HU#8: Validar forma de pago según tipo de envío
        if (dto.getTipoEnvio() == TipoEnvio.DELIVERY && dto.getFormaPago() != FormaPago.MERCADO_PAGO) {
            throw new Exception("Para envío a domicilio, solo se acepta Mercado Pago.");
        }


        Pedido pedido = Pedido.builder()
                .fechaPedido(LocalDate.now())
                .estado(Estado.A_CONFIRMAR) // HU#188
                .tipoEnvio(dto.getTipoEnvio())
                .formaPago(dto.getFormaPago())
                .cliente(cliente)
                .sucursal(sucursal)
                .domicilioEntrega(domicilioEntregaValidado)
                // .baja(false) // Removido: 'baja' es false por defecto desde BaseEntity
                .build();

        double subtotalGeneral = 0.0;
        double costoTotalPedido = 0.0;
        int tiempoEstimadoManufacturadosPedidoActual = 0;
        Set<DetallePedido> detallesEntidad = new HashSet<>();

        for (DetallePedidoCreateDTO detalleDTO : dto.getDetallesPedidos()) {
            // Validaciones de Detalle
            if (detalleDTO.getCantidad() <= 0) throw new Exception("La cantidad en el detalle debe ser mayor a cero.");
            if (detalleDTO.getArticuloManufacturadoId() == null && detalleDTO.getArticuloInsumoId() == null)
                throw new Exception("Cada detalle debe especificar un artículo (manufacturado o insumo).");
            if (detalleDTO.getArticuloManufacturadoId() != null && detalleDTO.getArticuloInsumoId() != null)
                throw new Exception("Cada detalle solo puede ser de un artículo manufacturado O un insumo, no ambos.");

            Articulo articulo;
            Double precioVentaUnitario;
            boolean esManufacturado = detalleDTO.getArticuloManufacturadoId() != null;

            if (esManufacturado) {
                ArticuloManufacturado manufacturado = articuloManufacturadoRepository.findById(detalleDTO.getArticuloManufacturadoId())
                        .orElseThrow(() -> new Exception("Artículo Manufacturado activo no encontrado con ID: " + detalleDTO.getArticuloManufacturadoId()));
                if (manufacturado.isBaja()) throw new Exception("El artículo manufacturado '" + manufacturado.getDenominacion() + "' está dado de baja.");

                // HU#59, HU#60: Validar stock de ingredientes y máximo producible
                Integer maxProducible = articuloService.calcularMaximoProducible(manufacturado.getId());
                if (maxProducible < detalleDTO.getCantidad()) {
                    throw new Exception("Stock insuficiente para el artículo '" + manufacturado.getDenominacion() + "'. Máximo producible: " + maxProducible);
                }
                articulo = manufacturado;
                precioVentaUnitario = manufacturado.getPrecioVenta();
                tiempoEstimadoManufacturadosPedidoActual += (manufacturado.getTiempoEstimadoMinutos() != null ? manufacturado.getTiempoEstimadoMinutos() : 0) * detalleDTO.getCantidad();
                // Costo del manufacturado (suma de costo de sus insumos)
                if(manufacturado.getDetalles() != null){
                    for(ArticuloManufacturadoDetalle amd : manufacturado.getDetalles()){
                        if(amd.getArticuloInsumo() != null && amd.getArticuloInsumo().getPrecioCompra() != null && amd.getCantidad() != null){
                            costoTotalPedido += amd.getArticuloInsumo().getPrecioCompra() * amd.getCantidad() * detalleDTO.getCantidad();
                        }
                    }
                }
            } else { // Es ArticuloInsumo
                ArticuloInsumo insumo = articuloInsumoRepository.findById(detalleDTO.getArticuloInsumoId())
                        .orElseThrow(() -> new Exception("Artículo Insumo activo no encontrado con ID: " + detalleDTO.getArticuloInsumoId()));
                if (insumo.isBaja()) throw new Exception("El artículo insumo '" + insumo.getDenominacion() + "' está dado de baja.");

                // HU#59: Validar stock del insumo
                if (insumo.getStockActual() < detalleDTO.getCantidad()) {
                    throw new Exception("Stock insuficiente para el insumo: " + insumo.getDenominacion());
                }
                articulo = insumo;
                precioVentaUnitario = insumo.getPrecioVenta();
                if(insumo.getPrecioCompra() != null) {
                    costoTotalPedido += insumo.getPrecioCompra() * detalleDTO.getCantidad();
                }
            }

            DetallePedido detallePedido = DetallePedido.builder()
                    .cantidad(detalleDTO.getCantidad())
                    .subTotal(precioVentaUnitario * detalleDTO.getCantidad())
                    .pedido(pedido) // Importante para la relación bidireccional
                    .build();
            if (esManufacturado) detallePedido.setArticuloManufacturado((ArticuloManufacturado) articulo);
            else detallePedido.setArticuloInsumo((ArticuloInsumo) articulo);

            detallesEntidad.add(detallePedido);
            subtotalGeneral += detallePedido.getSubTotal();
        }

        pedido.setDetallesPedidos(detallesEntidad);
        pedido.setTotalCosto(costoTotalPedido);

        // HU#7: Aplicar descuento si es retiro en local
        double montoDescuento = 0.0;
        if (dto.getTipoEnvio() == TipoEnvio.RETIRO_EN_LOCAL) {
            montoDescuento = subtotalGeneral * 0.10;
        }
        pedido.setMontoDescuento(montoDescuento);
        pedido.setTotal(subtotalGeneral - montoDescuento);

        // HU#10, HU#193: Calcular hora estimada de finalización
        pedido.setHoraEstimadaFinalizacion(calcularHoraEstimada(pedido, tiempoEstimadoManufacturadosPedidoActual));

        // Guardado: Pedido y sus Detalles (por cascada)
        Pedido savedPedido = pedidoRepository.save(pedido);

        // El decremento de stock (HU#187) se hará cuando el Cajero apruebe el pedido y lo envíe a cocina.
        // No aquí, porque el pedido está solo 'A_CONFIRMAR'.

        return convertToDTO(savedPedido);
    }

    // Helper para calcular hora estimada (HU#10, HU#193)
    private LocalTime calcularHoraEstimada(Pedido pedidoActual, int tiempoItemsPedidoActual) throws Exception {
        int tiempoCocinaOtrosPedidos = 0;
        // ∑ Sumatoria del tiempo estimado de los artículos manufacturados que se encuentran en la cocina
        List<Pedido> pedidosEnCocina = pedidoRepository.findPedidosEnCocinaPorSucursal(pedidoActual.getSucursal().getId(), Estado.EN_COCINA);
        for (Pedido pEnCocina : pedidosEnCocina) {
            if (pEnCocina.getDetallesPedidos() != null) {
                for (DetallePedido dp : pEnCocina.getDetallesPedidos()) {
                    if (dp.getArticuloManufacturado() != null && dp.getArticuloManufacturado().getTiempoEstimadoMinutos() != null) {
                        tiempoCocinaOtrosPedidos += dp.getArticuloManufacturado().getTiempoEstimadoMinutos() * dp.getCantidad();
                    }
                }
            }
        }

        int tiempoCocineros = (cantidadCocineros > 0) ? (tiempoCocinaOtrosPedidos / cantidadCocineros) : tiempoCocinaOtrosPedidos; // Evitar división por cero

        int tiempoTotalMinutos = tiempoItemsPedidoActual + tiempoCocineros;
        if (pedidoActual.getTipoEnvio() == TipoEnvio.DELIVERY) {
            tiempoTotalMinutos += 10; // 10 Minutos de entrega por delivery
        }

        // La HU#193 dice "Del tiempo estimado de cada uno de los artículos pedidos por el cliente se elige el mayor + De los pedidos que se encuentran en cocina, el artículo con el mayor tiempo estimado + 10 minutos..."
        // La fórmula que implementé arriba es "Sumatoria del tiempo estimado de los artículos manufacturados solicitados... + Sumatoria del tiempo de los que están en cocina / cocineros + 10 min..." (HU#10)
        // Aclarar cuál fórmula usar. La HU#10 parece más completa. Por ahora, mantengo la implementación de HU#10.
        // Si es la de HU#193:
        // int mayorTiempoItemPedidoActual = 0;
        // for (DetallePedido dp : pedidoActual.getDetallesPedidos()){... if (dp.getArticuloManufacturado().getTiempoEstimadoMinutos() > mayorTiempoItemPedidoActual) ...}
        // int mayorTiempoItemEnCocina = 0;
        // for (Pedido pEnCocina : pedidosEnCocina) { ... for (DetallePedido dp : pEnCocina.getDetallesPedidos()) ... if (dp.getArticuloManufacturado().getTiempoEstimadoMinutos() > mayorTiempoItemEnCocina) ...}
        // tiempoTotalMinutos = mayorTiempoItemPedidoActual + mayorTiempoItemEnCocina;
        // if (pedidoActual.getTipoEnvio() == TipoEnvio.DELIVERY) tiempoTotalMinutos += 10;

        return LocalTime.now().plusMinutes(tiempoTotalMinutos);
    }

    // Helper para verificar si la sucursal está abierta (HU#35, HU#66, HU#67)
    private boolean isSucursalAbierta(Sucursal sucursal, LocalDate fecha, LocalTime hora) {
        // Horario atención: L-D 20:00 a 00:00 (del día siguiente), S-D 11:00 a 15:00.
        // Esto necesita una lógica cuidadosa para los horarios que cruzan la medianoche.
        // La consigna dice "20:00 a 12:00", lo cual es extraño. Asumiré 20:00 a 00:00 (medianoche)
        // y que el horario de 11:00 a 15:00 es S-D. La consigna HU#66 dice L-D 20:00 a 00:00.

        // Horario noche: 20:00 a 00:00 (todos los días)
        LocalTime inicioNoche = LocalTime.of(20, 0);
        LocalTime finNoche = LocalTime.MAX; // Hasta 23:59:59.999...
        // Podríamos considerar 00:00 como fin, y si la hora actual es 00:00, está abierto si el día anterior abrió a las 20:00
        // Es más simple si el horario no cruza medianoche en la definición.
        // Si es "20:00 a 00:00" se interpreta como hasta fin del día. Si fuera "20:00 a 02:00 del día siguiente" es más complejo.
        // Las consignas tienen horarios contradictorios. HU#2 dice "20:00 a 12:00" (mediodía siguiente?), HU#66 "20:00 a 00:00".
        // Usaré 20:00-00:00 (hasta fin del día) y S-D 11:00-15:00.

        boolean abiertoNoche = !hora.isBefore(inicioNoche); // De 20:00 en adelante

        boolean abiertoDiaFinDeSemana = false;
        DayOfWeek diaSemana = fecha.getDayOfWeek();
        if (diaSemana == DayOfWeek.SATURDAY || diaSemana == DayOfWeek.SUNDAY) {
            LocalTime inicioDiaFS = LocalTime.of(11, 0);
            LocalTime finDiaFS = LocalTime.of(15, 0);
            abiertoDiaFinDeSemana = !hora.isBefore(inicioDiaFS) && hora.isBefore(finDiaFS);
        }

        // Logica para el horario de sucursal si se define en la entidad Sucursal
        // if (sucursal.getHorarioApertura() != null && sucursal.getHorarioCierre() != null) {
        //     LocalTime apertura = sucursal.getHorarioApertura();
        //     LocalTime cierre = sucursal.getHorarioCierre();
        //     if (cierre.isBefore(apertura)) { // Horario cruza medianoche
        //         return !hora.isBefore(apertura) || hora.isBefore(cierre);
        //     } else {
        //         return !hora.isBefore(apertura) && hora.isBefore(cierre);
        //     }
        // }
        // System.err.println("ADVERTENCIA: Usando horarios fijos de consignas. Implementar con Sucursal.horarioApertura/Cierre");
        return abiertoNoche || abiertoDiaFinDeSemana;
    }


    @Override
    @Transactional
    public PedidoResponseDTO cambiarEstadoPedido(Long pedidoId, Estado nuevoEstado, Usuario actorEmpleado) throws Exception {
        if (!(actorEmpleado instanceof Empleado)) {
            throw new Exception("Solo un Empleado puede cambiar el estado de un pedido.");
        }
        Empleado empleado = (Empleado) actorEmpleado;
        Rol rolEmpleado = empleado.getRol();

        Pedido pedido = pedidoRepository.findByIdRaw(pedidoId) // Buscar incluso si está 'baja' para cambiar estado
                .orElseThrow(() -> new Exception("Pedido no encontrado con ID: " + pedidoId));

        if (pedido.isBaja() && !(nuevoEstado == Estado.CANCELADO || nuevoEstado == Estado.RECHAZADO || nuevoEstado == Estado.FACTURADO)) {
            // Si el pedido fue anulado (baja=true), solo permitir cambiar a estados finales si es necesario o no permitir cambios.
            throw new Exception("No se puede cambiar el estado de un pedido anulado, excepto para ciertos flujos de corrección.");
        }

        Estado estadoActual = pedido.getEstado();
        // Lógica de transición de estados y permisos por ROL (HU#11, HU#12, HU#13, HU#71, HU#203-HU#209, HU#211, HU#217)
        switch (estadoActual) {
            case A_CONFIRMAR:
                if (rolEmpleado == Rol.CAJERO) {
                    if (nuevoEstado == Estado.EN_COCINA || nuevoEstado == Estado.LISTO) {
                        // HU#187, HU#22: Descontar stock al pasar a EN_COCINA o LISTO (si no requiere cocina)
                        descontarStockPorPedido(pedido);
                        pedido.setEstado(nuevoEstado);
                    } else if (nuevoEstado == Estado.CANCELADO || nuevoEstado == Estado.RECHAZADO){
                        pedido.setEstado(nuevoEstado); // No se repone stock porque no se descontó
                    }
                    else {
                        throw new Exception("Transición de estado inválida desde A_CONFIRMAR para CAJERO.");
                    }
                } else {
                    throw new Exception("Solo un CAJERO puede cambiar el estado de A_CONFIRMAR.");
                }
                break;
            case EN_COCINA:
                if (rolEmpleado == Rol.COCINERO) { // HU#217
                    if (nuevoEstado == Estado.LISTO) {
                        pedido.setEstado(nuevoEstado);
                    } else {
                        throw new Exception("Transición de estado inválida desde EN_COCINA para COCINERO.");
                    }
                } else if (rolEmpleado == Rol.CAJERO && (nuevoEstado == Estado.CANCELADO || nuevoEstado == Estado.RECHAZADO)) {
                    // Cajero puede cancelar un pedido que ya estaba en cocina (implica reponer stock)
                    reponerStockPorPedido(pedido);
                    pedido.setEstado(nuevoEstado);
                }
                else {
                    throw new Exception("Solo un COCINERO puede marcar como LISTO, o CAJERO cancelar.");
                }
                break;
            case LISTO:
                if (rolEmpleado == Rol.CAJERO) {
                    if (nuevoEstado == Estado.EN_DELIVERY && pedido.getTipoEnvio() == TipoEnvio.DELIVERY) {
                        pedido.setEstado(nuevoEstado);
                    } else if (nuevoEstado == Estado.FACTURADO && pedido.getTipoEnvio() == TipoEnvio.RETIRO_EN_LOCAL) { // HU#12, HU#204
                        // HU#205, HU#208: "no podrá asignar el estado de “Entregado” a un pedido si el cliente no lo ha pagado"
                        // Aquí se asume que el pago ya ocurrió o se maneja externamente antes de este cambio.
                        // La generación de factura ocurre aquí.
                        generarFacturaParaPedido(pedido);
                        pedido.setEstado(Estado.FACTURADO);
                    } else if (nuevoEstado == Estado.CANCELADO || nuevoEstado == Estado.RECHAZADO) {
                        reponerStockPorPedido(pedido);
                        pedido.setEstado(nuevoEstado);
                    }
                    else {
                        throw new Exception("Transición de estado inválida desde LISTO para CAJERO.");
                    }
                } else {
                    throw new Exception("Solo un CAJERO puede gestionar un pedido LISTO.");
                }
                break;
            case EN_DELIVERY: // HU#13, HU#211
                // El rol 'DELIVERY' podría ser un rol específico de empleado.
                if (rolEmpleado == Rol.DELIVERY || rolEmpleado == Rol.CAJERO) { // Asumiendo que cajero también puede confirmar
                    if (nuevoEstado == Estado.FACTURADO) { // Entrega exitosa
                        // Si el pago era contra-entrega y es Mercado Pago (ya debería estar pagado antes de EN_DELIVERY)
                        // O si el cajero confirma que el delivery reportó pago.
                        // La generación de factura ya debería haber ocurrido si el pago fue online antes de enviar.
                        // Si el pago MP ocurre y se confirma via webhook, el estado podría cambiar a FACTURADO.
                        // Si el pago es EFECTIVO (no permitido para delivery según HU#8), esta lógica no aplica.
                        // Por ahora, asumimos que el pago ya está resuelto para llegar a FACTURADO.
                        if (pedido.getFactura() == null && (pedido.getFormaPago() == FormaPago.MERCADO_PAGO /*&& pagoMPConfirmado*/ )) {
                            generarFacturaParaPedido(pedido);
                        }
                        pedido.setEstado(Estado.FACTURADO);
                    } else if (nuevoEstado == Estado.RECHAZADO) { // Ej. cliente no encontrado, rechaza pedido
                        reponerStockPorPedido(pedido);
                        pedido.setEstado(nuevoEstado);
                    }
                    else {
                        throw new Exception("Transición de estado inválida desde EN_DELIVERY.");
                    }
                } else {
                    throw new Exception("Solo personal de DELIVERY o CAJERO puede gestionar un pedido EN_DELIVERY.");
                }
                break;
            case ENTREGADO: // Este estado podría ser intermedio antes de FACTURADO si el pago no es inmediato
            case CANCELADO:
            case RECHAZADO:
            case FACTURADO:
                throw new Exception("El pedido ya está en un estado final (" + estadoActual + ") y no se puede modificar.");
            default:
                throw new Exception("Estado actual del pedido desconocido o transición no manejada.");
        }

        return convertToDTO(pedidoRepository.save(pedido));
    }

    private void generarFacturaParaPedido(Pedido pedido) throws Exception {
        if (pedido.getFactura() != null) {
            // Ya tiene factura, no generar otra o manejar según reglas de negocio (ej. re-emitir si se anuló antes)
            System.out.println("El pedido ID: " + pedido.getId() + " ya tiene una factura asociada.");
            return;
        }
        // HU#18, HU#75, HU#191
        Factura factura = Factura.builder()
                .fechaFacturacion(LocalDate.now())
                .formaPago(pedido.getFormaPago())
                .totalVenta(pedido.getTotal())
                // Campos de Mercado Pago se llenarían si el pago es por MP y exitoso (HU#190)
                // .mpPaymentId(pedido.getMpPaymentId()) // Si Pedido tuviera estos campos temporalmente
                .build();
        Factura savedFactura = facturaRepository.save(factura);
        pedido.setFactura(savedFactura);
        // Aquí iría la lógica de enviar factura por email (HU#14, HU#75, HU#191, HU#220)
        // emailService.enviarFactura(pedido.getCliente(), savedFactura, pedido);
    }

    private void descontarStockPorPedido(Pedido pedido) throws Exception { // HU#187
        for (DetallePedido detalle : pedido.getDetallesPedidos()) {
            if (detalle.getArticuloManufacturado() != null) {
                ArticuloManufacturado manufacturado = detalle.getArticuloManufacturado();
                for (ArticuloManufacturadoDetalle amd : manufacturado.getDetalles()) {
                    ArticuloInsumo insumoComponente = amd.getArticuloInsumo();
                    double cantidadRequerida = amd.getCantidad() * detalle.getCantidad();
                    if (insumoComponente.getStockActual() < cantidadRequerida) {
                        // Esto no debería pasar si la validación en crearPedido fue correcta,
                        // pero es una salvaguarda.
                        throw new Exception("Stock insuficiente para el insumo: " + insumoComponente.getDenominacion() +
                                " (requerido: " + cantidadRequerida + ", disponible: " + insumoComponente.getStockActual() +
                                ") al intentar descontar stock para el pedido " + pedido.getId());
                    }
                    insumoComponente.setStockActual(insumoComponente.getStockActual() - cantidadRequerida);
                    articuloInsumoRepository.save(insumoComponente);
                }
            } else if (detalle.getArticuloInsumo() != null) {
                ArticuloInsumo insumoVendido = detalle.getArticuloInsumo();
                if (insumoVendido.getStockActual() < detalle.getCantidad()) {
                    throw new Exception("Stock insuficiente para el insumo: " + insumoVendido.getDenominacion() +
                            " (requerido: " + detalle.getCantidad() + ", disponible: " + insumoVendido.getStockActual() +
                            ") al intentar descontar stock para el pedido " + pedido.getId());
                }
                insumoVendido.setStockActual(insumoVendido.getStockActual() - detalle.getCantidad());
                articuloInsumoRepository.save(insumoVendido);
            }
        }
    }

    @Override
    @Transactional
    public void reponerStockPorPedido(Pedido pedido) throws Exception { // HU#222 (anulación de factura)
        if (pedido.getDetallesPedidos() == null) return;
        for (DetallePedido detalle : pedido.getDetallesPedidos()) {
            if (detalle.getArticuloManufacturado() != null) {
                ArticuloManufacturado manufacturado = detalle.getArticuloManufacturado();
                if (manufacturado.getDetalles() == null) continue;
                for (ArticuloManufacturadoDetalle amd : manufacturado.getDetalles()) {
                    ArticuloInsumo insumoComponente = amd.getArticuloInsumo();
                    if (insumoComponente == null) continue;
                    double cantidadAReponer = amd.getCantidad() * detalle.getCantidad();
                    insumoComponente.setStockActual(insumoComponente.getStockActual() + cantidadAReponer);
                    articuloInsumoRepository.save(insumoComponente);
                }
            } else if (detalle.getArticuloInsumo() != null) {
                ArticuloInsumo insumoVendido = detalle.getArticuloInsumo();
                insumoVendido.setStockActual(insumoVendido.getStockActual() + detalle.getCantidad());
                articuloInsumoRepository.save(insumoVendido);
            }
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<PedidoResponseDTO> findPedidosByClienteId(Long clienteId) throws Exception {
        // pedidoRepository.findByClienteId ya usa @Where de Pedido para traer solo no anulados
        return pedidoRepository.findByClienteId(clienteId).stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PedidoResponseDTO> findPedidosByEstado(Estado estado, boolean soloActivos) throws Exception {
        List<Pedido> pedidos;
        if (soloActivos) {
            pedidos = pedidoRepository.findByEstado(estado); // @Where en Pedido filtra por baja=false
        } else {
            pedidos = pedidoRepository.findByEstadoRaw(estado); // Necesita este método en repo
        }
        return pedidos.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PedidoResponseDTO> findPedidosBySucursalAndEstado(Long sucursalId, Estado estado, boolean soloActivos) throws Exception {
        List<Pedido> pedidos;
        // Asumimos que findBySucursalIdAndEstado ya filtra por baja=false si soloActivos es true (debido a @Where en Pedido)
        // Si soloActivos es false, necesitamos un método Raw.
        if (soloActivos) {
            pedidos = pedidoRepository.findBySucursalIdAndEstado(sucursalId, estado);
        } else {
            // Necesitarías en PedidoRepository:
            // @Query("SELECT p FROM Pedido p WHERE p.sucursal.id = :sucursalId AND p.estado = :estado")
            // List<Pedido> findBySucursalIdAndEstadoRaw(@Param("sucursalId") Long sucursalId, @Param("estado") Estado estado);
            // pedidos = pedidoRepository.findBySucursalIdAndEstadoRaw(sucursalId, estado);
            System.err.println("ADVERTENCIA: findPedidosBySucursalAndEstado con soloActivos=false necesita método Raw en repositorio.");
            pedidos = pedidoRepository.findBySucursalIdAndEstado(sucursalId, estado); // Temporalmente devuelve activos
        }
        return pedidos.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public PedidoResponseDTO findPedidoByIdDTO(Long id) throws Exception {
        return convertToDTO(super.findById(id).orElse(null)); // Solo activos
    }

    @Override
    @Transactional(readOnly = true)
    public List<PedidoResponseDTO> findAllPedidosDTO(boolean soloActivos) throws Exception {
        List<Pedido> pedidos;
        if(soloActivos){
            pedidos = super.findAll(); // Solo activos
        } else {
            pedidos = pedidoRepository.findAllRaw();
        }
        return pedidos.stream().map(this::convertToDTO).collect(Collectors.toList());
    }


    // --- Implementación de métodos de BaseService para borrado lógico ---
    @Override
    @Transactional(readOnly = true)
    public Optional<Pedido> findByIdIncludingDeleted(Long id) throws Exception {
        return pedidoRepository.findByIdRaw(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Pedido> findAllIncludingDeleted() throws Exception {
        return pedidoRepository.findAllRaw();
    }

    @Override
    @Transactional
    public Pedido softDelete(Long id) throws Exception { // Anular pedido
        Pedido pedido = this.findByIdIncludingDeleted(id)
                .orElseThrow(() -> new Exception("Pedido no encontrado con ID: " + id + " para anular."));
        if (pedido.isBaja()) {
            throw new Exception("El pedido ya está anulado.");
        }
        // Según consignas, la anulación de factura (asociada al pedido) implica reponer stock.
        // Si el pedido se anula antes de facturar, también debería reponer stock si ya se descontó.
        if (pedido.getEstado() != Estado.A_CONFIRMAR && pedido.getEstado() != Estado.CANCELADO && pedido.getEstado() != Estado.RECHAZADO) {
            // Si el stock ya se descontó (ej. estaba EN_COCINA, LISTO, EN_DELIVERY)
            reponerStockPorPedido(pedido);
        }
        pedido.setBaja(true);
        pedido.setEstado(Estado.CANCELADO); // O un estado ANULADO si lo tienes

        // Si tiene factura y no está anulada, anularla también
        if (pedido.getFactura() != null && !pedido.getFactura().isBaja()) {
            Factura factura = pedido.getFactura();
            factura.setBaja(true); // Anular factura
            facturaRepository.save(factura);
            // Aquí no se genera nota de crédito automáticamente, solo se anula la factura.
            // La HU#19 implica un proceso más formal para la NC.
        }
        return pedidoRepository.save(pedido);
    }

    @Override
    @Transactional
    public Pedido reactivate(Long id) throws Exception {
        Pedido pedido = this.findByIdIncludingDeleted(id)
                .orElseThrow(() -> new Exception("Pedido no encontrado con ID: " + id + " para reactivar."));
        if (!pedido.isBaja()) {
            throw new Exception("El pedido no está anulado/dado de baja, no se puede reactivar.");
        }
        // Lógica de reactivación: ¿A qué estado vuelve? ¿Se vuelve a descontar stock?
        // Esto es complejo y depende de las reglas de negocio.
        // Por ahora, solo se quita la marca 'baja'. El estado podría necesitar ajuste manual.
        pedido.setBaja(false);
        // Considerar NO re-descontar stock automáticamente aquí. El estado debería pasar a A_CONFIRMAR.
        pedido.setEstado(Estado.A_CONFIRMAR); // Volver a un estado inicial
        return pedidoRepository.save(pedido);
    }

    // --- Implementación de métodos de PedidoService que devuelven DTO ---
    @Override
    @Transactional(readOnly = true)
    public List<PedidoResponseDTO> findAllPedidosIncludingAnulados() throws Exception {
        return this.findAllIncludingDeleted().stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public PedidoResponseDTO findPedidoByIdIncludingAnulado(Long id) throws Exception {
        return convertToDTO(this.findByIdIncludingDeleted(id).orElse(null));
    }

    // --- Helper de Conversión DTO ---
    // (El método convertToDTO que ya tenías, asegurándose que mapee todos los campos nuevos
    // y los DTOs anidados como ClienteSimpleResponseDTO, SucursalSimpleDTO, etc.)
    private PedidoResponseDTO convertToDTO(Pedido pedido) {
        if (pedido == null) return null;
        PedidoResponseDTO dto = new PedidoResponseDTO();
        dto.setId(pedido.getId());
        dto.setFechaPedido(pedido.getFechaPedido());
        dto.setHoraEstimadaFinalizacion(pedido.getHoraEstimadaFinalizacion());
        dto.setTotal(pedido.getTotal());
        dto.setTotalCosto(pedido.getTotalCosto());
        dto.setMontoDescuento(pedido.getMontoDescuento());
        dto.setEstado(pedido.getEstado());
        dto.setTipoEnvio(pedido.getTipoEnvio());
        dto.setFormaPago(pedido.getFormaPago());
        dto.setBaja(pedido.isBaja());

        if (pedido.getCliente() != null) {
            ClienteSimpleResponseDTO clienteDTO = new ClienteSimpleResponseDTO();
            clienteDTO.setId(pedido.getCliente().getId());
            clienteDTO.setNombre(pedido.getCliente().getNombre());
            clienteDTO.setApellido(pedido.getCliente().getApellido());
            clienteDTO.setEmail(pedido.getCliente().getEmail());
            clienteDTO.setTelefono(pedido.getCliente().getTelefono());
            clienteDTO.setBaja(pedido.getCliente().isBaja());
            dto.setCliente(clienteDTO);
        }

        if (pedido.getDomicilioEntrega() != null) {
            dto.setDomicilioEntrega(convertToDomicilioDTO(pedido.getDomicilioEntrega()));
        }

        if (pedido.getSucursal() != null) {
            SucursalSimpleDTO sucursalDTO = new SucursalSimpleDTO();
            sucursalDTO.setId(pedido.getSucursal().getId());
            sucursalDTO.setNombre(pedido.getSucursal().getNombre());
            sucursalDTO.setBaja(pedido.getSucursal().isBaja());
            dto.setSucursal(sucursalDTO);
        }

        if (pedido.getFactura() != null) {
            dto.setFactura(convertToFacturaDTO(pedido.getFactura()));
        }

        if (pedido.getDetallesPedidos() != null) {
            dto.setDetallesPedidos(pedido.getDetallesPedidos().stream()
                    .map(this::convertToDetallePedidoDTO).collect(Collectors.toSet()));
        }
        return dto;
    }

    private DetallePedidoDTO convertToDetallePedidoDTO(DetallePedido detalle) {
        if (detalle == null) return null;
        DetallePedidoDTO dto = new DetallePedidoDTO();
        dto.setId(detalle.getId());
        dto.setCantidad(detalle.getCantidad());
        dto.setSubTotal(detalle.getSubTotal());
        dto.setBaja(detalle.isBaja()); // El detalle hereda 'baja' de BaseEntity

        if (detalle.getArticuloManufacturado() != null) {
            dto.setArticuloManufacturado(convertToArticuloManufacturadoSimpleDTO(detalle.getArticuloManufacturado()));
        }
        if (detalle.getArticuloInsumo() != null) {
            dto.setArticuloInsumo(convertToArticuloInsumoSimpleDTO(detalle.getArticuloInsumo()));
        }
        return dto;
    }

    private DomicilioDTO convertToDomicilioDTO(Domicilio domicilio) {
        if (domicilio == null) return null;
        DomicilioDTO dto = new DomicilioDTO();
        dto.setId(domicilio.getId());
        dto.setCalle(domicilio.getCalle());
        dto.setNumero(domicilio.getNumero());
        dto.setCp(domicilio.getCp());
        dto.setBaja(domicilio.isBaja());
        if (domicilio.getLocalidad() != null) {
            // Convertir Localidad a LocalidadDTO (posiblemente simple)
            LocalidadDTO locDto = new LocalidadDTO();
            locDto.setId(domicilio.getLocalidad().getId());
            locDto.setNombre(domicilio.getLocalidad().getNombre());
            locDto.setBaja(domicilio.getLocalidad().isBaja());
            // Añadir Provincia y País si es necesario en LocalidadDTO
            dto.setLocalidad(locDto);
        }
        return dto;
    }

    private FacturaDTO convertToFacturaDTO(Factura factura) {
        if (factura == null) return null;
        FacturaDTO dto = new FacturaDTO();
        dto.setId(factura.getId());
        dto.setFechaFacturacion(factura.getFechaFacturacion());
        dto.setTotalVenta(factura.getTotalVenta());
        dto.setFormaPago(factura.getFormaPago());
        dto.setBaja(factura.isBaja());
        dto.setMpPaymentId(factura.getMpPaymentId());
        // ... otros campos de MP
        // Para pedidoId, necesitaríamos una forma de obtener el pedido al que pertenece esta factura
        // Pedido p = pedidoRepository.findByFacturaIdRaw(factura.getId()).orElse(null);
        // if (p!=null) dto.setPedidoId(p.getId());
        return dto;
    }

    private ArticuloManufacturadoSimpleDTO convertToArticuloManufacturadoSimpleDTO(ArticuloManufacturado am){
        if(am == null) return null;
        ArticuloManufacturadoSimpleDTO dto = new ArticuloManufacturadoSimpleDTO();
        dto.setId(am.getId());
        dto.setDenominacion(am.getDenominacion());
        dto.setPrecioVenta(am.getPrecioVenta());
        dto.setBaja(am.isBaja());
        return dto;
    }

    private ArticuloInsumoSimpleDTO convertToArticuloInsumoSimpleDTO(ArticuloInsumo ai){
        if(ai == null) return null;
        ArticuloInsumoSimpleDTO dto = new ArticuloInsumoSimpleDTO();
        dto.setId(ai.getId());
        dto.setDenominacion(ai.getDenominacion());
        dto.setPrecioVenta(ai.getPrecioVenta());
        dto.setBaja(ai.isBaja());
        if (ai.getUnidadMedida() != null) {
            UnidadMedidaDTO umDto = new UnidadMedidaDTO();
            umDto.setId(ai.getUnidadMedida().getId());
            umDto.setDenominacion(ai.getUnidadMedida().getDenominacion());
            umDto.setBaja(ai.getUnidadMedida().isBaja());
            dto.setUnidadMedida(umDto);
        }
        return dto;
    }
}
