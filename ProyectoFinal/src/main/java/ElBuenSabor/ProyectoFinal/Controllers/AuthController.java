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
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import java.security.GeneralSecurityException;
import java.util.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UsuarioService usuarioService;
    private final ClienteRepository clienteRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final Map<String, String> codigoRecuperacion = new HashMap<>();

    // 🔐 LOGIN manual (email + password)
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        Usuario usuario = usuarioService.findByEmail(request.getEmail());
        if (usuario == null || !passwordEncoder.matches(request.getPassword(), usuario.getPassword())) {
            return ResponseEntity.status(401).body("Credenciales inválidas");
        }

        Cliente cliente = clienteRepository.findByUsuario(usuario).orElse(null);

        UsuarioResponse response = new UsuarioResponse(
                usuario.getId(),
                cliente != null ? cliente.getNombre() : usuario.getUsername(),
                cliente != null ? cliente.getApellido() : "",
                usuario.getEmail(),
                cliente != null ? cliente.getTelefono() : "",
                usuario.getRol()
        );

        return ResponseEntity.ok(response);
    }

    // 🧾 REGISTRO de cliente común
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        try {
            if (usuarioService.findByEmail(request.getEmail()) != null) {
                return ResponseEntity.status(400).body("{\"error\": \"Ya existe un usuario con ese email.\"}");
            }

            if (!esPasswordSegura(request.getPassword())) {
                return ResponseEntity.status(400).body("{\"error\": \"La contraseña debe tener al menos 8 caracteres, una mayúscula, una minúscula y un símbolo.\"}");
            }

            Usuario usuario = new Usuario();
            usuario.setEmail(request.getEmail());
            usuario.setPassword(passwordEncoder.encode(request.getPassword()));
            usuario.setUsername(request.getNombre());
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

    // 🔁 RECUPERACIÓN DE CONTRASEÑA
    @PostMapping("/recuperar")
    public ResponseEntity<?> recuperar(@RequestParam String email) {
        Usuario usuario = usuarioService.findByEmail(email);
        if (usuario == null) {
            return ResponseEntity.status(404).body("No existe un usuario con ese email");
        }

        String codigo = String.valueOf(new Random().nextInt(900000) + 100000);
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

        if (!esPasswordSegura(nuevaPassword)) {
            return ResponseEntity.status(400).body("La nueva contraseña no cumple con los requisitos de seguridad");
        }

        usuario.setPassword(passwordEncoder.encode(nuevaPassword));
        usuarioService.save(usuario);
        codigoRecuperacion.remove(email);

        return ResponseEntity.ok("Contraseña actualizada correctamente");
    }

    // ✅ LOGIN CON GOOGLE
    @PostMapping("/google")
    public ResponseEntity<?> loginConGoogle(@RequestBody Map<String, String> body) {
        String token = body.get("token");
        String CLIENT_ID = "69075773198-5joq80nrsujctfiqjeap2lc9bhe7ot2q.apps.googleusercontent.com";

        try {
            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
                    GoogleNetHttpTransport.newTrustedTransport(),
                    JacksonFactory.getDefaultInstance()
            )
                    .setAudience(Collections.singletonList(CLIENT_ID))
                    .build();

            GoogleIdToken idToken = verifier.verify(token);

            if (idToken != null) {
                GoogleIdToken.Payload payload = idToken.getPayload();
                String email = payload.getEmail();
                String nombre = (String) payload.get("given_name");
                String apellido = (String) payload.get("family_name");

                // Verificar si ya existe el usuario
                Usuario usuario = usuarioService.findByEmail(email);
                Cliente cliente = null;

                if (usuario == null) {
                    // Crear nuevo usuario y cliente si no existe
                    usuario = new Usuario();
                    usuario.setEmail(email);
                    usuario.setUsername(nombre);
                    usuario.setRol(Rol.CLIENTE);
                    usuario.setPassword(passwordEncoder.encode(UUID.randomUUID().toString())); // clave aleatoria
                    usuario = usuarioService.save(usuario);

                    cliente = new Cliente();
                    cliente.setNombre(nombre);
                    cliente.setApellido(apellido);
                    cliente.setUsuario(usuario);
                    cliente.setBaja(false);
                    clienteRepository.save(cliente);
                } else {
                    cliente = clienteRepository.findByUsuario(usuario).orElse(null);
                }

                UsuarioResponse response = new UsuarioResponse(
                        usuario.getId(),
                        cliente != null ? cliente.getNombre() : nombre,
                        cliente != null ? cliente.getApellido() : apellido,
                        email,
                        cliente != null ? cliente.getTelefono() : "",
                        usuario.getRol()
                );

                return ResponseEntity.ok(response);
            }

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token inválido");

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al verificar el token: " + e.getMessage());
        }
    }

    // 🔐 Validación de contraseñas seguras
    private boolean esPasswordSegura(String password) {
        return password.length() >= 8 &&
                password.matches(".*[A-Z].*") &&
                password.matches(".*[a-z].*") &&
                password.matches(".*[!@#$%^&*(),.?\":{}|<>_\\-+=].*");
    }
}
