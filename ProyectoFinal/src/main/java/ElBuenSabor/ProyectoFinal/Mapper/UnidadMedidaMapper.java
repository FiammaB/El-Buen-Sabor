package ElBuenSabor.ProyectoFinal.Mapper;
import ElBuenSabor.ProyectoFinal.DTO.ArticuloManufacturadoDetalleDTO;
import ElBuenSabor.ProyectoFinal.DTO.UnidadMedidaDTO;
import ElBuenSabor.ProyectoFinal.Entities.ArticuloManufacturadoDetalle;
import ElBuenSabor.ProyectoFinal.Entities.UnidadMedida;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UnidadMedidaMapper extends BaseMapper <UnidadMedida, UnidadMedidaDTO> {

    UnidadMedidaDTO unidadMedidaToUnidadMedidaDto(UnidadMedida unidadMedida);
    UnidadMedida unidadMedidaDtoToUnidadMedida(UnidadMedidaDTO unidadMedidaDTO);

    List<UnidadMedidaDTO> unidadMedidaToUnidadMedidaDtoList(List<UnidadMedida> unidadMedidaList);
    List<UnidadMedida> unidadMedidaDtoToUnidadMedidaList(List<UnidadMedidaDTO> unidadMedidaDTOList);

}
