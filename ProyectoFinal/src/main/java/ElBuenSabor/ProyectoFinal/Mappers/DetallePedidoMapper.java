package ElBuenSabor.ProyectoFinal.Mappers;

import ElBuenSabor.ProyectoFinal.DTO.DetallePedidoCreateDTO;
import ElBuenSabor.ProyectoFinal.DTO.DetallePedidoDTO;
import ElBuenSabor.ProyectoFinal.Entities.DetallePedido;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", uses = {
        ArticuloManufacturadoMapper.class,
        ArticuloInsumoMapper.class
})
public interface DetallePedidoMapper {

    DetallePedidoDTO toDTO(DetallePedido detalle);

    DetallePedido toEntity(DetallePedidoDTO dto);

    DetallePedido toEntity(DetallePedidoCreateDTO dto);

    List<DetallePedidoDTO> toDTOList(List<DetallePedido> detalles);
}
