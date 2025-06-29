package ElBuenSabor.ProyectoFinal.Service;

import ElBuenSabor.ProyectoFinal.DTO.ClientePerfilUpdateDTO;
import ElBuenSabor.ProyectoFinal.DTO.UsuarioDTO;
import ElBuenSabor.ProyectoFinal.Entities.Usuario;

public interface UsuarioService extends BaseService<Usuario, Long> {

    Usuario findByEmail(String email);

    Usuario save(Usuario usuario);

    void actualizarPerfilCliente(String email, ClientePerfilUpdateDTO dto) throws Exception;

    Usuario registrarCocinero(UsuarioDTO usuarioDTO);

    Usuario registrarCajero(UsuarioDTO usuarioDTO);
}
