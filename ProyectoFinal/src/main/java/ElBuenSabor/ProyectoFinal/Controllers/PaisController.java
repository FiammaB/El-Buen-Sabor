package ElBuenSabor.ProyectoFinal.Controllers;

import ElBuenSabor.ProyectoFinal.DTO.PaisDTO;
import ElBuenSabor.ProyectoFinal.DTO.ProvinciaDTO; // Para la respuesta de provincias
import ElBuenSabor.ProyectoFinal.Entities.Pais; // Para el tipo de retorno de reactivate
import ElBuenSabor.ProyectoFinal.Service.PaisService;
import ElBuenSabor.ProyectoFinal.Service.ProvinciaService;
import jakarta.validation.Valid; // Para la validación de DTOs
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
// import java.util.Optional; // No se usa Optional directamente aquí si el servicio ya lo maneja
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/paises")
@CrossOrigin(origins = "*") // Ajusta según tus necesidades de seguridad en producción
public class PaisController {

    @Autowired
    private PaisService paisService;

    @Autowired
    private ProvinciaService provinciaService;

    // POST /api/v1/paises - Crear un nuevo país
    @PostMapping("")
    public ResponseEntity<?> createPais(@Valid @RequestBody PaisDTO paisDTO) {
        try {
            PaisDTO nuevoPais = paisService.createPais(paisDTO);
            return new ResponseEntity<>(nuevoPais, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    // GET /api/v1/paises/{id} - Obtener un país por ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getPaisById(@PathVariable Long id) {
        try {
            // CORREGIDO: Usar el método del servicio que devuelve PaisDTO
            PaisDTO paisDTO = paisService.findPaisById(id);
            if (paisDTO != null) { // findPaisById en el servicio devuelve DTO o null si no está activo
                return ResponseEntity.ok(paisDTO);
            } else {
                return new ResponseEntity<>("País activo no encontrado con ID: " + id, HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>("Error al obtener el país: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // GET /api/v1/paises - Obtener todos los países activos
    @GetMapping("")
    public ResponseEntity<?> getAllPaisesActivos() { // Renombrado para claridad
        try {
            List<PaisDTO> dtos = paisService.findAllPaises(); // Este método en PaisService devuelve DTOs de activos
            return ResponseEntity.ok(dtos);
        } catch (Exception e) {
            return new ResponseEntity<>("Error al obtener la lista de países activos: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // GET /api/v1/paises/buscar?nombre=Argentina - Obtener un país activo por nombre
    @GetMapping("/buscar")
    public ResponseEntity<?> getPaisByNombre(@RequestParam String nombre) {
        try {
            PaisDTO paisDTO = paisService.findByNombre(nombre); // Este método en PaisService devuelve DTO de activo
            if (paisDTO != null) {
                return ResponseEntity.ok(paisDTO);
            } else {
                return new ResponseEntity<>("País activo no encontrado con el nombre: " + nombre, HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>("Error al buscar el país por nombre: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // PUT /api/v1/paises/{id} - Actualizar un país existente
    @PutMapping("/{id}")
    public ResponseEntity<?> updatePais(@PathVariable Long id, @Valid @RequestBody PaisDTO paisDTO) {
        try {
            PaisDTO paisActualizado = paisService.updatePais(id, paisDTO); // Este método en PaisService devuelve DTO
            return ResponseEntity.ok(paisActualizado);
        } catch (Exception e) {
            if (e.getMessage().contains("no encontrado")) {
                return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    // DELETE /api/v1/paises/{id} - Dar de baja (borrado lógico) un país
    @DeleteMapping("/{id}")
    public ResponseEntity<?> darBajaPais(@PathVariable Long id) { // Nombre del método cambiado para claridad
        try {
            // CORREGIDO: Usar softDelete. Asumimos que PaisService hereda softDelete(id)
            // de BaseService y que PaisServiceImpl lo implementa correctamente.
            paisService.softDelete(id);
            return ResponseEntity.ok("País ID: " + id + " dado de baja correctamente (borrado lógico).");
        } catch (org.springframework.dao.DataIntegrityViolationException dive) {
            return new ResponseEntity<>("No se puede dar de baja el país. Puede que tenga provincias activas asociadas.", HttpStatus.CONFLICT);
        }
        catch (Exception e) {
            if (e.getMessage().contains("no encontrado") || e.getMessage().contains("ya está dado de baja")) {
                return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>("Error al dar de baja el país: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    // PATCH /api/v1/paises/{id}/reactivar - Reactivar un país dado de baja
    @PatchMapping("/{id}/reactivar")
    public ResponseEntity<?> reactivarPais(@PathVariable Long id) {
        try {
            Pais paisReactivadoEntity = paisService.reactivate(id); // reactivate devuelve la entidad
            return ResponseEntity.ok(convertToPaisDTO(paisReactivadoEntity)); // Convertir a DTO para la respuesta
        } catch (Exception e) {
            if (e.getMessage().contains("no encontrado") || e.getMessage().contains("no está dado de baja")) {
                return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
            }
            return new ResponseEntity<>("Error al reactivar el país: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // GET /api/v1/paises/admin/todos - Obtener TODOS los países, incluyendo los dados de baja (para admin)
    @GetMapping("/admin/todos")
    public ResponseEntity<?> getAllPaisesIncludingDeletedForAdmin() {
        try {
            // Asumimos que PaisService tiene un método que devuelve List<PaisDTO> incluyendo los de baja
            List<PaisDTO> dtos = paisService.findAllPaisesIncludingDeleted();
            return ResponseEntity.ok(dtos);
        } catch (Exception e) {
            return new ResponseEntity<>("Error al obtener todos los países (incluyendo bajas): " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // GET /api/v1/paises/admin/{id} - Obtener un país por ID, incluyendo si está dado de baja (para admin)
    @GetMapping("/admin/{id}")
    public ResponseEntity<?> getPaisByIdIncludingDeletedForAdmin(@PathVariable Long id) {
        try {
            // Asumimos que PaisService tiene un método que devuelve PaisDTO incluyendo si está de baja
            PaisDTO dto = paisService.findPaisByIdIncludingDeleted(id);
            if (dto != null) {
                return ResponseEntity.ok(dto);
            } else {
                return new ResponseEntity<>("País (activo o inactivo) no encontrado con ID: " + id, HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>("Error al obtener el país (incluyendo bajas): " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // GET /api/v1/paises/{id}/provincias - Obtener las provincias activas de un país específico
    @GetMapping("/{id}/provincias")
    public ResponseEntity<?> obtenerProvinciasPorPais(@PathVariable Long id) {
        try {
            // CORREGIDO: Verificar si el país activo existe usando el método que devuelve DTO
            PaisDTO paisDTO = paisService.findPaisById(id);
            if (paisDTO == null) {
                return new ResponseEntity<>("País activo no encontrado con ID: " + id, HttpStatus.NOT_FOUND);
            }
            List<ProvinciaDTO> dtos = provinciaService.findByPaisId(id); // Este método ya devuelve DTOs de provincias activas
            return ResponseEntity.ok(dtos);
        } catch (Exception e) {
            return new ResponseEntity<>("Error al obtener las provincias del país: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // --- Helper para convertir Entidad a DTO (si el servicio devuelve la entidad) ---
    private PaisDTO convertToPaisDTO(Pais pais) {
        if (pais == null) return null;
        PaisDTO dto = new PaisDTO();
        dto.setId(pais.getId());
        dto.setNombre(pais.getNombre());
        dto.setBaja(pais.isBaja()); // Asegurar que el DTO refleje el estado de baja
        return dto;
    }
}
