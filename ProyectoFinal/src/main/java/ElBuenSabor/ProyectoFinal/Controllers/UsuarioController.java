package ElBuenSabor.ProyectoFinal.Controllers;

import ElBuenSabor.ProyectoFinal.DTO.PersonaPerfilUpdateDTO;
import ElBuenSabor.ProyectoFinal.DTO.NombreDTO;
import ElBuenSabor.ProyectoFinal.DTO.PerfilDTO;
import ElBuenSabor.ProyectoFinal.DTO.UsuarioDTO;
import ElBuenSabor.ProyectoFinal.Entities.Rol;
import ElBuenSabor.ProyectoFinal.Entities.Usuario;
import ElBuenSabor.ProyectoFinal.Mappers.UsuarioMapper;
import ElBuenSabor.ProyectoFinal.Service.UsuarioService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
@CrossOrigin(origins = "https://localhost:5175")
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

    // -------------------- REGISTRO GENERAL ---------------------

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

    // -------------------- REGISTRO COCINERO (SOLO ADMIN) ---------------------

    @PostMapping("/registrar-cocinero")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<?> registrarCocinero(@RequestBody UsuarioDTO usuarioDTO) {
        try {
            Usuario nuevoCocinero = usuarioMapper.toEntity(usuarioDTO);
            nuevoCocinero.setRol(Rol.COCINERO);
            nuevoCocinero.setPassword(passwordEncoder.encode(usuarioDTO.getPassword()));
            Usuario saved = usuarioService.save(nuevoCocinero);
            return ResponseEntity.status(HttpStatus.CREATED).body("Cocinero creado con ID: " + saved.getId());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: " + e.getMessage());
        }
    }

    // -------------------- REGISTRO CAJERO (SOLO ADMIN) ---------------------

    @PostMapping("/registrar-cajero")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<?> registrarCajero(@RequestBody UsuarioDTO usuarioDTO) {
        try {
            Usuario nuevoCajero = usuarioMapper.toEntity(usuarioDTO);
            nuevoCajero.setRol(Rol.CAJERO);
            nuevoCajero.setPassword(passwordEncoder.encode(usuarioDTO.getPassword()));
            Usuario saved = usuarioService.save(nuevoCajero);
            return ResponseEntity.status(HttpStatus.CREATED).body("Cajero creado con ID: " + saved.getId());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: " + e.getMessage());
        }
    }

    // -------------------- LISTAR Y OBTENER USUARIOS ---------------------

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

    // -------------------- ACTUALIZAR USUARIO ---------------------

    @PutMapping(value = "/{id}", consumes = "application/json")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody UsuarioDTO dto) {
        try {
            Usuario existente = usuarioService.findById(id);
            existente.setEmail(dto.getEmail());
            existente.setUsername(dto.getUsername()); // ✅ CAMBIO
            existente.setRol(dto.getRol());
            if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
                existente.setPassword(passwordEncoder.encode(dto.getPassword()));
            }
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
            dto.setUsuario(usuarioMapper.toDTO(usuario));

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

            usuario.setEmail(datos.getUsuario().getEmail());
            usuarioService.save(usuario);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    // -------------------- PERFIL CLIENTE COMPLETO ---------------------

    @PutMapping("/perfil/cliente/{email}")
    public ResponseEntity<?> actualizarPerfilCliente(@PathVariable String email, @RequestBody PersonaPerfilUpdateDTO dto) {
        try {
            usuarioService.actualizarPerfilCliente(email, dto);
            return ResponseEntity.ok("Perfil actualizado correctamente");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    @PatchMapping("/{id}/nombre")
    public ResponseEntity<?> updateNombre(@PathVariable Long id, @RequestBody NombreDTO nombreDTO) {
        try {
            usuarioService.actualizarNombre(id, nombreDTO.nombre); // ⚠️ Este método debería llamarse actualizarUsername()
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    @PatchMapping("/{id}/baja")
    public ResponseEntity<?> toggleBaja(
            @PathVariable Long id,
            @RequestParam boolean baja
    ) {
        try {
            Usuario actualizado = baseService.toggleBaja(id, baja);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }
}
