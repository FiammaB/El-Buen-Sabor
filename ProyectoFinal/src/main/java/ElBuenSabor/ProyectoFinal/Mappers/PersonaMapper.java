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

    @Mapping(source = "usuario.username", target = "nombreUsuario")
    @Mapping(source = "usuario.email", target = "emailUsuario")
    PersonaDTO toDTO(Persona persona);

    @InheritInverseConfiguration
    @Mapping(target = "nombre", source = "nombreUsuario") // Agregá esto para el campo de Persona
    @Mapping(target = "usuario.username", source = "nombreUsuario") // También para el Usuario
    Persona toEntity(PersonaDTO dto);

    Persona toEntity(PersonaCreateDTO createDTO);

    List<PersonaDTO> toDTOList(List<Persona> personas);
}
