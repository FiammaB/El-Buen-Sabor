// ProyectoFinal/src/main/java/ElBuenSabor/ProyectoFinal/Controllers/BaseController.java
package ElBuenSabor.ProyectoFinal.Controllers;

import ElBuenSabor.ProyectoFinal.Entities.BaseEntity;
import ElBuenSabor.ProyectoFinal.Service.BaseService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.Serializable;
import java.util.List;

// Clase abstracta para un controlador base genérico
@RestController
public abstract class BaseController<E extends BaseEntity, ID extends Serializable> {

    protected BaseService<E, ID> baseService; // El servicio base para realizar operaciones

    // El constructor recibe el BaseService específico para la entidad
    public BaseController(BaseService<E, ID> baseService) {
        this.baseService = baseService;
    }

    // Endpoint para obtener todas las entidades
    @GetMapping
    public ResponseEntity<?> getAll() {
        try {
            List<E> entities = baseService.findAll();
            return ResponseEntity.ok(entities);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    // Endpoint para obtener una entidad por su ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getOne(@PathVariable ID id) {
        try {
            E entity = baseService.findById(id);
            return ResponseEntity.ok(entity);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    // Endpoint para crear una nueva entidad
    // NOTA: Este método recibe directamente la entidad.
    // La conversión de DTO a Entidad se hará en las subclases concretas.
    @PostMapping
    public ResponseEntity<?> create(@RequestBody E entity) {
        try {
            E savedEntity = baseService.save(entity);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedEntity);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    // Endpoint para actualizar una entidad existente
    // NOTA: Este método recibe directamente la entidad.
    // La conversión de DTO a Entidad se hará en las subclases concretas.
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable ID id, @RequestBody E entity) {
        try {
            E updatedEntity = baseService.update(id, entity);
            return ResponseEntity.ok(updatedEntity);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    // Endpoint para eliminar (borrado lógico por defecto) una entidad por su ID
    // Utilizará el método toggleBaja para cambiar el estado 'baja' a true.
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable ID id) {
        try {
            // Llama a toggleBaja para marcarla como 'baja=true'
            baseService.toggleBaja(id, true);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    // Endpoint para activar (cambiar 'baja' a false) una entidad por su ID
    @PatchMapping("/{id}/activate")
    public ResponseEntity<?> activate(@PathVariable ID id) {
        try {
            baseService.toggleBaja(id, false);
            return ResponseEntity.ok("{\"message\": \"Entidad activada exitosamente.\"}");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    // Endpoint para desactivar (cambiar 'baja' a true) una entidad por su ID
    // Similar a DELETE pero con un endpoint más semántico para "desactivar"
    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<?> deactivate(@PathVariable ID id) {
        try {
            baseService.toggleBaja(id, true);
            return ResponseEntity.ok("{\"message\": \"Entidad desactivada exitosamente.\"}");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }
}