package ElBuenSabor.ProyectoFinal.Service;

import ElBuenSabor.ProyectoFinal.Entities.Usuario;

public interface UsuarioService extends BaseService<Usuario, Long> {
    Usuario findByEmail(String email);
}
