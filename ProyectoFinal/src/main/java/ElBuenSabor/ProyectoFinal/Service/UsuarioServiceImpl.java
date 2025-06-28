package ElBuenSabor.ProyectoFinal.Service;

import ElBuenSabor.ProyectoFinal.DTO.ClientePerfilUpdateDTO;
import ElBuenSabor.ProyectoFinal.Entities.Cliente;
import ElBuenSabor.ProyectoFinal.Entities.Usuario;
import ElBuenSabor.ProyectoFinal.Repositories.ClienteRepository;
import ElBuenSabor.ProyectoFinal.Repositories.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UsuarioServiceImpl extends BaseServiceImpl<Usuario, Long> implements UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final ClienteRepository clienteRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioServiceImpl(
            UsuarioRepository usuarioRepository,
            ClienteRepository clienteRepository,
            PasswordEncoder passwordEncoder) {
        super(usuarioRepository);
        this.usuarioRepository = usuarioRepository;
        this.clienteRepository = clienteRepository;
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

    @Override
    public Usuario save(Usuario usuario) {
        return usuarioRepository.save(usuario);
    }

    // ✅ Método declarado en la interfaz UsuarioService
    @Override
    @Transactional
    public void actualizarPerfilCliente(String email, ClientePerfilUpdateDTO dto) throws Exception {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new Exception("Usuario no encontrado con email: " + email));

        Cliente cliente = clienteRepository.findByUsuario(usuario)
                .orElseThrow(() -> new Exception("Cliente no encontrado para el usuario con email: " + email));

        // Actualizar datos personales
        cliente.setNombre(dto.getNombre());
        cliente.setApellido(dto.getApellido());
        cliente.setTelefono(dto.getTelefono());
        cliente.setFechaNacimiento(dto.getFechaNacimiento());
        clienteRepository.save(cliente);

        // Actualizar email si cambió
        usuario.setEmail(dto.getEmail());

        // Validar y cambiar contraseña si corresponde
        if (dto.getPasswordActual() != null && !dto.getPasswordActual().isBlank()) {
            if (!passwordEncoder.matches(dto.getPasswordActual(), usuario.getPassword())) {
                throw new Exception("La contraseña actual es incorrecta.");
            }
            if (!dto.getNuevaPassword().equals(dto.getRepetirPassword())) {
                throw new Exception("Las nuevas contraseñas no coinciden.");
            }
            usuario.setPassword(passwordEncoder.encode(dto.getNuevaPassword()));
        }

        usuarioRepository.save(usuario);
    }
}
