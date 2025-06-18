package ElBuenSabor.ProyectoFinal.Controllers;

import ElBuenSabor.ProyectoFinal.DTO.ClienteCreateDTO;
import ElBuenSabor.ProyectoFinal.DTO.ClienteDTO;
import ElBuenSabor.ProyectoFinal.DTO.ClientePerfilUpdateDTO;
import ElBuenSabor.ProyectoFinal.DTO.PedidoDTO;
import ElBuenSabor.ProyectoFinal.Entities.*;
import ElBuenSabor.ProyectoFinal.Exceptions.ResourceNotFoundException; // Para manejar si no encuentra el ID
import ElBuenSabor.ProyectoFinal.Mappers.ClienteMapper;
import ElBuenSabor.ProyectoFinal.Mappers.PedidoMapper;
import ElBuenSabor.ProyectoFinal.Service.*;
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
    private final PedidoService pedidoService; // <-- ¡Inyectar PedidoService!
    private final PedidoMapper pedidoMapper;
    // El constructor inyecta el servicio específico de Cliente, el mapper y los nuevos servicios
    public ClienteController(
            ClienteService clienteService,
            ClienteMapper clienteMapper,
            DomicilioService domicilioService, // <-- Añadir inyección
            ImagenService imagenService,       // <-- Añadir inyección
            UsuarioService usuarioService,
            PedidoService pedidoService, // <-- Añadir al constructor
            PedidoMapper pedidoMapper) {   // <-- Añadir inyección
        super(clienteService);
        this.clienteMapper = clienteMapper;
        this.domicilioService = domicilioService; // Asignar
        this.imagenService = imagenService;       // Asignar
        this.usuarioService = usuarioService;     // Asignar
        this.pedidoService = pedidoService; // <-- Asignar
        this.pedidoMapper = pedidoMapper;
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
    @PutMapping("/{id}/perfil")
    public ResponseEntity<?> updatePerfil(
            @PathVariable Long id,
            @RequestBody ClientePerfilUpdateDTO dto
    ) {
        try {
            clienteService.actualizarPerfil(id, dto);
            return ResponseEntity.ok("{\"message\": \"Perfil actualizado correctamente\"}");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    @GetMapping("/{clienteId}/pedidos") // Nuevo endpoint para obtener pedidos por cliente
    public ResponseEntity<?> getPedidosByClienteId(@PathVariable Long clienteId) {
        try {
            // Opcional: Verificar que el cliente existe
            // Cliente cliente = baseService.findById(clienteId);
            // if (cliente == null) {
            //    return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"error\": \"Cliente no encontrado.\"}");
            // }

            List<Pedido> pedidos = pedidoService.findPedidosByClienteId(clienteId);
            List<PedidoDTO> dtos = pedidos.stream()
                    .map(pedidoMapper::toDTO)
                    .toList();
            return ResponseEntity.ok(dtos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("{\"error\": \"Error al obtener pedidos del cliente: " + e.getMessage() + "\"}");
        }
    }

}