package ElBuenSabor.ProyectoFinal.Controllers;

import ElBuenSabor.ProyectoFinal.DTO.DomicilioDTO;
import ElBuenSabor.ProyectoFinal.Entities.Domicilio;
import ElBuenSabor.ProyectoFinal.Entities.Localidad; // Importar Localidad
import ElBuenSabor.ProyectoFinal.Exceptions.ResourceNotFoundException;
import ElBuenSabor.ProyectoFinal.Mappers.DomicilioMapper;
import ElBuenSabor.ProyectoFinal.Repositories.LocalidadRepository;
import ElBuenSabor.ProyectoFinal.Service.DomicilioService; // Usar la interfaz específica
// Ya no es necesario si se inyecta por constructor explícito al padre
// import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/domicilios") // Define la URL base para este controlador
// DomicilioController ahora extiende BaseController
public class DomicilioController extends BaseController<Domicilio, Long> {

    private final DomicilioMapper domicilioMapper;
    private final LocalidadRepository localidadRepository; // Se sigue necesitando para buscar Localidad

    // El constructor inyecta el servicio específico de Domicilio, el mapper y el repositorio de Localidad
    public DomicilioController(
            DomicilioService domicilioService, // Servicio específico
            DomicilioMapper domicilioMapper,
            LocalidadRepository localidadRepository) {
        super(domicilioService); // Pasa el servicio al constructor del BaseController
        this.domicilioMapper = domicilioMapper;
        this.localidadRepository = localidadRepository;
    }

    // Sobrescribir getAll para devolver DTOs y manejar excepciones
    @GetMapping
    @Override // Sobrescribe el getAll del BaseController
    public ResponseEntity<?> getAll() {
        try {
            List<Domicilio> domicilios = baseService.findAll(); // Llama al findAll del padre
            List<DomicilioDTO> dtos = domicilios.stream()
                    .map(domicilioMapper::toDTO)
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
            Domicilio domicilio = baseService.findById(id); // Llama al findById del padre
            return ResponseEntity.ok(domicilioMapper.toDTO(domicilio));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    // Sobrescribir create para aceptar un DTO de entrada, mapear y manejar excepciones
    @PostMapping(consumes = "application/json")
    // @Override // <<--- Quitar @Override aquí, ya que la firma del método es diferente (recibe DTO)
    public ResponseEntity<?> create(@RequestBody DomicilioDTO dto) {
        try {
            Domicilio domicilio = domicilioMapper.toEntity(dto);

            // Cargar la Localidad si se proporciona el ID en el DTO
            if (dto.getLocalidad() != null && dto.getLocalidad().getId() != null) {
                Localidad localidad = localidadRepository.findById(dto.getLocalidad().getId())
                        .orElseThrow(() -> new ResourceNotFoundException("Localidad no encontrada"));
                domicilio.setLocalidad(localidad);
            }
            domicilio.setBaja(false); // Por defecto, un nuevo domicilio está activo

            Domicilio saved = baseService.save(domicilio); // Llama al save del padre
            return ResponseEntity.status(HttpStatus.CREATED).body(domicilioMapper.toDTO(saved)); // Convierte a DTO para la respuesta
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    // Sobrescribir update para aceptar un DTO de entrada, mapear y manejar excepciones
    @PutMapping(value = "/{id}", consumes = "application/json")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody DomicilioDTO dto) {
        try {
            // Obtener la entidad existente y actualizar sus propiedades
            Domicilio existingDomicilio = baseService.findById(id);

            existingDomicilio.setCalle(dto.getCalle());
            existingDomicilio.setNumero(dto.getNumero());
            existingDomicilio.setCp(dto.getCp());

            // Actualizar la Localidad si se proporciona el ID en el DTO
            if (dto.getLocalidad() != null && dto.getLocalidad().getId() != null) {
                Localidad localidad = localidadRepository.findById(dto.getLocalidad().getId())
                        .orElseThrow(() -> new ResourceNotFoundException("Localidad no encontrada"));
                existingDomicilio.setLocalidad(localidad);
            } else {
                existingDomicilio.setLocalidad(null); // Si la localidad es null en el DTO, remueve la relación
            }
            // La propiedad 'baja' se mantendrá o actualizará según la lógica de BaseServiceImpl.update
            // o puedes establecerla explícitamente si tu DTO lo soporta.

            Domicilio updated = baseService.update(id, existingDomicilio); // Llama al update del padre con la entidad EXISTENTE
            return ResponseEntity.ok(domicilioMapper.toDTO(updated)); // Convierte a DTO para la respuesta
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