package ElBuenSabor.ProyectoFinal.Controllers;

import ElBuenSabor.ProyectoFinal.DTO.PersonaPerfilUpdateDTO;
import ElBuenSabor.ProyectoFinal.DTO.NombreDTO;
import ElBuenSabor.ProyectoFinal.DTO.PerfilDTO;
import ElBuenSabor.ProyectoFinal.DTO.UsuarioDTO;
import ElBuenSabor.ProyectoFinal.Entities.Persona;
import ElBuenSabor.ProyectoFinal.Entities.Rol;
import ElBuenSabor.ProyectoFinal.Entities.Usuario;
import ElBuenSabor.ProyectoFinal.Mappers.PerfilMapper;
import ElBuenSabor.ProyectoFinal.Mappers.UsuarioMapper;
import ElBuenSabor.ProyectoFinal.Service.PersonaService;
import ElBuenSabor.ProyectoFinal.Service.UsuarioService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/usuarios")
@CrossOrigin(origins = "https://localhost:5175")
public class UsuarioController extends BaseController<Usuario, Long> {

    private final UsuarioMapper usuarioMapper;
    private final PerfilMapper perfilMapper;
    private final UsuarioService usuarioService;
    private final PersonaService personaService;
    private final PasswordEncoder passwordEncoder;

    public UsuarioController(
            UsuarioService usuarioService,
            UsuarioMapper usuarioMapper,
            PerfilMapper perfilMapper,
            PersonaService personaService,
            PasswordEncoder passwordEncoder) {
        super(usuarioService);
        this.usuarioService = usuarioService;
        this.usuarioMapper = usuarioMapper;
        this.perfilMapper = perfilMapper;
        this.personaService = personaService;
        this.passwordEncoder = passwordEncoder;
    }

    // -------------------- REGISTRO GENERAL ---------------------
    @PostMapping(consumes = "application/json")
    public ResponseEntity<?> create(@RequestBody UsuarioDTO dto) {
        try {
            Usuario usuario = usuarioMapper.toEntity(dto);

            usuario.setUsername(dto.getUsername() != null && !dto.getUsername().isBlank()
                    ? dto.getUsername()
                    : dto.getEmail().split("@")[0]);

            usuario.setPassword(passwordEncoder.encode(dto.getPassword()));
            usuario.setRol(dto.getRol() != null ? Rol.valueOf(dto.getRol().name()) : Rol.CLIENTE);
            usuario.setPrimerInicio(false);

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
            nuevoCocinero.setUsername(usuarioDTO.getUsername() != null && !usuarioDTO.getUsername().isBlank()
                    ? usuarioDTO.getUsername()
                    : usuarioDTO.getEmail().split("@")[0]);

            nuevoCocinero.setRol(Rol.COCINERO);
            nuevoCocinero.setPassword(passwordEncoder.encode(usuarioDTO.getPassword()));
            nuevoCocinero.setPrimerInicio(true);

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
            nuevoCajero.setUsername(usuarioDTO.getUsername() != null && !usuarioDTO.getUsername().isBlank()
                    ? usuarioDTO.getUsername()
                    : usuarioDTO.getEmail().split("@")[0]);

            nuevoCajero.setRol(Rol.CAJERO);
            nuevoCajero.setPassword(passwordEncoder.encode(usuarioDTO.getPassword()));
            nuevoCajero.setPrimerInicio(true);

            Usuario saved = usuarioService.save(nuevoCajero);
            return ResponseEntity.status(HttpStatus.CREATED).body("Cajero creado con ID: " + saved.getId());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: " + e.getMessage());
        }
    }

    // -------------------- REGISTRO DELIVERY (SOLO ADMIN) ---------------------
    @PostMapping("/registrar-delivery")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<?> registrarDelivery(@RequestBody UsuarioDTO usuarioDTO) {
        try {
            Usuario nuevoDelivery = usuarioMapper.toEntity(usuarioDTO);
            nuevoDelivery.setUsername(usuarioDTO.getUsername() != null && !usuarioDTO.getUsername().isBlank()
                    ? usuarioDTO.getUsername()
                    : usuarioDTO.getEmail().split("@")[0]);

            nuevoDelivery.setRol(Rol.DELIVERY);
            nuevoDelivery.setPassword(passwordEncoder.encode(usuarioDTO.getPassword()));
            nuevoDelivery.setPrimerInicio(true);

            Usuario saved = usuarioService.save(nuevoDelivery);
            return ResponseEntity.status(HttpStatus.CREATED).body("Delivery creado con ID: " + saved.getId());
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
            existente.setUsername(dto.getUsername() != null && !dto.getUsername().isBlank()
                    ? dto.getUsername()
                    : existente.getUsername());

            if (dto.getRol() != null) {
                existente.setRol(Rol.valueOf(dto.getRol().name()));
            }

            if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
                existente.setPassword(passwordEncoder.encode(dto.getPassword()));
            }

            Usuario updated = usuarioService.update(id, existente);
            return ResponseEntity.ok(usuarioMapper.toDTO(updated));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    // -------------------- PERFIL COMPLETO ---------------------
    @GetMapping("/perfil/{email}")
    public ResponseEntity<?> getPerfil(@PathVariable String email) {
        try {
            Usuario usuario = usuarioService.findByEmail(email);
            if (usuario == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("{\"error\": \"Usuario no encontrado con email: " + email + "\"}");
            }

            // ✅ Mejor búsqueda de la Persona asociada al Usuario
            Optional<Persona> personaOpt = personaService.findByUsuario(usuario);
            if (personaOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("{\"error\": \"No hay datos de perfil para este usuario\"}");
            }

            return ResponseEntity.ok(perfilMapper.toPerfilDTO(personaOpt.get()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    @PutMapping("/perfil/{email}")
    public ResponseEntity<?> actualizarPerfil(@PathVariable String email, @RequestBody PersonaPerfilUpdateDTO dto) {
        try {
            Persona personaActualizada = usuarioService.actualizarPerfil(email, dto);
            return ResponseEntity.ok(perfilMapper.toPerfilDTO(personaActualizada));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    // -------------------- PATCHES ---------------------
    @PatchMapping("/{id}/nombre")
    public ResponseEntity<?> updateNombre(@PathVariable Long id, @RequestBody NombreDTO nombreDTO) {
        try {
            usuarioService.actualizarNombre(id, nombreDTO.nombre);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    @PatchMapping("/{id}/baja")
    public ResponseEntity<?> toggleBaja(@PathVariable Long id, @RequestParam boolean baja) {
        try {
            baseService.toggleBaja(id, baja);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }
}
