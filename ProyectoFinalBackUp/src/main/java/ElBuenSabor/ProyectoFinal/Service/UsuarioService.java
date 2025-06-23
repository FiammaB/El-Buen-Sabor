package ElBuenSabor.ProyectoFinal.Service;

import ElBuenSabor.ProyectoFinal.Entities.Usuario;
import java.util.List;
import java.util.Optional;

public interface UsuarioService extends BaseService<Usuario, Long> { // Asumiendo ID Long
    Optional<Usuario> findByUsername(String username) throws Exception;
    // Podríamos añadir métodos para listar usuarios con DTOs específicos si es necesario.
    // List<UsuarioResponseDTO> findAllUsuariosDTO() throws Exception;
}