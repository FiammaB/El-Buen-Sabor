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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
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
    private final ClienteService clienteService;

    public ClienteController(
            ClienteService clienteService,
            ClienteMapper clienteMapper,
            DomicilioService domicilioService,
            ImagenService imagenService,
            UsuarioService usuarioService,
            PedidoService pedidoService,
            PedidoMapper pedidoMapper) {
        super(clienteService);
        this.clienteService = clienteService;
        this.clienteMapper = clienteMapper;
        this.domicilioService = domicilioService;
        this.imagenService = imagenService;
        this.usuarioService = usuarioService;
        this.pedidoService = pedidoService;
        this.pedidoMapper = pedidoMapper;
    }

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

    @PostMapping(consumes = "application/json")
    public ResponseEntity<?> create(@RequestBody ClienteCreateDTO createDTO) {
        try {
            Cliente cliente = clienteMapper.toEntity(createDTO);
            cliente.setBaja(false);

            if (createDTO.getUsuarioId() != null) {
                Usuario usuario = usuarioService.findById(createDTO.getUsuarioId());
                cliente.setUsuario(usuario);
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

    @PostMapping("/registro")
    public ResponseEntity<?> registrarCliente(@RequestBody ClienteCreateDTO dto) {
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

    @PatchMapping("/{id}/baja")
    public ResponseEntity<?> toggleBaja(
            @PathVariable Long id,
            @RequestParam boolean baja // O usa 'estaDadoDeBaja' según tu naming preferido
    ) {
        try {
            Cliente actualizado = baseService.toggleBaja(id, baja);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

}
