package ElBuenSabor.ProyectoFinal.Controllers;

import ElBuenSabor.ProyectoFinal.Auth.LoginRequest;
import ElBuenSabor.ProyectoFinal.Auth.RegisterRequest;
import ElBuenSabor.ProyectoFinal.Auth.UsuarioResponse;
import ElBuenSabor.ProyectoFinal.Entities.Cliente;
import ElBuenSabor.ProyectoFinal.Entities.Usuario;
import ElBuenSabor.ProyectoFinal.Repositories.ClienteRepository;
import ElBuenSabor.ProyectoFinal.Service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UsuarioService usuarioService;
    private final ClienteRepository clienteRepository;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        Usuario usuario = usuarioService.login(request.getEmail(), request.getPassword());
        if (usuario == null) {
            return ResponseEntity.status(401).body("Credenciales inválidas");
        }

        Cliente cliente = clienteRepository.findByUsuario(usuario).orElse(null);

        UsuarioResponse response = new UsuarioResponse(
                usuario.getId(),
                cliente != null ? cliente.getNombre() : "Sin nombre",
                cliente != null ? cliente.getEmail() : usuario.getUsername(),
                usuario.getRol().name()
        );
        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        Usuario nuevo = usuarioService.register(request);

        Cliente cliente = clienteRepository.findByUsuario(nuevo).orElse(null);

        UsuarioResponse response = new UsuarioResponse(
                nuevo.getId(),
                cliente != null ? cliente.getNombre() : "Sin nombre",
                cliente != null ? cliente.getEmail() : nuevo.getUsername(),
                nuevo.getRol().name()
        );
        return ResponseEntity.ok(response);
    }
}
