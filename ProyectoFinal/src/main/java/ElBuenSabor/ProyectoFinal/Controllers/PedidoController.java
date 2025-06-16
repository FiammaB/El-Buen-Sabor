package ElBuenSabor.ProyectoFinal.Controllers;

import ElBuenSabor.ProyectoFinal.DTO.DetallePedidoCreateDTO;
import ElBuenSabor.ProyectoFinal.Repositories.ArticuloInsumoRepository;
import ElBuenSabor.ProyectoFinal.Repositories.ArticuloManufacturadoRepository;
import ElBuenSabor.ProyectoFinal.DTO.FacturaCreateDTO;
import ElBuenSabor.ProyectoFinal.DTO.PedidoCreateDTO;
import ElBuenSabor.ProyectoFinal.DTO.PedidoDTO;
import ElBuenSabor.ProyectoFinal.Entities.*;
import ElBuenSabor.ProyectoFinal.Entities.FormaPago;
import ElBuenSabor.ProyectoFinal.Exceptions.ResourceNotFoundException;
import ElBuenSabor.ProyectoFinal.Mappers.PedidoMapper;
import ElBuenSabor.ProyectoFinal.Repositories.*;
import ElBuenSabor.ProyectoFinal.Service.PedidoService; // Usar la interfaz específica
// Ya no es necesario si se inyecta por constructor explícito al padre
// import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/pedidos") // Define la URL base para este controlador
@CrossOrigin(origins = "http://localhost:5173") // Mantén CrossOrigin si es necesario
// PedidoController ahora extiende BaseController
public class PedidoController extends BaseController<Pedido, Long> {

    private final PedidoMapper pedidoMapper;

    // Repositorios necesarios para resolver relaciones en el controlador
    private final ArticuloInsumoRepository articuloInsumoRepository;
    private final ArticuloManufacturadoRepository articuloManufacturadoRepository;
    private final ClienteRepository clienteRepository;
    private final DomicilioRepository domicilioRepository;
    private final SucursalRepository sucursalRepository;
    private final UsuarioRepository usuarioRepository;
    private final ArticuloRepository articuloRepository; // Aunque no se usa en el create/update, se mantiene si es una dependencia general.

    // El constructor inyecta el servicio específico de Pedido y todas las dependencias adicionales
    public PedidoController(
            PedidoService pedidoService, // Servicio específico
            PedidoMapper pedidoMapper,
            ArticuloInsumoRepository articuloInsumoRepository,
            ArticuloManufacturadoRepository articuloManufacturadoRepository,
            ClienteRepository clienteRepository,
            DomicilioRepository domicilioRepository,
            SucursalRepository sucursalRepository,
            UsuarioRepository usuarioRepository,
            ArticuloRepository articuloRepository) {
        super(pedidoService); // Pasa el servicio al constructor del BaseController
        this.pedidoMapper = pedidoMapper;
        this.articuloInsumoRepository = articuloInsumoRepository;
        this.articuloManufacturadoRepository = articuloManufacturadoRepository;
        this.clienteRepository = clienteRepository;
        this.domicilioRepository = domicilioRepository;
        this.sucursalRepository = sucursalRepository;
        this.usuarioRepository = usuarioRepository;
        this.articuloRepository = articuloRepository;
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
    // @Override // <<--- Quitar @Override aquí, ya que la firma del método es diferente (recibe DTO)
    public ResponseEntity<?> create(@RequestBody PedidoCreateDTO dto) {
        try {
            Pedido pedido = pedidoMapper.toEntity(dto); // Mapea el DTO a la entidad Pedido

            // 🧩 Asignación de relaciones obligatorias (resolviendo por ID)
            pedido.setCliente(clienteRepository.findById(dto.getClienteId())
                    .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado")));

            pedido.setDomicilioEntrega(domicilioRepository.findById(dto.getDomicilioId())
                    .orElseThrow(() -> new ResourceNotFoundException("Domicilio no encontrado")));

            // 🧩 Relaciones opcionales (resolviendo por ID)
            if (dto.getSucursalId() != null) {
                pedido.setSucursal(sucursalRepository.findById(dto.getSucursalId()).orElse(null));
            }

            if (dto.getEmpleadoId() != null) {
                pedido.setEmpleado(usuarioRepository.findById(dto.getEmpleadoId()).orElse(null));
            }

            // 🧾 Factura (si viene en el DTO de creación)
            if (dto.getFactura() != null) {
                FacturaCreateDTO f = dto.getFactura();
                Factura factura = Factura.builder()
                        .fechaFacturacion(f.getFechaFacturacion())
                        .mpPaymentId(f.getMpPaymentId())
                        .mpMerchantOrderId(f.getMpMerchantOrderId())
                        .mpPreferenceId(f.getMpPreferenceId())
                        .mpPaymentType(f.getMpPaymentType())
                        .formaPago(FormaPago.valueOf(String.valueOf(f.getFormaPago())))
                        .totalVenta(f.getTotalVenta())
                        .build();
                pedido.setFactura(factura);
            }

            // 🧾 Detalles del pedido (colección)
            if (dto.getDetalles() != null) {
                Set<DetallePedido> detalles = dto.getDetalles().stream().map(detalleDTO -> {
                    DetallePedido detalle = new DetallePedido();
                    detalle.setCantidad(detalleDTO.getCantidad());
                    detalle.setSubTotal(detalleDTO.getSubTotal());

                    // Resolver el tipo de artículo según a cuál repositorio pertenezca
                    ArticuloInsumo insumo = articuloInsumoRepository.findById(detalleDTO.getArticuloId()).orElse(null);
                    if (insumo != null) {
                        detalle.setArticuloInsumo(insumo);
                    } else {
                        ArticuloManufacturado manufacturado = articuloManufacturadoRepository.findById(detalleDTO.getArticuloId())
                                .orElseThrow(() -> new ResourceNotFoundException("Artículo no encontrado"));
                        detalle.setArticuloManufacturado(manufacturado);
                    }

                    detalle.setPedido(pedido); // Establecer la relación inversa
                    return detalle;
                }).collect(Collectors.toSet());
                pedido.setDetallesPedidos(detalles);
            }
            pedido.setBaja(false); // Por defecto, un nuevo pedido no está dado de baja

            Pedido saved = baseService.save(pedido); // Llama al save del padre
            return ResponseEntity.status(HttpStatus.CREATED).body(pedidoMapper.toDTO(saved)); // Convierte a DTO para la respuesta
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"error\": \"" + e.getMessage() + "\"}");
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
            existingPedido.setEstado(Estado.valueOf(dto.getEstado())); // Convertir String a Enum
            existingPedido.setTipoEnvio(TipoEnvio.valueOf(dto.getTipoEnvio())); // Convertir String a Enum
            existingPedido.setFormaPago(FormaPago.valueOf(dto.getFormaPago())); // Convertir String a Enum
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


            Pedido updated = baseService.update(id, existingPedido); // Llama al update del padre con la entidad EXISTENTE
            return ResponseEntity.ok(pedidoMapper.toDTO(updated)); // Convierte a DTO para la respuesta
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

}