package ElBuenSabor.ProyectoFinal.Mapper;

import ElBuenSabor.ProyectoFinal.DTO.DetallePedidoDTO;
import ElBuenSabor.ProyectoFinal.Entities.DetallePedido;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface DetallePedidoMapper extends BaseMapper <DetallePedido, DetallePedidoDTO>{

    DetallePedidoDTO detallePedidoToDetallePedidoFullDTO(DetallePedido detallePedido);
    DetallePedido detallePedidoFullDTOToDetallePedido(DetallePedidoDTO detallePedidoFullDTO);

    List<DetallePedidoDTO> detallePedidoFullDTOToDetallePedidoList(List<DetallePedidoDTO> detallePedidoDTOList);
    List<DetallePedido> detallePedidoDTOToDetallePedidoList(List<DetallePedido> detallePedidoList);

}
