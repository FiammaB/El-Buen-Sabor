package ElBuenSabor.ProyectoFinal.Mappers;

import ElBuenSabor.ProyectoFinal.DTO.LocalidadDTO;
import ElBuenSabor.ProyectoFinal.Entities.Localidad;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {ProvinciaMapper.class})
public interface LocalidadMapper {

    LocalidadDTO toDTO(Localidad localidad);

    Localidad toEntity(LocalidadDTO dto);
}