package ElBuenSabor.ProyectoFinal.Mapper;


import ElBuenSabor.ProyectoFinal.DTO.UsuarioDTO;
import ElBuenSabor.ProyectoFinal.Entities.Usuario;

import org.mapstruct.Mapper;
import java.util.List;

@Mapper(componentModel = "spring")
public interface UsuarioMapper extends BaseMapper <Usuario, UsuarioDTO>{

    UsuarioDTO usuarioToUsuarioDto(Usuario usuario);
    Usuario usuarioDtoToUsuario(UsuarioDTO usuarioDTO);

    List<UsuarioDTO> usuarioListToUsuarioDtoList(List<Usuario> usuarios);
    List<Usuario> usuarioDtoListToUsuarioList(List<UsuarioDTO> usuarioDTO);

}
