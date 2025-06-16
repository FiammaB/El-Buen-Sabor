package ElBuenSabor.ProyectoFinal.Mappers;

import ElBuenSabor.ProyectoFinal.DTO.UnidadMedidaDTO;
import ElBuenSabor.ProyectoFinal.Entities.UnidadMedida;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UnidadMedidaMapper {

    UnidadMedidaDTO toDTO(UnidadMedida entity);

    UnidadMedida toEntity(UnidadMedidaDTO dto);

    List<UnidadMedidaDTO> toDTOList(List<UnidadMedida> unidades);
}