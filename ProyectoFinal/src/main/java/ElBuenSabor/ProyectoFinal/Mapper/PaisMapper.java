package ElBuenSabor.ProyectoFinal.Mapper;


import ElBuenSabor.ProyectoFinal.DTO.PaisDTO;
import ElBuenSabor.ProyectoFinal.Entities.Pais;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PaisMapper extends BaseMapper<Pais, PaisDTO> {

    PaisDTO PaisToPaisDto(Pais pais);
    Pais PaisDtoToPais(PaisDTO paisDTO);

    List<PaisDTO> PaisToPaisDtoList(List<Pais> paises);
    List<Pais> PaisDtoListToPaisList(List<PaisDTO> paisesDTO);

}
