package ElBuenSabor.ProyectoFinal.Controllers;

import ElBuenSabor.ProyectoFinal.DTO.FacturaDTO;
import ElBuenSabor.ProyectoFinal.Service.FacturaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/facturas")
@CrossOrigin(origins = "*")
public class FacturaController {

    @Autowired
    private FacturaService facturaService;

    @GetMapping("/{id}")
    public ResponseEntity<?> getFacturaById(@PathVariable Long id) {
        try {
            FacturaDTO factura = facturaService.findFacturaById(id);
            return ResponseEntity.ok(factura);
        } catch (Exception e) {
            if (e.getMessage().contains("no encontrada")) {
                return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("")
    public ResponseEntity<?> getAllFacturas() {
        // Considerar paginación para este endpoint si pueden ser muchas facturas.
        // Endpoint probablemente para administradores.
        try {
            List<FacturaDTO> facturas = facturaService.findAllFacturas();
            return ResponseEntity.ok(facturas);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Endpoint para obtener la factura de un pedido específico
    @GetMapping("/pedido/{pedidoId}")
    public ResponseEntity<?> getFacturaByPedidoId(@PathVariable Long pedidoId) {
        try {
            FacturaDTO factura = facturaService.findByPedidoId(pedidoId);
            if (factura != null) {
                return ResponseEntity.ok(factura);
            } else {
                return new ResponseEntity<>("No se encontró factura para el pedido ID: " + pedidoId, HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            if (e.getMessage().contains("Pedido no encontrado")) { // Si el pedido en sí no existe
                return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Normalmente no se crean/actualizan/eliminan facturas directamente vía API de esta forma.
    // La creación es automática o un proceso interno.
    // La anulación de una factura es un proceso contable más complejo (nota de crédito).
}