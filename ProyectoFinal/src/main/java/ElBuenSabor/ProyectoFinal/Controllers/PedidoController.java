package ElBuenSabor.ProyectoFinal.Controllers;

import ElBuenSabor.ProyectoFinal.DTO.PedidoCreateDTO;
import ElBuenSabor.ProyectoFinal.DTO.PedidoResponseDTO;
import ElBuenSabor.ProyectoFinal.Entities.Estado;
import ElBuenSabor.ProyectoFinal.Service.PedidoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/pedidos")
@CrossOrigin(origins = "*") // Ajustar según necesidades
public class PedidoController {

    @Autowired
    private PedidoService pedidoService;

    // Endpoint para que un cliente cree un nuevo pedido
    @PostMapping("")
    public ResponseEntity<?> crearPedido(@RequestBody PedidoCreateDTO pedidoCreateDTO) {
        try {
            PedidoResponseDTO nuevoPedido = pedidoService.crearPedido(pedidoCreateDTO);
            return new ResponseEntity<>(nuevoPedido, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    // Endpoint para obtener un pedido por su ID
    // Tanto clientes (para sus propios pedidos) como empleados/admins podrían usarlo.
    // La lógica de autorización (quién puede ver qué) se manejaría con Spring Security.
    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerPedidoPorId(@PathVariable Long id) {
        try {
            PedidoResponseDTO pedido = pedidoService.findPedidoById(id);
            return ResponseEntity.ok(pedido);
        } catch (Exception e) {
            // Si la excepción es "Pedido no encontrado", podría devolverse NOT_FOUND
            if (e.getMessage().contains("no encontrado")) {
                return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Endpoint para que un cliente obtenga su historial de pedidos
    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<?> obtenerPedidosPorCliente(@PathVariable Long clienteId) {
        // Aquí debería haber seguridad para asegurar que el clienteId corresponde al usuario autenticado
        // o que el usuario autenticado es un administrador.
        try {
            List<PedidoResponseDTO> pedidos = pedidoService.findByClienteId(clienteId); //
            return ResponseEntity.ok(pedidos);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Endpoint para que empleados/administradores listen pedidos por estado
    @GetMapping("/estado/{estado}")
    public ResponseEntity<?> obtenerPedidosPorEstado(@PathVariable Estado estado) {
        // Este endpoint probablemente sería para roles de empleado/admin.
        try {
            List<PedidoResponseDTO> pedidos = pedidoService.findByEstado(estado); //
            return ResponseEntity.ok(pedidos);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Endpoint para que empleados/administradores listen todos los pedidos
    @GetMapping("")
    public ResponseEntity<?> listarTodosLosPedidos() {
        // Este endpoint probablemente sería para roles de empleado/admin.
        try {
            List<PedidoResponseDTO> pedidos = pedidoService.findAllPedidos();
            return ResponseEntity.ok(pedidos);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    // Endpoint para que empleados/administradores cambien el estado de un pedido
    @PatchMapping("/{id}/cambiar-estado")
    public ResponseEntity<?> cambiarEstadoPedido(@PathVariable Long id, @RequestParam Estado nuevoEstado) {
        // Endpoint para empleados/admins.
        try {
            PedidoResponseDTO pedidoActualizado = pedidoService.cambiarEstadoPedido(id, nuevoEstado);
            return ResponseEntity.ok(pedidoActualizado);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    // No suele haber un PUT para actualizar un pedido completo una vez creado,
    // las modificaciones suelen ser cambios de estado o cancelación (que es un cambio de estado).
    // La cancelación podría ser un endpoint específico si involucra lógica adicional más allá de cambiar el estado.

    // @DeleteMapping("/{id}") // Borrar pedidos podría ser complejo debido a facturas, stock, etc.
    // public ResponseEntity<?> cancelarPedido(@PathVariable Long id) {
    //     // Usualmente, los pedidos no se eliminan físicamente, se cancelan (cambio de estado).
    //     // Si se implementa delete, el servicio debe manejar la lógica de anulación (reponer stock, etc.)
    //     try {
    //         // Lógica para cancelar podría ser cambiar estado a CANCELADO
    //         pedidoService.cambiarEstadoPedido(id, Estado.CANCELADO);
    //         return ResponseEntity.ok("Pedido cancelado.");
    //     } catch (Exception e) {
    //         return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    //     }
    // }
}