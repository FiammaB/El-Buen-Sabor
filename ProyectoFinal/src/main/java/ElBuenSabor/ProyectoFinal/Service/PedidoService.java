package ElBuenSabor.ProyectoFinal.Service;

import ElBuenSabor.ProyectoFinal.DTO.PedidoCreateDTO;
// Considera un PedidoUpdateDTO si la actualización es diferente a la creación o muy limitada
import ElBuenSabor.ProyectoFinal.DTO.PedidoResponseDTO; // Para devolver datos formateados
import ElBuenSabor.ProyectoFinal.Entities.Estado;
import ElBuenSabor.ProyectoFinal.Entities.Pedido;

import java.util.List;

public interface PedidoService extends BaseService<Pedido, Long> {

    PedidoResponseDTO crearPedido(PedidoCreateDTO pedidoCreateDTO) throws Exception;

    PedidoResponseDTO cambiarEstadoPedido(Long pedidoId, Estado nuevoEstado) throws Exception;

    List<PedidoResponseDTO> findByClienteId(Long clienteId) throws Exception; //

    List<PedidoResponseDTO> findByEstado(Estado estado) throws Exception; //

    PedidoResponseDTO findPedidoById(Long id) throws Exception; // Para obtener un DTO específico

    List<PedidoResponseDTO> findAllPedidos() throws Exception; // Para obtener todos como DTOs
}