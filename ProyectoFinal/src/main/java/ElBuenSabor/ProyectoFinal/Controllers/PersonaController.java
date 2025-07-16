package ElBuenSabor.ProyectoFinal.Controllers;

import ElBuenSabor.ProyectoFinal.DTO.*;
import ElBuenSabor.ProyectoFinal.Entities.*;
import ElBuenSabor.ProyectoFinal.Exceptions.ResourceNotFoundException;
import ElBuenSabor.ProyectoFinal.Mappers.PersonaMapper;
import ElBuenSabor.ProyectoFinal.Mappers.PedidoMapper;
import ElBuenSabor.ProyectoFinal.Service.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid; // <-- Importar @Valid
import org.springframework.web.bind.MethodArgumentNotValidException; // <-- Importar esto para el handler

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

@RestController
@RequestMapping("/api/persona")
public class PersonaController extends BaseController<Persona, Long> {

    private final PersonaMapper personaMapper;
    private final DomicilioService domicilioService;
    private final ImagenService imagenService;
    private final UsuarioService usuarioService;
    private final PedidoService pedidoService;
    private final PedidoMapper pedidoMapper;
    private final PersonaService personaService; // Para poder llamar métodos específicos del servicio

    public PersonaController(
            PersonaService personaService,
            PersonaMapper personaMapper,
            DomicilioService domicilioService,
            ImagenService imagenService,
            UsuarioService usuarioService,
            PedidoService pedidoService,
            PedidoMapper pedidoMapper) {
        super(personaService); // Llama al constructor de BaseController
        this.personaService = personaService;
        this.personaMapper = personaMapper;
        this.domicilioService = domicilioService;
        this.imagenService = imagenService;
        this.usuarioService = usuarioService;
        this.pedidoService = pedidoService;
        this.pedidoMapper = pedidoMapper;
    }

    @GetMapping
    @Override // Sobrescribe getAll para devolver DTOs si es necesario
    public ResponseEntity<?> getAll() {
        try {
            List<Persona> personas = baseService.findAll();
            List<PersonaDTO> dtos = personas.stream()
                    .map(personaMapper::toDTO)
                    .toList();
            return ResponseEntity.ok(dtos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    @GetMapping("/{id}")
    @Override // Sobrescribe getOne para devolver un DTO si es necesario
    public ResponseEntity<?> getOne(@PathVariable Long id) {
        try {
            Persona persona = baseService.findById(id);
            return ResponseEntity.ok(personaMapper.toDTO(persona));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    // Endpoint para crear una nueva entidad (general, puede que no uses esta directamente para personas)
    // Se añade @Valid para que las validaciones del DTO se ejecuten.
    // NOTA: Esta ruta POST /api/personas colisiona con POST /api/personas/registro si se usa el mismo DTO,
    // pero si 'create' se usa para entidades 'Persona' genéricas y 'registro' para el flujo de alta de usuario,
    // entonces está bien que exista. Asumo que usas 'registro' para la creación de un persona desde el frontend.
    @PostMapping(consumes = "application/json")
    public ResponseEntity<?> create(@Valid @RequestBody PersonaCreateDTO createDTO) { // <-- AGREGAR @Valid AQUÍ
        try {
            Persona persona = personaMapper.toEntity(createDTO);
            persona.setBaja(false);

            if (createDTO.getUsuarioId() != null) {
                Usuario usuario = usuarioService.findById(createDTO.getUsuarioId());
                persona.setUsuario(usuario);
                persona.setId(usuario.getId()); // Usar el mismo ID que el usuario
            }

            if (createDTO.getImagenId() != null) {
                Imagen imagen = imagenService.findById(createDTO.getImagenId());
                persona.setImagen(imagen);
            }

            if (createDTO.getDomicilioIds() != null && !createDTO.getDomicilioIds().isEmpty()) {
                List<Domicilio> domicilios = new ArrayList<>();
                for (Long domicilioId : createDTO.getDomicilioIds()) {
                    Domicilio domicilio = domicilioService.findById(domicilioId);
                    domicilios.add(domicilio);
                }
                persona.setDomicilios(domicilios);
            }

            Persona saved = baseService.save(persona);
            return ResponseEntity.status(HttpStatus.CREATED).body(personaMapper.toDTO(saved));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    // Endpoint específico para el registro de personas desde el frontend
    // Se añade @Valid para que las validaciones del DTO se ejecuten.
    @PostMapping("/registro")
    public ResponseEntity<?> registrarCliente(@Valid @RequestBody PersonaCreateDTO dto) { // <-- AGREGAR @Valid AQUÍ
        try {
            Usuario nuevoUsuario = Usuario.builder()
                    .email(dto.getEmail())
                    .password(dto.getPassword()) // En producción: encriptar con BCrypt
                    .username(dto.getNombre())
                    .rol(Rol.CLIENTE)
                    .build();
            Usuario usuarioGuardado = usuarioService.save(nuevoUsuario);

            Persona nuevoPersona = Persona.builder()
                    .nombre(dto.getNombre())
                    .apellido(dto.getApellido())
                    .telefono(dto.getTelefono())
                    .fechaNacimiento(dto.getFechaNacimiento())
                    .usuario(usuarioGuardado)
                    .build();

            if (dto.getImagenId() != null) {
                Imagen imagen = imagenService.findById(dto.getImagenId());
                nuevoPersona.setImagen(imagen);
            }

            if (dto.getDomicilioIds() != null && !dto.getDomicilioIds().isEmpty()) {
                List<Domicilio> domicilios = new ArrayList<>();
                for (Long domicilioId : dto.getDomicilioIds()) {
                    Domicilio domicilio = domicilioService.findById(domicilioId);
                    domicilios.add(domicilio);
                }
                nuevoPersona.setDomicilios(domicilios);
            }

            Persona personaGuardado = personaService.save(nuevoPersona);
            return ResponseEntity.status(HttpStatus.CREATED).body(personaMapper.toDTO(personaGuardado));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    // --- NUEVO MÉTODO ESPECÍFICO PARA ACTUALIZAR EL PERFIL DEL CLIENTE ---
    // NOTA: Se cambia la ruta a "/{id}/perfil" para evitar conflicto con el método 'update' del BaseController.
    @PutMapping("/{id}/perfil") // <-- RUTA CAMBIADA AQUÍ
    public ResponseEntity<?> updatePerfil(@PathVariable Long id, @Valid @RequestBody PersonaPerfilUpdateDTO updateDTO) { // <-- AGREGAR @Valid AQUÍ
        try {
            // Busca el persona existente
            Persona existingPersona = personaService.findById(id);
            if (existingPersona == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"error\": \"Persona no encontrado con ID: " + id + "\"}");
            }

            // Actualiza campos que no sean nulos en el DTO
            if (Objects.nonNull(updateDTO.getNombre())) {
                existingPersona.setNombre(updateDTO.getNombre());
            }
            if (Objects.nonNull(updateDTO.getApellido())) {
                existingPersona.setApellido(updateDTO.getApellido());
            }
            if (Objects.nonNull(updateDTO.getTelefono())) {
                existingPersona.setTelefono(updateDTO.getTelefono());
            }
            if (Objects.nonNull(updateDTO.getFechaNacimiento())) {
                existingPersona.setFechaNacimiento(updateDTO.getFechaNacimiento());
            }
            // Solo actualiza la imagen si se proporciona un ID válido
            if (Objects.nonNull(updateDTO.getImagenId())) {
                Imagen imagen = imagenService.findById(updateDTO.getImagenId());
                existingPersona.setImagen(imagen);
            }

            // Manejo de la actualización de contraseña y email (más complejo, necesita lógica de servicio)
            if (Objects.nonNull(updateDTO.getNuevaPassword()) && !updateDTO.getNuevaPassword().isEmpty()) {
                Usuario usuarioAsociado = existingPersona.getUsuario();
                if (usuarioAsociado != null) {
                    // **IMPORTANTE**: Aquí DEBES verificar que updateDTO.getPasswordActual()
                    // coincida con la contraseña HASHEADA de usuarioAsociado
                    // y luego HASHEAR updateDTO.getNuevaPassword() antes de asignarla.
                    // Esto debe hacerse en tu UsuarioService o un servicio de autenticación.
                    // Ejemplo (NO PARA PROD SIN HASHING):
                    usuarioAsociado.setPassword(updateDTO.getNuevaPassword());
                    usuarioService.save(usuarioAsociado);
                }
            }

            if (Objects.nonNull(updateDTO.getEmail())) {
                Usuario usuarioAsociado = existingPersona.getUsuario();
                if (usuarioAsociado != null) {
                    usuarioAsociado.setEmail(updateDTO.getEmail());
                    usuarioService.save(usuarioAsociado);
                }
            }

            // Lógica para actualizar domicilios, si es necesario.
            // Esto implicaría añadir/quitar domicilios existentes o vincular nuevos.
            // Es una operación más compleja y dependerá de tu lógica de negocio para domicilios.
            if (updateDTO.getDomicilioIds() != null) {
                Set<Domicilio> nuevosDomicilios = new HashSet<>();
                for (Long domId : updateDTO.getDomicilioIds()) {
                    Domicilio domicilio = domicilioService.findById(domId); // Asume que findById lanza ResourceNotFoundException
                    nuevosDomicilios.add(domicilio);
                }
                existingPersona.setDomicilios(new ArrayList<>(nuevosDomicilios));
            }

            // Guarda el persona actualizado
            Persona updatedPersona = personaService.update(id, existingPersona); // Asumiendo un método update en tu servicio

            return ResponseEntity.ok(personaMapper.toDTO(updatedPersona));

        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"error\": \"" + e.getMessage() + "\"}");
        } catch (Exception e) {
            // Captura errores genéricos o validaciones no manejadas por @Valid (menos probable)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"error\": \"Error al actualizar el persona: " + e.getMessage() + "\"}");
        }
    }

    // --- MANEJADOR GLOBAL DE EXCEPCIONES DE VALIDACIÓN ---
    // Este método captura las MethodArgumentNotValidException lanzadas por @Valid
    // y las transforma en un mapa amigable para el frontend.
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((org.springframework.validation.FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return errors;
    }

    @GetMapping("/{personaId}/pedidos")
    public ResponseEntity<?> getPedidosByClienteId(@PathVariable Long personaId) {
        try {
            List<Pedido> pedidos = pedidoService.findPedidosByClienteId(personaId);
            List<PedidoDTO> dtos = pedidos.stream()
                    .map(pedidoMapper::toDTO)
                    .toList();
            return ResponseEntity.ok(dtos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("{\"error\": \"Error al obtener pedidos del persona: " + e.getMessage() + "\"}");
        }
    }

    // Método para cambiar el estado de baja (toggleBaja)
    // El método toggleBaja en BaseController podría ser suficiente, pero si necesitas lógica específica,
    // puedes tener este. Asumo que el de BaseController hace lo mismo.
    @PatchMapping("/{id}/baja")
    public ResponseEntity<?> toggleBaja(
            @PathVariable Long id,
            @RequestParam boolean baja
    ) {
        try {
            Persona actualizado = baseService.toggleBaja(id, baja); // Llama al método del BaseService
            return ResponseEntity.ok().build(); // Podrías devolver un DTO actualizado si es útil
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    @PostMapping("/empleado")
    public ResponseEntity<?> registrarEmpleado(@Valid @RequestBody PersonaEmpleadoCreateDTO dto) {
        try {
            Usuario usuario = usuarioService.findById(dto.getUsuarioId());
            if (usuario == null) {
                return ResponseEntity.badRequest().body("Usuario no encontrado");
            }
            Persona persona = Persona.builder()
                    .nombre(dto.getNombre())
                    .apellido(dto.getApellido())
                    .telefono(dto.getTelefono())
                    .fechaNacimiento(dto.getFechaNacimiento())
                    .usuario(usuario)
                    .build();
            // Si hace falta, setear imagen y domicilios...
            Persona saved = personaService.save(persona);
            return ResponseEntity.status(HttpStatus.CREATED).body(personaMapper.toDTO(saved));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }




    // Si tu BaseController también tiene un @DeleteMapping y quieres usarlo para el borrado lógico,
    // déjalo como está. Si quieres personalizarlo, lo sobrescribirías aquí.
    // Ejemplo:
    // @Override
    // @DeleteMapping("/{id}")
    // public ResponseEntity<?> delete(@PathVariable Long id) {
    //     // Lógica personalizada si es diferente al BaseController
    //     return super.delete(id); // O llama a tu servicio directamente
    // }
}