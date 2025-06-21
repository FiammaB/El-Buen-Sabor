package ElBuenSabor.ProyectoFinal.Service;

import ElBuenSabor.ProyectoFinal.Entities.Usuario;
import ElBuenSabor.ProyectoFinal.Repositories.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UsuarioServiceImpl extends BaseServiceImpl<Usuario, Long> implements UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioServiceImpl(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        super(usuarioRepository);
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Usuario findByEmail(String email) {
        return usuarioRepository.findByEmail(email).orElse(null);
    }

    @Override
    @Transactional
    public Usuario update(Long id, Usuario updatedUsuario) throws Exception {
        try {
            Usuario existing = findById(id);

            existing.setNombre(updatedUsuario.getNombre());
            existing.setEmail(updatedUsuario.getEmail());
            existing.setRol(updatedUsuario.getRol());

            if (updatedUsuario.getPassword() != null && !updatedUsuario.getPassword().isEmpty()) {
                existing.setPassword(passwordEncoder.encode(updatedUsuario.getPassword()));
            }

            return usuarioRepository.save(existing);
        } catch (Exception e) {
            throw new Exception("Error al actualizar el usuario: " + e.getMessage());
        }
    }
}
