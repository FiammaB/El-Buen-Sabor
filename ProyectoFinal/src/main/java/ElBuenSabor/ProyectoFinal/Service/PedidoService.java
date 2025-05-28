package ElBuenSabor.ProyectoFinal.Service;

import ElBuenSabor.ProyectoFinal.DTO.PedidoCreateDTO;
import ElBuenSabor.ProyectoFinal.DTO.PedidoResponseDTO;
import ElBuenSabor.ProyectoFinal.Entities.Estado;
import ElBuenSabor.ProyectoFinal.Entities.Pedido; // Para Optional
import ElBuenSabor.ProyectoFinal.Entities.Usuario; // Para el actor
import java.util.List;
import java.util.Optional;

public interface PedidoService extends BaseService<Pedido, Long> {

    PedidoResponseDTO crearPedido(PedidoCreateDTO pedidoCreateDTO, Usuario actorCliente) throws Exception;

    // El actor es importante para la lógica de permisos de cambio de estado
    PedidoResponseDTO cambiarEstadoPedido(Long pedidoId, Estado nuevoEstado, Usuario actorEmpleado) throws Exception;

    List<PedidoResponseDTO> findPedidosByClienteId(Long clienteId) throws Exception; // Solo activos
    List<PedidoResponseDTO> findPedidosByEstado(Estado estado, boolean soloActivos) throws Exception;
    List<PedidoResponseDTO> findPedidosBySucursalAndEstado(Long sucursalId, Estado estado, boolean soloActivos) throws Exception;

    PedidoResponseDTO findPedidoByIdDTO(Long id) throws Exception; // Activo
    List<PedidoResponseDTO> findAllPedidosDTO(boolean soloActivos) throws Exception;

    // Para la lógica de anulación de factura
    void reponerStockPorPedido(Pedido pedido) throws Exception;

    // Heredados de BaseService: softDelete (anular), reactivate
    // Implementaciones específicas para DTOs:
    List<PedidoResponseDTO> findAllPedidosIncludingAnulados() throws Exception;
    PedidoResponseDTO findPedidoByIdIncludingAnulado(Long id) throws Exception;
}
