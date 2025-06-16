package ElBuenSabor.ProyectoFinal.Controllers;

import ElBuenSabor.ProyectoFinal.DTO.CategoriaShortDTO;
import ElBuenSabor.ProyectoFinal.DTO.CategoriaDTO;
import ElBuenSabor.ProyectoFinal.Entities.Categoria;
import ElBuenSabor.ProyectoFinal.Mappers.CategoriaMapper;
import ElBuenSabor.ProyectoFinal.Service.CategoriaService; // Usar la interfaz específica
// Ya no es necesario si se inyecta por constructor explícito al padre
// import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categorias") // Define la URL base para este controlador
// CategoriaController ahora extiende BaseController
public class CategoriaController extends BaseController<Categoria, Long> {

    private final CategoriaMapper categoriaMapper;
    // Necesitamos el servicio inyectado aquí directamente para poder buscar la categoría padre
    private final CategoriaService categoriaService; // Mantenemos la referencia al servicio específico

    // El constructor inyecta el servicio específico de Categoria y el mapper
    public CategoriaController(
            CategoriaService categoriaService, // Servicio específico
            CategoriaMapper categoriaMapper) {
        super(categoriaService); // Pasa el servicio al constructor del BaseController
        this.categoriaService = categoriaService; // Asigna la referencia específica
        this.categoriaMapper = categoriaMapper;
    }

    // Sobrescribir getAll para devolver DTOs y manejar excepciones
    @GetMapping
    @Override // Sobrescribe el getAll del BaseController
    public ResponseEntity<?> getAll() {
        try {
            List<Categoria> categorias = baseService.findAll(); // Llama al findAll del padre
            List<CategoriaDTO> dtos = categorias.stream()
                    .map(categoriaMapper::toDTO)
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
            Categoria categoria = baseService.findById(id); // Llama al findById del padre
            return ResponseEntity.ok(categoriaMapper.toDTO(categoria));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    // Sobrescribir create para aceptar un DTO de entrada, mapear y manejar excepciones
    @PostMapping(consumes = "application/json")
    // @Override // <<--- Quitar @Override aquí, ya que la firma del método es diferente (recibe DTO)
    public ResponseEntity<?> create(@RequestBody CategoriaShortDTO dto) {
        try {
            Categoria categoria = categoriaMapper.toEntity(dto);

            // Cargar manualmente la categoría padre si tiene ID
            if (dto.getCategoriaPadreId() != null) {
                Categoria padre = categoriaService.findById(dto.getCategoriaPadreId()); // Usa el servicio específico
                categoria.setCategoriaPadre(padre);
            }
            categoria.setBaja(false); // Por defecto, una nueva categoría está activa

            Categoria saved = baseService.save(categoria); // Llama al save del padre
            return ResponseEntity.status(HttpStatus.CREATED).body(categoriaMapper.toDTO(saved)); // Convierte a DTO para la respuesta
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    // Sobrescribir update para aceptar un DTO de entrada, mapear y manejar excepciones
    @PutMapping(value = "/{id}", consumes = "application/json")

    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody CategoriaShortDTO dto) {
        try {
            // No crees una nueva entidad y luego la actualices, sino que busca la existente
            // y actualiza sus propiedades.
            Categoria existingCategory = baseService.findById(id); // Obtiene la categoría existente

            existingCategory.setDenominacion(dto.getDenominacion());

            if (dto.getCategoriaPadreId() != null) {
                Categoria padre = categoriaService.findById(dto.getCategoriaPadreId()); // Usa el servicio específico
                existingCategory.setCategoriaPadre(padre);
            } else {
                existingCategory.setCategoriaPadre(null); // Si el padre es null en el DTO, remueve la relación
            }
            // La propiedad 'baja' se mantendrá o actualizará según la lógica de BaseServiceImpl.update
            // o puedes establecerla explícitamente si tu DTO lo soporta.

            Categoria updated = baseService.update(id, existingCategory); // Llama al update del padre
            return ResponseEntity.ok(categoriaMapper.toDTO(updated)); // Convierte a DTO para la respuesta
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