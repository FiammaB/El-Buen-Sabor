package ElBuenSabor.ProyectoFinal.Controllers;

import ElBuenSabor.ProyectoFinal.DTO.EmpresaDTO;
import ElBuenSabor.ProyectoFinal.Entities.Empresa;
import ElBuenSabor.ProyectoFinal.Mappers.EmpresaMapper;
import ElBuenSabor.ProyectoFinal.Service.EmpresaService; // Usar la interfaz específica
// Ya no es necesario si se inyecta por constructor explícito al padre
// import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/empresas") // Define la URL base para este controlador
// EmpresaController ahora extiende BaseController
public class EmpresaController extends BaseController<Empresa, Long> {

    private final EmpresaMapper empresaMapper;

    // El constructor inyecta el servicio específico de Empresa y el mapper
    public EmpresaController(
            EmpresaService empresaService, // Servicio específico
            EmpresaMapper empresaMapper) {
        super(empresaService); // Pasa el servicio al constructor del BaseController
        this.empresaMapper = empresaMapper;
    }

    // Sobrescribir getAll para devolver DTOs y manejar excepciones
    @GetMapping
    @Override // Sobrescribe el getAll del BaseController
    public ResponseEntity<?> getAll() {
        try {
            List<Empresa> empresas = baseService.findAll(); // Llama al findAll del padre
            List<EmpresaDTO> dtos = empresas.stream()
                    .map(empresaMapper::toDTO)
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
            Empresa empresa = baseService.findById(id); // Llama al findById del padre
            return ResponseEntity.ok(empresaMapper.toDTO(empresa));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    // Sobrescribir create para aceptar un DTO de entrada, mapear y manejar excepciones
    @PostMapping(consumes = "application/json")
    // @Override // <<--- Quitar @Override aquí, ya que la firma del método es diferente (recibe DTO)
    public ResponseEntity<?> create(@RequestBody EmpresaDTO dto) {
        try {
            Empresa empresa = empresaMapper.toEntity(dto);
            empresa.setBaja(false); // Por defecto, una nueva empresa está activa

            Empresa saved = baseService.save(empresa); // Llama al save del padre
            return ResponseEntity.status(HttpStatus.CREATED).body(empresaMapper.toDTO(saved)); // Convierte a DTO para la respuesta
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    // Sobrescribir update para aceptar un DTO de entrada, mapear y manejar excepciones
    @PutMapping(value = "/{id}", consumes = "application/json")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody EmpresaDTO dto) {
        try {
            // Obtener la entidad existente y actualizar sus propiedades
            Empresa existingEmpresa = baseService.findById(id);

            existingEmpresa.setNombre(dto.getNombre());
            existingEmpresa.setRazonSocial(dto.getRazonSocial());
            existingEmpresa.setCuil(dto.getCuil());
            // La propiedad 'baja' se mantendrá o actualizará según la lógica de BaseServiceImpl.update
            // o puedes establecerla explícitamente si tu DTO lo soporta.

            Empresa updated = baseService.update(id, existingEmpresa); // Llama al update del padre con la entidad EXISTENTE
            return ResponseEntity.ok(empresaMapper.toDTO(updated)); // Convierte a DTO para la respuesta
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