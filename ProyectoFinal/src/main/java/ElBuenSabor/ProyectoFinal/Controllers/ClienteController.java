package ElBuenSabor.ProyectoFinal.Controllers;

import ElBuenSabor.ProyectoFinal.DTO.ClienteCreateDTO;
import ElBuenSabor.ProyectoFinal.DTO.ClienteDTO;
import ElBuenSabor.ProyectoFinal.DTO.ClientePerfilUpdateDTO;
import ElBuenSabor.ProyectoFinal.DTO.PedidoDTO;
import ElBuenSabor.ProyectoFinal.Entities.*;
import ElBuenSabor.ProyectoFinal.Exceptions.ResourceNotFoundException;
import ElBuenSabor.ProyectoFinal.Mappers.ClienteMapper;
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
@RequestMapping("/api/clientes")
public class ClienteController extends BaseController<Cliente, Long> {

    private final ClienteMapper clienteMapper;
    private final DomicilioService domicilioService;
    private final ImagenService imagenService;
    private final UsuarioService usuarioService;
    private final PedidoService pedidoService;
    private final PedidoMapper pedidoMapper;
    private final ClienteService clienteService; // Para poder llamar métodos específicos del servicio

    public ClienteController(
            ClienteService clienteService,
            ClienteMapper clienteMapper,
            DomicilioService domicilioService,
            ImagenService imagenService,
            UsuarioService usuarioService,
            PedidoService pedidoService,
            PedidoMapper pedidoMapper) {
        super(clienteService); // Llama al constructor de BaseController
        this.clienteService = clienteService;
        this.clienteMapper = clienteMapper;
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
            List<Cliente> clientes = baseService.findAll();
            List<ClienteDTO> dtos = clientes.stream()
                    .map(clienteMapper::toDTO)
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
            Cliente cliente = baseService.findById(id);
            return ResponseEntity.ok(clienteMapper.toDTO(cliente));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    // Endpoint para crear una nueva entidad (general, puede que no uses esta directamente para clientes)
    // Se añade @Valid para que las validaciones del DTO se ejecuten.
    // NOTA: Esta ruta POST /api/clientes colisiona con POST /api/clientes/registro si se usa el mismo DTO,
    // pero si 'create' se usa para entidades 'Cliente' genéricas y 'registro' para el flujo de alta de usuario,
    // entonces está bien que exista. Asumo que usas 'registro' para la creación de un cliente desde el frontend.
    @PostMapping(consumes = "application/json")
    public ResponseEntity<?> create(@Valid @RequestBody ClienteCreateDTO createDTO) { // <-- AGREGAR @Valid AQUÍ
        try {
            Cliente cliente = clienteMapper.toEntity(createDTO);
            cliente.setBaja(false);

            if (createDTO.getUsuarioId() != null) {
                Usuario usuario = usuarioService.findById(createDTO.getUsuarioId());
                cliente.setUsuario(usuario);
                cliente.setId(usuario.getId()); // Usar el mismo ID que el usuario
            }

            if (createDTO.getImagenId() != null) {
                Imagen imagen = imagenService.findById(createDTO.getImagenId());
                cliente.setImagen(imagen);
            }

            if (createDTO.getDomicilioIds() != null && !createDTO.getDomicilioIds().isEmpty()) {
                List<Domicilio> domicilios = new ArrayList<>();
                for (Long domicilioId : createDTO.getDomicilioIds()) {
                    Domicilio domicilio = domicilioService.findById(domicilioId);
                    domicilios.add(domicilio);
                }
                cliente.setDomicilios(domicilios);
            }

            Cliente saved = baseService.save(cliente);
            return ResponseEntity.status(HttpStatus.CREATED).body(clienteMapper.toDTO(saved));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    // Endpoint específico para el registro de clientes desde el frontend
    // Se añade @Valid para que las validaciones del DTO se ejecuten.
    @PostMapping("/registro")
    public ResponseEntity<?> registrarCliente(@Valid @RequestBody ClienteCreateDTO dto) { // <-- AGREGAR @Valid AQUÍ
        try {
            Usuario nuevoUsuario = Usuario.builder()
                    .email(dto.getEmail())
                    .password(dto.getPassword()) // En producción: encriptar con BCrypt
                    .nombre(dto.getNombre())
                    .rol(Rol.CLIENTE)
                    .build();
            Usuario usuarioGuardado = usuarioService.save(nuevoUsuario);

            Cliente nuevoCliente = Cliente.builder()
                    .nombre(dto.getNombre())
                    .apellido(dto.getApellido())
                    .telefono(dto.getTelefono())
                    .fechaNacimiento(dto.getFechaNacimiento())
                    .usuario(usuarioGuardado)
                    .build();

            if (dto.getImagenId() != null) {
                Imagen imagen = imagenService.findById(dto.getImagenId());
                nuevoCliente.setImagen(imagen);
            }

            if (dto.getDomicilioIds() != null && !dto.getDomicilioIds().isEmpty()) {
                List<Domicilio> domicilios = new ArrayList<>();
                for (Long domicilioId : dto.getDomicilioIds()) {
                    Domicilio domicilio = domicilioService.findById(domicilioId);
                    domicilios.add(domicilio);
                }
                nuevoCliente.setDomicilios(domicilios);
            }

            Cliente clienteGuardado = clienteService.save(nuevoCliente);
            return ResponseEntity.status(HttpStatus.CREATED).body(clienteMapper.toDTO(clienteGuardado));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    // --- NUEVO MÉTODO ESPECÍFICO PARA ACTUALIZAR EL PERFIL DEL CLIENTE ---
    // NOTA: Se cambia la ruta a "/{id}/perfil" para evitar conflicto con el método 'update' del BaseController.
    @PutMapping("/{id}/perfil") // <-- RUTA CAMBIADA AQUÍ
    public ResponseEntity<?> updatePerfil(@PathVariable Long id, @Valid @RequestBody ClientePerfilUpdateDTO updateDTO) { // <-- AGREGAR @Valid AQUÍ
        try {
            // Busca el cliente existente
            Cliente existingCliente = clienteService.findById(id);
            if (existingCliente == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"error\": \"Cliente no encontrado con ID: " + id + "\"}");
            }

            // Actualiza campos que no sean nulos en el DTO
            if (Objects.nonNull(updateDTO.getNombre())) {
                existingCliente.setNombre(updateDTO.getNombre());
            }
            if (Objects.nonNull(updateDTO.getApellido())) {
                existingCliente.setApellido(updateDTO.getApellido());
            }
            if (Objects.nonNull(updateDTO.getTelefono())) {
                existingCliente.setTelefono(updateDTO.getTelefono());
            }
            if (Objects.nonNull(updateDTO.getFechaNacimiento())) {
                existingCliente.setFechaNacimiento(updateDTO.getFechaNacimiento());
            }
            // Solo actualiza la imagen si se proporciona un ID válido
            if (Objects.nonNull(updateDTO.getImagenId())) {
                Imagen imagen = imagenService.findById(updateDTO.getImagenId());
                existingCliente.setImagen(imagen);
            }

            // Manejo de la actualización de contraseña y email (más complejo, necesita lógica de servicio)
            if (Objects.nonNull(updateDTO.getNuevaPassword()) && !updateDTO.getNuevaPassword().isEmpty()) {
                Usuario usuarioAsociado = existingCliente.getUsuario();
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
                Usuario usuarioAsociado = existingCliente.getUsuario();
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
                existingCliente.setDomicilios(new ArrayList<>(nuevosDomicilios));
            }

            // Guarda el cliente actualizado
            Cliente updatedCliente = clienteService.update(id, existingCliente); // Asumiendo un método update en tu servicio

            return ResponseEntity.ok(clienteMapper.toDTO(updatedCliente));

        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"error\": \"" + e.getMessage() + "\"}");
        } catch (Exception e) {
            // Captura errores genéricos o validaciones no manejadas por @Valid (menos probable)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"error\": \"Error al actualizar el cliente: " + e.getMessage() + "\"}");
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

    @GetMapping("/{clienteId}/pedidos")
    public ResponseEntity<?> getPedidosByClienteId(@PathVariable Long clienteId) {
        try {
            List<Pedido> pedidos = pedidoService.findPedidosByClienteId(clienteId);
            List<PedidoDTO> dtos = pedidos.stream()
                    .map(pedidoMapper::toDTO)
                    .toList();
            return ResponseEntity.ok(dtos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("{\"error\": \"Error al obtener pedidos del cliente: " + e.getMessage() + "\"}");
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
            Cliente actualizado = baseService.toggleBaja(id, baja); // Llama al método del BaseService
            return ResponseEntity.ok().build(); // Podrías devolver un DTO actualizado si es útil
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"error\": \"" + e.getMessage() + "\"}");
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