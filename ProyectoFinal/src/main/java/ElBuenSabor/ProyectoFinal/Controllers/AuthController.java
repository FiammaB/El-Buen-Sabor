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

    // 🔐 LOGIN
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

    // 🧾 REGISTRO DE CLIENTE con validación de email duplicado y contraseña segura
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        try {
            // 🛑 Verificar que no exista otro usuario con el mismo email
            if (usuarioService.findByEmail(request.getEmail()) != null) {
                return ResponseEntity.status(400).body("{\"error\": \"Ya existe un usuario con ese email.\"}");
            }

            // 🛑 Validar formato de contraseña segura
            if (!esPasswordSegura(request.getPassword())) {
                return ResponseEntity.status(400).body("{\"error\": \"La contraseña debe tener al menos 8 caracteres, una mayúscula, una minúscula y un símbolo.\"}");
            }

            // ✅ Crear y guardar usuario
            Usuario usuario = new Usuario();
            usuario.setEmail(request.getEmail());
            usuario.setPassword(passwordEncoder.encode(request.getPassword()));
            usuario.setNombre(request.getNombre());
            usuario.setRol(Rol.CLIENTE);
            Usuario nuevoUsuario = usuarioService.save(usuario);

            // ✅ Crear y guardar cliente asociado
            Cliente cliente = new Cliente();
            cliente.setNombre(request.getNombre());
            cliente.setApellido(request.getApellido());
            cliente.setTelefono(request.getTelefono());
            cliente.setFechaNacimiento(request.getFechaNacimiento());
            cliente.setUsuario(nuevoUsuario);
            cliente.setBaja(false);
            clienteRepository.save(cliente);

            // ✅ Devolver datos del nuevo usuario
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

    // 🔁 RECUPERACIÓN DE CONTRASEÑA
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

        // ✅ Validar contraseña segura
        if (!esPasswordSegura(nuevaPassword)) {
            return ResponseEntity.status(400).body("La nueva contraseña no cumple con los requisitos de seguridad");
        }

        usuario.setPassword(passwordEncoder.encode(nuevaPassword));
        usuarioService.save(usuario);
        codigoRecuperacion.remove(email); // eliminar el código usado

        return ResponseEntity.ok("Contraseña actualizada correctamente");
    }

    // 🔐 Método reutilizable para validar contraseñas seguras
    private boolean esPasswordSegura(String password) {
        return password.length() >= 8 &&
                password.matches(".*[A-Z].*") &&     // al menos una mayúscula
                password.matches(".*[a-z].*") &&     // al menos una minúscula
                password.matches(".*[!@#$%^&*(),.?\":{}|<>_\\-+=].*"); // al menos un símbolo
    }
}
