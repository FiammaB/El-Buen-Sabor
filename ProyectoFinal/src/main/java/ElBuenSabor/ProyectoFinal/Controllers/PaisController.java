package ElBuenSabor.ProyectoFinal.Controllers;

import ElBuenSabor.ProyectoFinal.DTO.PaisDTO;
import ElBuenSabor.ProyectoFinal.DTO.ProvinciaDTO; // Para la respuesta de provincias
import ElBuenSabor.ProyectoFinal.Entities.Pais;
import ElBuenSabor.ProyectoFinal.Service.PaisService;
import ElBuenSabor.ProyectoFinal.Service.ProvinciaService;
import jakarta.validation.Valid; // Para la validación de DTOs
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/paises")
@CrossOrigin(origins = "*") // Ajusta según tus necesidades de seguridad en producción
public class PaisController {

    @Autowired
    private PaisService paisService;

    @Autowired
    private ProvinciaService provinciaService; // Para el endpoint de provincias por país

    // POST /api/v1/paises - Crear un nuevo país
    @PostMapping("")
    public ResponseEntity<?> createPais(@Valid @RequestBody PaisDTO paisDTO) {
        try {
            Pais nuevoPais = paisService.createPais(paisDTO);
            return new ResponseEntity<>(convertToPaisDTO(nuevoPais), HttpStatus.CREATED);
        } catch (Exception e) {
            // Si la validación del DTO falla, GlobalExceptionHandler manejará MethodArgumentNotValidException.
            // Otras excepciones del servicio (ej. "nombre ya existe") se capturan aquí.
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    // GET /api/v1/paises/{id} - Obtener un país por ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getPaisById(@PathVariable Long id) {
        try {
            Optional<Pais> paisOptional = paisService.findById(id);
            if (paisOptional.isPresent()) {
                return ResponseEntity.ok(convertToPaisDTO(paisOptional.get()));
            } else {
                return new ResponseEntity<>("País no encontrado con ID: " + id, HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>("Error al obtener el país: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // GET /api/v1/paises - Obtener todos los países
    @GetMapping("")
    public ResponseEntity<?> getAllPaises() {
        try {
            List<Pais> paises = paisService.findAll();
            List<PaisDTO> dtos = paises.stream()
                    .map(this::convertToPaisDTO)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(dtos);
        } catch (Exception e) {
            return new ResponseEntity<>("Error al obtener la lista de países: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // GET /api/v1/paises/buscar?nombre=Argentina - Obtener un país por nombre
    @GetMapping("/buscar")
    public ResponseEntity<?> getPaisByNombre(@RequestParam String nombre) {
        try {
            Pais pais = paisService.findByNombre(nombre);
            if (pais != null) {
                return ResponseEntity.ok(convertToPaisDTO(pais));
            } else {
                return new ResponseEntity<>("País no encontrado con el nombre: " + nombre, HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>("Error al buscar el país por nombre: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // PUT /api/v1/paises/{id} - Actualizar un país existente
    @PutMapping("/{id}")
    public ResponseEntity<?> updatePais(@PathVariable Long id, @Valid @RequestBody PaisDTO paisDTO) {
        try {
            Pais paisActualizado = paisService.updatePais(id, paisDTO);
            return ResponseEntity.ok(convertToPaisDTO(paisActualizado));
        } catch (Exception e) {
            if (e.getMessage().contains("no encontrado")) {
                return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    // DELETE /api/v1/paises/{id} - Eliminar un país
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePais(@PathVariable Long id) {
        try {
            // El PaisService.delete(id) debería idealmente verificar si el país tiene provincias asociadas
            // y, de ser así, impedir el borrado o manejarlo según las reglas de negocio.
            // Por ahora, asumimos que el servicio lo intenta borrar.
            boolean eliminado = paisService.delete(id);
            if (eliminado) {
                return ResponseEntity.ok("País eliminado correctamente.");
            } else {
                // Esto podría ocurrir si el servicio findById no encuentra nada antes de intentar borrar.
                return new ResponseEntity<>("País no encontrado con ID: " + id + " para eliminar.", HttpStatus.NOT_FOUND);
            }
        } catch (org.springframework.dao.DataIntegrityViolationException dive) {
            // Captura específica para errores de integridad referencial (ej. si tiene provincias)
            return new ResponseEntity<>("No se puede eliminar el país. Puede que tenga provincias asociadas.", HttpStatus.CONFLICT);
        }
        catch (Exception e) {
            if (e.getMessage().contains("no encontrado")) {
                return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>("Error al eliminar el país: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    // GET /api/v1/paises/{id}/provincias - Obtener las provincias de un país específico
    @GetMapping("/{id}/provincias")
    public ResponseEntity<?> obtenerProvinciasPorPais(@PathVariable Long id) {
        try {
            // Verificar si el país existe primero
            if (!paisService.existsById(id)) { // Añadir existsById a BaseService y PaisService si no existe
                return new ResponseEntity<>("País no encontrado con ID: " + id, HttpStatus.NOT_FOUND);
            }
            List<ProvinciaDTO> dtos = provinciaService.findByPaisId(id);
            return ResponseEntity.ok(dtos);
        } catch (Exception e) {
            return new ResponseEntity<>("Error al obtener las provincias del país: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // --- Helper para convertir Entidad a DTO ---
    private PaisDTO convertToPaisDTO(Pais pais) {
        if (pais == null) return null;
        PaisDTO dto = new PaisDTO();
        dto.setId(pais.getId());
        dto.setNombre(pais.getNombre());
        return dto;
    }
}