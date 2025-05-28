package ElBuenSabor.ProyectoFinal.Controllers;

import ElBuenSabor.ProyectoFinal.DTO.LocalidadCreateUpdateDTO;
import ElBuenSabor.ProyectoFinal.DTO.LocalidadDTO;
import ElBuenSabor.ProyectoFinal.DTO.PaisDTO;
import ElBuenSabor.ProyectoFinal.DTO.ProvinciaDTO;
import ElBuenSabor.ProyectoFinal.Entities.Localidad; // Para el tipo de lista del servicio
import ElBuenSabor.ProyectoFinal.Entities.Pais;
import ElBuenSabor.ProyectoFinal.Entities.Provincia;
import ElBuenSabor.ProyectoFinal.Service.LocalidadService;
import ElBuenSabor.ProyectoFinal.Service.ProvinciaService; // Para validar existencia de provincia
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/localidades")
@CrossOrigin(origins = "*")
public class LocalidadController {

    @Autowired
    private LocalidadService localidadService;

    @Autowired
    private ProvinciaService provinciaService; // Para verificar si la provincia existe

    @PostMapping("")
    public ResponseEntity<?> createLocalidad(@Valid @RequestBody LocalidadCreateUpdateDTO localidadDTO) {
        try {
            // Validar que la provincia exista y esté activa antes de crear la localidad
            if (localidadDTO.getProvinciaId() == null || provinciaService.findProvinciaById(localidadDTO.getProvinciaId()) == null) {
                return new ResponseEntity<>("La provincia especificada no existe o no está activa.", HttpStatus.BAD_REQUEST);
            }
            LocalidadDTO nuevaLocalidad = localidadService.createLocalidad(localidadDTO);
            return new ResponseEntity<>(nuevaLocalidad, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getLocalidadById(@PathVariable Long id) {
        try {
            LocalidadDTO localidadDTO = localidadService.findLocalidadById(id); // Devuelve activas
            if (localidadDTO != null) {
                return ResponseEntity.ok(localidadDTO);
            } else {
                return new ResponseEntity<>("Localidad activa no encontrada con ID: " + id, HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            if (e.getMessage().contains("no encontrada")) {
                return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>("Error al obtener la localidad: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/buscar")
    public ResponseEntity<?> getLocalidadByNombre(@RequestParam String nombre) {
        try {
            LocalidadDTO localidadDTO = localidadService.findByNombre(nombre); // Devuelve activas
            if (localidadDTO != null) {
                return ResponseEntity.ok(localidadDTO);
            } else {
                return new ResponseEntity<>("Localidad activa no encontrada con el nombre: " + nombre, HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>("Error al buscar la localidad: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("")
    public ResponseEntity<?> getAllLocalidadesActivas(@RequestParam(required = false) Long provinciaId) {
        try {
            List<LocalidadDTO> localidades;
            if (provinciaId != null) {
                // Validar que la provincia exista y esté activa
                if (provinciaService.findProvinciaById(provinciaId) == null) {
                    return new ResponseEntity<>("Provincia activa no encontrada con ID: " + provinciaId, HttpStatus.NOT_FOUND);
                }
                localidades = localidadService.findByProvinciaId(provinciaId); // Devuelve DTOs de activas
            } else {
                localidades = localidadService.findAllLocalidades(); // Devuelve DTOs de activas
            }
            return ResponseEntity.ok(localidades);
        } catch (Exception e) {
            return new ResponseEntity<>("Error al obtener localidades activas: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateLocalidad(@PathVariable Long id, @Valid @RequestBody LocalidadCreateUpdateDTO localidadDTO) {
        try {
            // Validar que la provincia exista y esté activa
            if (localidadDTO.getProvinciaId() == null || provinciaService.findProvinciaById(localidadDTO.getProvinciaId()) == null) {
                return new ResponseEntity<>("La provincia especificada para la actualización no existe o no está activa.", HttpStatus.BAD_REQUEST);
            }
            LocalidadDTO localidadActualizada = localidadService.updateLocalidad(id, localidadDTO);
            return ResponseEntity.ok(localidadActualizada);
        } catch (Exception e) {
            if (e.getMessage().contains("no encontrada")) {
                return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> darBajaLocalidad(@PathVariable Long id) {
        try {
            localidadService.softDelete(id);
            return ResponseEntity.ok("Localidad dada de baja correctamente (borrado lógico).");
        } catch (Exception e) {
            if (e.getMessage().contains("no encontrada") || e.getMessage().contains("ya está dada de baja")) {
                return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
            }
            // Considerar DataIntegrityViolationException si tiene domicilios activos
            return new ResponseEntity<>("Error al dar de baja la localidad: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PatchMapping("/{id}/reactivar")
    public ResponseEntity<?> reactivarLocalidad(@PathVariable Long id) {
        try {
            Localidad localidadReactivadaEntity = localidadService.reactivate(id);
            return ResponseEntity.ok(convertToLocalidadDTO(localidadReactivadaEntity)); // Convertir entidad
        } catch (Exception e) {
            if (e.getMessage().contains("no encontrada") || e.getMessage().contains("no está dada de baja") || e.getMessage().contains("provincia asociada no está activa")) {
                return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
            }
            return new ResponseEntity<>("Error al reactivar la localidad: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/admin/todos")
    // @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getAllLocalidadesIncludingDeletedForAdmin() {
        try {
            List<LocalidadDTO> dtos = localidadService.findAllLocalidadesIncludingDeleted();
            return ResponseEntity.ok(dtos);
        } catch (Exception e) {
            return new ResponseEntity<>("Error al obtener todas las localidades (incluyendo bajas): " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/admin/{id}")
    // @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getLocalidadByIdIncludingDeletedForAdmin(@PathVariable Long id) {
        try {
            LocalidadDTO localidadDTO = localidadService.findLocalidadByIdIncludingDeleted(id);
            if (localidadDTO != null) {
                return ResponseEntity.ok(localidadDTO);
            } else {
                return new ResponseEntity<>("Localidad (activa o inactiva) no encontrada con ID: " + id, HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>("Error al obtener la localidad (incluyendo bajas): " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Helper para convertir, si el servicio devuelve entidad en algún caso (ej. reactivate)
    private LocalidadDTO convertToLocalidadDTO(Localidad localidad) {
        if (localidad == null) return null;
        LocalidadDTO dto = new LocalidadDTO();
        dto.setId(localidad.getId());
        dto.setNombre(localidad.getNombre());
        dto.setBaja(localidad.isBaja());
        if (localidad.getProvincia() != null) {
            Provincia provincia = localidad.getProvincia();
            ProvinciaDTO provinciaDTO = new ProvinciaDTO();
            provinciaDTO.setId(provincia.getId());
            provinciaDTO.setNombre(provincia.getNombre());
            provinciaDTO.setBaja(provincia.isBaja());
            if (provincia.getPais() != null) {
                Pais pais = provincia.getPais();
                PaisDTO paisDTO = new PaisDTO();
                paisDTO.setId(pais.getId());
                paisDTO.setNombre(pais.getNombre());
                paisDTO.setBaja(pais.isBaja());
                provinciaDTO.setPais(paisDTO);
            }
            dto.setProvincia(provinciaDTO);
        }
        return dto;
    }
}