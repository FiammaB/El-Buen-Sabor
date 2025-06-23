package ElBuenSabor.ProyectoFinal.Controllers;

import ElBuenSabor.ProyectoFinal.DTO.LocalidadCreateUpdateDTO;
import ElBuenSabor.ProyectoFinal.DTO.LocalidadDTO;
import ElBuenSabor.ProyectoFinal.Service.LocalidadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/localidades")
@CrossOrigin(origins = "*")
public class LocalidadController {

    @Autowired
    private LocalidadService localidadService;

    @PostMapping("")
    public ResponseEntity<?> createLocalidad(@RequestBody LocalidadCreateUpdateDTO localidadDTO) {
        try {
            LocalidadDTO nuevaLocalidad = localidadService.createLocalidad(localidadDTO);
            return new ResponseEntity<>(nuevaLocalidad, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getLocalidadById(@PathVariable Long id) {
        try {
            LocalidadDTO localidad = localidadService.findLocalidadById(id);
            return ResponseEntity.ok(localidad);
        } catch (Exception e) {
            if (e.getMessage().contains("no encontrada")) {
                return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/buscar")
    public ResponseEntity<?> getLocalidadByNombre(@RequestParam String nombre) {
        try {
            LocalidadDTO localidad = localidadService.findByNombre(nombre);
            if (localidad != null) {
                return ResponseEntity.ok(localidad);
            } else {
                return new ResponseEntity<>("Localidad no encontrada con el nombre: " + nombre, HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("")
    public ResponseEntity<?> getAllLocalidades(@RequestParam(required = false) Long provinciaId) {
        try {
            List<LocalidadDTO> localidades;
            if (provinciaId != null) {
                localidades = localidadService.findByProvinciaId(provinciaId);
            } else {
                localidades = localidadService.findAllLocalidades();
            }
            return ResponseEntity.ok(localidades);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateLocalidad(@PathVariable Long id, @RequestBody LocalidadCreateUpdateDTO localidadDTO) {
        try {
            LocalidadDTO localidadActualizada = localidadService.updateLocalidad(id, localidadDTO);
            return ResponseEntity.ok(localidadActualizada);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteLocalidad(@PathVariable Long id) {
        try {
            // Considerar no eliminar si tiene domicilios asociados.
            boolean eliminado = localidadService.delete(id);
            if (eliminado) {
                return ResponseEntity.ok("Localidad eliminada correctamente.");
            } else {
                return new ResponseEntity<>("Localidad no encontrada.", HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}