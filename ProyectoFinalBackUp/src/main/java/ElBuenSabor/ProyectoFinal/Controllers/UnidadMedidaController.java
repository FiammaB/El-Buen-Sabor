package ElBuenSabor.ProyectoFinal.Controllers;

import ElBuenSabor.ProyectoFinal.DTO.UnidadMedidaDTO;
import ElBuenSabor.ProyectoFinal.Entities.UnidadMedida;
import ElBuenSabor.ProyectoFinal.Service.UnidadMedidaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/unidades-medida")
@CrossOrigin(origins = "*")
public class UnidadMedidaController {

    @Autowired
    private UnidadMedidaService unidadMedidaService;

    @PostMapping("")
    public ResponseEntity<?> createUnidadMedida(@RequestBody UnidadMedidaDTO unidadMedidaDTO) {
        try {
            UnidadMedida nuevaUnidad = unidadMedidaService.createUnidadMedida(unidadMedidaDTO);
            return new ResponseEntity<>(convertToUnidadMedidaDTO(nuevaUnidad), HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUnidadMedidaById(@PathVariable Long id) {
        try {
            Optional<UnidadMedida> unidadOptional = unidadMedidaService.findById(id);
            if (unidadOptional.isPresent()) {
                return ResponseEntity.ok(convertToUnidadMedidaDTO(unidadOptional.get()));
            } else {
                return new ResponseEntity<>("Unidad de Medida no encontrada.", HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/buscar")
    public ResponseEntity<?> getUnidadMedidaByDenominacion(@RequestParam String denominacion) {
        try {
            UnidadMedida unidad = unidadMedidaService.findByDenominacion(denominacion);
            if (unidad != null) {
                return ResponseEntity.ok(convertToUnidadMedidaDTO(unidad));
            } else {
                return new ResponseEntity<>("Unidad de Medida no encontrada: " + denominacion, HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("")
    public ResponseEntity<?> getAllUnidadesMedida() {
        try {
            List<UnidadMedida> unidades = unidadMedidaService.findAll();
            List<UnidadMedidaDTO> dtos = unidades.stream()
                    .map(this::convertToUnidadMedidaDTO)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(dtos);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateUnidadMedida(@PathVariable Long id, @RequestBody UnidadMedidaDTO unidadMedidaDTO) {
        try {
            UnidadMedida unidadActualizada = unidadMedidaService.updateUnidadMedida(id, unidadMedidaDTO);
            return ResponseEntity.ok(convertToUnidadMedidaDTO(unidadActualizada));
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUnidadMedida(@PathVariable Long id) {
        try {
            // Considerar no eliminar si está en uso por algún artículo.
            boolean eliminado = unidadMedidaService.delete(id);
            if (eliminado) {
                return ResponseEntity.ok("Unidad de Medida eliminada correctamente.");
            } else {
                return new ResponseEntity<>("Unidad de Medida no encontrada.", HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    private UnidadMedidaDTO convertToUnidadMedidaDTO(UnidadMedida unidad) {
        if (unidad == null) return null;
        UnidadMedidaDTO dto = new UnidadMedidaDTO();
        dto.setId(unidad.getId());
        dto.setDenominacion(unidad.getDenominacion());
        return dto;
    }
}