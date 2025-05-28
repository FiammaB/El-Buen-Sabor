package ElBuenSabor.ProyectoFinal.Service;

import ElBuenSabor.ProyectoFinal.Entities.Usuario;
import ElBuenSabor.ProyectoFinal.Repositories.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioServiceImpl extends BaseServiceImpl<Usuario, Long> implements UsuarioService {

    private final UsuarioRepository usuarioRepository;

    @Autowired
    public UsuarioServiceImpl(UsuarioRepository usuarioRepository) {
        super(usuarioRepository); // Asegúrate que UsuarioRepository sea JpaRepository<Usuario, Long>
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Usuario> findByUsername(String username) throws Exception {
        try {
            // Este método será afectado por @Where en las entidades hijas (Cliente, Empleado)
            return usuarioRepository.findByUsername(username);
        } catch (Exception e) {
            throw new Exception("Error al buscar usuario activo por username: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Usuario> findByAuth0Id(String auth0Id) throws Exception {
        try {
            return usuarioRepository.findByAuth0Id(auth0Id); // Afectado por @Where
        } catch (Exception e) {
            throw new Exception("Error al buscar usuario activo por auth0Id: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Usuario> findByUsernameRaw(String username) throws Exception {
        try {
            return usuarioRepository.findByUsernameRaw(username);
        } catch (Exception e) {
            throw new Exception("Error al buscar usuario (raw) por username: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByUsernameRaw(String username) throws Exception {
        try {
            return usuarioRepository.existsByUsernameRaw(username);
        } catch (Exception e) {
            throw new Exception("Error al verificar existencia de usuario (raw) por username: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Usuario> findByAuth0IdRaw(String auth0Id) throws Exception {
        try {
            return usuarioRepository.findByAuth0IdRaw(auth0Id);
        } catch (Exception e) {
            throw new Exception("Error al buscar usuario (raw) por auth0Id: " + e.getMessage(), e);
        }
    }

    // --- Implementación de métodos de BaseService para borrado lógico ---
    // Estos métodos operan sobre la entidad Usuario genérica.
    // La lógica específica de "no se puede borrar si está en uso" o
    // "no se puede reactivar si X" debería estar en los servicios de las subclases
    // (ClienteServiceImpl, EmpleadoServiceImpl) antes de llamar a estos métodos base.

    @Override
    @Transactional(readOnly = true)
    public List<Usuario> findAllIncludingDeleted() throws Exception {
        return usuarioRepository.findAllRaw();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Usuario> findByIdIncludingDeleted(Long id) throws Exception {
        return usuarioRepository.findByIdRaw(id);
    }

    @Override
    @Transactional
    public Usuario softDelete(Long id) throws Exception {
        Usuario usuario = this.findByIdIncludingDeleted(id)
                .orElseThrow(() -> new Exception("Usuario no encontrado con ID: " + id + " para dar de baja."));
        if (usuario.isBaja()) {
            throw new Exception("El usuario ya está dado de baja.");
        }
        // Aquí, la lógica específica (ej. no dar de baja si es el último admin)
        // debería ser manejada por el servicio de la subclase (EmpleadoServiceImpl)
        // antes de llamar a este método genérico.
        usuario.setBaja(true);
        return usuarioRepository.save(usuario);
    }

    @Override
    @Transactional
    public Usuario reactivate(Long id) throws Exception {
        Usuario usuario = this.findByIdIncludingDeleted(id)
                .orElseThrow(() -> new Exception("Usuario no encontrado con ID: " + id + " para reactivar."));
        if (!usuario.isBaja()) {
            throw new Exception("El usuario no está dado de baja, no se puede reactivar.");
        }
        usuario.setBaja(false);
        return usuarioRepository.save(usuario);
    }
}
