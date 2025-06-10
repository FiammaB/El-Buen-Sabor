package ElBuenSabor.ProyectoFinal.Mapper;

import ElBuenSabor.ProyectoFinal.DTO.PedidoDTO;
import ElBuenSabor.ProyectoFinal.Entities.Pedido;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PedidoMapper extends BaseMapper <Pedido, PedidoDTO>{

    PedidoDTO pedidoToPedidoFullDTO(Pedido pedido);
    Pedido pedidoFullDTOToPedido(PedidoDTO pedidoFullDTO);

    List<PedidoDTO> pedidoFullDTOToPedidoDTO(List<Pedido> pedidoFullDTO);
    List<Pedido> pedidoDTOToPedidoFullDTO(List<PedidoDTO> pedidoDTO);

}
