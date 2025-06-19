package ElBuenSabor.ProyectoFinal.Controllers;

import ElBuenSabor.ProyectoFinal.DTO.ClienteCreateDTO;
import ElBuenSabor.ProyectoFinal.DTO.ClienteDTO;
import ElBuenSabor.ProyectoFinal.Entities.Cliente;
import ElBuenSabor.ProyectoFinal.Entities.Domicilio; // Importa Domicilio
import ElBuenSabor.ProyectoFinal.Entities.Imagen;   // Importa Imagen si ClienteCreateDTO tiene imagenId
import ElBuenSabor.ProyectoFinal.Entities.Usuario;  // Importa Usuario si ClienteCreateDTO tiene usuarioId
import ElBuenSabor.ProyectoFinal.Exceptions.ResourceNotFoundException; // Para manejar si no encuentra el ID
import ElBuenSabor.ProyectoFinal.Mappers.ClienteMapper;
import ElBuenSabor.ProyectoFinal.Service.ClienteService;
import ElBuenSabor.ProyectoFinal.Service.DomicilioService; // <-- ¡Necesitamos este servicio!
import ElBuenSabor.ProyectoFinal.Service.ImagenService;   // <-- ¡Necesitamos este servicio si mapeamos imagenId!
import ElBuenSabor.ProyectoFinal.Service.UsuarioService;  // <-- ¡Necesitamos este servicio si mapeamos usuarioId!
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet; // Importa HashSet
import java.util.List;
import java.util.Set;     // Importa Set

@RestController
@RequestMapping("/api/clientes") // Define la URL base para este controlador
public class ClienteController extends BaseController<Cliente, Long> {

    private final ClienteMapper clienteMapper;
    private final DomicilioService domicilioService; // <-- Inyectamos DomicilioService
    private final ImagenService imagenService;       // <-- Inyectamos ImagenService
    private final UsuarioService usuarioService;      // <-- Inyectamos UsuarioService

    // El constructor inyecta el servicio específico de Cliente, el mapper y los nuevos servicios
    public ClienteController(
            ClienteService clienteService,
            ClienteMapper clienteMapper,
            DomicilioService domicilioService, // <-- Añadir inyección
            ImagenService imagenService,       // <-- Añadir inyección
            UsuarioService usuarioService) {   // <-- Añadir inyección
        super(clienteService);
        this.clienteMapper = clienteMapper;
        this.domicilioService = domicilioService; // Asignar
        this.imagenService = imagenService;       // Asignar
        this.usuarioService = usuarioService;     // Asignar
    }

    // Sobrescribir getAll para devolver DTOs y manejar excepciones
    @GetMapping
    @Override
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

    // Sobrescribir getOne para devolver un DTO y manejar excepciones
    @GetMapping("/{id}")
    @Override
    public ResponseEntity<?> getOne(@PathVariable Long id) {
        try {
            Cliente cliente = baseService.findById(id);
            return ResponseEntity.ok(clienteMapper.toDTO(cliente));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    // Sobrescribir create para aceptar un DTO de entrada, mapear y manejar excepciones
    @PostMapping(consumes = "application/json")
    public ResponseEntity<?> create(@RequestBody ClienteCreateDTO createDTO) {
        try {
            Cliente cliente = clienteMapper.toEntity(createDTO);
            cliente.setBaja(false); // Por defecto, un nuevo cliente está activo

            // Establecer relaciones ManyToOne (Usuario, Imagen)
            if (createDTO.getUsuarioId() != null) {
                Usuario usuario = usuarioService.findById(createDTO.getUsuarioId());
                cliente.setUsuario(usuario);
            }
            if (createDTO.getImagenId() != null) {
                Imagen imagen = imagenService.findById(createDTO.getImagenId());
                cliente.setImagen(imagen);
            }

            // Establecer relaciones ManyToMany para Domicilios
            if (createDTO.getDomicilioIds() != null && !createDTO.getDomicilioIds().isEmpty()) {
                Set<Domicilio> domicilios = new HashSet<>();
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

    // Sobrescribir update para aceptar un DTO de entrada, mapear y manejar excepciones
    @PutMapping(value = "/{id}", consumes = "application/json")

    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody ClienteCreateDTO updateDTO) {
        try {
            // Es mejor obtener la entidad existente y actualizar sus propiedades
            Cliente existing = baseService.findById(id); // Obtiene el cliente existente

            // Actualizar propiedades básicas
            existing.setNombre(updateDTO.getNombre());
            existing.setApellido(updateDTO.getApellido());
            existing.setTelefono(updateDTO.getTelefono());
            existing.setEmail(updateDTO.getEmail());
            // Cuidado: la password no suele actualizarse así. Solo actualiza si tu lógica lo requiere.
            // existing.setPassword(updateDTO.getPassword());
            existing.setFechaNacimiento(updateDTO.getFechaNacimiento());
            // La baja se maneja con toggleBaja o si tu ClienteService.update lo permite a través del DTO.
            // Si el DTO de actualización no debe cambiar la 'baja', omite esta línea:
            // existing.setBaja(updateDTO.getBaja());

            // Actualizar relaciones ManyToOne (Usuario, Imagen)
            if (updateDTO.getUsuarioId() != null) {
                Usuario usuario = usuarioService.findById(updateDTO.getUsuarioId());
                existing.setUsuario(usuario);
            } else {
                existing.setUsuario(null); // Si el ID es null, remover la relación
            }
            if (updateDTO.getImagenId() != null) {
                Imagen imagen = imagenService.findById(updateDTO.getImagenId());
                existing.setImagen(imagen);
            } else {
                existing.setImagen(null); // Si el ID es null, remover la relación
            }

            // Sincronizar relaciones ManyToMany para Domicilios
            if (updateDTO.getDomicilioIds() != null) {
                existing.getDomicilios().clear(); // Limpia la colección existente
                for (Long domicilioId : updateDTO.getDomicilioIds()) {
                    Domicilio domicilio = domicilioService.findById(domicilioId);
                    existing.getDomicilios().add(domicilio);
                    // IMPORTANTE: Asegúrate de que la relación inversa Domicilio.clientes se actualice también
                    // si es manejada manualmente o si Cascade no es suficiente (común en ManyToMany)
                    domicilio.getClientes().add(existing); // <-- Añadir la inversa si es bidireccional y no por Cascade
                }
            } else {
                existing.getDomicilios().clear(); // Si no se envían IDs, limpiar la colección
            }

            Cliente updated = baseService.update(id, existing);
            return ResponseEntity.ok(clienteMapper.toDTO(updated));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    // Los métodos DELETE, ACTIVATE, DEACTIVATE pueden heredarse directamente de BaseController
}