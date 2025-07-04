package ElBuenSabor.ProyectoFinal.Controllers;

import ElBuenSabor.ProyectoFinal.DTO.ProvinciaDTO;
import ElBuenSabor.ProyectoFinal.Entities.Pais; // Importar Pais
import ElBuenSabor.ProyectoFinal.Entities.Provincia;
import ElBuenSabor.ProyectoFinal.Exceptions.ResourceNotFoundException;
import ElBuenSabor.ProyectoFinal.Mappers.ProvinciaMapper;
import ElBuenSabor.ProyectoFinal.Repositories.PaisRepository; // Se sigue necesitando para buscar Pais
import ElBuenSabor.ProyectoFinal.Service.ProvinciaService; // Usar la interfaz específica
// Importaciones que no se usan o se manejan de otra forma:
// import ElBuenSabor.ProyectoFinal.Service.LocalidadService; // Para listar localidades - si no la necesitas aquí, puedes quitarla
// import ElBuenSabor.ProyectoFinal.DTO.LocalidadDTO; // Para la respuesta de localidades - si no la necesitas aquí, puedes quitarla
// Ya no es necesario si se inyecta por constructor explícito al padre
// import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/provincias") // Define la URL base para este controlador
// ProvinciaController ahora extiende BaseController
public class ProvinciaController extends BaseController<Provincia, Long> {

    private final ProvinciaMapper provinciaMapper;
    private final PaisRepository paisRepository; // Se sigue necesitando para buscar Pais

    // El constructor inyecta el servicio específico de Provincia, el mapper y el repositorio de Pais
    public ProvinciaController(
            ProvinciaService provinciaService, // Servicio específico
            ProvinciaMapper provinciaMapper,
            PaisRepository paisRepository) {
        super(provinciaService); // Pasa el servicio al constructor del BaseController
        this.provinciaMapper = provinciaMapper;
        this.paisRepository = paisRepository;
    }

    // Sobrescribir getAll para devolver DTOs y manejar excepciones
    @GetMapping
    @Override // Sobrescribe el getAll del BaseController
    public ResponseEntity<?> getAll() {
        try {
            List<Provincia> provincias = baseService.findAll(); // Llama al findAll del padre
            List<ProvinciaDTO> dtos = provincias.stream()
                    .map(provinciaMapper::toDTO)
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
            Provincia provincia = baseService.findById(id); // Llama al findById del padre
            return ResponseEntity.ok(provinciaMapper.toDTO(provincia));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    // Sobrescribir create para aceptar un DTO de entrada, mapear y manejar excepciones
    @PostMapping(consumes = "application/json")
    // @Override // <<--- Quitar @Override aquí, ya que la firma del método es diferente (recibe DTO)
    public ResponseEntity<?> create(@RequestBody ProvinciaDTO dto) {
        try {
            Provincia provincia = provinciaMapper.toEntity(dto);

            // Cargar el País si se proporciona el ID en el DTO
            if (dto.getPais() != null && dto.getPais().getId() != null) {
                Pais pais = paisRepository.findById(dto.getPais().getId())
                        .orElseThrow(() -> new ResourceNotFoundException("País no encontrado"));
                provincia.setPais(pais);
            }
            provincia.setBaja(false); // Por defecto, una nueva provincia está activa

            Provincia saved = baseService.save(provincia); // Llama al save del padre
            return ResponseEntity.status(HttpStatus.CREATED).body(provinciaMapper.toDTO(saved)); // Convierte a DTO para la respuesta
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    // Sobrescribir update para aceptar un DTO de entrada, mapear y manejar excepciones
    @PutMapping(value = "/{id}", consumes = "application/json")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody ProvinciaDTO dto) {
        try {
            // Obtener la entidad existente y actualizar sus propiedades
            Provincia existingProvincia = baseService.findById(id);

            existingProvincia.setNombre(dto.getNombre());

            // Actualizar el País si se proporciona el ID en el DTO
            if (dto.getPais() != null && dto.getPais().getId() != null) {
                Pais pais = paisRepository.findById(dto.getPais().getId())
                        .orElseThrow(() -> new ResourceNotFoundException("País no encontrado"));
                existingProvincia.setPais(pais);
            } else {
                existingProvincia.setPais(null); // Si el país es null en el DTO, remueve la relación
            }
            // La propiedad 'baja' se mantendrá o actualizará según la lógica de BaseServiceImpl.update
            // o puedes establecerla explícitamente si tu DTO lo soporta.

            Provincia updated = baseService.update(id, existingProvincia); // Llama al update del padre con la entidad EXISTENTE
            return ResponseEntity.ok(provinciaMapper.toDTO(updated)); // Convierte a DTO para la respuesta
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