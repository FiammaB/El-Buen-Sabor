// ProyectoFinal/src/main/java/ElBuenSabor/ProyectoFinal/Mappers/DomicilioMapper.java
package ElBuenSabor.ProyectoFinal.Mappers;

import ElBuenSabor.ProyectoFinal.DTO.DomicilioDTO;
import ElBuenSabor.ProyectoFinal.Entities.Domicilio;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping; // Importa Mapping

import java.util.List;


@Mapper(componentModel = "spring", uses = {LocalidadMapper.class})
public interface DomicilioMapper {

    // Cuando mapeas de Domicilio (Entity) a DomicilioDTO, ignora la colección 'clientes'

    DomicilioDTO toDTO(Domicilio domicilio);

    // Cuando mapeas de DomicilioDTO a Domicilio (Entity), no habrá un 'clientes' en el DTO para mapear.
    Domicilio toEntity(DomicilioDTO dto);

    List<DomicilioDTO> toDTOList(List<Domicilio> domicilios);
}