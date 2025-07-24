package ElBuenSabor.ProyectoFinal.Service;

import ElBuenSabor.ProyectoFinal.DTO.PersonaPerfilUpdateDTO;
import ElBuenSabor.ProyectoFinal.DTO.UsuarioDTO;
import ElBuenSabor.ProyectoFinal.Entities.Persona;
import ElBuenSabor.ProyectoFinal.Entities.Rol;
import ElBuenSabor.ProyectoFinal.Entities.Usuario;
import ElBuenSabor.ProyectoFinal.Mappers.UsuarioMapper;
import ElBuenSabor.ProyectoFinal.Repositories.PersonaRepository;
import ElBuenSabor.ProyectoFinal.Repositories.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UsuarioServiceImpl extends BaseServiceImpl<Usuario, Long> implements UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PersonaRepository personaRepository;
    private final PasswordEncoder passwordEncoder;
    private final UsuarioMapper usuarioMapper;

    public UsuarioServiceImpl(
            UsuarioRepository usuarioRepository,
            PersonaRepository personaRepository,
            PasswordEncoder passwordEncoder,
            UsuarioMapper usuarioMapper) {
        super(usuarioRepository);
        this.usuarioRepository = usuarioRepository;
        this.personaRepository = personaRepository;
        this.passwordEncoder = passwordEncoder;
        this.usuarioMapper = usuarioMapper;
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

            existing.setUsername(updatedUsuario.getUsername());
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

    // ✅ Ahora devuelve la Persona actualizada
    @Override
    @Transactional
    public Persona actualizarPerfil(String email, PersonaPerfilUpdateDTO dto) throws Exception {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new Exception("Usuario no encontrado con email: " + email));

        Persona persona = personaRepository.findByUsuario(usuario)
                .orElseThrow(() -> new Exception("Persona no encontrada para el usuario con email: " + email));

        // ✅ Actualizar datos personales
        persona.setNombre(dto.getNombre());
        persona.setApellido(dto.getApellido());
        persona.setTelefono(dto.getTelefono());
        persona.setFechaNacimiento(dto.getFechaNacimiento());
        personaRepository.save(persona);

        // ✅ Actualizar email si cambió
        usuario.setEmail(dto.getEmail());

        // ✅ Validar y cambiar contraseña si corresponde
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

        return persona; // ✅ Devolvemos la Persona ya actualizada
    }

    // 🔒 Validación de contraseña segura
    private boolean esPasswordSegura(String password) {
        return password.length() >= 8 &&
                password.matches(".*[A-Z].*") &&
                password.matches(".*[a-z].*") &&
                password.matches(".*[!@#$%^&*(),.?\":{}|<>_\\-+=].*");
    }

    @Override
    public Usuario registrarCocinero(UsuarioDTO usuarioDTO) {
        if (usuarioRepository.existsByEmail(usuarioDTO.getEmail())) {
            throw new IllegalArgumentException("Ya existe un usuario registrado con ese email.");
        }

        if (!esPasswordSegura(usuarioDTO.getPassword())) {
            throw new IllegalArgumentException("La contraseña debe tener al menos 8 caracteres, una mayúscula, una minúscula y un símbolo.");
        }

        Usuario usuario = usuarioMapper.toEntity(usuarioDTO);
        usuario.setRol(Rol.COCINERO);
        usuario.setPassword(passwordEncoder.encode(usuarioDTO.getPassword()));
        usuario.setPrimerInicio(true);
        return usuarioRepository.save(usuario);
    }

    @Override
    public Usuario registrarCajero(UsuarioDTO usuarioDTO) {
        if (usuarioRepository.existsByEmail(usuarioDTO.getEmail())) {
            throw new IllegalArgumentException("Ya existe un usuario registrado con ese email.");
        }

        if (!esPasswordSegura(usuarioDTO.getPassword())) {
            throw new IllegalArgumentException("La contraseña debe tener al menos 8 caracteres, una mayúscula, una minúscula y un símbolo.");
        }

        Usuario usuario = usuarioMapper.toEntity(usuarioDTO);
        usuario.setRol(Rol.CAJERO);
        usuario.setPassword(passwordEncoder.encode(usuarioDTO.getPassword()));
        usuario.setPrimerInicio(true);
        return usuarioRepository.save(usuario);
    }

    @Override
    public Usuario registrarDelivery(UsuarioDTO usuarioDTO) {
        if (usuarioRepository.existsByEmail(usuarioDTO.getEmail())) {
            throw new IllegalArgumentException("Ya existe un usuario registrado con ese email.");
        }

        if (!esPasswordSegura(usuarioDTO.getPassword())) {
            throw new IllegalArgumentException("La contraseña debe tener al menos 8 caracteres, una mayúscula, una minúscula y un símbolo.");
        }

        Usuario usuario = usuarioMapper.toEntity(usuarioDTO);
        usuario.setRol(Rol.DELIVERY);
        usuario.setPassword(passwordEncoder.encode(usuarioDTO.getPassword()));
        usuario.setPrimerInicio(true);
        return usuarioRepository.save(usuario);
    }

    @Override
    @Transactional
    public void actualizarNombre(Long id, String nuevoUsername) {
        try {
            Usuario usuario = findById(id);
            usuario.setUsername(nuevoUsername);
            save(usuario);
        } catch (Exception e) {
            throw new RuntimeException("No se pudo actualizar el nombre de usuario: " + e.getMessage(), e);
        }
    }

    @Transactional
    public Usuario toggleBaja(Long id, boolean baja) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        usuario.setBaja(baja);
        return usuarioRepository.save(usuario); // <<< Esto es lo que PERSISTE
    }
}
