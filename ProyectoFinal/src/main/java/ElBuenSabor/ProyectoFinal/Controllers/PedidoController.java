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
import com.itextpdf.io.source.ByteArrayOutputStream;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize; // Importa si usas seguridad Spring
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/pedidos")
@CrossOrigin(origins = "http://localhost:5173") // Mantén si tu frontend está en 5173
public class PedidoController extends BaseController<Pedido, Long> {

    private final PedidoMapper pedidoMapper;
    private final PedidoService pedidoService; // Inyectado desde el constructor del padre y aquí
    private final ArticuloInsumoRepository articuloInsumoRepository; // Inyectado para validación/carga
    private final ArticuloManufacturadoRepository articuloManufacturadoRepository; // Inyectado para validación/carga
    private final PromocionRepository promocionRepository; // ✨ INYECTA ESTO
    private final UsuarioService usuarioService; // Para buscar el usuario anulador
    private final NotaCreditoMapper notaCreditoMapper;
    private final MPController mpController;
    private final PersonaService personaService;
    private final DomicilioService domicilioService;
    private final SucursalService sucursalService;
    private final FacturaService facturaService; // Para descarga de PDF

    // Constructor actualizado con todas las inyecciones
    public PedidoController(
            PedidoService pedidoService,
            PedidoMapper pedidoMapper,
            ArticuloInsumoRepository articuloInsumoRepository,
            ArticuloManufacturadoRepository articuloManufacturadoRepository,
            PersonaRepository personaRepository, // Aunque no se use directamente en este controller, si es parte del constructor BaseController lo necesita
            DomicilioRepository domicilioRepository, // Similar al anterior
            SucursalRepository sucursalRepository, // Similar al anterior
            UsuarioRepository usuarioRepository, // Similar al anterior
            ArticuloRepository articuloRepository, // Similar al anterior
            UsuarioService usuarioService,
            NotaCreditoMapper notaCreditoMapper,
            MPController mpController,
            PersonaService personaService,
            DomicilioService domicilioService,
            SucursalService sucursalService,
            PromocionRepository promocionRepository, // ✨ Asegúrate de que está aquí
            FacturaService facturaService) {
        super(pedidoService); // Pasa el servicio al constructor del BaseController
        this.pedidoMapper = pedidoMapper;
        this.pedidoService = pedidoService; // Asignación aquí también
        this.articuloInsumoRepository = articuloInsumoRepository;
        this.articuloManufacturadoRepository = articuloManufacturadoRepository;
        // Mantener para el super
        this.usuarioService = usuarioService;
        this.notaCreditoMapper = notaCreditoMapper;
        this.mpController = mpController;
        this.personaService = personaService;
        this.domicilioService = domicilioService;
        this.sucursalService = sucursalService;
        this.facturaService = facturaService;
        this.promocionRepository = promocionRepository; // Asignación de la nueva inyección
    }

    // --- MÉTODOS EXISTENTES (sin cambios funcionales, solo limpieza o refactor) ---

    @GetMapping
    @Override
    public ResponseEntity<?> getAll() {
        try {
            List<Pedido> pedidos = baseService.findAll();
            List<PedidoDTO> dtos = pedidos.stream()
                    .map(pedidoMapper::toDTO)
                    .toList();
            return ResponseEntity.ok(dtos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    @GetMapping("/reporte/monetario-diario")
    public ResponseEntity<List<ReporteMonetarioDiarioDTO>> obtenerReporteMonetarioDiario(
            @RequestParam("desde") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam("hasta") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta) {
        List<ReporteMonetarioDiarioDTO> reporte = pedidoService.obtenerReporteMonetarioDiario(desde, hasta);
        return ResponseEntity.ok(reporte);
    }

    @GetMapping("/{id}")
    @Override
    public ResponseEntity<?> getOne(@PathVariable Long id) {
        try {
            Pedido pedido = baseService.findById(id);
            return ResponseEntity.ok(pedidoMapper.toDTO(pedido));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    // --- MÉTODO CREATE (CON LA NUEVA LÓGICA DE VALIDACIÓN DE STOCK AL INICIO) ---

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE) // Especifica el tipo de consumo
    public ResponseEntity<?> create(@RequestBody PedidoCreateDTO dto) {
        try {
            Pedido pedidoParaPersistir = new Pedido();

            // 1. Asignación de relaciones obligatorias y opcionales
            pedidoParaPersistir.setPersona(personaService.findById(dto.getPersonaId()));
            if (dto.getDomicilioId() != null) {
                pedidoParaPersistir.setDomicilioEntrega(domicilioService.findById(dto.getDomicilioId()));
            }
            if (dto.getSucursalId() != null) {
                pedidoParaPersistir.setSucursal(sucursalService.findById(dto.getSucursalId()));
            }
            if (dto.getEmpleadoId() != null) {
                pedidoParaPersistir.setEmpleado(usuarioService.findById(dto.getEmpleadoId()));
            }

            // 2. Construcción de detalles del pedido y cálculo de sub-totales
            double totalCalculadoPedido = 0.0;
            if (dto.getDetalles() == null || dto.getDetalles().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("message", "El pedido debe contener al menos un detalle."));
            }

            Set<DetallePedido> detalles = new HashSet<>();
            for (DetallePedidoCreateDTO detalleDTO : dto.getDetalles()) {
                DetallePedido detalle = new DetallePedido();
                detalle.setCantidad(detalleDTO.getCantidad());
                double subTotalDetalle = 0.0;

                if (detalleDTO.getArticuloId() != null) {
                    ArticuloInsumo insumo = articuloInsumoRepository.findById(detalleDTO.getArticuloId()).orElse(null);
                    if (insumo != null) {
                        detalle.setArticuloInsumo(insumo);
                        subTotalDetalle = insumo.getPrecioVenta() * detalle.getCantidad();
                    } else {
                        ArticuloManufacturado manufacturado = articuloManufacturadoRepository.findById(detalleDTO.getArticuloId())
                                .orElseThrow(() -> new ResourceNotFoundException("Artículo con ID " + detalleDTO.getArticuloId() + " no encontrado."));
                        detalle.setArticuloManufacturado(manufacturado);
                        subTotalDetalle = manufacturado.getPrecioVenta() * detalle.getCantidad();
                    }
                } else if (detalleDTO.getPromocionId() != null) {
                    Promocion promo = promocionRepository.findById(detalleDTO.getPromocionId())
                            .orElseThrow(() -> new ResourceNotFoundException("Promoción con ID " + detalleDTO.getPromocionId() + " no encontrada."));
                    detalle.setPromocion(promo);
                    subTotalDetalle = promo.getPrecioPromocional() * detalle.getCantidad();
                } else {
                    return ResponseEntity.badRequest().body(Map.of("message", "Cada detalle del pedido debe tener un articuloId o un promocionId."));
                }

                detalle.setSubTotal(subTotalDetalle);
                totalCalculadoPedido += subTotalDetalle;
                detalle.setPedido(pedidoParaPersistir);
                detalles.add(detalle);
            }
            pedidoParaPersistir.setDetallesPedidos(detalles);

            // 3. Aplicar descuento por Retiro en Local
            if (dto.getTipoEnvio() == TipoEnvio.RETIRO_EN_LOCAL) {
                double descuento = totalCalculadoPedido * 0.10;
                totalCalculadoPedido -= descuento;
            }
            pedidoParaPersistir.setTotal(totalCalculadoPedido);

            // 4. Calcular y asignar el costo total del pedido y la hora estimada
            pedidoParaPersistir.setTotalCosto(pedidoService.calcularTotalCostoPedido(pedidoParaPersistir));
            pedidoParaPersistir.setHoraEstimadaFinalizacion(pedidoService.calcularTiempoEstimadoFinalizacion(pedidoParaPersistir));

            // 5. Asignar el resto de propiedades básicas del DTO
            pedidoParaPersistir.setFechaPedido(LocalDate.now());
            pedidoParaPersistir.setEstado(Estado.A_CONFIRMAR);
            pedidoParaPersistir.setBaja(false);
            pedidoParaPersistir.setFormaPago(dto.getFormaPago());
            pedidoParaPersistir.setTipoEnvio(dto.getTipoEnvio());

            // 6. ✨ PUNTO CLAVE: VERIFICACIÓN DE STOCK ANTES DE CUALQUIER PAGO O PERSISTENCIA
            List<String> erroresStock = pedidoService.verificarStockParaPedido(pedidoParaPersistir);
            if (!erroresStock.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("message", erroresStock)); // Devuelve el array de mensajes de error
            }

            // 7. LÓGICA DE DECISIÓN POR FORMA DE PAGO (solo si el stock es válido)
            if (dto.getFormaPago() == FormaPago.MERCADO_PAGO) {
                dto.setTotal(pedidoParaPersistir.getTotal());
                return mpController.crearPreferencia(dto); // Delega al MPController
            } else if (dto.getFormaPago() == FormaPago.EFECTIVO) {
                Factura factura = Factura.builder()
                        .fechaFacturacion(LocalDate.now())
                        .formaPago(FormaPago.EFECTIVO)
                        .totalVenta(pedidoParaPersistir.getTotal())
                        .build();
                pedidoParaPersistir.setFactura(factura);

                Pedido saved = pedidoService.save(pedidoParaPersistir); // Guarda el pedido completo
                return ResponseEntity.status(HttpStatus.CREATED).body(pedidoMapper.toDTO(saved));
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "Forma de pago no válida."));
            }

        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "Error al crear el pedido: " + e.getMessage()));
        }
    }


    // --- MÉTODO UPDATE ---
    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody PedidoCreateDTO dto) {
        try {
            Pedido existingPedido = baseService.findById(id);

            // Actualiza propiedades básicas
            existingPedido.setFechaPedido(dto.getFechaPedido() != null ? dto.getFechaPedido() : existingPedido.getFechaPedido());
            // Para la hora estimada, si no viene en el DTO, no la actualizamos
            // Si el DTO.getEstado() es un String y necesitas convertirlo a Enum Estado:
            if (dto.getEstado() != null) {
                existingPedido.setEstado(Estado.valueOf(dto.getEstado()));
            }
            existingPedido.setTipoEnvio(dto.getTipoEnvio());
            existingPedido.setFormaPago(dto.getFormaPago());
            existingPedido.setTotal(dto.getTotal());

            // Actualizar relaciones (asegurando manejo de nulls si es opcional)
            existingPedido.setPersona(personaService.findById(dto.getPersonaId()));
            existingPedido.setDomicilioEntrega(domicilioService.findById(dto.getDomicilioId()));

            existingPedido.setSucursal(dto.getSucursalId() != null ? sucursalService.findById(dto.getSucursalId()) : null);
            existingPedido.setEmpleado(dto.getEmpleadoId() != null ? usuarioService.findById(dto.getEmpleadoId()) : null);

            // Actualizar Factura (si se proporciona en el DTO)
            if (dto.getFactura() != null) {
                FacturaCreateDTO f = dto.getFactura();
                Factura facturaToUpdate = existingPedido.getFactura();
                if (facturaToUpdate == null) {
                    facturaToUpdate = new Factura();
                    existingPedido.setFactura(facturaToUpdate);
                }
                facturaToUpdate.setFechaFacturacion(f.getFechaFacturacion());
                facturaToUpdate.setMpPaymentId(f.getMpPaymentId());
                facturaToUpdate.setMpMerchantOrderId(f.getMpMerchantOrderId());
                facturaToUpdate.setMpPreferenceId(f.getMpPreferenceId());
                facturaToUpdate.setMpPaymentType(f.getMpPaymentType());
                facturaToUpdate.setFormaPago(f.getFormaPago()); // Ya es FormaPago, no necesita valueOf
                facturaToUpdate.setTotalVenta(f.getTotalVenta());
            } else {
                existingPedido.setFactura(null);
            }

            // Sincronizar Detalles del Pedido
            // Considera si quieres que el update borre y recree o actualice existentes
            if (dto.getDetalles() != null) {
                existingPedido.getDetallesPedidos().clear(); // Limpia los detalles existentes
                for (DetallePedidoCreateDTO detalleDTO : dto.getDetalles()) {
                    DetallePedido detalle = new DetallePedido();
                    detalle.setCantidad(detalleDTO.getCantidad());
                    detalle.setSubTotal(detalleDTO.getSubTotal()); // Asume que subTotal viene en DTO para update

                    // Resolver el tipo de artículo o promoción
                    if (detalleDTO.getArticuloId() != null) {
                        ArticuloInsumo insumo = articuloInsumoRepository.findById(detalleDTO.getArticuloId()).orElse(null);
                        if (insumo != null) {
                            detalle.setArticuloInsumo(insumo);
                        } else {
                            ArticuloManufacturado manufacturado = articuloManufacturadoRepository.findById(detalleDTO.getArticuloId())
                                    .orElseThrow(() -> new ResourceNotFoundException("Artículo manufacturado con ID " + detalleDTO.getArticuloId() + " no encontrado."));
                            detalle.setArticuloManufacturado(manufacturado);
                        }
                    } else if (detalleDTO.getPromocionId() != null) {
                        Promocion promo = promocionRepository.findById(detalleDTO.getPromocionId())
                                .orElseThrow(() -> new ResourceNotFoundException("Promoción con ID " + detalleDTO.getPromocionId() + " no encontrada."));
                        detalle.setPromocion(promo);
                    } else {
                        throw new IllegalArgumentException("El detalle del pedido debe tener articuloId o promocionId.");
                    }
                    detalle.setPedido(existingPedido);
                    existingPedido.getDetallesPedidos().add(detalle);
                }
            } else {
                existingPedido.getDetallesPedidos().clear();
            }

            // Antes de guardar, podrías volver a calcular totales o validaciones
            existingPedido.setTotalCosto(pedidoService.calcularTotalCostoPedido(existingPedido));
            // Recalcular hora estimada si el estado o detalles cambian y afecta el tiempo
            existingPedido.setHoraEstimadaFinalizacion(pedidoService.calcularTiempoEstimadoFinalizacion(existingPedido));


            Pedido updated = baseService.update(id, existingPedido);
            return ResponseEntity.ok(pedidoMapper.toDTO(updated));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "Error al actualizar el pedido: " + e.getMessage()));
        }
    }


    // --- OTROS ENDPOINTS (Mantenidos sin cambios significativos) ---

    @GetMapping("/{id}/factura-pdf")
    public ResponseEntity<?> getFacturaPdfUrl(@PathVariable Long id) {
        try {
            Pedido pedido = baseService.findById(id);

            if (pedido == null || pedido.getFactura() == null || pedido.getFactura().getUrlPdf() == null || pedido.getFactura().getUrlPdf().isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "Factura o URL del PDF no disponible para el pedido con ID: " + id));
            }

            return ResponseEntity.ok(pedido.getFactura().getUrlPdf());

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "Error al obtener la URL del PDF de la factura: " + e.getMessage()));
        }
    }

    @PatchMapping("/{pedidoId}/anular")
    public ResponseEntity<?> anularFactura(@PathVariable Long pedidoId, @RequestBody AnulacionRequestDTO anulacionRequest) {
        try {
            Usuario usuarioAnulador = usuarioService.findById(anulacionRequest.getUsuarioAnuladorId());
            if (usuarioAnulador == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "Usuario anulador no encontrado."));
            }

            NotaCredito notaCreditoGenerada = pedidoService.anularFacturaYGenerarNotaCredito(
                    pedidoId,
                    anulacionRequest.getMotivoAnulacion(),
                    usuarioAnulador
            );

            return ResponseEntity.status(HttpStatus.CREATED).body(notaCreditoMapper.toDTO(notaCreditoGenerada));

        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "Error al anular factura y generar nota de crédito: " + e.getMessage()));
        }
    }

    @GetMapping("/cocinero")
    public ResponseEntity<?> getPedidosCocina() {
        try {
            List<Pedido> pedidos = pedidoService.findPedidosByEstados(Arrays.asList(Estado.EN_PREPARACION, Estado.EN_COCINA));
            List<PedidoDTO> dtos = pedidos.stream()
                    .map(pedidoMapper::toDTO)
                    .toList();
            return ResponseEntity.ok(dtos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Error al obtener los pedidos en cocina/preparacion: " + e.getMessage()));
        }
    }

    @PreAuthorize("hasRole('CAJERO')") // Mantenido si usas seguridad
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
                    .body(Map.of("message", "Error al obtener pedidos para cajero: " + e.getMessage()));
        }
    }


    @PatchMapping("/{id}/estado")
    public ResponseEntity<?> actualizarEstado(@PathVariable Long id, @RequestBody Map<String, String> request) {
        try {
            String nuevoEstadoStr = request.get("estado");
            if (nuevoEstadoStr == null) {
                return ResponseEntity.badRequest().body(Map.of("message", "El estado es requerido."));
            }
            Estado nuevoEstado = Estado.valueOf(nuevoEstadoStr); // Convertir String a Enum

            Pedido pedido = pedidoService.findById(id); // Obtener el pedido siempre

            if (nuevoEstado == Estado.PAGADO && pedido.getFormaPago() == FormaPago.EFECTIVO) {
                // Si el nuevo estado es PAGADO y la forma de pago es EFECTIVO, llama al método específico
                Pedido actualizado = pedidoService.marcarPedidoComoPagadoYFacturar(id);
                return ResponseEntity.ok(pedidoMapper.toDTO(actualizado));
            } else {
                // Para cualquier otro cambio de estado
                pedido.setEstado(nuevoEstado);
                Pedido actualizado = pedidoService.save(pedido);
                return ResponseEntity.ok(pedidoMapper.toDTO(actualizado));
            }
        } catch (IllegalArgumentException e) { // Captura si el estado no es válido
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "Estado '" + request.get("estado") + "' no válido."));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", "Error al actualizar el estado del pedido: " + e.getMessage()));
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
            return ResponseEntity.badRequest().body(Map.of("message", "Falta horaEstimadaFinalizacion."));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", e.getMessage()));
        }
    }

    @GetMapping("/ranking")
    public List<ProductoRankingDTO> obtenerRanking(@RequestParam LocalDate desde, @RequestParam LocalDate hasta) {
        return pedidoService.obtenerRankingProductosMasVendidos(desde, hasta);
    }

    @GetMapping("/reporte/clientes")
    public ResponseEntity<List<PersonaReporteDTO>> obtenerReporteClientes(
            @RequestParam("desde") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam("hasta") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta,
            @RequestParam("orden") String orden
    ) {
        List<PersonaReporteDTO> reporte = pedidoService.obtenerReporteClientes(desde, hasta, orden);
        return ResponseEntity.ok(reporte);
    }

    @GetMapping("/estado/{estado}")
    public ResponseEntity<?> getPedidosByEstado(@PathVariable String estado) {
        try {
            Estado estadoEnum = Estado.valueOf(estado);
            List<Pedido> pedidos = pedidoService.findPedidosByEstados(Collections.singletonList(estadoEnum));
            List<PedidoDTO> dtos = pedidos.stream()
                    .map(pedidoMapper::toDTO)
                    .toList();
            return ResponseEntity.ok(dtos);
        } catch (IllegalArgumentException e) { // Captura para Enum.valueOf
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", "Estado '" + estado + "' no válido."));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Error al filtrar pedidos por estado: " + e.getMessage()));
        }
    }

    @GetMapping("/persona/{personaId}")
    public ResponseEntity<?> getPedidosByClienteId(@PathVariable Long personaId) {
        try {
            Persona persona = personaService.findById(personaId);
            if (persona == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "Persona no encontrado con ID: " + personaId));
            }

            List<Pedido> pedidos = pedidoService.findPedidosByClienteId(personaId);
            List<PedidoDTO> dtos = pedidos.stream()
                    .map(pedidoMapper::toDTO)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(dtos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "Error al obtener el historial de pedidos: " + e.getMessage()));
        }
    }

    @GetMapping(value = "/{id}/descargar-factura", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> downloadFacturaPdf(@PathVariable Long id) {
        try {
            Pedido pedido = pedidoService.findById(id);

            if (pedido == null || pedido.getFactura() == null || pedido.getFactura().getUrlPdf() == null || pedido.getFactura().getUrlPdf().isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new byte[0]); // Devuelve un array vacío para 404
            }

            ByteArrayOutputStream pdfStream = (ByteArrayOutputStream) facturaService.generarFacturaPdf(pedido);
            byte[] pdfBytes = pdfStream.toByteArray();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentDispositionFormData("attachment", "factura_pedido_" + id + ".pdf");
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentLength(pdfBytes.length);

            return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);

        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new byte[0]);
        } catch (Exception e) {
            System.err.println("Error al servir el PDF de la factura para el pedido " + id + ": " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new byte[0]);
        }
    }

    // ✨ NUEVO ENDPOINT PARA VALIDAR STOCK
    @PostMapping("/validar-stock")
    public ResponseEntity<?> validarStock(@RequestBody PedidoStockValidationRequest request) {
        try {
            Pedido dummyPedido = new Pedido();
            Set<DetallePedido> dummyDetalles = new HashSet<>();

            if (request.getDetalles() == null || request.getDetalles().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("message", List.of("El pedido no contiene detalles para validar.")));
            }

            for (DetallePedidoCreateDTO detalleDTO : request.getDetalles()) {
                System.out.println("DEBUG: Procesando detalleDTO: " + detalleDTO); // Log el DTO recibido
                DetallePedido dp = new DetallePedido();
                dp.setCantidad(detalleDTO.getCantidad());

                if (detalleDTO.getArticuloId() != null) {
                    System.out.println("DEBUG: Buscando Articulo con ID: " + detalleDTO.getArticuloId());
                    ArticuloInsumo ai = articuloInsumoRepository.findById(detalleDTO.getArticuloId()).orElse(null);
                    if (ai != null) {
                        dp.setArticuloInsumo(ai);
                        System.out.println("DEBUG: Encontrado ArticuloInsumo: " + ai.getDenominacion());
                    } else {
                        ArticuloManufacturado am = articuloManufacturadoRepository.findById(detalleDTO.getArticuloId()).orElse(null);
                        if (am != null) {
                            dp.setArticuloManufacturado(am);
                            System.out.println("DEBUG: Encontrado ArticuloManufacturado: " + am.getDenominacion());
                        } else {
                            System.out.println("ERROR: Articulo con ID " + detalleDTO.getArticuloId() + " no encontrado como Insumo ni Manufacturado.");
                            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", List.of("Artículo con ID " + detalleDTO.getArticuloId() + " no encontrado o tipo desconocido.")));
                        }
                    }
                } else if (detalleDTO.getPromocionId() != null) {
                    System.out.println("DEBUG: Buscando Promocion con ID: " + detalleDTO.getPromocionId());
                    Promocion promo = promocionRepository.findById(detalleDTO.getPromocionId()).orElse(null);
                    if (promo != null) {
                        dp.setPromocion(promo);
                        System.out.println("DEBUG: Encontrada Promoción: " + promo.getDenominacion());
                    } else {
                        System.out.println("ERROR: Promoción con ID " + detalleDTO.getPromocionId() + " no encontrada.");
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", List.of("Promoción con ID " + detalleDTO.getPromocionId() + " no encontrada.")));
                    }
                } else {
                    System.out.println("ERROR: Detalle sin articuloId ni promocionId.");
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", List.of("Cada detalle debe tener un 'articuloId' o 'promocionId'.")));
                }
                dummyDetalles.add(dp);
            }
            dummyPedido.setDetallesPedidos(dummyDetalles);

            List<String> errores = pedidoService.verificarStockParaPedido(dummyPedido);
            System.out.println("DEBUG: Errores de stock del servicio: " + errores); // Log los errores del servicio

            if (!errores.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", errores));
            } else {
                return ResponseEntity.ok(Map.of("message", "Stock disponible."));
            }
        } catch (ResourceNotFoundException e) {
            System.err.println("ERROR: ResourceNotFoundException en validarStock: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", List.of(e.getMessage())));
        } catch (Exception e) {
            System.err.println("ERROR: Excepción general en validarStock: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", List.of("Error interno al validar stock: " + e.getMessage())));
        }
    }

}