package ElBuenSabor.ProyectoFinal.Controllers;

import ElBuenSabor.ProyectoFinal.DTO.UnidadMedidaDTO;
import ElBuenSabor.ProyectoFinal.Entities.UnidadMedida; // Para el tipo de retorno de reactivate
import ElBuenSabor.ProyectoFinal.Service.UnidadMedidaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/unidades-medida")
@CrossOrigin(origins = "*")
public class UnidadMedidaController {

    @Autowired
    private UnidadMedidaService unidadMedidaService;

    @PostMapping("")
    public ResponseEntity<?> createUnidadMedida(@Valid @RequestBody UnidadMedidaDTO unidadMedidaDTO) {
        try {
            UnidadMedidaDTO nuevaUnidad = unidadMedidaService.createUnidadMedida(unidadMedidaDTO);
            return new ResponseEntity<>(nuevaUnidad, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUnidadMedidaById(@PathVariable Long id) {
        try {
            UnidadMedidaDTO dto = unidadMedidaService.findUnidadMedidaById(id); // Devuelve activas
            if (dto != null) {
                return ResponseEntity.ok(dto);
            } else {
                return new ResponseEntity<>("Unidad de Medida activa no encontrada con ID: " + id, HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>("Error al obtener la unidad de medida: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/buscar")
    public ResponseEntity<?> getUnidadMedidaByDenominacion(@RequestParam String denominacion) {
        try {
            UnidadMedidaDTO dto = unidadMedidaService.findByDenominacion(denominacion); // Devuelve activas
            if (dto != null) {
                return ResponseEntity.ok(dto);
            } else {
                return new ResponseEntity<>("Unidad de Medida activa no encontrada: " + denominacion, HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>("Error al buscar la unidad de medida: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("")
    public ResponseEntity<?> getAllUnidadesMedidaActivas() {
        try {
            List<UnidadMedidaDTO> dtos = unidadMedidaService.findAllUnidadesMedida(); // Devuelve DTOs de activas
            return ResponseEntity.ok(dtos);
        } catch (Exception e) {
            return new ResponseEntity<>("Error al obtener unidades de medida activas: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateUnidadMedida(@PathVariable Long id, @Valid @RequestBody UnidadMedidaDTO unidadMedidaDTO) {
        try {
            UnidadMedidaDTO unidadActualizada = unidadMedidaService.updateUnidadMedida(id, unidadMedidaDTO);
            return ResponseEntity.ok(unidadActualizada);
        } catch (Exception e) {
            if (e.getMessage().contains("no encontrada")) {
                return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> darBajaUnidadMedida(@PathVariable Long id) {
        try {
            unidadMedidaService.softDelete(id);
            return ResponseEntity.ok("Unidad de Medida dada de baja correctamente (borrado lógico).");
        } catch (Exception e) {
            if (e.getMessage().contains("no encontrada") || e.getMessage().contains("ya está dada de baja")) {
                return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
            }
            // Considerar DataIntegrityViolationException si está en uso por artículos activos
            return new ResponseEntity<>("Error al dar de baja la unidad de medida: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PatchMapping("/{id}/reactivar")
    public ResponseEntity<?> reactivarUnidadMedida(@PathVariable Long id) {
        try {
            UnidadMedida unidadReactivadaEntity = unidadMedidaService.reactivate(id);
            return ResponseEntity.ok(convertToUnidadMedidaDTO(unidadReactivadaEntity)); // Convertir entidad
        } catch (Exception e) {
            if (e.getMessage().contains("no encontrada") || e.getMessage().contains("no está dada de baja")) {
                return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
            }
            return new ResponseEntity<>("Error al reactivar la unidad de medida: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/admin/todos")
    // @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getAllUnidadesMedidaIncludingDeletedForAdmin() {
        try {
            List<UnidadMedidaDTO> dtos = unidadMedidaService.findAllUnidadesMedidaIncludingDeleted();
            return ResponseEntity.ok(dtos);
        } catch (Exception e) {
            return new ResponseEntity<>("Error al obtener todas las unidades de medida (incluyendo bajas): " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/admin/{id}")
    // @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getUnidadMedidaByIdIncludingDeletedForAdmin(@PathVariable Long id) {
        try {
            UnidadMedidaDTO dto = unidadMedidaService.findUnidadMedidaByIdIncludingDeleted(id);
            if (dto != null) {
                return ResponseEntity.ok(dto);
            } else {
                return new ResponseEntity<>("Unidad de Medida (activa o inactiva) no encontrada con ID: " + id, HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>("Error al obtener la unidad de medida (incluyendo bajas): " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Helper para convertir, si el servicio devuelve entidad en algún caso (ej. reactivate)
    private UnidadMedidaDTO convertToUnidadMedidaDTO(UnidadMedida unidadMedida) {
        if (unidadMedida == null) return null;
        UnidadMedidaDTO dto = new UnidadMedidaDTO();
        dto.setId(unidadMedida.getId());
        dto.setDenominacion(unidadMedida.getDenominacion());
        dto.setBaja(unidadMedida.isBaja());
        return dto;
    }
}
