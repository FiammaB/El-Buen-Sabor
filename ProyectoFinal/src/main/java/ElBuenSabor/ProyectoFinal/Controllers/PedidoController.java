package ElBuenSabor.ProyectoFinal.Controllers;

import ElBuenSabor.ProyectoFinal.DTO.*;
import ElBuenSabor.ProyectoFinal.Mappers.NotaCreditoMapper;
import ElBuenSabor.ProyectoFinal.Repositories.ArticuloInsumoRepository;
import ElBuenSabor.ProyectoFinal.Repositories.ArticuloManufacturadoRepository;
import ElBuenSabor.ProyectoFinal.Entities.*;
import ElBuenSabor.ProyectoFinal.Entities.FormaPago;
import ElBuenSabor.ProyectoFinal.Exceptions.ResourceNotFoundException;
import ElBuenSabor.ProyectoFinal.Mappers.PedidoMapper;
import ElBuenSabor.ProyectoFinal.Repositories.*;
import ElBuenSabor.ProyectoFinal.Service.*;
// Ya no es necesario si se inyecta por constructor explícito al padre
// import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/pedidos") // Define la URL base para este controlador
@CrossOrigin(origins = "http://localhost:5173") // Mantén CrossOrigin si es necesario
// PedidoController ahora extiende BaseController
public class PedidoController extends BaseController<Pedido, Long> {

    private final PedidoMapper pedidoMapper;
    private final PedidoService pedidoService;
    // Repositorios necesarios para resolver relaciones en el controlador
    private final ArticuloInsumoRepository articuloInsumoRepository;
    private final ArticuloManufacturadoRepository articuloManufacturadoRepository;
    private final ClienteRepository clienteRepository;
    private final DomicilioRepository domicilioRepository;
    private final SucursalRepository sucursalRepository;
    private final UsuarioRepository usuarioRepository;
    private final ArticuloRepository articuloRepository; // Aunque no se usa en el create/update, se mantiene si es una dependencia general.
    private final UsuarioService usuarioService; // Asegúrate de que ya está, o añádelo
    private final NotaCreditoMapper notaCreditoMapper;
    private final MPController mpController;
    private final ClienteService clienteService;
    private final DomicilioService domicilioService;
    private final SucursalService sucursalService;
    // El constructor inyecta el servicio específico de Pedido y todas las dependencias adicionales
    public PedidoController(
            PedidoService pedidoService, // Servicio específico
            PedidoMapper pedidoMapper, PedidoService pedidoService1,
            ArticuloInsumoRepository articuloInsumoRepository,
            ArticuloManufacturadoRepository articuloManufacturadoRepository,
            ClienteRepository clienteRepository,
            DomicilioRepository domicilioRepository,
            SucursalRepository sucursalRepository,
            UsuarioRepository usuarioRepository,
            ArticuloRepository articuloRepository,
            UsuarioService usuarioService, // Asegúrate de que ya esté en el constructor
            NotaCreditoMapper notaCreditoMapper,
            MPController mpController,
            ClienteService clienteService,
            DomicilioService domicilioService,
            SucursalService sucursalService


            ) {
        super(pedidoService); // Pasa el servicio al constructor del BaseController
        this.pedidoMapper = pedidoMapper;
        this.pedidoService = pedidoService;
        this.articuloInsumoRepository = articuloInsumoRepository;
        this.articuloManufacturadoRepository = articuloManufacturadoRepository;
        this.clienteRepository = clienteRepository;
        this.domicilioRepository = domicilioRepository;
        this.sucursalRepository = sucursalRepository;
        this.usuarioRepository = usuarioRepository;
        this.articuloRepository = articuloRepository;
        this.usuarioService = usuarioService;
        this.notaCreditoMapper = notaCreditoMapper;
        this.mpController = mpController;
        this.clienteService = clienteService;
        this.domicilioService = domicilioService;
        this.sucursalService = sucursalService;
    }

    // Sobrescribir getAll para devolver DTOs y manejar excepciones
    @GetMapping
    @Override // Sobrescribe el getAll del BaseController
    public ResponseEntity<?> getAll() {
        try {
            List<Pedido> pedidos = baseService.findAll(); // Llama al findAll del padre
            List<PedidoDTO> dtos = pedidos.stream()
                    .map(pedidoMapper::toDTO)
                    .toList();
            return ResponseEntity.ok(dtos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    // Sobrescribir getOne para devolver un DTO y manejar excepciones
    @GetMapping("/{id}")
    @Override // Sobrescribe el getOne del BaseController
    public ResponseEntity<?> getOne(@PathVariable Long id) {
        try {
            Pedido pedido = baseService.findById(id); // Llama al findById del padre
            return ResponseEntity.ok(pedidoMapper.toDTO(pedido));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    // Sobrescribir create para aceptar un DTO de entrada, mapear y manejar excepciones
    @PostMapping(consumes = "application/json")
    public ResponseEntity<?> create(@RequestBody PedidoCreateDTO dto) {
        try {
            // --- LÓGICA DE PREPARACIÓN DEL PEDIDO (común para MP y EFECTIVO) ---

            Pedido pedidoParaCalculos = new Pedido();

            // Validaciones de IDs obligatorios
            if (dto.getClienteId() == null) {
                return ResponseEntity.badRequest().body("El ID del cliente no puede ser nulo");
            }
            if (dto.getDomicilioId() == null) {
                return ResponseEntity.badRequest().body("El ID del domicilio no puede ser nulo");
            }

            // Resolver relaciones necesarias
            pedidoParaCalculos.setCliente(clienteService.findById(dto.getClienteId()));
            pedidoParaCalculos.setDomicilioEntrega(domicilioService.findById(dto.getDomicilioId()));

            if (dto.getSucursalId() != null) {
                pedidoParaCalculos.setSucursal(sucursalService.findById(dto.getSucursalId()));
            }

            if (dto.getEmpleadoId() != null) {
                pedidoParaCalculos.setEmpleado(usuarioService.findById(dto.getEmpleadoId()));
            }

            // Descuento por Retiro en Local
            Double totalCalculado = dto.getTotal();
            if (dto.getTipoEnvio() == TipoEnvio.RETIRO_EN_LOCAL) {
                double descuento = totalCalculado * 0.10;
                totalCalculado -= descuento;
                System.out.println("DEBUG Descuento: Aplicado 10% por Retiro en Local. Total Original: " + dto.getTotal() + ", Final: " + totalCalculado);
            } else {
                System.out.println("DEBUG Descuento: No aplica descuento. Tipo de Envío: " + dto.getTipoEnvio());
            }

            pedidoParaCalculos.setTotal(totalCalculado);
            pedidoParaCalculos.setTipoEnvio(dto.getTipoEnvio());
            pedidoParaCalculos.setFormaPago(dto.getFormaPago());

            // Construcción de detalles del pedido
            if (dto.getDetalles() != null) {
                Set<DetallePedido> detalles = new HashSet<>();
                for (DetallePedidoCreateDTO detalleDTO : dto.getDetalles()) {
                    DetallePedido detalle = new DetallePedido();
                    detalle.setCantidad(detalleDTO.getCantidad());

                    if (detalleDTO.getArticuloId() != null) {
                        ArticuloInsumo insumo = articuloInsumoRepository.findById(detalleDTO.getArticuloId()).orElse(null);
                        if (insumo != null) {
                            detalle.setArticuloInsumo(insumo);
                            detalle.setSubTotal(insumo.getPrecioVenta() * detalle.getCantidad());
                        } else {
                            ArticuloManufacturado manufacturado = articuloManufacturadoRepository.findById(detalleDTO.getArticuloId())
                                    .orElseThrow(() -> new ResourceNotFoundException("Artículo con ID " + detalleDTO.getArticuloId() + " no encontrado"));
                            detalle.setArticuloManufacturado(manufacturado);
                            detalle.setSubTotal(detalleDTO.getSubTotal());
                        }
                    }


                    detalle.setPedido(pedidoParaCalculos); // Relación inversa
                    detalles.add(detalle);
                }
                pedidoParaCalculos.setDetallesPedidos(detalles);
            }

            // --- ¡CALCULAR Y ASIGNAR LA HORA ESTIMADA DE FINALIZACIÓN AQUÍ! ---
            LocalTime horaEstimada = pedidoService.calcularTiempoEstimadoFinalizacion(pedidoParaCalculos); // Llama al servicio para calcular
            pedidoParaCalculos.setHoraEstimadaFinalizacion(horaEstimada); // <-- Asignar al pedido
            System.out.println("DEBUG Tiempo Estimado: Hora estimada finalización asignada: " + horaEstimada);

            // Asignar el resto de propiedades del DTO al pedidoParaCalculos
            pedidoParaCalculos.setFechaPedido(dto.getFechaPedido());
            pedidoParaCalculos.setEstado(Estado.A_CONFIRMAR); // Estado inicial
            pedidoParaCalculos.setBaja(false); // No dado de baja por defecto


            // --- LÓGICA DE DECISIÓN POR FORMA DE PAGO ---
            if (dto.getFormaPago() == FormaPago.MERCADO_PAGO) {
                dto.setTotal(pedidoParaCalculos.getTotal());
                return mpController.crearPreferencia(dto); // Delega al MPController

            } else if (dto.getFormaPago() == FormaPago.EFECTIVO) {
                // Para efectivo, ya tenemos el pedidoParaCalculos completo.
                // Ahora, creamos la factura final para efectivo.
                Factura factura = Factura.builder()
                        .fechaFacturacion(LocalDate.now())
                        .formaPago(FormaPago.EFECTIVO)
                        .totalVenta(pedidoParaCalculos.getTotal()) // Usar el total ya calculado
                        .build();
                pedidoParaCalculos.setFactura(factura); // Asignar al pedidoParaCalculos

                Pedido saved = baseService.save(pedidoParaCalculos); // Guarda el pedido completo
                return ResponseEntity.status(HttpStatus.CREATED).body(pedidoMapper.toDTO(saved));
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"error\": \"Forma de pago no válida.\"}");
            }

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"error\": \"Error al crear el pedido: " + e.getMessage() + "\"}");
        }
    }
    // Sobrescribir update para aceptar un DTO de entrada, mapear y manejar excepciones
    // (Tu controlador original no tenía un PUT explícito, pero es buena práctica añadirlo)
    @PutMapping(value = "/{id}", consumes = "application/json")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody PedidoCreateDTO dto) { // Usa PedidoCreateDTO para update también
        try {
            Pedido existingPedido = baseService.findById(id); // Obtén el pedido existente

            // Actualiza las propiedades básicas
            existingPedido.setFechaPedido(dto.getFechaPedido());
            // No hay hora estimada en PedidoCreateDTO, si la necesitas, agrégala al DTO
            existingPedido.setEstado(Estado.valueOf(dto.getEstado()));
            System.out.println(dto.getEstado());//me parece q lo actualizo aca o no ?
            existingPedido.setTipoEnvio(dto.getTipoEnvio()); // Convertir String a Enum
            existingPedido.setFormaPago(dto.getFormaPago()); // Convertir String a Enum
            existingPedido.setTotal(dto.getTotal());
            // Si hay Observaciones en Pedido, asegúrate de que el DTO las tenga
            // existingPedido.setObservaciones(dto.getObservaciones());

            // Actualizar relaciones
            existingPedido.setCliente(clienteRepository.findById(dto.getClienteId())
                    .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado")));
            existingPedido.setDomicilioEntrega(domicilioRepository.findById(dto.getDomicilioId())
                    .orElseThrow(() -> new ResourceNotFoundException("Domicilio no encontrado")));

            if (dto.getSucursalId() != null) {
                existingPedido.setSucursal(sucursalRepository.findById(dto.getSucursalId()).orElse(null));
            } else {
                existingPedido.setSucursal(null);
            }
            if (dto.getEmpleadoId() != null) {
                existingPedido.setEmpleado(usuarioRepository.findById(dto.getEmpleadoId()).orElse(null));
            } else {
                existingPedido.setEmpleado(null);
            }

            // Actualizar Factura (si se proporciona en el DTO)
            if (dto.getFactura() != null) {
                FacturaCreateDTO f = dto.getFactura();
                Factura facturaToUpdate = existingPedido.getFactura();
                if (facturaToUpdate == null) { // Si no tenía factura, crea una nueva
                    facturaToUpdate = Factura.builder().build();
                    existingPedido.setFactura(facturaToUpdate);
                }
                facturaToUpdate.setFechaFacturacion(f.getFechaFacturacion());
                facturaToUpdate.setMpPaymentId(f.getMpPaymentId());
                facturaToUpdate.setMpMerchantOrderId(f.getMpMerchantOrderId());
                facturaToUpdate.setMpPreferenceId(f.getMpPreferenceId());
                facturaToUpdate.setMpPaymentType(f.getMpPaymentType());
                facturaToUpdate.setFormaPago(FormaPago.valueOf(String.valueOf(f.getFormaPago())));
                facturaToUpdate.setTotalVenta(f.getTotalVenta());
            } else {
                existingPedido.setFactura(null); // Si el DTO no trae factura, la eliminamos
            }

            // Sincronizar Detalles del Pedido
            if (dto.getDetalles() != null) {
                existingPedido.getDetallesPedidos().clear(); // Limpia los detalles existentes
                for (DetallePedidoCreateDTO detalleDTO : dto.getDetalles()) {
                    DetallePedido detalle = new DetallePedido(); // Crea nuevo detalle (o busca si necesitas actualizar)
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
                    detalle.setPedido(existingPedido); // Establecer la relación inversa
                    existingPedido.getDetallesPedidos().add(detalle);


                }

            } else {
                existingPedido.getDetallesPedidos().clear(); // Si no se envían detalles, limpiar los existentes
            }

            System.out.println("CONTROLADOR: Estado en DTO -> " + dto.getEstado());
            Pedido updated = baseService.update(id, existingPedido); // Llama al update del padre con la entidad EXISTENTE
            return ResponseEntity.ok(pedidoMapper.toDTO(updated)); // Convierte a DTO para la respuesta
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    @GetMapping("/{id}/factura-pdf") // Nuevo endpoint para obtener la URL del PDF
    public ResponseEntity<?> getFacturaPdfUrl(@PathVariable Long id) {
        try {
            Pedido pedido = baseService.findById(id); // Obtener el pedido usando el servicio base

            if (pedido == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"error\": \"Pedido no encontrado.\"}");
            }
            if (pedido.getFactura() == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"error\": \"Factura no asociada al pedido.\"}");
            }
            String urlPdf = pedido.getFactura().getUrlPdf();
            if (urlPdf == null || urlPdf.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"error\": \"URL del PDF de la factura no disponible.\"}");
            }

            // Devolver la URL del PDF
            // El frontend redirigirá a esta URL o la usará para un enlace de descarga
            return ResponseEntity.ok(urlPdf);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("{\"error\": \"Error al obtener la URL del PDF de la factura: " + e.getMessage() + "\"}");
        }
    }

    @PatchMapping("/{pedidoId}/anular") // Endpoint para anular factura y generar NC
    public ResponseEntity<?> anularFactura(@PathVariable Long pedidoId, @RequestBody AnulacionRequestDTO anulacionRequest) {
        try {
            // Obtener el usuario que realiza la anulación
            Usuario usuarioAnulador = usuarioService.findById(anulacionRequest.getUsuarioAnuladorId());
            if (usuarioAnulador == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"error\": \"Usuario anulador no encontrado.\"}");
            }

            // Llamar al servicio para realizar la anulación
            NotaCredito notaCreditoGenerada = pedidoService.anularFacturaYGenerarNotaCredito(
                    pedidoId,
                    anulacionRequest.getMotivoAnulacion(),
                    usuarioAnulador
            );

            // Devolver la Nota de Crédito generada como DTO
            return ResponseEntity.status(HttpStatus.CREATED).body(notaCreditoMapper.toDTO(notaCreditoGenerada));

        } catch (ResourceNotFoundException e) { // Capturar si el pedido/factura no se encuentra
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"error\": \"" + e.getMessage() + "\"}");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("{\"error\": \"Error al anular factura y generar nota de crédito: " + e.getMessage() + "\"}");
        }
    }
    @GetMapping("/cocinero")
    public ResponseEntity<?> getPedidosCocina() {
        try {
            // Filtrar ambos estados
            List<Pedido> pedidos = pedidoService.findPedidosByEstados(Arrays.asList(Estado.EN_PREPARACION, Estado.EN_COCINA));
            List<PedidoDTO> dtos = pedidos.stream()
                    .map(pedidoMapper::toDTO)
                    .toList();
            return ResponseEntity.ok(dtos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"error\": \"Error al obtener los pedidos en cocina/preparacion: " + e.getMessage() + "\"}");
        }
    }
    @PreAuthorize("hasRole('CAJERO')")
    @GetMapping("/cajero")
    public ResponseEntity<?> getPedidosParaCobrar() {
        try {
            List<Pedido> pedidos = pedidoService.findPedidosByEstados(Arrays.asList(Estado.LISTO, Estado.ENTREGADO));
            List<PedidoDTO> dtos = pedidos.stream()
                    .map(pedidoMapper::toDTO)
                    .toList();
            return ResponseEntity.ok(dtos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"error\": \"Error al obtener pedidos para cajero: " + e.getMessage() + "\"}");
        }
    }


    @PatchMapping("/{id}/estado")
    public ResponseEntity<?> actualizarEstado(@PathVariable Long id, @RequestBody Map<String, String> request) {
        try {
            String nuevoEstado = request.get("estado");
            if (nuevoEstado == null) {
                return ResponseEntity.badRequest().body("{\"error\": \"El estado es requerido\"}");
            }
            Pedido pedido = pedidoService.findById(id);
            pedido.setEstado(Estado.valueOf(nuevoEstado));
            Pedido actualizado = pedidoService.save(pedido);
            return ResponseEntity.ok(pedidoMapper.toDTO(actualizado));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    @PatchMapping("/{id}/hora-estimada")
    public ResponseEntity<?> actualizarHoraEstimada(@PathVariable Long id, @RequestBody Map<String, String> body) {
        try {
            Pedido pedido = pedidoService.findById(id);
            String nuevaHora = body.get("horaEstimadaFinalizacion");
            if (nuevaHora != null) {
                pedido.setHoraEstimadaFinalizacion(LocalTime.parse(nuevaHora));
                pedidoService.save(pedido);
                return ResponseEntity.ok().build();
            }
            return ResponseEntity.badRequest().body("Falta horaEstimadaFinalizacion");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
    @GetMapping("/ranking")
    public List<ProductoRankingDTO> obtenerRanking(@RequestParam LocalDate desde, @RequestParam LocalDate hasta) {
        return pedidoService.obtenerRankingProductosMasVendidos(desde, hasta);
    }

    //-------------------------------RANKING PEDIDOS-CLIENTES---------------------------
    @GetMapping("/reporte/clientes")
    public ResponseEntity<List<ClienteReporteDTO>> obtenerReporteClientes(
            @RequestParam("desde") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam("hasta") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta,
            @RequestParam("orden") String orden // "cantidad" o "importe"
    ) {
        List<ClienteReporteDTO> reporte = pedidoService.obtenerReporteClientes(desde, hasta, orden);
        return ResponseEntity.ok(reporte);
    }

    // GET /api/pedidos/estado/{estado}
    @GetMapping("/estado/{estado}")
    public ResponseEntity<?> getPedidosByEstado(@PathVariable String estado) {
        try {
            Estado estadoEnum = Estado.valueOf(estado); // Convierte string a Enum (A_CONFIRMAR, LISTO, etc.)
            List<Pedido> pedidos = pedidoService.findPedidosByEstados(Collections.singletonList(estadoEnum));
            List<PedidoDTO> dtos = pedidos.stream()
                    .map(pedidoMapper::toDTO)
                    .toList();
            return ResponseEntity.ok(dtos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("{\"error\": \"Estado inválido o error al filtrar pedidos: " + e.getMessage() + "\"}");
        }
    }

}
