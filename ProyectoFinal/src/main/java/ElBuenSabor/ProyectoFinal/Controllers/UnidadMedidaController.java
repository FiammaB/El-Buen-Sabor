package ElBuenSabor.ProyectoFinal.Controllers;

import ElBuenSabor.ProyectoFinal.DTO.UnidadMedidaDTO;
import ElBuenSabor.ProyectoFinal.Entities.UnidadMedida;
import ElBuenSabor.ProyectoFinal.Mappers.UnidadMedidaMapper;
import ElBuenSabor.ProyectoFinal.Service.UnidadMedidaService; // Usar la interfaz específica
// Ya no es necesario si se inyecta por constructor explícito al padre
// import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/unidades-medida") // Define la URL base para este controlador
// UnidadMedidaController ahora extiende BaseController
public class UnidadMedidaController extends BaseController<UnidadMedida, Long> {

    private final UnidadMedidaMapper unidadMapper;

    // El constructor inyecta el servicio específico de UnidadMedida y el mapper
    public UnidadMedidaController(
            UnidadMedidaService unidadMedidaService, // Servicio específico
            UnidadMedidaMapper unidadMapper) {
        super(unidadMedidaService); // Pasa el servicio al constructor del BaseController
        this.unidadMapper = unidadMapper;
    }

    // Sobrescribir getAll para devolver DTOs y manejar excepciones
    @GetMapping
    @Override // Sobrescribe el getAll del BaseController
    public ResponseEntity<?> getAll() {
        try {
            List<UnidadMedida> unidades = baseService.findAll(); // Llama al findAll del padre
            List<UnidadMedidaDTO> dtos = unidades.stream()
                    .map(unidadMapper::toDTO)
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
            UnidadMedida unidad = baseService.findById(id); // Llama al findById del padre
            return ResponseEntity.ok(unidadMapper.toDTO(unidad));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    // Sobrescribir create para aceptar un DTO de entrada, mapear y manejar excepciones
    @PostMapping(consumes = "application/json")
    // @Override // <<--- Quitar @Override aquí, ya que la firma del método es diferente (recibe DTO)
    public ResponseEntity<?> create(@RequestBody UnidadMedidaDTO dto) {
        try {
            UnidadMedida unidad = unidadMapper.toEntity(dto);
            unidad.setBaja(false); // Por defecto, una nueva unidad de medida está activa

            UnidadMedida saved = baseService.save(unidad); // Llama al save del padre
            return ResponseEntity.status(HttpStatus.CREATED).body(unidadMapper.toDTO(saved)); // Convierte a DTO para la respuesta
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    // Sobrescribir update para aceptar un DTO de entrada, mapear y manejar excepciones
    @PutMapping(value = "/{id}", consumes = "application/json")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody UnidadMedidaDTO dto) {
        try {
            // Obtener la entidad existente y actualizar sus propiedades
            UnidadMedida existingUnidad = baseService.findById(id);

            existingUnidad.setDenominacion(dto.getDenominacion());
            // Si UnidadMedida tiene colecciones (ej. articulos),
            // necesitarías lógica adicional aquí para sincronizarlas.
            // La propiedad 'baja' se mantendrá o actualizará según la lógica de BaseServiceImpl.update
            // o puedes establecerla explícitamente si tu DTO lo soporta.

            UnidadMedida updated = baseService.update(id, existingUnidad); // Llama al update del padre con la entidad EXISTENTE
            return ResponseEntity.ok(unidadMapper.toDTO(updated)); // Convierte a DTO para la respuesta
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }


}