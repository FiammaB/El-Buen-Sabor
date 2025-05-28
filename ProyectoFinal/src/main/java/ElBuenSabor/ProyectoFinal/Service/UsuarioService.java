package ElBuenSabor.ProyectoFinal.Service;

import ElBuenSabor.ProyectoFinal.Entities.Usuario;
import java.util.List;
import java.util.Optional;

public interface UsuarioService extends BaseService<Usuario, Long> {
    // Métodos para buscar usuarios (activos por defecto debido a @Where en hijas)
    Optional<Usuario> findByUsername(String username) throws Exception;
    Optional<Usuario> findByAuth0Id(String auth0Id) throws Exception; // Para login social

    // Métodos para buscar usuarios incluyendo los 'baja = true' (para validaciones, admin)
    Optional<Usuario> findByUsernameRaw(String username) throws Exception;
    boolean existsByUsernameRaw(String username) throws Exception;
    Optional<Usuario> findByAuth0IdRaw(String auth0Id) throws Exception;

    // Los métodos softDelete, reactivate, findAllIncludingDeleted, findByIdIncludingDeleted
    // son heredados de BaseService. La implementación en UsuarioServiceImpl
    // debe ser consciente de que opera sobre una entidad abstracta.
    // El borrado lógico real (marcar 'baja') sucede en las entidades concretas.
}
