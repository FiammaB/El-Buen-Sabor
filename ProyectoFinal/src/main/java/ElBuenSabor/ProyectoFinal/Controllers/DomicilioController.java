package ElBuenSabor.ProyectoFinal.Controllers;

import ElBuenSabor.ProyectoFinal.DTO.DomicilioCreateUpdateDTO;
import ElBuenSabor.ProyectoFinal.DTO.DomicilioDTO;
import ElBuenSabor.ProyectoFinal.DTO.LocalidadDTO; // Para el DTO de conversión
import ElBuenSabor.ProyectoFinal.DTO.PaisDTO;      // Para el DTO de conversión
import ElBuenSabor.ProyectoFinal.DTO.ProvinciaDTO; // Para el DTO de conversión
import ElBuenSabor.ProyectoFinal.Entities.Domicilio; // Para el tipo de retorno de reactivate
import ElBuenSabor.ProyectoFinal.Entities.Localidad;
import ElBuenSabor.ProyectoFinal.Entities.Pais;
import ElBuenSabor.ProyectoFinal.Entities.Provincia;
import ElBuenSabor.ProyectoFinal.Service.DomicilioService;
import ElBuenSabor.ProyectoFinal.Service.LocalidadService; // Para validar existencia de localidad
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/domicilios")
@CrossOrigin(origins = "*")
public class DomicilioController {

    @Autowired
    private DomicilioService domicilioService;

    @Autowired
    private LocalidadService localidadService; // Para validar que la localidad exista

    @PostMapping("")
    public ResponseEntity<?> createDomicilio(@Valid @RequestBody DomicilioCreateUpdateDTO domicilioDTO) {
        try {
            // Validar que la localidad exista y esté activa
            if (domicilioDTO.getLocalidadId() == null || localidadService.findLocalidadById(domicilioDTO.getLocalidadId()) == null) {
                return new ResponseEntity<>("La localidad especificada no existe o no está activa.", HttpStatus.BAD_REQUEST);
            }
            DomicilioDTO nuevoDomicilio = domicilioService.createDomicilio(domicilioDTO);
            return new ResponseEntity<>(nuevoDomicilio, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getDomicilioById(@PathVariable Long id) {
        try {
            DomicilioDTO dto = domicilioService.findDomicilioById(id); // Devuelve activos
            if (dto != null) {
                return ResponseEntity.ok(dto);
            } else {
                return new ResponseEntity<>("Domicilio activo no encontrado con ID: " + id, HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            if (e.getMessage().contains("no encontrado")) {
                return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>("Error al obtener el domicilio: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("")
    public ResponseEntity<?> getAllDomiciliosActivos(@RequestParam(required = false) Long localidadId) {
        try {
            List<DomicilioDTO> domicilios;
            if (localidadId != null) {
                // Validar que la localidad exista y esté activa
                if (localidadService.findLocalidadById(localidadId) == null) {
                    return new ResponseEntity<>("Localidad activa no encontrada con ID: " + localidadId, HttpStatus.NOT_FOUND);
                }
                domicilios = domicilioService.findByLocalidadId(localidadId); // Devuelve DTOs de activos
            } else {
                domicilios = domicilioService.findAllDomicilios(); // Devuelve DTOs de activos
            }
            return ResponseEntity.ok(domicilios);
        } catch (Exception e) {
            return new ResponseEntity<>("Error al obtener domicilios activos: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateDomicilio(@PathVariable Long id, @Valid @RequestBody DomicilioCreateUpdateDTO domicilioDTO) {
        try {
            // Validar que la localidad exista y esté activa
            if (domicilioDTO.getLocalidadId() == null || localidadService.findLocalidadById(domicilioDTO.getLocalidadId()) == null) {
                return new ResponseEntity<>("La localidad especificada para la actualización no existe o no está activa.", HttpStatus.BAD_REQUEST);
            }
            DomicilioDTO domicilioActualizado = domicilioService.updateDomicilio(id, domicilioDTO);
            return ResponseEntity.ok(domicilioActualizado);
        } catch (Exception e) {
            if (e.getMessage().contains("no encontrado")) {
                return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> darBajaDomicilio(@PathVariable Long id) {
        try {
            domicilioService.softDelete(id); // El servicio ahora verifica si está en uso
            return ResponseEntity.ok("Domicilio dado de baja correctamente (borrado lógico).");
        } catch (Exception e) {
            if (e.getMessage().contains("no encontrado") || e.getMessage().contains("ya está dado de baja")) {
                return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
            }
            if (e.getMessage().contains("está en uso activo")) { // Mensaje del servicio
                return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT); // 409 Conflict
            }
            return new ResponseEntity<>("Error al dar de baja el domicilio: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PatchMapping("/{id}/reactivar")
    public ResponseEntity<?> reactivarDomicilio(@PathVariable Long id) {
        try {
            Domicilio domicilioReactivadoEntity = domicilioService.reactivate(id);
            return ResponseEntity.ok(convertToDTO(domicilioReactivadoEntity)); // Convertir entidad
        } catch (Exception e) {
            if (e.getMessage().contains("no encontrado") || e.getMessage().contains("no está dado de baja") || e.getMessage().contains("localidad asociada no está activa")) {
                return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
            }
            return new ResponseEntity<>("Error al reactivar el domicilio: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/admin/todos")
    // @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getAllDomiciliosIncludingDeletedForAdmin() {
        try {
            List<DomicilioDTO> dtos = domicilioService.findAllDomiciliosIncludingDeleted();
            return ResponseEntity.ok(dtos);
        } catch (Exception e) {
            return new ResponseEntity<>("Error al obtener todos los domicilios (incluyendo bajas): " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/admin/{id}")
    // @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getDomicilioByIdIncludingDeletedForAdmin(@PathVariable Long id) {
        try {
            DomicilioDTO dto = domicilioService.findDomicilioByIdIncludingDeleted(id);
            if (dto != null) {
                return ResponseEntity.ok(dto);
            } else {
                return new ResponseEntity<>("Domicilio (activo o inactivo) no encontrado con ID: " + id, HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>("Error al obtener el domicilio (incluyendo bajas): " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Helper para convertir Entidad a DTO (si el servicio devuelve la entidad)
    private DomicilioDTO convertToDTO(Domicilio domicilio) {
        if (domicilio == null) return null;
        DomicilioDTO dto = new DomicilioDTO();
        dto.setId(domicilio.getId());
        dto.setCalle(domicilio.getCalle());
        dto.setNumero(domicilio.getNumero());
        dto.setCp(domicilio.getCp());
        dto.setBaja(domicilio.isBaja());

        if (domicilio.getLocalidad() != null) {
            Localidad locEnt = domicilio.getLocalidad();
            LocalidadDTO locDto = new LocalidadDTO();
            locDto.setId(locEnt.getId());
            locDto.setNombre(locEnt.getNombre());
            locDto.setBaja(locEnt.isBaja());

            if (locEnt.getProvincia() != null) {
                Provincia provEnt = locEnt.getProvincia();
                ProvinciaDTO provDto = new ProvinciaDTO();
                provDto.setId(provEnt.getId());
                provDto.setNombre(provEnt.getNombre());
                provDto.setBaja(provEnt.isBaja());

                if (provEnt.getPais() != null) {
                    Pais paisEnt = provEnt.getPais();
                    PaisDTO paisDto = new PaisDTO();
                    paisDto.setId(paisEnt.getId());
                    paisDto.setNombre(paisEnt.getNombre());
                    paisDto.setBaja(paisEnt.isBaja());
                    provDto.setPais(paisDto);
                }
                locDto.setProvincia(provDto);
            }
            dto.setLocalidad(locDto);
        }
        return dto;
    }
}
