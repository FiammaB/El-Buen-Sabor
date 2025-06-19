package ElBuenSabor.ProyectoFinal.Mappers;

import ElBuenSabor.ProyectoFinal.DTO.PedidoCreateDTO;
import ElBuenSabor.ProyectoFinal.DTO.PedidoDTO;
import ElBuenSabor.ProyectoFinal.Entities.Pedido;
import org.mapstruct.*;

@Mapper(componentModel = "spring", uses = {
        ClienteMapper.class,
        DomicilioMapper.class,
        SucursalMapper.class,
        UsuarioMapper.class,
        FacturaMapper.class,
        DetallePedidoMapper.class
})
public interface PedidoMapper {
    @Mapping(target = "domicilio", source = "domicilioEntrega")
    @Mapping(target = "detalles", source = "detallesPedidos")
    PedidoDTO toDTO(Pedido pedido);

    @Mapping(target = "domicilioEntrega", ignore = true)
    @Mapping(target = "detallesPedidos", ignore = true)
    @Mapping(target = "cliente", ignore = true)
    @Mapping(target = "sucursal", ignore = true)
    @Mapping(target = "empleado", ignore = true)
    @Mapping(target = "factura", ignore = true)
    Pedido toEntity(PedidoCreateDTO dto);
}
