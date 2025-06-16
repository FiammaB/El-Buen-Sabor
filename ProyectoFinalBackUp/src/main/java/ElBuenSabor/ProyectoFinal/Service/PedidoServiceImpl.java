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
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public  class PedidoServiceImpl extends BaseServiceImpl<Pedido, Long> implements PedidoService {

    private final PedidoRepository pedidoRepository;
    private final ClienteRepository clienteRepository;
    private final DomicilioRepository domicilioRepository;
    private final SucursalRepository sucursalRepository;
    private final ArticuloManufacturadoRepository articuloManufacturadoRepository;
    private final ArticuloInsumoRepository articuloInsumoRepository;
    private final ArticuloManufacturadoDetalleRepository articuloManufacturadoDetalleRepository;
    private final FacturaService facturaService;

    @Autowired
    public PedidoServiceImpl(PedidoRepository pedidoRepository, ClienteRepository clienteRepository,
                             DomicilioRepository domicilioRepository, SucursalRepository sucursalRepository,
                             ArticuloManufacturadoRepository articuloManufacturadoRepository,
                             ArticuloInsumoRepository articuloInsumoRepository,
                             ArticuloManufacturadoDetalleRepository articuloManufacturadoDetalleRepository,
                             FacturaService facturaService) {
        super(pedidoRepository);
        this.pedidoRepository = pedidoRepository;
        this.clienteRepository = clienteRepository;
        this.domicilioRepository = domicilioRepository;
        this.sucursalRepository = sucursalRepository;
        this.articuloManufacturadoRepository = articuloManufacturadoRepository;
        this.articuloInsumoRepository = articuloInsumoRepository;
        this.articuloManufacturadoDetalleRepository = articuloManufacturadoDetalleRepository;
        this.facturaService = facturaService;
    }

    @Override
    @Transactional
    public PedidoResponseDTO crearPedido(PedidoCreateDTO pedidoDTO) throws Exception {
        Pedido savedPedido = crearPedidoBase(pedidoDTO);
        return mapToPedidoResponseDTO(savedPedido);
    }

    @Override
    @Transactional
    public Pedido crearPedidoEntidad(PedidoCreateDTO pedidoDTO) throws Exception {
        return crearPedidoBase(pedidoDTO);
    }

    private Pedido crearPedidoBase(PedidoCreateDTO pedidoDTO) throws Exception {
        if (pedidoDTO.getDetallesPedidos() == null || pedidoDTO.getDetallesPedidos().isEmpty()) {
            throw new Exception("El pedido debe contener al menos un detalle de producto.");
        }

        Cliente cliente = clienteRepository.findById(pedidoDTO.getClienteId())
                .orElseThrow(() -> new Exception("Cliente no encontrado con ID: " + pedidoDTO.getClienteId()));

        Sucursal sucursal = sucursalRepository.findById(pedidoDTO.getSucursalId())
                .orElseThrow(() -> new Exception("Sucursal no encontrada con ID: " + pedidoDTO.getSucursalId()));

        Domicilio domicilioEntrega = null;
        if (pedidoDTO.getTipoEnvio() == TipoEnvio.DELIVERY) {
            if (pedidoDTO.getDomicilioEntregaId() == null) {
                throw new Exception("Se requiere un ID de domicilio de entrega para el tipo de envío DELIVERY.");
            }
            domicilioEntrega = domicilioRepository.findById(pedidoDTO.getDomicilioEntregaId())
                    .orElseThrow(() -> new Exception("Domicilio de entrega no encontrado con ID: " + pedidoDTO.getDomicilioEntregaId()));
        }

        Pedido pedido = new Pedido();
        pedido.setCliente(cliente);
        pedido.setSucursal(sucursal);
        pedido.setDomicilioEntrega(domicilioEntrega);
        pedido.setTipoEnvio(pedidoDTO.getTipoEnvio());
        pedido.setFormaPago(pedidoDTO.getFormaPago());
        pedido.setFechaPedido(LocalDate.now());
        pedido.setEstado(Estado.A_CONFIRMAR);

        Set<DetallePedido> detalles = new HashSet<>();
        double totalPedido = 0.0;
        double totalCosto = 0.0;

        for (DetallePedidoCreateDTO detalleDTO : pedidoDTO.getDetallesPedidos()) {
            Articulo articuloBase = null;
            double precioUnitario = 0.0;
            double costoUnitario = 0.0;
            int cantidad = detalleDTO.getCantidad();

            if (cantidad <= 0) {
                throw new Exception("La cantidad para un detalle de pedido debe ser mayor a cero.");
            }
            if (detalleDTO.getArticuloManufacturadoId() != null && detalleDTO.getArticuloInsumoId() != null) {
                throw new Exception("Un detalle de pedido no puede tener ArticuloManufacturado y ArticuloInsumo a la vez.");
            }

            if (detalleDTO.getArticuloManufacturadoId() != null) {
                ArticuloManufacturado manufacturado = articuloManufacturadoRepository.findById(detalleDTO.getArticuloManufacturadoId())
                        .orElseThrow(() -> new Exception("Artículo manufacturado no encontrado con ID: " + detalleDTO.getArticuloManufacturadoId()));
                articuloBase = manufacturado;
                precioUnitario = manufacturado.getPrecioVenta();

                if (manufacturado.getDetalles() == null || manufacturado.getDetalles().isEmpty()) {
                    throw new Exception("El artículo manufacturado '" + manufacturado.getDenominacion() + "' no tiene detalles de ingredientes configurados.");
                }
                double costoManufacturadoActual = 0.0;
                for (ArticuloManufacturadoDetalle amd : manufacturado.getDetalles()) {
                    ArticuloInsumo insumoParaDescontar = amd.getArticuloInsumo();
                    if (insumoParaDescontar == null) {
                        throw new Exception("Ingrediente nulo en el detalle del artículo manufacturado " + manufacturado.getDenominacion());
                    }
                    double cantidadInsumoNecesaria = amd.getCantidad() * cantidad;

                    ArticuloInsumo insumoActualizado = articuloInsumoRepository.findById(insumoParaDescontar.getId())
                            .orElseThrow(() -> new Exception("Insumo no encontrado para descuento de stock con ID: " + insumoParaDescontar.getId()));

                    if (insumoActualizado.getStockActual() < cantidadInsumoNecesaria) {
                        throw new Exception("Stock insuficiente para el ingrediente '" + insumoActualizado.getDenominacion() + "'. Stock actual: " + insumoActualizado.getStockActual() + ", Necesario: " + cantidadInsumoNecesaria);
                    }
                    insumoActualizado.setStockActual(insumoActualizado.getStockActual() - cantidadInsumoNecesaria);
                    articuloInsumoRepository.save(insumoActualizado);
                    costoManufacturadoActual += amd.getCantidad() * insumoActualizado.getPrecioCompra();
                }
                costoUnitario = costoManufacturadoActual;

            } else if (detalleDTO.getArticuloInsumoId() != null) {
                ArticuloInsumo insumo = articuloInsumoRepository.findById(detalleDTO.getArticuloInsumoId())
                        .orElseThrow(() -> new Exception("Artículo insumo no encontrado con ID: " + detalleDTO.getArticuloInsumoId()));
                articuloBase = insumo;
                precioUnitario = insumo.getPrecioVenta();
                costoUnitario = insumo.getPrecioCompra();

                if (insumo.getEsParaElaborar() != null && !insumo.getEsParaElaborar()) {
                    ArticuloInsumo insumoActualizado = articuloInsumoRepository.findById(insumo.getId())
                            .orElseThrow(() -> new Exception("Insumo no encontrado para descuento de stock con ID: " + insumo.getId()));

                    if (insumoActualizado.getStockActual() < cantidad) {
                        throw new Exception("Stock insuficiente para el artículo '" + insumoActualizado.getDenominacion() + "'. Stock actual: " + insumoActualizado.getStockActual() + ", Necesario: " + cantidad);
                    }
                    insumoActualizado.setStockActual(insumoActualizado.getStockActual() - cantidad);
                    articuloInsumoRepository.save(insumoActualizado);
                }
            } else {
                throw new Exception("Detalle de pedido sin ID de artículo manufacturado ni insumo especificado.");
            }

            double subTotalDetalle = cantidad * precioUnitario;
            double subCostoDetalle = cantidad * costoUnitario;

            DetallePedido detalle = new DetallePedido();
            detalle.setCantidad(cantidad);
            detalle.setSubTotal(subTotalDetalle);
            detalle.setPedido(pedido);

            if (articuloBase instanceof ArticuloManufacturado) {
                detalle.setArticuloManufacturado((ArticuloManufacturado) articuloBase);
            } else if (articuloBase instanceof ArticuloInsumo) {
                detalle.setArticuloInsumo((ArticuloInsumo) articuloBase);
            }
            detalles.add(detalle);

            totalPedido += subTotalDetalle;
            totalCosto += subCostoDetalle;
        }

        pedido.setDetallesPedidos(detalles);
        pedido.setTotal(totalPedido);
        pedido.setTotalCosto(totalCosto);
        pedido.setHoraEstimadaFinalizacion(calcularTiempoEstimadoPedido(pedido));

        Pedido savedPedido = pedidoRepository.save(pedido);

        if (pedidoDTO.getFormaPago() == FormaPago.MERCADO_PAGO) {
            // facturaService.generarFacturaPorPedido(savedPedido);
        }

        return savedPedido;
    }

    @Override
    @Transactional(readOnly = true)
    public PedidoResponseDTO findPedidoById(Long id) throws Exception {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new Exception("Pedido no encontrado con ID: " + id));
        return mapToPedidoResponseDTO(pedido);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PedidoResponseDTO> findAllPedidos() throws Exception {
        return pedidoRepository.findAll().stream()
                .map(this::mapToPedidoResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public PedidoResponseDTO actualizarEstadoPedido(Long pedidoId, Estado nuevoEstado) throws Exception {
        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new Exception("Pedido no encontrado con ID: " + pedidoId));

        if (!esCambioDeEstadoValido(pedido.getEstado(), nuevoEstado)) {
            throw new Exception("Transición de estado inválida de " + pedido.getEstado() + " a " + nuevoEstado);
        }

        if (nuevoEstado == Estado.ENTREGADO && pedido.getFormaPago() == FormaPago.EFECTIVO && pedido.getFactura() == null) {
            throw new Exception("No se puede marcar como entregado si el pago es en efectivo y no se ha marcado como cobrado.");
        }

        pedido.setEstado(nuevoEstado);
        Pedido updatedPedido = pedidoRepository.save(pedido);
        return mapToPedidoResponseDTO(updatedPedido);
    }

    @Transactional(readOnly = true)
    @Override
    public List<PedidoResponseDTO> findPedidosByClienteId(Long clienteId) throws Exception {
        List<Pedido> pedidos = pedidoRepository.findByClienteId(clienteId);
        return pedidos.stream()
                .map(this::mapToPedidoResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public List<PedidoResponseDTO> findPedidosByEstado(Estado estado) throws Exception {
        List<Pedido> pedidos = pedidoRepository.findByEstado(estado);
        return pedidos.stream()
                .map(this::mapToPedidoResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public PedidoResponseDTO marcarPedidoComoPagado(Long pedidoId) throws Exception {
        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new Exception("Pedido no encontrado con ID: " + pedidoId));

        if (pedido.getFactura() != null) {
            throw new Exception("El pedido con ID " + pedidoId + " ya tiene una factura y ha sido marcado como pagado.");
        }
        if (pedido.getFormaPago() == FormaPago.MERCADO_PAGO) {
            throw new Exception("Este pedido fue pagado por Mercado Pago. La factura se generó automáticamente.");
        }

        facturaService.generarFacturaPorPedido(pedido);
        Pedido updatedPedido = pedidoRepository.save(pedido);
        return mapToPedidoResponseDTO(updatedPedido);
    }

    // --- Métodos Helper para lógica interna y mapeo a DTOs ---

    private LocalTime calcularTiempoEstimadoPedido(Pedido pedido) {
        int maxTiempoArticulo = 0;
        for (DetallePedido detalle : pedido.getDetallesPedidos()) {
            if (detalle.getArticuloManufacturado() != null) {
                maxTiempoArticulo = Math.max(maxTiempoArticulo, detalle.getArticuloManufacturado().getTiempoEstimadoMinutos());
            }
        }

        int tiempoPedidosEnCocina = 0;
        try {
            List<Pedido> pedidosEnCocina = pedidoRepository.findByEstado(Estado.EN_COCINA);
            if (pedidosEnCocina != null && !pedidosEnCocina.isEmpty()) {
                Optional<LocalTime> maxHoraEstimada = pedidosEnCocina.stream()
                        .map(Pedido::getHoraEstimadaFinalizacion)
                        .filter(java.util.Objects::nonNull)
                        .max(LocalTime::compareTo);
                if (maxHoraEstimada.isPresent()) {
                    long minutosPendientes = LocalTime.now().until(maxHoraEstimada.get(), java.time.temporal.ChronoUnit.MINUTES);
                    tiempoPedidosEnCocina = (int) Math.max(0, minutosPendientes);
                }
            }
        } catch (Exception e) {
            System.err.println("Error al calcular tiempo estimado de pedidos en cocina: " + e.getMessage());
        }

        int tiempoTotalEstimadoMinutos = maxTiempoArticulo + tiempoPedidosEnCocina;

        if (pedido.getTipoEnvio() == TipoEnvio.DELIVERY) {
            tiempoTotalEstimadoMinutos += 10;
        }
        if (tiempoTotalEstimadoMinutos < 0) {
            tiempoTotalEstimadoMinutos = 0;
        }

        return LocalTime.now().plusMinutes(tiempoTotalEstimadoMinutos);
    }

    private boolean esCambioDeEstadoValido(Estado estadoActual, Estado nuevoEstado) {
        switch (estadoActual) {
            case A_CONFIRMAR:
                return nuevoEstado == Estado.EN_COCINA || nuevoEstado == Estado.LISTO || nuevoEstado == Estado.CANCELADO || nuevoEstado == Estado.RECHAZADO;
            case EN_COCINA:
                return nuevoEstado == Estado.LISTO || nuevoEstado == Estado.CANCELADO || nuevoEstado == Estado.RECHAZADO;
            case LISTO:
                return nuevoEstado == Estado.EN_DELIVERY || nuevoEstado == Estado.ENTREGADO || nuevoEstado == Estado.CANCELADO || nuevoEstado == Estado.RECHAZADO;
            case EN_DELIVERY:
                return nuevoEstado == Estado.ENTREGADO || nuevoEstado == Estado.RECHAZADO;
            case ENTREGADO:
            case CANCELADO:
            case RECHAZADO:
                return false;
            default:
                return false;
        }
    }

    private PedidoResponseDTO mapToPedidoResponseDTO(Pedido pedido) {
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

        // Mapear Cliente
        if (pedido.getCliente() != null) {
            dto.setCliente(mapToClienteResponseDTO(pedido.getCliente()));
        }

        // Mapear Domicilio de Entrega
        if (pedido.getDomicilioEntrega() != null) {
            dto.setDomicilioEntrega(mapToDomicilioDTO(pedido.getDomicilioEntrega()));
        }

        // Mapear Factura
        if (pedido.getFactura() != null) {
            dto.setFactura(mapToFacturaDTO(pedido.getFactura(), pedido.getId()));
        }

        // Mapear Sucursal
        if (pedido.getSucursal() != null) {
            dto.setSucursal(mapToSucursalDTO(pedido.getSucursal()));
        }

        // Mapear Detalles del Pedido
        if (pedido.getDetallesPedidos() != null) {
            Set<DetallePedidoDTO> detallesDTO = pedido.getDetallesPedidos().stream()
                    .map(this::mapToDetallePedidoDTO)
                    .collect(Collectors.toSet());
            dto.setDetallesPedidos(detallesDTO);
        }

        return dto;
    }

    private ClienteResponseDTO mapToClienteResponseDTO(Cliente cliente) {
        ClienteResponseDTO dto = new ClienteResponseDTO();
        dto.setId(cliente.getId());
        dto.setNombre(cliente.getNombre());
        dto.setApellido(cliente.getApellido());
        dto.setTelefono(cliente.getTelefono());
        dto.setEmail(cliente.getEmail());
        dto.setFechaNacimiento(cliente.getFechaNacimiento());
        dto.setUsername(cliente.getUsername());
        dto.setAuth0Id(cliente.getAuth0Id());
        dto.setEstaDadoDeBaja(cliente.isEstaDadoDeBaja());

        if (cliente.getImagen() != null) {
            dto.setImagen(mapToImagenDTO(cliente.getImagen()));
        }

        if (cliente.getDomicilios() != null) {
            dto.setDomicilios(cliente.getDomicilios().stream()
                    .map(this::mapToDomicilioDTO)
                    .collect(Collectors.toList()));
        }

        return dto;
    }

    private DomicilioDTO mapToDomicilioDTO(Domicilio domicilio) {
        DomicilioDTO dto = new DomicilioDTO();
        dto.setId(domicilio.getId());
        dto.setCalle(domicilio.getCalle());
        dto.setNumero(domicilio.getNumero());
        dto.setCp(domicilio.getCp());

        if (domicilio.getLocalidad() != null) {
            dto.setLocalidadId(domicilio.getLocalidad().getId());
            dto.setLocalidad(mapToLocalidadDTO(domicilio.getLocalidad()));
        }

        return dto;
    }

    private LocalidadDTO mapToLocalidadDTO(Localidad localidad) {
        LocalidadDTO dto = new LocalidadDTO();
        dto.setId(localidad.getId());
        dto.setNombre(localidad.getNombre());

        if (localidad.getProvincia() != null) {
            dto.setProvinciaId(localidad.getProvincia().getId());
            dto.setProvincia(mapToProvinciaDTO(localidad.getProvincia()));
        }

        return dto;
    }

    private ProvinciaDTO mapToProvinciaDTO(Provincia provincia) {
        ProvinciaDTO dto = new ProvinciaDTO();
        dto.setId(provincia.getId());
        dto.setNombre(provincia.getNombre());

        if (provincia.getPais() != null) {
            dto.setPaisId(provincia.getPais().getId());
            dto.setPais(mapToPaisDTO(provincia.getPais()));
        }

        return dto;
    }

    private PaisDTO mapToPaisDTO(Pais pais) {
        PaisDTO dto = new PaisDTO();
        dto.setId(pais.getId());
        dto.setNombre(pais.getNombre());
        return dto;
    }

    private FacturaDTO mapToFacturaDTO(Factura factura, Long pedidoId) {
        FacturaDTO dto = new FacturaDTO();
        dto.setId(factura.getId());
        dto.setFechaFacturacion(factura.getFechaFacturacion());
        dto.setMpPaymentId(factura.getMpPaymentId());
        dto.setMpMerchantOrderId(factura.getMpMerchantOrderId());
        dto.setMpPreferenceId(factura.getMpPreferenceId());
        dto.setMpPaymentType(factura.getMpPaymentType());
        dto.setFormaPago(factura.getFormaPago());
        dto.setTotalVenta(factura.getTotalVenta());
        dto.setPedidoId(pedidoId);
        return dto;
    }

    private SucursalDTO mapToSucursalDTO(Sucursal sucursal) {
        SucursalDTO dto = new SucursalDTO();
        dto.setId(sucursal.getId());
        dto.setNombre(sucursal.getNombre());
        dto.setHorarioApertura(sucursal.getHorarioApertura());
        dto.setHorarioCierre(sucursal.getHorarioCierre());

        if (sucursal.getDomicilio() != null) {
            dto.setDomicilioId(sucursal.getDomicilio().getId());
            dto.setDomicilio(mapToDomicilioDTO(sucursal.getDomicilio()));
        }

        if (sucursal.getEmpresa() != null) {
            dto.setEmpresaId(sucursal.getEmpresa().getId());
        }

        if (sucursal.getCategorias() != null) {
            dto.setCategoriaIds(sucursal.getCategorias().stream()
                    .map(Categoria::getId)
                    .collect(Collectors.toList()));
            dto.setCategorias(sucursal.getCategorias().stream()
                    .map(this::mapToCategoriaDTO)
                    .collect(Collectors.toList()));
        }

        return dto;
    }

    private CategoriaDTO mapToCategoriaDTO(Categoria categoria) {
        CategoriaDTO dto = new CategoriaDTO();
        dto.setId(categoria.getId());
        dto.setDenominacion(categoria.getDenominacion());

        if (categoria.getCategoriaPadre() != null) {
            dto.setCategoriaPadreId(categoria.getCategoriaPadre().getId());
        }

        if (categoria.getSucursales() != null) {
            dto.setSucursalIds(categoria.getSucursales().stream()
                    .map(Sucursal::getId)
                    .collect(Collectors.toList()));
        }

        return dto;
    }

    private ImagenDTO mapToImagenDTO(Imagen imagen) {
        ImagenDTO dto = new ImagenDTO();
        dto.setId(imagen.getId());
        dto.setDenominacion(imagen.getDenominacion());
        return dto;
    }

    private DetallePedidoDTO mapToDetallePedidoDTO(DetallePedido detalle) {
        DetallePedidoDTO dto = new DetallePedidoDTO();
        dto.setId(detalle.getId());
        dto.setCantidad(detalle.getCantidad());
        dto.setSubTotal(detalle.getSubTotal());

        if (detalle.getArticuloManufacturado() != null) {
            dto.setArticuloManufacturadoId(detalle.getArticuloManufacturado().getId());
            dto.setArticuloManufacturado(mapToArticuloManufacturadoDTO(detalle.getArticuloManufacturado()));
        }

        if (detalle.getArticuloInsumo() != null) {
            dto.setArticuloInsumoId(detalle.getArticuloInsumo().getId());
            dto.setArticuloInsumo(mapToArticuloInsumoDTO(detalle.getArticuloInsumo()));
        }

        return dto;
    }

    private ArticuloManufacturadoDTO mapToArticuloManufacturadoDTO(ArticuloManufacturado articulo) {
        ArticuloManufacturadoDTO dto = new ArticuloManufacturadoDTO();
        dto.setId(articulo.getId());
        dto.setDenominacion(articulo.getDenominacion());
        dto.setPrecioVenta(articulo.getPrecioVenta());
        dto.setDescripcion(articulo.getDescripcion());
        dto.setTiempoEstimadoMinutos(articulo.getTiempoEstimadoMinutos());
        dto.setPreparacion(articulo.getPreparacion());

        if (articulo.getCategoria() != null) {
            dto.setCategoriaId(articulo.getCategoria().getId());
            dto.setCategoria(mapToCategoriaDTO(articulo.getCategoria()));
        }


        if (articulo.getImagen() != null) {
            dto.setImagenId(articulo.getImagen().getId());
            dto.setImagen(mapToImagenDTO(articulo.getImagen()));
        }


        return dto;
    }

    private ArticuloInsumoDTO mapToArticuloInsumoDTO(ArticuloInsumo articulo) {
        ArticuloInsumoDTO dto = new ArticuloInsumoDTO();
        dto.setId(articulo.getId());
        dto.setDenominacion(articulo.getDenominacion());
        dto.setPrecioVenta(articulo.getPrecioVenta());
        dto.setPrecioCompra(articulo.getPrecioCompra());
        dto.setStockActual(articulo.getStockActual());
        dto.setStockMinimo(articulo.getStockMinimo());
        dto.setEsParaElaborar(articulo.getEsParaElaborar());


        if (articulo.getCategoria() != null) {
            dto.setCategoriaId(articulo.getCategoria().getId());
        }
        return dto;
    }

    @Override
    public List<Pedido> findAll() throws Exception {
        return List.of();
    }

    @Override
    public Optional<Pedido> findById(Long aLong) throws Exception {
        return Optional.empty();
    }
}