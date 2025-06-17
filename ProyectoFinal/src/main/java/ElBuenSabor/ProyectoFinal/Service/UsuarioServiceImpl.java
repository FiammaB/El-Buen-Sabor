package ElBuenSabor.ProyectoFinal.Service;

import ElBuenSabor.ProyectoFinal.Auth.RegisterRequest;
import ElBuenSabor.ProyectoFinal.Entities.Cliente;
import ElBuenSabor.ProyectoFinal.Entities.Usuario;
import ElBuenSabor.ProyectoFinal.Entities.Rol;
import ElBuenSabor.ProyectoFinal.Repositories.ClienteRepository;
import ElBuenSabor.ProyectoFinal.Repositories.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class UsuarioServiceImpl extends BaseServiceImpl<Usuario, Long> implements UsuarioService {

    private final UsuarioRepository usuarioRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    public UsuarioServiceImpl(UsuarioRepository usuarioRepository) {
        super(usuarioRepository);
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public Usuario login(String username, String password) {
        Optional<Cliente> clienteOpt = clienteRepository.findByEmailAndPassword(username, password);
        return clienteOpt.map(Cliente::getUsuario).orElse(null);
    }

    @Override
    public Usuario register(RegisterRequest request) {
        Usuario usuario = new Usuario();
        usuario.setUsername(request.getUsername()); // Cambiado: antes era getEmail
        usuario.setRol(Rol.CLIENTE); // rol por defecto
        usuario = usuarioRepository.save(usuario);

        Cliente cliente = new Cliente();
        cliente.setNombre(request.getNombre());
        cliente.setApellido(request.getApellido());
        cliente.setEmail(request.getUsername()); // También corregido
        cliente.setPassword(request.getPassword());
        cliente.setTelefono(request.getTelefono());
        cliente.setFechaNacimiento(request.getFechaNacimiento());
        cliente.setUsuario(usuario);

        clienteRepository.save(cliente);

        return usuario;
    }



    @Override
    @Transactional
    public Usuario update(Long id, Usuario updatedUsuario) throws Exception {
        try {
            Usuario existing = findById(id);
            existing.setAuth0Id(updatedUsuario.getAuth0Id());
            existing.setUsername(updatedUsuario.getUsername());
            return baseRepository.save(existing);
        } catch (Exception e) {
            throw new Exception("Error al actualizar el usuario: " + e.getMessage());
        }
    }
}
