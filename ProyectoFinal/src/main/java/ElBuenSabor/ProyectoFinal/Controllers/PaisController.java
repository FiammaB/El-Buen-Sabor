package ElBuenSabor.ProyectoFinal.Controllers;

import ElBuenSabor.ProyectoFinal.DTO.PaisDTO;
import ElBuenSabor.ProyectoFinal.Entities.Pais;
import ElBuenSabor.ProyectoFinal.Mappers.PaisMapper;
import ElBuenSabor.ProyectoFinal.Service.PaisService; // Usar la interfaz específica
// Ya no es necesario si se inyecta por constructor explícito al padre
// import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/paises") // Define la URL base para este controlador
// PaisController ahora extiende BaseController
public class PaisController extends BaseController<Pais, Long> {

    private final PaisMapper paisMapper;

    // El constructor inyecta el servicio específico de Pais y el mapper
    public PaisController(
            PaisService paisService, // Servicio específico
            PaisMapper paisMapper) {
        super(paisService); // Pasa el servicio al constructor del BaseController
        this.paisMapper = paisMapper;
    }

    // Sobrescribir getAll para devolver DTOs y manejar excepciones
    @GetMapping
    @Override // Sobrescribe el getAll del BaseController
    public ResponseEntity<?> getAll() {
        try {
            List<Pais> paises = baseService.findAll(); // Llama al findAll del padre
            List<PaisDTO> dtos = paises.stream()
                    .map(paisMapper::toDTO)
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
            Pais pais = baseService.findById(id); // Llama al findById del padre
            return ResponseEntity.ok(paisMapper.toDTO(pais));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    // Sobrescribir create para aceptar un DTO de entrada, mapear y manejar excepciones
    @PostMapping(consumes = "application/json")
    // @Override // <<--- Quitar @Override aquí, ya que la firma del método es diferente (recibe DTO)
    public ResponseEntity<?> create(@RequestBody PaisDTO dto) {
        try {
            Pais pais = paisMapper.toEntity(dto);
            pais.setBaja(false); // Por defecto, un nuevo país está activo

            Pais saved = baseService.save(pais); // Llama al save del padre
            return ResponseEntity.status(HttpStatus.CREATED).body(paisMapper.toDTO(saved)); // Convierte a DTO para la respuesta
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    // Sobrescribir update para aceptar un DTO de entrada, mapear y manejar excepciones
    @PutMapping(value = "/{id}", consumes = "application/json")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody PaisDTO dto) {
        try {
            // Obtener la entidad existente y actualizar sus propiedades
            Pais existingPais = baseService.findById(id);

            existingPais.setNombre(dto.getNombre());
            // Si País tiene colecciones (ej. provincias),
            // necesitarías lógica adicional aquí para sincronizarlas.
            // La propiedad 'baja' se mantendrá o actualizará según la lógica de BaseServiceImpl.update
            // o puedes establecerla explícitamente si tu DTO lo soporta.

            Pais updated = baseService.update(id, existingPais); // Llama al update del padre con la entidad EXISTENTE
            return ResponseEntity.ok(paisMapper.toDTO(updated)); // Convierte a DTO para la respuesta
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