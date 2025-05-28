package ElBuenSabor.ProyectoFinal.Controllers;

import ElBuenSabor.ProyectoFinal.DTO.ImagenDTO;
import ElBuenSabor.ProyectoFinal.Entities.Imagen; // Para el tipo de retorno de reactivate
import ElBuenSabor.ProyectoFinal.Service.ImagenService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/imagenes")
@CrossOrigin(origins = "*")
public class ImagenController {

    @Autowired
    private ImagenService imagenService;

    @PostMapping("")
    public ResponseEntity<?> createImagen(@Valid @RequestBody ImagenDTO imagenDTO) {
        try {
            ImagenDTO nuevaImagen = imagenService.createImagen(imagenDTO);
            return new ResponseEntity<>(nuevaImagen, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getImagenById(@PathVariable Long id) {
        try {
            ImagenDTO dto = imagenService.findImagenById(id); // Devuelve activas
            if (dto != null) {
                return ResponseEntity.ok(dto);
            } else {
                return new ResponseEntity<>("Imagen activa no encontrada con ID: " + id, HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>("Error al obtener la imagen: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("")
    public ResponseEntity<?> getAllImagenesActivas() {
        try {
            List<ImagenDTO> dtos = imagenService.findAllImagenes(); // Devuelve DTOs de activas
            return ResponseEntity.ok(dtos);
        } catch (Exception e) {
            return new ResponseEntity<>("Error al obtener imágenes activas: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateImagen(@PathVariable Long id, @Valid @RequestBody ImagenDTO imagenDTO) {
        try {
            ImagenDTO imagenActualizada = imagenService.updateImagen(id, imagenDTO);
            return ResponseEntity.ok(imagenActualizada);
        } catch (Exception e) {
            if (e.getMessage().contains("no encontrada")) {
                return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> darBajaImagen(@PathVariable Long id) {
        try {
            imagenService.softDelete(id); // El servicio ahora verifica si está en uso
            return ResponseEntity.ok("Imagen dada de baja correctamente (borrado lógico), si no estaba en uso.");
        } catch (Exception e) {
            if (e.getMessage().contains("no encontrada") || e.getMessage().contains("ya está dada de baja")) {
                return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
            }
            if (e.getMessage().contains("está actualmente en uso")) { // Mensaje del servicio
                return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT); // 409 Conflict
            }
            return new ResponseEntity<>("Error al dar de baja la imagen: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PatchMapping("/{id}/reactivar")
    public ResponseEntity<?> reactivarImagen(@PathVariable Long id) {
        try {
            Imagen imagenReactivadaEntity = imagenService.reactivate(id);
            return ResponseEntity.ok(convertToImagenDTO(imagenReactivadaEntity)); // Convertir entidad
        } catch (Exception e) {
            if (e.getMessage().contains("no encontrada") || e.getMessage().contains("no está dada de baja")) {
                return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
            }
            return new ResponseEntity<>("Error al reactivar la imagen: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/admin/todos")
    // @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getAllImagenesIncludingDeletedForAdmin() {
        try {
            List<ImagenDTO> dtos = imagenService.findAllImagenesIncludingDeleted();
            return ResponseEntity.ok(dtos);
        } catch (Exception e) {
            return new ResponseEntity<>("Error al obtener todas las imágenes (incluyendo bajas): " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/admin/{id}")
    // @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getImagenByIdIncludingDeletedForAdmin(@PathVariable Long id) {
        try {
            ImagenDTO dto = imagenService.findImagenByIdIncludingDeleted(id);
            if (dto != null) {
                return ResponseEntity.ok(dto);
            } else {
                return new ResponseEntity<>("Imagen (activa o inactiva) no encontrada con ID: " + id, HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>("Error al obtener la imagen (incluyendo bajas): " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private ImagenDTO convertToImagenDTO(Imagen imagen) {
        if (imagen == null) return null;
        ImagenDTO dto = new ImagenDTO();
        dto.setId(imagen.getId());
        dto.setDenominacion(imagen.getDenominacion());
        dto.setBaja(imagen.isBaja());
        return dto;
    }
}