// Archivo: ElBuenSabor/ProyectoFinal/src/main/java/ElBuenSabor/ProyectoFinal/Controllers/ArticuloManufacturadoController.java
package ElBuenSabor.ProyectoFinal.Controllers;

import ElBuenSabor.ProyectoFinal.DTO.ArticuloManufacturadoCreateDTO;
import ElBuenSabor.ProyectoFinal.DTO.ArticuloManufacturadoDTO;
import ElBuenSabor.ProyectoFinal.Entities.ArticuloManufacturado;
import ElBuenSabor.ProyectoFinal.Entities.ArticuloManufacturadoDetalle;
import ElBuenSabor.ProyectoFinal.Entities.Sucursal;
import ElBuenSabor.ProyectoFinal.Exceptions.ResourceNotFoundException;
import ElBuenSabor.ProyectoFinal.Mappers.ArticuloManufacturadoMapper;
import ElBuenSabor.ProyectoFinal.Repositories.*;
import ElBuenSabor.ProyectoFinal.Service.ArticuloManufacturadoService;
import ElBuenSabor.ProyectoFinal.Service.SucursalService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/articuloManufacturado")
public class ArticuloManufacturadoController extends BaseController<ArticuloManufacturado, Long> {

    private final ArticuloManufacturadoMapper mapper;
    private final CategoriaRepository categoriaRepository;
    private final UnidadMedidaRepository unidadMedidaRepository;
    private final ImagenRepository imagenRepository;
    private final ArticuloInsumoRepository articuloInsumoRepository;
    private final ArticuloManufacturadoDetalleRepository articuloManufacturadoDetalleRepository;
    private final ArticuloManufacturadoService articuloManufacturadoService; // Declaración correcta
    private final SucursalService sucursalService; // Declaración correcta

    // El constructor inyecta los servicios y repositorios necesarios
    public ArticuloManufacturadoController(
            ArticuloManufacturadoService articuloManufacturadoService,
            ArticuloManufacturadoMapper mapper,
            CategoriaRepository categoriaRepository,
            UnidadMedidaRepository unidadMedidaRepository,
            ImagenRepository imagenRepository,
            ArticuloInsumoRepository articuloInsumoRepository,
            ArticuloManufacturadoDetalleRepository articuloManufacturadoDetalleRepository,
            SucursalService sucursalService) {
        super(articuloManufacturadoService); // Pasa el servicio al constructor del BaseController
        this.articuloManufacturadoService = articuloManufacturadoService; // Asignación correcta
        this.mapper = mapper;
        this.categoriaRepository = categoriaRepository;
        this.unidadMedidaRepository = unidadMedidaRepository;
        this.imagenRepository = imagenRepository;
        this.articuloInsumoRepository = articuloInsumoRepository;
        this.articuloManufacturadoDetalleRepository = articuloManufacturadoDetalleRepository;
        this.sucursalService = sucursalService;
    }

    // Nuevo endpoint para obtener todos los artículos manufacturados de una sucursal específica
    @GetMapping("/sucursal/{sucursalId}/manufacturados")
    public ResponseEntity<?> getAllBySucursal(@PathVariable Long sucursalId) {
        try {
            List<ArticuloManufacturado> articulos = articuloManufacturadoService.findAllBySucursalId(sucursalId); // Usar la instancia inyectada
            List<ArticuloManufacturadoDTO> dtos = articulos.stream()
                    .map(mapper::toDTO)
                    .toList();
            return ResponseEntity.ok(dtos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    // Sobrescribir getAll para que, por defecto, pida el sucursalId
    // Podrías decidir eliminar este o mantenerlo como una búsqueda global si la necesitas
    @GetMapping("/manufacturados")
    @Override
    public ResponseEntity<?> getAll() {
        // En un escenario de múltiples sucursales, rara vez querrías todos los artículos de todas las sucursales
        // sin un filtro. Considera eliminar este método o hacer que requiera un sucursalId.
        // Por ahora, lo mantenemos pero recomendamos usar el endpoint con sucursalId.
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"error\": \"Para obtener artículos manufacturados, debe especificar un ID de sucursal. Use /api/articuloManufacturado/sucursal/{sucursalId}/manufacturados\"}");
        // Alternativamente, si quieres mostrar todos los artículos de todas las sucursales (con precaución por rendimiento):
        // try {
        //     List<ArticuloManufacturado> articulos = articuloManufacturadoService.findAll(); // Usar la instancia inyectada
        //     List<ArticuloManufacturadoDTO> dtos = articulos.stream()
        //             .map(mapper::toDTO)
        //             .toList();
        //     return ResponseEntity.ok(dtos);
        // } catch (Exception e) {
        //     return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"error\": \"" + e.getMessage() + "\"}");
        // }
    }

    // Sobrescribir getOne para que también pueda validar por sucursal (opcional, pero buena práctica)
    // Para simplificar, mantenemos el comportamiento original, pero si necesitas asegurar que solo se accede
    // a un artículo si pertenece a una sucursal específica, deberías añadir un sucursalId aquí.
    @GetMapping("/{id}")
    @Override
    public ResponseEntity<?> getOne(@PathVariable Long id) {
        try {
            ArticuloManufacturado articulo = articuloManufacturadoService.findById(id); // Usar la instancia inyectada
            return ResponseEntity.ok(mapper.toDTO(articulo));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    // Modificado: create ahora requiere un sucursalId en la URL
    @PostMapping("/sucursal/{sucursalId}")
    public ResponseEntity<?> create(@RequestBody ArticuloManufacturadoCreateDTO dto, @PathVariable Long sucursalId) {
        try {
            // Obtener la entidad ArticuloManufacturado a partir del DTO
            ArticuloManufacturado entity = mapper.toEntity(dto, articuloInsumoRepository);

            // Cargar la entidad Sucursal y asignarla al artículo
            // Corregido: findById de BaseService no devuelve Optional, por lo que no se usa orElseThrow
            Sucursal sucursal = sucursalService.findById(sucursalId);
            if (sucursal == null) {
                throw new ResourceNotFoundException("Sucursal no encontrada con ID: " + sucursalId);
            }
            entity.setSucursal(sucursal);

            // Establecer las demás relaciones ManyToOne
            entity.setCategoria(categoriaRepository.findById(dto.getCategoriaId()).orElse(null));
            entity.setUnidadMedida(unidadMedidaRepository.findById(dto.getUnidadMedidaId()).orElse(null));
            entity.setImagen(imagenRepository.findById(dto.getImagenId()).orElse(null));
            entity.setBaja(false); // Por defecto, un nuevo artículo manufacturado está activo

            // Guardar la entidad
            ArticuloManufacturado saved = articuloManufacturadoService.save(entity); // Usar la instancia inyectada
            return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toDTO(saved));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"error\": \"Error al crear el artículo manufacturado: " + e.getMessage() + "\"}");
        }
    }

    // Modificado: update ahora requiere un sucursalId en la URL
    @PutMapping("/sucursal/{sucursalId}/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody ArticuloManufacturadoCreateDTO dto, @PathVariable Long sucursalId) {
        try {
            // Obtener la entidad existente
            ArticuloManufacturado existingEntity = articuloManufacturadoService.findById(id); // Usar la instancia inyectada

            // Validar que el artículo pertenece a la sucursal especificada en la URL
            if (!existingEntity.getSucursal().getId().equals(sucursalId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("{\"error\": \"El artículo no pertenece a la sucursal especificada.\"}");
            }

            // Mapear solo los campos necesarios del DTO, exceptuando la sucursal que ya se validó
            existingEntity.setDenominacion(dto.getDenominacion());
            existingEntity.setPrecioVenta(dto.getPrecioVenta());
            existingEntity.setDescripcion(dto.getDescripcion());
            existingEntity.setTiempoEstimadoMinutos(dto.getTiempoEstimadoMinutos());
            existingEntity.setPreparacion(dto.getPreparacion());

            // Actualizar relaciones ManyToOne
            existingEntity.setCategoria(categoriaRepository.findById(dto.getCategoriaId()).orElse(null));
            existingEntity.setUnidadMedida(unidadMedidaRepository.findById(dto.getUnidadMedidaId()).orElse(null));
            existingEntity.setImagen(imagenRepository.findById(dto.getImagenId()).orElse(null));

            // Manejar los detalles de manera específica para evitar duplicados
            if (dto.getDetalles() != null) {
                articuloManufacturadoDetalleRepository.deleteByArticuloManufacturadoId(existingEntity.getId());
                existingEntity.getDetalles().clear();

                List<ArticuloManufacturadoDetalle> nuevosDetalles = dto.getDetalles().stream()
                        .map(detalleDTO -> {
                            ArticuloManufacturadoDetalle detalle = new ArticuloManufacturadoDetalle();
                            detalle.setCantidad(detalleDTO.getCantidad());
                            detalle.setArticuloInsumo(
                                    articuloInsumoRepository.findById(detalleDTO.getArticuloInsumoId()).orElse(null)
                            );
                            detalle.setArticuloManufacturado(existingEntity);
                            return detalle;
                        })
                        .collect(Collectors.toList());
                existingEntity.getDetalles().addAll(nuevosDetalles);
            }

            ArticuloManufacturado updated = articuloManufacturadoService.update(id, existingEntity); // Usar update del servicio
            return ResponseEntity.ok(mapper.toDTO(updated));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"error\": \"Error al actualizar el artículo manufacturado: " + e.getMessage() + "\"}");
        }
    }

    // Modificado: toggleBaja ahora requiere un sucursalId en la URL
    @PatchMapping("/sucursal/{sucursalId}/{id}/baja")
    public ResponseEntity<?> toggleBaja(
            @PathVariable Long id,
            @PathVariable Long sucursalId,
            @RequestParam boolean baja
    ) {
        try {
            ArticuloManufacturado existingEntity = articuloManufacturadoService.findById(id); // Usar la instancia inyectada
            // Validar que el artículo pertenece a la sucursal especificada en la URL
            if (!existingEntity.getSucursal().getId().equals(sucursalId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("{\"error\": \"El artículo no pertenece a la sucursal especificada.\"}");
            }

            ArticuloManufacturado actualizado = articuloManufacturadoService.toggleBaja(id, baja); // Usar la instancia inyectada
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    // Modificado: Filtrar ahora requiere un sucursalId
    @GetMapping("/sucursal/{sucursalId}/filtrar")
    public ResponseEntity<?> filtrarArticulos(
            @PathVariable Long sucursalId,
            @RequestParam(required = false) Long categoriaId,
            @RequestParam(required = false) String denominacion,
            @RequestParam(required = false) Boolean baja
    ) {
        try {
            List<ArticuloManufacturado> articulos = articuloManufacturadoService
                    .filtrar(sucursalId, categoriaId, denominacion, baja);//

            List<ArticuloManufacturadoDTO> dtos = articulos.stream()
                    .map(mapper::toDTO)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(dtos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    // Los métodos DELETE, ACTIVATE, DEACTIVATE también deberían ser modificados si deseas
    // que respeten el contexto de la sucursal. Por ejemplo:
    @DeleteMapping("/sucursal/{sucursalId}/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id, @PathVariable Long sucursalId) {
        try {
            ArticuloManufacturado existingEntity = articuloManufacturadoService.findById(id); // Usar la instancia inyectada
            if (!existingEntity.getSucursal().getId().equals(sucursalId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("{\"error\": \"El artículo no pertenece a la sucursal especificada.\"}");
            }
            // Corregido: Usar deleteById
            articuloManufacturadoService.deleteById(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"error\": \"Error al eliminar el artículo: " + e.getMessage() + "\"}");
        }
    }

    @PatchMapping("/sucursal/{sucursalId}/{id}/activate")
    public ResponseEntity<?> activate(@PathVariable Long id, @PathVariable Long sucursalId) {
        try {
            ArticuloManufacturado existingEntity = articuloManufacturadoService.findById(id); // Usar la instancia inyectada
            if (!existingEntity.getSucursal().getId().equals(sucursalId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("{\"error\": \"El artículo no pertenece a la sucursal especificada.\"}");
            }
            // Corregido: Usar toggleBaja con true para activar
            articuloManufacturadoService.toggleBaja(id, false); // false = no está dado de baja, es decir, está activo
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"error\": \"Error al activar el artículo: " + e.getMessage() + "\"}");
        }
    }

    @PatchMapping("/sucursal/{sucursalId}/{id}/deactivate")
    public ResponseEntity<?> deactivate(@PathVariable Long id, @PathVariable Long sucursalId) {
        try {
            ArticuloManufacturado existingEntity = articuloManufacturadoService.findById(id); // Usar la instancia inyectada
            if (!existingEntity.getSucursal().getId().equals(sucursalId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("{\"error\": \"El artículo no pertenece a la sucursal especificada.\"}");
            }
            // Corregido: Usar toggleBaja con true para desactivar (dar de baja)
            articuloManufacturadoService.toggleBaja(id, true); // true = está dado de baja, es decir, está inactivo
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"error\": \"Error al desactivar el artículo: " + e.getMessage() + "\"}");
        }
    }
}
