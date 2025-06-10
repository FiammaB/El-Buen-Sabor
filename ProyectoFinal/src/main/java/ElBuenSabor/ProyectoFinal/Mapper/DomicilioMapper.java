package ElBuenSabor.ProyectoFinal.Mapper;

import ElBuenSabor.ProyectoFinal.DTO.DomicilioDTO;
import ElBuenSabor.ProyectoFinal.Entities.Domicilio;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface DomicilioMapper extends BaseMapper <Domicilio, DomicilioDTO>{

    DomicilioDTO domicilioToDomicilioDTO(Domicilio domicilio);
    Domicilio domicilioDtoToDomicilio(DomicilioDTO domicilioDTO);

    List<DomicilioDTO> domicilioToDomicilioDtoList(List<Domicilio> domicilios);
    List<Domicilio> domicilioDtoToDomicilioList(List<DomicilioDTO> domicilioDTO);

}
