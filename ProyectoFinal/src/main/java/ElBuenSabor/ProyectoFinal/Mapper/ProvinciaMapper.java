package ElBuenSabor.ProyectoFinal.Mapper;

import ElBuenSabor.ProyectoFinal.DTO.ProvinciaDTO;
import ElBuenSabor.ProyectoFinal.Entities.Provincia;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProvinciaMapper extends BaseMapper<Provincia, ProvinciaDTO> {

    ProvinciaDTO provinciaToProvinciaDto(Provincia provincia);
    Provincia provinciaDtoToProvincia(ProvinciaDTO provinciaDTO);

    List<ProvinciaDTO> provinciaListToProvinciaDtoList(List<Provincia> provincias);
    List<Provincia> provinciaDtoToProvinciaList(List<ProvinciaDTO> provinciasDTO);

}
