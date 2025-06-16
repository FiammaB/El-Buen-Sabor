package ElBuenSabor.ProyectoFinal.Mappers;

import ElBuenSabor.ProyectoFinal.DTO.PedidoCreateDTO;
import ElBuenSabor.ProyectoFinal.DTO.PedidoDTO;
import ElBuenSabor.ProyectoFinal.Entities.Pedido;
import org.mapstruct.*;

@Mapper(componentModel = "spring", uses = {
        ClienteMapper.class,
        DomicilioMapper.class,
        SucursalMapper.class,
        UsuarioMapper.class,       // ← para el empleado
        FacturaMapper.class,
        DetallePedidoMapper.class
})
public interface PedidoMapper {

    PedidoDTO toDTO(Pedido pedido);

    Pedido toEntity(PedidoCreateDTO dto);
}
