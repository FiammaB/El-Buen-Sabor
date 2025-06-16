package ElBuenSabor.ProyectoFinal.Controllers;

import ElBuenSabor.ProyectoFinal.DTO.ArticuloManufacturadoCreateDTO;
import ElBuenSabor.ProyectoFinal.DTO.ArticuloManufacturadoDTO;
import ElBuenSabor.ProyectoFinal.Entities.ArticuloManufacturado;
import ElBuenSabor.ProyectoFinal.Mappers.ArticuloManufacturadoMapper;
import ElBuenSabor.ProyectoFinal.Repositories.CategoriaRepository;
import ElBuenSabor.ProyectoFinal.Repositories.ImagenRepository;
import ElBuenSabor.ProyectoFinal.Repositories.UnidadMedidaRepository;
import ElBuenSabor.ProyectoFinal.Service.ArticuloManufacturadoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/articuloManufacturado") // Define la URL base para este controlador
// ArticuloManufacturadoController ahora extiende BaseController
public class ArticuloManufacturadoController extends BaseController<ArticuloManufacturado, Long> {

    private final ArticuloManufacturadoMapper mapper;
    private final CategoriaRepository categoriaRepository;
    private final UnidadMedidaRepository unidadMedidaRepository;
    private final ImagenRepository imagenRepository;

    // El constructor inyecta el servicio específico de ArticuloManufacturado
    public ArticuloManufacturadoController(
            ArticuloManufacturadoService articuloManufacturadoService, // Servicio específico
            ArticuloManufacturadoMapper mapper,
            CategoriaRepository categoriaRepository,
            UnidadMedidaRepository unidadMedidaRepository,
            ImagenRepository imagenRepository) {
        super(articuloManufacturadoService); // Pasa el servicio al constructor del BaseController
        this.mapper = mapper;
        this.categoriaRepository = categoriaRepository;
        this.unidadMedidaRepository = unidadMedidaRepository;
        this.imagenRepository = imagenRepository;
    }

    // Sobrescribir getAll para devolver DTOs y manejar excepciones
    @GetMapping("/manufacturados") // Puedes mantener tu endpoint específico si quieres
    @Override // Sobrescribe el getAll del BaseController
    public ResponseEntity<?> getAll() {
        try {
            List<ArticuloManufacturado> articulos = baseService.findAll(); // Llama al findAll del padre
            List<ArticuloManufacturadoDTO> dtos = articulos.stream()
                    .map(mapper::toDTO)
                    .toList();
            return ResponseEntity.ok(dtos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    // Sobrescribir getOne para devolver un DTO y manejar excepciones
    @GetMapping("/{id}") // Puedes mantener tu endpoint específico si quieres
    @Override // Sobrescribe el getOne del BaseController
    public ResponseEntity<?> getOne(@PathVariable Long id) {
        try {
            ArticuloManufacturado articulo = baseService.findById(id); // Llama al findById del padre
            return ResponseEntity.ok(mapper.toDTO(articulo));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    // Sobrescribir create para aceptar un DTO de entrada, mapear y manejar excepciones
    @PostMapping(consumes = "application/json")
    // @Override // <<--- Quitar @Override aquí, ya que la firma del método es diferente (recibe DTO)
    public ResponseEntity<?> create(@RequestBody ArticuloManufacturadoCreateDTO dto) {
        try {
            ArticuloManufacturado entity = mapper.toEntity(dto);

            // Establecer las relaciones ManyToOne
            entity.setCategoria(categoriaRepository.findById(dto.getCategoriaId()).orElse(null));
            entity.setUnidadMedida(unidadMedidaRepository.findById(dto.getUnidadMedidaId()).orElse(null));
            entity.setImagen(imagenRepository.findById(dto.getImagenId()).orElse(null));
            entity.setBaja(false); // Por defecto, un nuevo artículo manufacturado está activo

            ArticuloManufacturado saved = baseService.save(entity); // Llama al save del padre
            return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toDTO(saved)); // Convierte a DTO para la respuesta
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    // Sobrescribir update para aceptar un DTO de entrada, mapear y manejar excepciones
    @PutMapping(value = "/{id}", consumes = "application/json")

    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody ArticuloManufacturadoCreateDTO dto) {
        try {
            ArticuloManufacturado entity = mapper.toEntity(dto);

            // Asegúrate de establecer las relaciones necesarias antes de actualizar
            entity.setCategoria(categoriaRepository.findById(dto.getCategoriaId()).orElse(null));
            entity.setUnidadMedida(unidadMedidaRepository.findById(dto.getUnidadMedidaId()).orElse(null));
            entity.setImagen(imagenRepository.findById(dto.getImagenId()).orElse(null));

            ArticuloManufacturado updated = baseService.update(id, entity); // Llama al update del padre
            return ResponseEntity.ok(mapper.toDTO(updated)); // Convierte a DTO para la respuesta
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    // Los métodos DELETE, ACTIVATE, DEACTIVATE pueden heredarse directamente de BaseController
    // si la lógica de borrado/activación/desactivación ya implementada en BaseController
    // es suficiente y no necesitas una respuesta con DTOs específicos.
    // @DeleteMapping("/{id}") ya está cubierto por BaseController
    // @PatchMapping("/{id}/activate") ya está cubierto por BaseController
    // @PatchMapping("/{id}/deactivate") ya está cubierto por BaseController
}