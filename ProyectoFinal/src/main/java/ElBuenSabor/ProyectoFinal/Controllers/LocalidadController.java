package ElBuenSabor.ProyectoFinal.Controllers;

import ElBuenSabor.ProyectoFinal.DTO.LocalidadCreateUpdateDTO;
import ElBuenSabor.ProyectoFinal.DTO.LocalidadDTO;
import ElBuenSabor.ProyectoFinal.Entities.Localidad;
import ElBuenSabor.ProyectoFinal.Entities.Provincia; // Importar Provincia
import ElBuenSabor.ProyectoFinal.Exceptions.ResourceNotFoundException;
import ElBuenSabor.ProyectoFinal.Mappers.LocalidadMapper;
import ElBuenSabor.ProyectoFinal.Repositories.ProvinciaRepository;
import ElBuenSabor.ProyectoFinal.Service.LocalidadService; // Usar la interfaz específica
// Ya no es necesario si se inyecta por constructor explícito al padre
// import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/localidades") // Define la URL base para este controlador
// LocalidadController ahora extiende BaseController
public class LocalidadController extends BaseController<Localidad, Long> {

    private final LocalidadMapper localidadMapper;
    private final ProvinciaRepository provinciaRepository; // Se sigue necesitando para buscar Provincia

    // El constructor inyecta el servicio específico de Localidad, el mapper y el repositorio de Provincia
    public LocalidadController(
            LocalidadService localidadService, // Servicio específico
            LocalidadMapper localidadMapper,
            ProvinciaRepository provinciaRepository) {
        super(localidadService); // Pasa el servicio al constructor del BaseController
        this.localidadMapper = localidadMapper;
        this.provinciaRepository = provinciaRepository;
    }

    // Sobrescribir getAll para devolver DTOs y manejar excepciones
    @GetMapping
    @Override // Sobrescribe el getAll del BaseController
    public ResponseEntity<?> getAll() {
        try {
            List<Localidad> localidades = baseService.findAll(); // Llama al findAll del padre
            List<LocalidadDTO> dtos = localidades.stream()
                    .map(localidadMapper::toDTO)
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
            Localidad localidad = baseService.findById(id); // Llama al findById del padre
            return ResponseEntity.ok(localidadMapper.toDTO(localidad));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    // Sobrescribir create para aceptar un DTO de entrada, mapear y manejar excepciones
    @PostMapping(consumes = "application/json")
    // @Override // <<--- Quitar @Override aquí, ya que la firma del método es diferente (recibe DTO)
    public ResponseEntity<?> create(@RequestBody LocalidadCreateUpdateDTO dto) {
        try {
            Provincia provincia = provinciaRepository.findById(dto.getProvinciaId())
                    .orElseThrow(() -> new ResourceNotFoundException("Provincia no encontrada"));

            Localidad localidad = new Localidad();
            localidad.setNombre(dto.getNombre());
            localidad.setProvincia(provincia);
            localidad.setBaja(dto.isEstaDadoDeBaja()); // Asumo que el DTO define si está de baja o no

            Localidad saved = baseService.save(localidad); // Llama al save del padre
            return ResponseEntity.status(HttpStatus.CREATED).body(localidadMapper.toDTO(saved)); // Convierte a DTO para la respuesta
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    // Sobrescribir update para aceptar un DTO de entrada, mapear y manejar excepciones
    @PutMapping(value = "/{id}", consumes = "application/json")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody LocalidadCreateUpdateDTO dto) {
        try {
            // Obtener la entidad existente y actualizar sus propiedades
            Localidad existingLocalidad = baseService.findById(id);

            Provincia provincia = provinciaRepository.findById(dto.getProvinciaId())
                    .orElseThrow(() -> new ResourceNotFoundException("Provincia no encontrada"));

            existingLocalidad.setNombre(dto.getNombre());
            existingLocalidad.setProvincia(provincia);
            existingLocalidad.setBaja(dto.isEstaDadoDeBaja()); // Asumo que el DTO define si está de baja o no

            Localidad updated = baseService.update(id, existingLocalidad); // Llama al update del padre con la entidad EXISTENTE
            return ResponseEntity.ok(localidadMapper.toDTO(updated)); // Convierte a DTO para la respuesta
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

}