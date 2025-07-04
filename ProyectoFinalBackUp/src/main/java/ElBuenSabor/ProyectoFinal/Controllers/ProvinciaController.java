package ElBuenSabor.ProyectoFinal.Controllers;

import ElBuenSabor.ProyectoFinal.DTO.ProvinciaCreateUpdateDTO;
import ElBuenSabor.ProyectoFinal.DTO.ProvinciaDTO;
import ElBuenSabor.ProyectoFinal.Service.ProvinciaService;
import ElBuenSabor.ProyectoFinal.Service.LocalidadService; // Para listar localidades
import ElBuenSabor.ProyectoFinal.DTO.LocalidadDTO; // Para la respuesta de localidades

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/provincias")
@CrossOrigin(origins = "*")
public class ProvinciaController {

    @Autowired
    private ProvinciaService provinciaService;

    @Autowired
    private LocalidadService localidadService; // Para el endpoint de localidades por provincia

    @PostMapping("")
    public ResponseEntity<?> createProvincia(@RequestBody ProvinciaCreateUpdateDTO provinciaDTO) {
        try {
            ProvinciaDTO nuevaProvincia = provinciaService.createProvincia(provinciaDTO);
            return new ResponseEntity<>(nuevaProvincia, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getProvinciaById(@PathVariable Long id) {
        try {
            ProvinciaDTO provincia = provinciaService.findProvinciaById(id);
            return ResponseEntity.ok(provincia);
        } catch (Exception e) {
            if (e.getMessage().contains("no encontrada")) {
                return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/buscar")
    public ResponseEntity<?> getProvinciaByNombre(@RequestParam String nombre) {
        try {
            ProvinciaDTO provincia = provinciaService.findByNombre(nombre);
            if (provincia != null) {
                return ResponseEntity.ok(provincia);
            } else {
                return new ResponseEntity<>("Provincia no encontrada con el nombre: " + nombre, HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("")
    public ResponseEntity<?> getAllProvincias(@RequestParam(required = false) Long paisId) {
        try {
            List<ProvinciaDTO> provincias;
            if (paisId != null) {
                provincias = provinciaService.findByPaisId(paisId);
            } else {
                provincias = provinciaService.findAllProvincias();
            }
            return ResponseEntity.ok(provincias);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateProvincia(@PathVariable Long id, @RequestBody ProvinciaCreateUpdateDTO provinciaDTO) {
        try {
            ProvinciaDTO provinciaActualizada = provinciaService.updateProvincia(id, provinciaDTO);
            return ResponseEntity.ok(provinciaActualizada);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProvincia(@PathVariable Long id) {
        try {
            // Considerar no eliminar si tiene localidades asociadas.
            boolean eliminado = provinciaService.delete(id);
            if (eliminado) {
                return ResponseEntity.ok("Provincia eliminada correctamente.");
            } else {
                return new ResponseEntity<>("Provincia no encontrada.", HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    // Endpoint para obtener las localidades de una provincia específica
    @GetMapping("/{id}/localidades")
    public ResponseEntity<?> obtenerLocalidadesPorProvincia(@PathVariable Long id) {
        try {
            // Verificar si la provincia existe
            provinciaService.findProvinciaById(id); // Esto lanzará excepción si no existe

            List<LocalidadDTO> dtos = localidadService.findByProvinciaId(id);
            return ResponseEntity.ok(dtos);
        } catch (Exception e) {
            if (e.getMessage().contains("no encontrada")) { // Específicamente para la provincia no encontrada
                return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}