package ElBuenSabor.ProyectoFinal.Controllers;

import ElBuenSabor.ProyectoFinal.Auth.LoginRequest;
import ElBuenSabor.ProyectoFinal.Auth.RegisterRequest;
import ElBuenSabor.ProyectoFinal.Auth.UsuarioResponse;
import ElBuenSabor.ProyectoFinal.Entities.Cliente;
import ElBuenSabor.ProyectoFinal.Entities.Rol;
import ElBuenSabor.ProyectoFinal.Entities.Usuario;
import ElBuenSabor.ProyectoFinal.Repositories.ClienteRepository;
import ElBuenSabor.ProyectoFinal.Service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UsuarioService usuarioService;
    private final ClienteRepository clienteRepository;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        Usuario usuario = usuarioService.findByEmail(request.getEmail());
        if (usuario == null || !passwordEncoder.matches(request.getPassword(), usuario.getPassword())) {
            return ResponseEntity.status(401).body("Credenciales inválidas");
        }

        Cliente cliente = clienteRepository.findByUsuario(usuario).orElse(null);

        UsuarioResponse response = new UsuarioResponse(
                usuario.getId(),
                cliente != null ? cliente.getNombre() : usuario.getNombre(),
                usuario.getEmail(),
                usuario.getRol()
        );
        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        try {
            // 1. Crear Usuario
            Usuario usuario = new Usuario();
            usuario.setEmail(request.getEmail());
            usuario.setPassword(passwordEncoder.encode(request.getPassword()));
            usuario.setNombre(request.getNombre());
            usuario.setRol(Rol.CLIENTE);

            Usuario nuevoUsuario = usuarioService.save(usuario);

            // 2. Crear Cliente vinculado
            Cliente cliente = new Cliente();
            cliente.setNombre(request.getNombre());
            cliente.setApellido(request.getApellido());
            cliente.setTelefono(request.getTelefono());
            cliente.setFechaNacimiento(request.getFechaNacimiento());
            cliente.setUsuario(nuevoUsuario);
            cliente.setBaja(false);
            clienteRepository.save(cliente);

            // 3. Devolver respuesta
            UsuarioResponse response = new UsuarioResponse(
                    nuevoUsuario.getId(),
                    cliente.getNombre(),
                    nuevoUsuario.getEmail(),
                    nuevoUsuario.getRol()
            );
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.status(400).body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }
}
