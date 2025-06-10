package ElBuenSabor.ProyectoFinal.Mapper;


import ElBuenSabor.ProyectoFinal.DTO.LocalidadDTO;
import ElBuenSabor.ProyectoFinal.Entities.Localidad;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface LocalidadMapper extends BaseMapper<Localidad, LocalidadDTO> {

    LocalidadDTO localidadToLocalidadDto(Localidad localidad);
    Localidad localidadDtoToLocalidad(LocalidadDTO localidadDTO);

    List<LocalidadDTO> localidadToLocalidadDtoList(List<Localidad> localidades);
    List<Localidad> localidadDtoListToLocalidadList(List<LocalidadDTO> localidadesDTO);

}
