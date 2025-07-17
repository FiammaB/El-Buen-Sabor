package ElBuenSabor.ProyectoFinal.Mappers;

import ElBuenSabor.ProyectoFinal.DTO.PersonaCreateDTO;
import ElBuenSabor.ProyectoFinal.DTO.PersonaDTO;
import ElBuenSabor.ProyectoFinal.DTO.PersonaEmpleadoCreateDTO;
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
    @Mapping(source = "usuario.username", target = "nombre")
    @Mapping(source = "usuario.email", target = "emailUsuario")
    PersonaDTO toDTO(Persona persona);

    Persona toEntity(PersonaDTO dto);

    Persona toEntity(PersonaCreateDTO createDTO);

    @Mapping(source = "nombre", target = "nombre")
    Persona toEntity(PersonaEmpleadoCreateDTO dto);

    List<PersonaDTO> toDTOList(List<Persona> personas);
}
