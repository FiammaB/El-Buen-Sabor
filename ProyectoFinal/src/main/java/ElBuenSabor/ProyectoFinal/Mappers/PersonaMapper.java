package ElBuenSabor.ProyectoFinal.Mappers;

import ElBuenSabor.ProyectoFinal.DTO.PersonaCreateDTO;
import ElBuenSabor.ProyectoFinal.DTO.PersonaDTO;
import ElBuenSabor.ProyectoFinal.Entities.Persona;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", uses = {
        DomicilioMapper.class,
        LocalidadMapper.class,
        ProvinciaMapper.class,
        PaisMapper.class
})
public interface PersonaMapper {

    PersonaDTO toDTO(Persona persona);

    Persona toEntity(PersonaDTO dto);

    Persona toEntity(PersonaCreateDTO createDTO);

    List<PersonaDTO> toDTOList(List<Persona> personas);
}
