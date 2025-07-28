package ElBuenSabor.ProyectoFinal.Controllers;

import ElBuenSabor.ProyectoFinal.Auth.LoginRequest;
import ElBuenSabor.ProyectoFinal.Auth.RegisterRequest;
import ElBuenSabor.ProyectoFinal.Auth.UsuarioResponse;
import ElBuenSabor.ProyectoFinal.Entities.Persona;
import ElBuenSabor.ProyectoFinal.Entities.Rol;
import ElBuenSabor.ProyectoFinal.Entities.Usuario;
import ElBuenSabor.ProyectoFinal.Repositories.PersonaRepository;
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

import java.util.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UsuarioService usuarioService;
    private final PersonaRepository personaRepository;
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

        Persona persona = personaRepository.findByUsuario(usuario).orElse(null);

        // ✅ Verificar si debe cambiar contraseña
        boolean debeCambiarPassword =
                usuario.isPrimerInicio() &&
                        (usuario.getRol() == Rol.CAJERO ||
                                usuario.getRol() == Rol.COCINERO ||
                                usuario.getRol() == Rol.DELIVERY);

        UsuarioResponse response = new UsuarioResponse(
                usuario.getId(),
                persona != null ? persona.getNombre() : usuario.getUsername(),
                persona != null ? persona.getApellido() : "",
                usuario.getEmail(),
                persona != null ? persona.getTelefono() : "",
                usuario.getRol(),
                persona != null ? persona.getBaja() : false

        );

        // ✅ Devolvemos usuario + flag de cambio de password
        Map<String, Object> loginData = new HashMap<>();
        loginData.put("usuario", response);
        loginData.put("cambiarPassword", debeCambiarPassword);

        return ResponseEntity.ok(loginData);
    }

    // 🧾 REGISTRO de persona común
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
            usuario.setPrimerInicio(true); // ✅ Por defecto

            Usuario nuevoUsuario = usuarioService.save(usuario);

            Persona persona = new Persona();
            persona.setNombre(request.getNombre());
            persona.setApellido(request.getApellido());
            persona.setTelefono(request.getTelefono());
            persona.setFechaNacimiento(request.getFechaNacimiento());
            persona.setUsuario(nuevoUsuario);
            persona.setBaja(false);
            personaRepository.save(persona);

            UsuarioResponse response = new UsuarioResponse(
                    nuevoUsuario.getId(),
                    persona.getNombre(),
                    persona.getApellido(),
                    nuevoUsuario.getEmail(),
                    persona.getTelefono(),
                    nuevoUsuario.getRol(),
                    persona.getBaja()
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

    // ✅ CAMBIO OBLIGATORIO DE CONTRASEÑA (primer inicio)
    @PostMapping("/cambiar-password-inicial")
    public ResponseEntity<?> cambiarPasswordInicial(@RequestParam String email, @RequestParam String nuevaPassword) {
        Usuario usuario = usuarioService.findByEmail(email);

        if (usuario == null) {
            return ResponseEntity.status(404).body("Usuario no encontrado");
        }

        if (!usuario.isPrimerInicio()) {
            return ResponseEntity.status(400).body("El cambio de contraseña inicial ya fue realizado");
        }

        if (!esPasswordSegura(nuevaPassword)) {
            return ResponseEntity.status(400).body("La nueva contraseña no cumple con los requisitos de seguridad");
        }

        usuario.setPassword(passwordEncoder.encode(nuevaPassword));
        usuario.setPrimerInicio(false); // ✅ Ya no es primer inicio
        usuarioService.save(usuario);

        return ResponseEntity.ok("Contraseña actualizada correctamente. Ahora puede iniciar sesión normalmente.");
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
                Persona persona = null;

                if (usuario == null) {
                    usuario = new Usuario();
                    usuario.setEmail(email);
                    usuario.setUsername(nombre);
                    usuario.setRol(Rol.CLIENTE);
                    usuario.setPassword(passwordEncoder.encode(UUID.randomUUID().toString()));
                    usuario.setPrimerInicio(false);
                    usuario = usuarioService.save(usuario);

                    persona = new Persona();
                    persona.setNombre(nombre);
                    persona.setApellido(apellido);
                    persona.setUsuario(usuario);
                    persona.setBaja(false);
                    personaRepository.save(persona);
                } else {
                    persona = personaRepository.findByUsuario(usuario).orElse(null);
                }

                UsuarioResponse response = new UsuarioResponse(
                        usuario.getId(),
                        persona != null ? persona.getNombre() : nombre,
                        persona != null ? persona.getApellido() : apellido,
                        email,
                        persona != null ? persona.getTelefono() : "",
                        usuario.getRol(),
                        persona != null ? persona.getBaja() : false

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
