package ElBuenSabor.ProyectoFinal.Controllers;

import ElBuenSabor.ProyectoFinal.DTO.UsuarioDTO;
import ElBuenSabor.ProyectoFinal.Entities.Usuario;
import ElBuenSabor.ProyectoFinal.Mappers.UsuarioMapper;
import ElBuenSabor.ProyectoFinal.Service.UsuarioService; // Usar la interfaz específica
// Las importaciones de ClienteDTO, UsuarioBaseResponseDTO, Cliente, ClienteService, ArrayList, Optional, Collectors
// no parecen usarse en este controlador, se pueden eliminar si no son necesarias.
// import lombok.RequiredArgsConstructor; // Ya no es necesario si se inyecta por constructor explícito al padre
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios") // Define la URL base para este controlador
@CrossOrigin(origins = "http://localhost:5173") // Mantén CrossOrigin si es necesario
// UsuarioController ahora extiende BaseController
public class UsuarioController extends BaseController<Usuario, Long> {

    private final UsuarioMapper usuarioMapper;

    // El constructor inyecta el servicio específico de Usuario y el mapper
    public UsuarioController(
            UsuarioService usuarioService, // Servicio específico
            UsuarioMapper usuarioMapper) {
        super(usuarioService); // Pasa el servicio al constructor del BaseController
        this.usuarioMapper = usuarioMapper;
    }

    // Sobrescribir getAll para devolver DTOs y manejar excepciones
    @GetMapping
    @Override // Sobrescribe el getAll del BaseController
    public ResponseEntity<?> getAll() {
        try {
            List<Usuario> usuarios = baseService.findAll(); // Llama al findAll del padre
            List<UsuarioDTO> dtos = usuarios.stream()
                    .map(usuarioMapper::toDTO)
                    .toList();
            return ResponseEntity.ok(dtos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    // Sobrescribir getOne para devolver un DTO y manejar excepciones
    @GetMapping("/{id}")
    @Override // Sobrescribe el getOne del BaseController
    public ResponseEntity<?> getOne(@PathVariable Long id) {
        try {
            Usuario usuario = baseService.findById(id); // Llama al findById del padre
            return ResponseEntity.ok(usuarioMapper.toDTO(usuario));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    // Sobrescribir create para aceptar un DTO de entrada, mapear y manejar excepciones
    @PostMapping(consumes = "application/json")
    public ResponseEntity<?> create(@RequestBody UsuarioDTO dto) {
        try {
            Usuario usuario = usuarioMapper.toEntity(dto);
            usuario.setBaja(false); // Por defecto, un nuevo usuario está activo

            Usuario saved = baseService.save(usuario); // Llama al save del padre
            return ResponseEntity.status(HttpStatus.CREATED).body(usuarioMapper.toDTO(saved)); // Convierte a DTO para la respuesta
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    // Sobrescribir update para aceptar un DTO de entrada, mapear y manejar excepciones
    @PutMapping(value = "/{id}", consumes = "application/json")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody UsuarioDTO dto) {
        try {
            // Obtener la entidad existente y actualizar sus propiedades
            Usuario existingUsuario = baseService.findById(id);

            existingUsuario.setAuth0Id(dto.getAuth0Id());
            existingUsuario.setUsername(dto.getUsername());

            Usuario updated = baseService.update(id, existingUsuario); // Llama al update del padre con la entidad EXISTENTE
            return ResponseEntity.ok(usuarioMapper.toDTO(updated)); // Convierte a DTO para la respuesta
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }


}