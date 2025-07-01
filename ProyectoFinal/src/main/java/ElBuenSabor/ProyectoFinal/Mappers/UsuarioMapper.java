package ElBuenSabor.ProyectoFinal.Mappers;

import ElBuenSabor.ProyectoFinal.DTO.UsuarioDTO;
import ElBuenSabor.ProyectoFinal.Entities.Usuario;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UsuarioMapper {

    @Mapping(source = "baja", target = "baja")
    @Mapping(source = "primerIngreso", target = "primerIngreso")
    UsuarioDTO toDTO(Usuario usuario);

    @Mapping(source = "baja", target = "baja")
    @Mapping(source = "primerIngreso", target = "primerIngreso")
    Usuario toEntity(UsuarioDTO dto);
}
