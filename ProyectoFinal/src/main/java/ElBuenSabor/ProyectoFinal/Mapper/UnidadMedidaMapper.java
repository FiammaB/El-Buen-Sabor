package ElBuenSabor.ProyectoFinal.Mapper;
import ElBuenSabor.ProyectoFinal.DTO.UnidadMedidaDTO;
import ElBuenSabor.ProyectoFinal.Entities.UnidadMedida;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UnidadMedidaMapper {

    UnidadMedidaDTO unidadMedidaToUnidadMedidaDto(UnidadMedida unidadMedida);
    UnidadMedida unidadMedidaDtoToUnidadMedida(UnidadMedidaDTO unidadMedidaDTO);

}
