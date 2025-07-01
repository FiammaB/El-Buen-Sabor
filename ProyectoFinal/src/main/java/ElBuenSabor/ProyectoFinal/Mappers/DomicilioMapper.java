// ProyectoFinal/src/main/java/ElBuenSabor/ProyectoFinal/Mappers/DomicilioMapper.java
package ElBuenSabor.ProyectoFinal.Mappers;

import ElBuenSabor.ProyectoFinal.DTO.DomicilioDTO;
import ElBuenSabor.ProyectoFinal.Entities.Domicilio;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping; // Importa Mapping

import java.util.List;


@Mapper(componentModel = "spring", uses = {LocalidadMapper.class})
public interface DomicilioMapper {
    DomicilioDTO toDTO(Domicilio domicilio);

    Domicilio toEntity(DomicilioDTO dto);

    List<DomicilioDTO> toDTOList(List<Domicilio> domicilios);
}