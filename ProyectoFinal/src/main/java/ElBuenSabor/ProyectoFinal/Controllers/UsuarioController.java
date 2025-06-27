package ElBuenSabor.ProyectoFinal.Controllers;

import ElBuenSabor.ProyectoFinal.DTO.PerfilDTO;
import ElBuenSabor.ProyectoFinal.DTO.UsuarioDTO;
import ElBuenSabor.ProyectoFinal.Entities.Usuario;
import ElBuenSabor.ProyectoFinal.Mappers.UsuarioMapper;
import ElBuenSabor.ProyectoFinal.Service.UsuarioService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
@CrossOrigin(origins = "http://localhost:5173")
public class UsuarioController extends BaseController<Usuario, Long> {

    private final UsuarioMapper usuarioMapper;
    private final UsuarioService usuarioService;
    private final PasswordEncoder passwordEncoder;

    public UsuarioController(
            UsuarioService usuarioService,
            UsuarioMapper usuarioMapper,
            PasswordEncoder passwordEncoder) {
        super(usuarioService);
        this.usuarioService = usuarioService;
        this.usuarioMapper = usuarioMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping
    @Override
    public ResponseEntity<?> getAll() {
        try {
            List<Usuario> usuarios = baseService.findAll();
            List<UsuarioDTO> dtos = usuarios.stream()
                    .map(usuarioMapper::toDTO)
                    .toList();
            return ResponseEntity.ok(dtos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    @GetMapping("/{id}")
    @Override
    public ResponseEntity<?> getOne(@PathVariable Long id) {
        try {
            Usuario usuario = baseService.findById(id);
            return ResponseEntity.ok(usuarioMapper.toDTO(usuario));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    @PostMapping(consumes = "application/json")
    public ResponseEntity<?> create(@RequestBody UsuarioDTO dto) {
        try {
            Usuario usuario = usuarioMapper.toEntity(dto);
            usuario.setPassword(passwordEncoder.encode(dto.getPassword()));
            Usuario saved = usuarioService.save(usuario);
            return ResponseEntity.status(HttpStatus.CREATED).body(usuarioMapper.toDTO(saved));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    @PutMapping(value = "/{id}", consumes = "application/json")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody UsuarioDTO dto) {
        try {
            Usuario existente = usuarioService.findById(id);
            existente.setEmail(dto.getEmail());
            existente.setPassword(passwordEncoder.encode(dto.getPassword()));
            existente.setRol(dto.getRol());
            Usuario updated = usuarioService.update(id, existente);
            return ResponseEntity.ok(usuarioMapper.toDTO(updated));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    // -------------------- PERFIL REDUCIDO ---------------------

    @GetMapping("/perfil/{email}")
    public ResponseEntity<?> getPerfil(@PathVariable String email) {
        try {
            Usuario usuario = usuarioService.findByEmail(email);
            if (usuario == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"error\": \"Usuario no encontrado\"}");
            }

            PerfilDTO dto = new PerfilDTO();
            dto.setId(usuario.getId());
            dto.setUsuario(usuarioMapper.toDTO(usuario)); // incluye email y rol

            return ResponseEntity.ok(dto);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    @PutMapping("/perfil/{email}")
    public ResponseEntity<?> actualizarPerfil(@PathVariable String email, @RequestBody PerfilDTO datos) {
        try {
            Usuario usuario = usuarioService.findByEmail(email);
            if (usuario == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"error\": \"Usuario no encontrado\"}");
            }

            usuario.setEmail(datos.getUsuario().getEmail()); // si querés permitir editar el email
            usuarioService.save(usuario);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }
}
