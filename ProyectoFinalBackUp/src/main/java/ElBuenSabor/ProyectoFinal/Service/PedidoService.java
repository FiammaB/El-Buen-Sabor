// src/main/java/ElBuenSabor/ProyectoFinal/Service/PedidoService.java
package ElBuenSabor.ProyectoFinal.Service;

import ElBuenSabor.ProyectoFinal.DTO.PedidoCreateDTO;
import ElBuenSabor.ProyectoFinal.DTO.PedidoResponseDTO;
import ElBuenSabor.ProyectoFinal.Entities.Estado;
import ElBuenSabor.ProyectoFinal.Entities.Pedido; // ¡Importa Pedido aquí!
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

public interface PedidoService extends BaseService<Pedido, Long> {

    // Método original para crear y devolver DTO al frontend
    PedidoResponseDTO crearPedido(PedidoCreateDTO pedidoDTO) throws Exception;

    // *** NUEVO MÉTODO: Para crear el pedido y devolver la ENTIDAD PERSISTIDA ***
    Pedido crearPedidoEntidad(PedidoCreateDTO pedidoDTO) throws Exception;


    // Métodos para buscar pedidos y devolver DTOs
    PedidoResponseDTO findPedidoById(Long id) throws Exception;
    List<PedidoResponseDTO> findAllPedidos() throws Exception;
    List<PedidoResponseDTO> findPedidosByClienteId(Long clienteId) throws Exception;
    List<PedidoResponseDTO> findPedidosByEstado(Estado estado) throws Exception;

    // Método para actualizar el estado del pedido
    PedidoResponseDTO actualizarEstadoPedido(Long pedidoId, Estado nuevoEstado) throws Exception;

    // Método para marcar pedido como pagado (ej. efectivo)
    PedidoResponseDTO marcarPedidoComoPagado(Long pedidoId) throws Exception;
}