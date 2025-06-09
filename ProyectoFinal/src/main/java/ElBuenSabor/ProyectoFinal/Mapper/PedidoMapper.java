package ElBuenSabor.ProyectoFinal.Mapper;

import ElBuenSabor.ProyectoFinal.Entities.Pedido;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface PedidoMapper {

    PedidoFullDTO pedidoToPedidoFullDTO(Pedido pedido);
    Pedido pedidoFullDTOToPedido(PedidoFullDTO pedidoFullDTO);

    @Mappings({
            @Mapping(source = "cliente.id", target = "clienteId"),
            @Mapping(source = "domicilioEntrega.id", target = "domicilioEntregaId"),
            @Mapping(source = "sucursal.id", target = "sucursalId")
    })
    PedidoShortDTO PedidoToPedidoShortDTO(Pedido pedido);

}
