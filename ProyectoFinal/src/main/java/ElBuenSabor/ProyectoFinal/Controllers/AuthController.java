package ElBuenSabor.ProyectoFinal.Controllers;

import ElBuenSabor.ProyectoFinal.Auth.LoginRequest;
import ElBuenSabor.ProyectoFinal.Auth.RegisterRequest;
import ElBuenSabor.ProyectoFinal.Auth.UsuarioResponse;
import ElBuenSabor.ProyectoFinal.Entities.Cliente;
import ElBuenSabor.ProyectoFinal.Entities.Rol;
import ElBuenSabor.ProyectoFinal.Entities.Usuario;
import ElBuenSabor.ProyectoFinal.Repositories.ClienteRepository;
import ElBuenSabor.ProyectoFinal.Service.EmailService;
import ElBuenSabor.ProyectoFinal.Service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UsuarioService usuarioService;
    private final ClienteRepository clienteRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    private final Map<String, String> codigoRecuperacion = new HashMap<>();

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
                cliente != null ? cliente.getApellido() : "",
                usuario.getEmail(),
                cliente != null ? cliente.getTelefono() : "",
                usuario.getRol()
        );

        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        try {
            Usuario usuario = new Usuario();
            usuario.setEmail(request.getEmail());
            usuario.setPassword(passwordEncoder.encode(request.getPassword()));
            usuario.setNombre(request.getNombre());
            usuario.setRol(Rol.CLIENTE);

            Usuario nuevoUsuario = usuarioService.save(usuario);

            Cliente cliente = new Cliente();
            cliente.setNombre(request.getNombre());
            cliente.setApellido(request.getApellido());
            cliente.setTelefono(request.getTelefono());
            cliente.setFechaNacimiento(request.getFechaNacimiento());
            cliente.setUsuario(nuevoUsuario);
            cliente.setBaja(false);
            clienteRepository.save(cliente);

            UsuarioResponse response = new UsuarioResponse(
                    nuevoUsuario.getId(),
                    cliente.getNombre(),
                    cliente.getApellido(),
                    nuevoUsuario.getEmail(),
                    cliente.getTelefono(),
                    nuevoUsuario.getRol()
            );

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.status(400).body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    // ------------------------------
    // RECUPERACIÓN DE CONTRASEÑA
    // ------------------------------

    @PostMapping("/recuperar")
    public ResponseEntity<?> recuperar(@RequestParam String email) {
        Usuario usuario = usuarioService.findByEmail(email);
        if (usuario == null) {
            return ResponseEntity.status(404).body("No existe un usuario con ese email");
        }

        String codigo = String.valueOf(new Random().nextInt(900000) + 100000); // 6 dígitos
        codigoRecuperacion.put(email, codigo);

        String mensaje = "<p>Tu código de recuperación de contraseña es: <strong>" + codigo + "</strong></p>";
        try {
            emailService.sendEmail(email, "Código de recuperación", mensaje, null, null);
            return ResponseEntity.ok("Código enviado al correo");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error al enviar el correo: " + e.getMessage());
        }
    }

    @PostMapping("/verificar-codigo")
    public ResponseEntity<?> verificarCodigo(@RequestParam String email, @RequestParam String codigo) {
        String codigoGuardado = codigoRecuperacion.get(email);
        if (codigoGuardado != null && codigoGuardado.equals(codigo)) {
            return ResponseEntity.ok("Código válido");
        }
        return ResponseEntity.status(400).body("Código inválido o expirado");
    }

    @PostMapping("/cambiar-password")
    public ResponseEntity<?> cambiarPassword(@RequestParam String email, @RequestParam String codigo, @RequestParam String nuevaPassword) {
        String codigoGuardado = codigoRecuperacion.get(email);
        if (codigoGuardado == null || !codigoGuardado.equals(codigo)) {
            return ResponseEntity.status(400).body("Código inválido o expirado");
        }

        Usuario usuario = usuarioService.findByEmail(email);
        if (usuario == null) {
            return ResponseEntity.status(404).body("Usuario no encontrado");
        }

        usuario.setPassword(passwordEncoder.encode(nuevaPassword));
        usuarioService.save(usuario);
        codigoRecuperacion.remove(email); // eliminar el código usado

        return ResponseEntity.ok("Contraseña actualizada correctamente");
    }
}
