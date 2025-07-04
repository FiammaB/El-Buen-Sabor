package ElBuenSabor.ProyectoFinal.Controllers;

import ElBuenSabor.ProyectoFinal.DTO.PedidoCreateDTO;
import ElBuenSabor.ProyectoFinal.DTO.PedidoResponseDTO; // Importa tu DTO de respuesta
import ElBuenSabor.ProyectoFinal.Entities.Estado; // Para el método actualizarEstadoPedido
import ElBuenSabor.ProyectoFinal.Service.PedidoService; // Usa la interfaz de tu servicio
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pedidos")
@CrossOrigin(origins = "http://localhost:5173")
public class PedidoController {

    @Autowired
    private PedidoService pedidoService; // Usa la interfaz PedidoService

    // Endpoint para crear un nuevo pedido desde el frontend
    // Este endpoint se usaría si quieres crear un pedido en tu DB y obtener su ID ANTES de ir a Mercado Pago,
    // o si el pago es en efectivo y no usa MP.
    @PostMapping
    public ResponseEntity<?> createPedido(@RequestBody PedidoCreateDTO pedidoDTO) {
        try {
            PedidoResponseDTO newPedido = pedidoService.crearPedido(pedidoDTO); // Llama al método del servicio
            return ResponseEntity.status(HttpStatus.CREATED).body(newPedido); // Devuelve el DTO completo
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error al crear el pedido: " + e.getMessage());
        }
    }

    // Endpoint para obtener un pedido por ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getPedidoById(@PathVariable Long id) {
        try {
            PedidoResponseDTO pedido = pedidoService.findPedidoById(id); // Llama al método del servicio
            return ResponseEntity.ok(pedido); // Devuelve el DTO completo
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Pedido no encontrado o error: " + e.getMessage());
        }
    }

    // Endpoint para obtener todos los pedidos
    @GetMapping
    public ResponseEntity<List<PedidoResponseDTO>> getAllPedidos() {
        try {
            List<PedidoResponseDTO> pedidos = pedidoService.findAllPedidos(); // Llama al método del servicio
            return ResponseEntity.ok(pedidos); // Devuelve la lista de DTOs
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null); // O un mensaje de error más específico
        }
    }

    // Endpoint para actualizar el estado de un pedido (ej. por un empleado)
    // El webhook de MP se encargaría del estado de pago, este es para otros cambios.
    @PutMapping("/{id}/estado")
    public ResponseEntity<?> updatePedidoEstado(@PathVariable Long id, @RequestParam Estado nuevoEstado) {
        try {
            PedidoResponseDTO updatedPedido = pedidoService.actualizarEstadoPedido(id, nuevoEstado);
            return ResponseEntity.ok(updatedPedido);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error al actualizar el estado del pedido: " + e.getMessage());
        }
    }

    // Endpoint para obtener pedidos por ID de cliente
    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<List<PedidoResponseDTO>> getPedidosByClienteId(@PathVariable Long clienteId) {
        try {
            List<PedidoResponseDTO> pedidos = pedidoService.findPedidosByClienteId(clienteId);
            return ResponseEntity.ok(pedidos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    // Endpoint para obtener pedidos por estado
    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<PedidoResponseDTO>> getPedidosByEstado(@PathVariable Estado estado) {
        try {
            List<PedidoResponseDTO> pedidos = pedidoService.findPedidosByEstado(estado);
            return ResponseEntity.ok(pedidos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    // Endpoint para marcar un pedido como pagado (ej. para pagos en efectivo)
    // Este NO se usaría para pagos de Mercado Pago, ya que eso lo manejaría el webhook.
    @PutMapping("/{id}/marcar-pagado")
    public ResponseEntity<?> marcarPedidoComoPagado(@PathVariable Long id) {
        try {
            PedidoResponseDTO updatedPedido = pedidoService.marcarPedidoComoPagado(id);
            return ResponseEntity.ok(updatedPedido);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error al marcar pedido como pagado: " + e.getMessage());
        }
    }
}