package ElBuenSabor.ProyectoFinal.Service;

import ElBuenSabor.ProyectoFinal.DTO.*;
import ElBuenSabor.ProyectoFinal.Entities.*;
import ElBuenSabor.ProyectoFinal.Repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.List;
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
    private final DetallePedidoRepository detallePedidoRepository;
    private final FacturaRepository facturaRepository;

    @Autowired
    public PedidoServiceImpl(PedidoRepository pedidoRepository,
                             ClienteRepository clienteRepository,
                             DomicilioRepository domicilioRepository,
                             SucursalRepository sucursalRepository,
                             ArticuloInsumoRepository articuloInsumoRepository,
                             ArticuloManufacturadoRepository articuloManufacturadoRepository,
                             DetallePedidoRepository detallePedidoRepository,
                             FacturaRepository facturaRepository) {
        super(pedidoRepository);
        this.pedidoRepository = pedidoRepository;
        this.clienteRepository = clienteRepository;
        this.domicilioRepository = domicilioRepository;
        this.sucursalRepository = sucursalRepository;
        this.articuloInsumoRepository = articuloInsumoRepository;
        this.articuloManufacturadoRepository = articuloManufacturadoRepository;
        this.detallePedidoRepository = detallePedidoRepository;
        this.facturaRepository = facturaRepository;
    }

    @Override
    @Transactional
    public PedidoResponseDTO crearPedido(PedidoCreateDTO pedidoCreateDTO) throws Exception {
        try {
            Cliente cliente = clienteRepository.findById(pedidoCreateDTO.getClienteId())
                    .orElseThrow(() -> new Exception("Cliente no encontrado con ID: " + pedidoCreateDTO.getClienteId()));

            if (cliente.isEstaDadoDeBaja()){
                throw new Exception("El cliente está dado de baja y no puede realizar pedidos.");
            }

            Sucursal sucursal = sucursalRepository.findById(pedidoCreateDTO.getSucursalId())
                    .orElseThrow(() -> new Exception("Sucursal no encontrada con ID: " + pedidoCreateDTO.getSucursalId()));

            Domicilio domicilioParaElPedidoBuilder = null; // Variable que se usará en Pedido.builder()

            if (pedidoCreateDTO.getTipoEnvio() == TipoEnvio.DELIVERY) {
                if (pedidoCreateDTO.getDomicilioEntregaId() == null) {
                    throw new Exception("Se requiere domicilio de entrega para el tipo de envío DELIVERY.");
                }
                // Esta variable es local al bloque if y es final o efectivamente final para la lambda
                final Domicilio domicilioValidado = domicilioRepository.findById(pedidoCreateDTO.getDomicilioEntregaId())
                        .orElseThrow(() -> new Exception("Domicilio de entrega no encontrado con ID: " + pedidoCreateDTO.getDomicilioEntregaId()));

                // Validar que el domicilio pertenezca al cliente
                boolean domicilioValido = cliente.getDomicilios().stream()
                        .anyMatch(d -> d.getId().equals(domicilioValidado.getId())); // La lambda usa 'domicilioValidado'
                if (!domicilioValido) {
                    throw new Exception("El domicilio de entrega no pertenece al cliente.");
                }
                domicilioParaElPedidoBuilder = domicilioValidado; // Asignar a la variable que usará el builder
            }

            Pedido pedido = Pedido.builder()
                    .fechaPedido(LocalDate.now())
                    .estado(Estado.A_CONFIRMAR) // Estado inicial
                    .tipoEnvio(pedidoCreateDTO.getTipoEnvio())
                    .formaPago(pedidoCreateDTO.getFormaPago())
                    .cliente(cliente)
                    .sucursal(sucursal)
                    .domicilioEntrega(domicilioParaElPedidoBuilder) // Usar la variable correcta
                    .detallesPedidos(new HashSet<>())
                    .build();

            double totalPedido = 0.0;
            double totalCostoPedido = 0.0;
            int tiempoEstimadoTotal = 0;

            Set<DetallePedido> detalles = new HashSet<>();
            for (DetallePedidoCreateDTO detalleDTO : pedidoCreateDTO.getDetallesPedidos()) {
                if (detalleDTO.getCantidad() <= 0) {
                    throw new Exception("La cantidad en el detalle del pedido debe ser mayor a cero.");
                }
                Articulo articulo; // No es necesario inicializar a null si siempre se asigna en los if/else if
                Double precioVentaUnitario;
                // Double precioCostoUnitario = 0.0; // Implementar si se calcula el costo

                if (detalleDTO.getArticuloManufacturadoId() != null) {
                    ArticuloManufacturado manufacturado = articuloManufacturadoRepository.findById(detalleDTO.getArticuloManufacturadoId())
                            .orElseThrow(() -> new Exception("Artículo Manufacturado no encontrado con ID: " + detalleDTO.getArticuloManufacturadoId()));
                    articulo = manufacturado;
                    precioVentaUnitario = manufacturado.getPrecioVenta();
                    tiempoEstimadoTotal += manufacturado.getTiempoEstimadoMinutos() != null ? manufacturado.getTiempoEstimadoMinutos() : 0;

                    for(ArticuloManufacturadoDetalle amd : manufacturado.getDetalles()){
                        ArticuloInsumo insumoComponente = amd.getArticuloInsumo();
                        double cantidadRequerida = amd.getCantidad() * detalleDTO.getCantidad();
                        if(insumoComponente.getStockActual() < cantidadRequerida){
                            throw new Exception("Stock insuficiente para el insumo: " + insumoComponente.getDenominacion() + " requerido para " + manufacturado.getDenominacion());
                        }
                        insumoComponente.setStockActual(insumoComponente.getStockActual() - cantidadRequerida);
                        articuloInsumoRepository.save(insumoComponente);
                    }

                } else if (detalleDTO.getArticuloInsumoId() != null) {
                    ArticuloInsumo insumo = articuloInsumoRepository.findById(detalleDTO.getArticuloInsumoId())
                            .orElseThrow(() -> new Exception("Artículo Insumo no encontrado con ID: " + detalleDTO.getArticuloInsumoId()));
                    articulo = insumo;
                    precioVentaUnitario = insumo.getPrecioVenta();

                    if (insumo.getStockActual() < detalleDTO.getCantidad()) {
                        throw new Exception("Stock insuficiente para el insumo: " + insumo.getDenominacion());
                    }
                    insumo.setStockActual(insumo.getStockActual() - detalleDTO.getCantidad());
                    articuloInsumoRepository.save(insumo);
                } else {
                    throw new Exception("Cada detalle del pedido debe especificar un artículo manufacturado o un insumo.");
                }

                DetallePedido detallePedido = DetallePedido.builder()
                        .cantidad(detalleDTO.getCantidad())
                        .subTotal(precioVentaUnitario * detalleDTO.getCantidad())
                        .pedido(pedido) // Establecer la relación bidireccional
                        .build();

                if(articulo instanceof ArticuloManufacturado) {
                    detallePedido.setArticuloManufacturado((ArticuloManufacturado) articulo);
                } else if (articulo instanceof ArticuloInsumo) {
                    detallePedido.setArticuloInsumo((ArticuloInsumo) articulo);
                }

                detalles.add(detallePedido); // Añadir a la colección de detalles del pedido
                totalPedido += detallePedido.getSubTotal();
            }

            // Es importante guardar el pedido ANTES de intentar guardar los detalles si DetallePedido
            // tiene una FK a Pedido y no estás usando CascadeType.PERSIST o ALL desde Pedido a DetallePedido
            // de una manera que JPA maneje la persistencia de los detalles automáticamente al guardar el pedido.
            // Sin embargo, como estamos construyendo los DetallePedido con la referencia al 'pedido' (que aún no tiene ID),
            // es mejor guardar el pedido primero, luego los detalles (o configurar CascadeType.ALL en Pedido.detallesPedidos).

            // Si Pedido.detallesPedidos tiene CascadeType.ALL (o PERSIST y MERGE),
            // al asignar 'detalles' a 'pedido' y guardar 'pedido', los detalles también se guardarán.
            pedido.setDetallesPedidos(detalles);
            pedido.setTotal(totalPedido);
            pedido.setTotalCosto(totalCostoPedido);

            if (tiempoEstimadoTotal == 0 && !detalles.isEmpty()) tiempoEstimadoTotal = 15;
            pedido.setHoraEstimadaFinalizacion(LocalTime.now().plusMinutes(tiempoEstimadoTotal));

            Pedido savedPedido = pedidoRepository.save(pedido);

            // Si no usas CascadeType.ALL en Pedido.detallesPedidos o quieres ser explícito:
            // for (DetallePedido dp : savedPedido.getDetallesPedidos()) {
            //    dp.setPedido(savedPedido); // Asegurar que la referencia al pedido persistido esté en cada detalle
            //    detallePedidoRepository.save(dp); // Guardar cada detalle individualmente
            // }
            // Pero con CascadeType.ALL en la relación OneToMany de Pedido a DetallePedido,
            // el save(pedido) anterior ya debería haber persistido los detalles.

            return convertToDto(savedPedido);

        } catch (Exception e) {
            throw new Exception("Error al crear el pedido: " + e.getMessage(), e);
        }
    }

    // ... (resto de los métodos de PedidoServiceImpl como cambiarEstadoPedido, convertToDto, etc.)
    // Asegúrate de que el método reponerStockPorPedido y cambiarEstadoPedido estén completos y correctos.
    // El método convertToDto también debe estar presente y funcionar como se espera.

    @Override
    @Transactional
    public PedidoResponseDTO cambiarEstadoPedido(Long pedidoId, Estado nuevoEstado) throws Exception {
        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new Exception("Pedido no encontrado con ID: " + pedidoId));

        if (pedido.getEstado() == Estado.CANCELADO || pedido.getEstado() == Estado.RECHAZADO || pedido.getEstado() == Estado.ENTREGADO) {
            if (nuevoEstado != pedido.getEstado()) {
                throw new Exception("No se puede cambiar el estado de un pedido que ya está " + pedido.getEstado());
            }
        }

        if ((nuevoEstado == Estado.CANCELADO || nuevoEstado == Estado.RECHAZADO) &&
                (pedido.getEstado() != Estado.CANCELADO && pedido.getEstado() != Estado.RECHAZADO)) {
            reponerStockPorPedido(pedido);
        }

        pedido.setEstado(nuevoEstado);

        if (nuevoEstado == Estado.ENTREGADO && pedido.getFactura() == null) {
            Factura factura = Factura.builder()
                    .fechaFacturacion(LocalDate.now())
                    .formaPago(pedido.getFormaPago())
                    .totalVenta(pedido.getTotal())
                    .build();
            Factura savedFactura = facturaRepository.save(factura);
            pedido.setFactura(savedFactura);
        }

        return convertToDto(pedidoRepository.save(pedido));
    }

    private void reponerStockPorPedido(Pedido pedido) throws Exception {
        if (pedido.getDetallesPedidos() == null) return; // Seguridad adicional
        for (DetallePedido detalle : pedido.getDetallesPedidos()) {
            if (detalle.getArticuloManufacturado() != null) {
                ArticuloManufacturado manufacturado = detalle.getArticuloManufacturado();
                if (manufacturado.getDetalles() == null) continue; // Seguridad adicional
                for (ArticuloManufacturadoDetalle amd : manufacturado.getDetalles()) {
                    ArticuloInsumo insumoComponente = amd.getArticuloInsumo();
                    if (insumoComponente == null) continue; // Seguridad adicional
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
    public List<PedidoResponseDTO> findByClienteId(Long clienteId) throws Exception {
        List<Pedido> pedidos = pedidoRepository.findByClienteId(clienteId);
        return pedidos.stream().map(this::convertToDto).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PedidoResponseDTO> findByEstado(Estado estado) throws Exception {
        List<Pedido> pedidos = pedidoRepository.findByEstado(estado);
        return pedidos.stream().map(this::convertToDto).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public PedidoResponseDTO findPedidoById(Long id) throws Exception {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new Exception("Pedido no encontrado con ID: " + id));
        return convertToDto(pedido);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PedidoResponseDTO> findAllPedidos() throws Exception {
        List<Pedido> pedidos = pedidoRepository.findAll();
        return pedidos.stream().map(this::convertToDto).collect(Collectors.toList());
    }

    private PedidoResponseDTO convertToDto(Pedido pedido) {
        if (pedido == null) return null;

        PedidoResponseDTO dto = new PedidoResponseDTO();
        dto.setId(pedido.getId());
        dto.setHoraEstimadaFinalizacion(pedido.getHoraEstimadaFinalizacion());
        dto.setTotal(pedido.getTotal());
        dto.setTotalCosto(pedido.getTotalCosto());
        dto.setEstado(pedido.getEstado());
        dto.setTipoEnvio(pedido.getTipoEnvio());
        dto.setFormaPago(pedido.getFormaPago());
        dto.setFechaPedido(pedido.getFechaPedido());

        if (pedido.getCliente() != null) {
            ClienteResponseDTO clienteDTO = new ClienteResponseDTO();
            clienteDTO.setId(pedido.getCliente().getId());
            clienteDTO.setNombre(pedido.getCliente().getNombre());
            clienteDTO.setApellido(pedido.getCliente().getApellido());
            clienteDTO.setEmail(pedido.getCliente().getEmail());
            clienteDTO.setTelefono(pedido.getCliente().getTelefono());
            dto.setCliente(clienteDTO);
        }

        if (pedido.getDomicilioEntrega() != null) {
            DomicilioDTO domicilioDTO = new DomicilioDTO();
            domicilioDTO.setId(pedido.getDomicilioEntrega().getId());
            domicilioDTO.setCalle(pedido.getDomicilioEntrega().getCalle());
            domicilioDTO.setNumero(pedido.getDomicilioEntrega().getNumero());
            domicilioDTO.setCp(pedido.getDomicilioEntrega().getCp());
            if(pedido.getDomicilioEntrega().getLocalidad() != null){
                LocalidadDTO localidadDTO = new LocalidadDTO();
                localidadDTO.setId(pedido.getDomicilioEntrega().getLocalidad().getId());
                localidadDTO.setNombre(pedido.getDomicilioEntrega().getLocalidad().getNombre());
                if (pedido.getDomicilioEntrega().getLocalidad().getProvincia() != null) {
                    ProvinciaDTO provinciaDTO = new ProvinciaDTO();
                    provinciaDTO.setId(pedido.getDomicilioEntrega().getLocalidad().getProvincia().getId());
                    provinciaDTO.setNombre(pedido.getDomicilioEntrega().getLocalidad().getProvincia().getNombre());
                    if (pedido.getDomicilioEntrega().getLocalidad().getProvincia().getPais() != null) {
                        PaisDTO paisDTO = new PaisDTO();
                        paisDTO.setId(pedido.getDomicilioEntrega().getLocalidad().getProvincia().getPais().getId());
                        paisDTO.setNombre(pedido.getDomicilioEntrega().getLocalidad().getProvincia().getPais().getNombre());
                        provinciaDTO.setPais(paisDTO);
                    }
                    localidadDTO.setProvincia(provinciaDTO);
                }
                domicilioDTO.setLocalidad(localidadDTO);
            }
            dto.setDomicilioEntrega(domicilioDTO);
        }

        if (pedido.getSucursal() != null) {
            SucursalDTO sucursalDTO = new SucursalDTO();
            sucursalDTO.setId(pedido.getSucursal().getId());
            sucursalDTO.setNombre(pedido.getSucursal().getNombre());
            dto.setSucursal(sucursalDTO);
        }

        if (pedido.getFactura() != null) {
            FacturaDTO facturaDTO = new FacturaDTO();
            facturaDTO.setId(pedido.getFactura().getId());
            facturaDTO.setFechaFacturacion(pedido.getFactura().getFechaFacturacion());
            facturaDTO.setTotalVenta(pedido.getFactura().getTotalVenta());
            facturaDTO.setFormaPago(pedido.getFactura().getFormaPago());
            dto.setFactura(facturaDTO);
        }

        if (pedido.getDetallesPedidos() != null) {
            dto.setDetallesPedidos(pedido.getDetallesPedidos().stream().map(detalle -> {
                DetallePedidoDTO detalleDTO = new DetallePedidoDTO();
                detalleDTO.setId(detalle.getId());
                detalleDTO.setCantidad(detalle.getCantidad());
                detalleDTO.setSubTotal(detalle.getSubTotal());
                if (detalle.getArticuloManufacturado() != null) {
                    ArticuloManufacturadoDTO amDto = new ArticuloManufacturadoDTO();
                    amDto.setId(detalle.getArticuloManufacturado().getId());
                    amDto.setDenominacion(detalle.getArticuloManufacturado().getDenominacion());
                    amDto.setPrecioVenta(detalle.getArticuloManufacturado().getPrecioVenta());
                    detalleDTO.setArticuloManufacturado(amDto);
                }
                if (detalle.getArticuloInsumo() != null) {
                    ArticuloInsumoDTO aiDto = new ArticuloInsumoDTO();
                    aiDto.setId(detalle.getArticuloInsumo().getId());
                    aiDto.setDenominacion(detalle.getArticuloInsumo().getDenominacion());
                    aiDto.setPrecioVenta(detalle.getArticuloInsumo().getPrecioVenta());
                    detalleDTO.setArticuloInsumo(aiDto);
                }
                return detalleDTO;
            }).collect(Collectors.toSet()));
        }
        return dto;
    }
}