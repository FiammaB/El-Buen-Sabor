package ElBuenSabor.ProyectoFinal.Mapper;

import ElBuenSabor.ProyectoFinal.Entities.DetallePedido;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface DetallePedidoMapper {

    DetallePedidoFullDTO detallePedidoToDetallePedidoFullDTO(DetallePedido detallePedido);
    DetallePedido detallePedidoFullDTOToDetallePedido(DetallePedidoFullDTO detallePedidoFullDTO);

    DetallePedidoShortDTO detallePedidoToDetallePedidoShortDTO(DetallePedido detallePedido);

}
