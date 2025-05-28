package ElBuenSabor.ProyectoFinal.Controllers;

import ElBuenSabor.ProyectoFinal.DTO.ClienteActualizacionDTO;
import ElBuenSabor.ProyectoFinal.DTO.ClienteRegistroDTO;
import ElBuenSabor.ProyectoFinal.DTO.LoginDTO;
import ElBuenSabor.ProyectoFinal.DTO.ClienteResponseDTO;
import ElBuenSabor.ProyectoFinal.Entities.Cliente; // Solo para el helper convertToClienteResponseDTO si el servicio devuelve entidad
import ElBuenSabor.ProyectoFinal.Entities.Usuario; // Para el actor en actualizarCliente
import ElBuenSabor.ProyectoFinal.Service.ClienteService;
import ElBuenSabor.ProyectoFinal.Service.UsuarioService; // Para obtener el actor
import jakarta.validation.Valid; // Para validación
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication; // Para obtener el actor
import org.springframework.security.core.context.SecurityContextHolder; // Para obtener el actor
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import ElBuenSabor.ProyectoFinal.DTO.DomicilioDTO;
import ElBuenSabor.ProyectoFinal.DTO.ImagenDTO;
import ElBuenSabor.ProyectoFinal.DTO.LocalidadDTO;
import ElBuenSabor.ProyectoFinal.DTO.ProvinciaDTO;
import ElBuenSabor.ProyectoFinal.DTO.PaisDTO;
import ElBuenSabor.ProyectoFinal.Entities.Domicilio;
import ElBuenSabor.ProyectoFinal.Entities.Imagen;
import ElBuenSabor.ProyectoFinal.Entities.Localidad;
import ElBuenSabor.ProyectoFinal.Entities.Pais;
import ElBuenSabor.ProyectoFinal.Entities.Provincia;
import java.util.ArrayList;


@RestController
@RequestMapping("/api/v1/clientes")
@CrossOrigin(origins = "*")
public class ClienteController {

    @Autowired
    private ClienteService clienteService;

    @Autowired
    private UsuarioService usuarioService;

    // Endpoint para registrar un nuevo cliente
    @PostMapping("/registrar")
    public ResponseEntity<?> registrarCliente(@Valid @RequestBody ClienteRegistroDTO registroDTO) {
        try {
            // CORREGIDO: clienteService.registrarCliente ahora devuelve ClienteResponseDTO
            ClienteResponseDTO nuevoClienteDTO = clienteService.registrarCliente(registroDTO);
            return new ResponseEntity<>(nuevoClienteDTO, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    // Endpoint para login de cliente
    @PostMapping("/login")
    public ResponseEntity<?> loginCliente(@Valid @RequestBody LoginDTO loginDTO) {
        try {
            // CORREGIDO: clienteService.loginCliente ahora devuelve ClienteResponseDTO
            ClienteResponseDTO clienteDTO = clienteService.loginCliente(loginDTO);
            return ResponseEntity.ok(clienteDTO);
        } catch (Exception e) {
            return new ResponseEntity<>("Credenciales inválidas.", HttpStatus.UNAUTHORIZED);
        }
    }

    // Endpoint para obtener un cliente por su ID (solo activos)
    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerClientePorId(@PathVariable Long id) {
        try {
            // CORREGIDO: Usar findClienteByIdDTO que devuelve ClienteResponseDTO
            ClienteResponseDTO clienteDTO = clienteService.findClienteByIdDTO(id);
            if (clienteDTO != null) { // El servicio devuelve DTO o null/lanza excepción
                return ResponseEntity.ok(clienteDTO);
            } else {
                // Este caso podría no alcanzarse si el servicio lanza excepción cuando no encuentra
                return new ResponseEntity<>("Cliente activo no encontrado con ID: " + id, HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            if (e.getMessage().contains("no encontrado")) { // Si el servicio lanza excepción
                return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>("Error al obtener el cliente: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Endpoint para actualizar la información de un cliente
    @PutMapping("/{id}/perfil")
    public ResponseEntity<?> actualizarCliente(@PathVariable Long id, @Valid @RequestBody ClienteActualizacionDTO actualizacionDTO) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            Usuario actor = null;
            // Lógica para obtener el 'actor' desde el contexto de seguridad (placeholder)
            if (authentication != null && authentication.getPrincipal() instanceof Usuario) {
                actor = (Usuario) authentication.getPrincipal();
            } else if (authentication != null && authentication.getPrincipal() instanceof org.springframework.security.core.userdetails.User) {
                String username = ((org.springframework.security.core.userdetails.User) authentication.getPrincipal()).getUsername();
                Optional<Usuario> optActor = usuarioService.findByUsernameRaw(username); // Usar Raw para encontrarlo siempre
                if(optActor.isPresent()) {
                    actor = optActor.get();
                } else {
                    System.err.println("ADVERTENCIA: Actor UserDetails no pudo ser mapeado a entidad Usuario para actualizarCliente.");
                    // Podría lanzar error si el actor es estrictamente necesario y no se encuentra
                    // throw new Exception("Actor no encontrado para la operación.");
                }
            } else {
                System.err.println("ADVERTENCIA: No se pudo obtener el actor autenticado para actualizarCliente.");
                // Para pruebas sin seguridad, si el servicio puede manejar actor null o si se simula:
                // Si el ID del cliente a actualizar coincide con un ID de prueba y no hay actor, se podría simular que es él mismo.
                // Esta lógica es compleja sin seguridad real.
            }

            // CORREGIDO: clienteService.actualizarCliente espera el actor y devuelve ClienteResponseDTO
            ClienteResponseDTO clienteActualizadoDTO = clienteService.actualizarCliente(id, actualizacionDTO, actor);
            return ResponseEntity.ok(clienteActualizadoDTO);
        } catch (Exception e) {
            if (e.getMessage().contains("no encontrado")) {
                return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
            }
            if (e.getMessage().contains("No tiene permisos")) {
                return new ResponseEntity<>(e.getMessage(), HttpStatus.FORBIDDEN);
            }
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    // Endpoint para dar de baja un cliente (lógica de soft delete) - Admin
    @PatchMapping("/admin/{id}/dar-baja")
    public ResponseEntity<?> darBajaClienteAdmin(@PathVariable Long id) {
        try {
            // CORREGIDO: Usar el método softDelete heredado o uno específico del servicio
            // Asumiendo que ClienteService hereda softDelete(id) que devuelve la entidad,
            // o tiene un void darBajaCliente(id)
            clienteService.softDelete(id); // Si ClienteService.softDelete() es void o devuelve la entidad
            return ResponseEntity.ok("Cliente ID: " + id + " dado de baja correctamente.");
        } catch (Exception e) {
            if (e.getMessage().contains("no encontrado") || e.getMessage().contains("ya está dado de baja")) {
                return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
            }
            if (e.getMessage().contains("tiene pedidos activos")) {
                return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
            }
            return new ResponseEntity<>("Error al dar de baja cliente: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    // Endpoint para dar de alta un cliente (revertir soft delete) - Admin
    @PatchMapping("/admin/{id}/dar-alta")
    public ResponseEntity<?> darAltaClienteAdmin(@PathVariable Long id) {
        try {
            // CORREGIDO: Usar el método reactivate heredado o uno específico del servicio
            clienteService.reactivate(id); // Si ClienteService.reactivate() es void o devuelve la entidad
            return ResponseEntity.ok("Cliente ID: " + id + " dado de alta correctamente.");
        } catch (Exception e) {
            if (e.getMessage().contains("no encontrado") || e.getMessage().contains("no está dado de baja")) {
                return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
            }
            return new ResponseEntity<>("Error al dar de alta cliente: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Endpoint para listar todos los clientes activos (general)
    @GetMapping("")
    public ResponseEntity<?> listarClientesActivos() {
        try {
            // CORREGIDO: Usar findAllClientesDTO que devuelve List<ClienteResponseDTO>
            List<ClienteResponseDTO> responseDTOs = clienteService.findAllClientesDTO();
            return ResponseEntity.ok(responseDTOs);
        } catch (Exception e) {
            return new ResponseEntity<>("Error al listar clientes activos: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Endpoint para listar todos los clientes incluyendo bajas (solo para ADMIN)
    @GetMapping("/admin/todos")
    public ResponseEntity<?> listarTodosLosClientesAdmin() {
        try {
            List<ClienteResponseDTO> dtos = clienteService.findAllClientesIncludingDeleted();
            return ResponseEntity.ok(dtos);
        } catch (Exception e) {
            return new ResponseEntity<>("Error al listar todos los clientes (admin): " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Endpoint para obtener un cliente por ID incluyendo bajas (solo para ADMIN)
    @GetMapping("/admin/{id}")
    public ResponseEntity<?> obtenerClientePorIdAdmin(@PathVariable Long id) {
        try {
            ClienteResponseDTO dto = clienteService.findClienteByIdIncludingDeletedDTO(id);
            if (dto != null) {
                return ResponseEntity.ok(dto);
            } else {
                return new ResponseEntity<>("Cliente (activo o inactivo) no encontrado con ID: " + id, HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>("Error al obtener cliente (admin): " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // --- Helper para convertir Entidad Cliente a ClienteResponseDTO ---
    // Este helper es útil si algún método del servicio (como reactivate que devuelve la entidad Cliente)
    // necesita ser convertido a DTO en el controlador.
    // Si el servicio siempre devuelve DTOs, este helper podría moverse al servicio o a una clase Mapper.
    private ClienteResponseDTO convertToClienteResponseDTO(Cliente cliente) {
        if (cliente == null) {
            return null;
        }
        ClienteResponseDTO dto = new ClienteResponseDTO();
        dto.setId(cliente.getId());
        dto.setNombre(cliente.getNombre());
        dto.setApellido(cliente.getApellido());
        dto.setTelefono(cliente.getTelefono());
        dto.setEmail(cliente.getEmail());
        dto.setUsername(cliente.getUsername());
        dto.setAuth0Id(cliente.getAuth0Id());
        dto.setFechaNacimiento(cliente.getFechaNacimiento());
        dto.setBaja(cliente.isBaja()); // CORREGIDO: Usar el campo 'baja'

        if (cliente.getImagen() != null) {
            ImagenDTO imagenDTO = new ImagenDTO();
            imagenDTO.setId(cliente.getImagen().getId());
            imagenDTO.setDenominacion(cliente.getImagen().getDenominacion());
            imagenDTO.setBaja(cliente.getImagen().isBaja());
            dto.setImagen(imagenDTO);
        }

        if (cliente.getDomicilios() != null) {
            dto.setDomicilios(cliente.getDomicilios().stream()
                    .filter(dom -> !dom.isBaja())
                    .map(this::convertToSimpleDomicilioDTO)
                    .collect(Collectors.toList()));
        } else {
            dto.setDomicilios(new ArrayList<>());
        }
        return dto;
    }

    private DomicilioDTO convertToSimpleDomicilioDTO(Domicilio domicilio) {
        if (domicilio == null) return null;
        DomicilioDTO dto = new DomicilioDTO();
        dto.setId(domicilio.getId());
        dto.setCalle(domicilio.getCalle());
        dto.setNumero(domicilio.getNumero());
        dto.setCp(domicilio.getCp());
        dto.setBaja(domicilio.isBaja());
        if (domicilio.getLocalidad() != null) {
            LocalidadDTO locDto = new LocalidadDTO();
            locDto.setId(domicilio.getLocalidad().getId());
            locDto.setNombre(domicilio.getLocalidad().getNombre());
            locDto.setBaja(domicilio.getLocalidad().isBaja());
            // Para simplificar, no anidamos provincia/país aquí, pero se podría si es necesario.
            // Si ProvinciaDTO y PaisDTO se necesitan, deben ser convertidos también.
            // Ejemplo si LocalidadDTO tuviera ProvinciaDTO:
            // if (domicilio.getLocalidad().getProvincia() != null) {
            //    ProvinciaDTO provDto = new ProvinciaDTO();
            //    // ... mapear provincia ...
            //    locDto.setProvincia(provDto);
            // }
            dto.setLocalidad(locDto);
        }
        return dto;
    }
}
