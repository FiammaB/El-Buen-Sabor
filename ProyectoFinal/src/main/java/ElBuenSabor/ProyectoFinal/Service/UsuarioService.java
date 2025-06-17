package ElBuenSabor.ProyectoFinal.Service;

import ElBuenSabor.ProyectoFinal.Auth.RegisterRequest;
import ElBuenSabor.ProyectoFinal.Entities.Usuario;

import java.util.List;

public interface UsuarioService  extends BaseService<Usuario, Long>{
    Usuario login(String email, String password);
    Usuario register(RegisterRequest request);


}
