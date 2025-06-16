package ElBuenSabor.ProyectoFinal.Controllers;

import ElBuenSabor.ProyectoFinal.DTO.DomicilioCreateUpdateDTO;
import ElBuenSabor.ProyectoFinal.DTO.DomicilioDTO;
import ElBuenSabor.ProyectoFinal.Service.DomicilioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/domicilios") // Ruta para gestión independiente de domicilios (admin)
@CrossOrigin(origins = "*")
public class DomicilioController {

    @Autowired
    private DomicilioService domicilioService;

    // Crear un domicilio de forma independiente (ej. por un admin)
    // La creación de domicilios para clientes/sucursales se maneja en sus respectivos controladores.
    @PostMapping("")
    public ResponseEntity<?> createDomicilio(@RequestBody DomicilioCreateUpdateDTO domicilioDTO) {
        try {
            DomicilioDTO nuevoDomicilio = domicilioService.createDomicilioIndependiente(domicilioDTO);
            return new ResponseEntity<>(nuevoDomicilio, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getDomicilioById(@PathVariable Long id) {
        try {
            DomicilioDTO domicilio = domicilioService.findDomicilioById(id);
            return ResponseEntity.ok(domicilio);
        } catch (Exception e) {
            if (e.getMessage().contains("no encontrado")) {
                return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Listar todos los domicilios (para un admin, por ejemplo)
    @GetMapping("")
    public ResponseEntity<?> getAllDomicilios() {
        try {
            List<DomicilioDTO> domicilios = domicilioService.findAllDomicilios();
            return ResponseEntity.ok(domicilios);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Actualizar un domicilio de forma independiente
    @PutMapping("/{id}")
    public ResponseEntity<?> updateDomicilio(@PathVariable Long id, @RequestBody DomicilioCreateUpdateDTO domicilioDTO) {
        try {
            DomicilioDTO domicilioActualizado = domicilioService.updateDomicilioIndependiente(id, domicilioDTO);
            return ResponseEntity.ok(domicilioActualizado);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    // Eliminar un domicilio de forma independiente. ¡CUIDADO!
    // Un domicilio podría estar asociado a un cliente, pedido, sucursal.
    // El servicio delete debería tener una lógica muy robusta o este endpoint no debería existir
    // o debería ser un borrado lógico.
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteDomicilio(@PathVariable Long id) {
        try {
            // EL SERVICIO DEBE VERIFICAR QUE NO ESTÉ EN USO ANTES DE BORRAR
            // O DESASOCIARLO DE CLIENTES/PEDIDOS/SUCURSALES (COMPLEJO)
            // O SOLO PERMITIR BORRADO LÓGICO.
            boolean eliminado = domicilioService.delete(id);
            if (eliminado) {
                return ResponseEntity.ok("Domicilio eliminado (si no estaba en uso).");
            } else {
                return new ResponseEntity<>("Domicilio no encontrado.", HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            // Capturar DataIntegrityViolationException si hay FKs
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}