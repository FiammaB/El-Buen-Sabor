package ElBuenSabor.ProyectoFinal.Mappers;

import ElBuenSabor.ProyectoFinal.DTO.ProvinciaDTO;
import ElBuenSabor.ProyectoFinal.Entities.Provincia;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring", uses = PaisMapper.class)
public interface ProvinciaMapper {

    ProvinciaDTO toDTO(Provincia provincia);

    Provincia toEntity(ProvinciaDTO dto);

    List<ProvinciaDTO> toDTOList(List<Provincia> provincias);
}
