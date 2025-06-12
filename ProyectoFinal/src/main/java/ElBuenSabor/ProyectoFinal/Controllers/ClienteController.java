package ElBuenSabor.ProyectoFinal.Controllers;

import ElBuenSabor.ProyectoFinal.DTO.ClienteActualizacionDTO;
import ElBuenSabor.ProyectoFinal.DTO.ClienteRegistroDTO;
import ElBuenSabor.ProyectoFinal.DTO.LoginDTO;
import ElBuenSabor.ProyectoFinal.DTO.ClienteResponseDTO; // Asumiendo que creamos este DTO
import ElBuenSabor.ProyectoFinal.Entities.Cliente;
import ElBuenSabor.ProyectoFinal.Service.ClienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import ElBuenSabor.ProyectoFinal.DTO.DomicilioDTO; // Para mapear domicilios
import ElBuenSabor.ProyectoFinal.DTO.ImagenDTO; // Para mapear imagen
import ElBuenSabor.ProyectoFinal.DTO.LocalidadDTO;
import ElBuenSabor.ProyectoFinal.DTO.ProvinciaDTO;
import ElBuenSabor.ProyectoFinal.DTO.PaisDTO;
import java.util.ArrayList;


@RestController
@RequestMapping("/api/v1/clientes") // Versión de API en la URL es una buena práctica
@CrossOrigin(origins = "*") // Permite peticiones de cualquier origen. Ajusta según tus necesidades de seguridad.
public class ClienteController {

    @Autowired
    private ClienteService clienteService;

    // Endpoint para registrar un nuevo cliente
    @PostMapping("/registrar")
    public ResponseEntity<?> registrarCliente(@RequestBody ClienteRegistroDTO registroDTO) {
        try {
            Cliente nuevoCliente = clienteService.registrarCliente(registroDTO); //
            return new ResponseEntity<>(convertToClienteResponseDTO(nuevoCliente), HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    // Endpoint para login de cliente
    @PostMapping("/login")
    public ResponseEntity<?> loginCliente(@RequestBody LoginDTO loginDTO) {
        try {
            Cliente cliente = clienteService.loginCliente(loginDTO); //
            // En una aplicación real, aquí se generaría un token JWT (JSON Web Token)
            // y se devolvería al cliente para autenticar las siguientes peticiones.
            return ResponseEntity.ok(convertToClienteResponseDTO(cliente));
        } catch (Exception e) {
            // Es mejor ser genérico en mensajes de error de login por seguridad
            return new ResponseEntity<>("Credenciales inválidas.", HttpStatus.UNAUTHORIZED);
        }
    }

    // Endpoint para obtener un cliente por su ID
    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerClientePorId(@PathVariable Long id) {
        try {
            Optional<Cliente> clienteOptional = clienteService.findById(id); //
            if (clienteOptional.isPresent()) {
                return ResponseEntity.ok(convertToClienteResponseDTO(clienteOptional.get()));
            } else {
                return new ResponseEntity<>("Cliente no encontrado.", HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Endpoint para actualizar la información de un cliente
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarCliente(@PathVariable Long id, @RequestBody ClienteActualizacionDTO actualizacionDTO) {
        try {
            Cliente clienteActualizado = clienteService.actualizarCliente(id, actualizacionDTO); //
            return ResponseEntity.ok(convertToClienteResponseDTO(clienteActualizado));
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    // Endpoint para dar de baja un cliente (lógica de soft delete)
    // Este endpoint podría requerir un rol de ADMIN
    @PatchMapping("/{id}/dar-baja")
    public ResponseEntity<?> darBajaCliente(@PathVariable Long id) {
        try {
            clienteService.darBajaCliente(id); //
            return ResponseEntity.ok("Cliente dado de baja correctamente.");
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    // Endpoint para dar de alta un cliente (revertir soft delete)
    // Este endpoint podría requerir un rol de ADMIN
    @PatchMapping("/{id}/dar-alta")
    public ResponseEntity<?> darAltaCliente(@PathVariable Long id) {
        try {
            clienteService.darAltaCliente(id); //
            return ResponseEntity.ok("Cliente dado de alta correctamente.");
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    // Endpoint para listar todos los clientes (podría ser solo para ADMIN)
    @GetMapping("")
    public ResponseEntity<?> listarClientes() {
        try {
            List<Cliente> clientes = clienteService.findAll(); //
            List<ClienteResponseDTO> responseDTOs = clientes.stream()
                    .map(this::convertToClienteResponseDTO)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(responseDTOs);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Método para eliminar un cliente (Hard Delete, usar con precaución, podría ser solo para ADMIN)
    // @DeleteMapping("/{id}")
    // public ResponseEntity<?> eliminarCliente(@PathVariable Long id) {
    // try {
    // boolean eliminado = clienteService.delete(id);
    // if (eliminado) {
    // return ResponseEntity.ok("Cliente eliminado permanentemente.");
    // } else {
    // return new ResponseEntity<>("Cliente no encontrado para eliminar.", HttpStatus.NOT_FOUND);
    // }
    // } catch (Exception e) {
    // return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    // }
    // }

    // --- Helper para convertir Entidad Cliente a ClienteResponseDTO ---
    // Este DTO es para las respuestas, para no exponer la contraseña y formatear los datos.
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
        dto.setEstaDadoDeBaja(cliente.isEstaDadoDeBaja());

        if (cliente.getImagen() != null) {
            ImagenDTO imagenDTO = new ImagenDTO();
            imagenDTO.setId(cliente.getImagen().getId());
            imagenDTO.setDenominacion(cliente.getImagen().getDenominacion());
            dto.setImagen(imagenDTO);
        }

        if (cliente.getDomicilios() != null) {
            dto.setDomicilios(cliente.getDomicilios().stream().map(dom -> {
                DomicilioDTO domDto = new DomicilioDTO();
                domDto.setId(dom.getId());
                domDto.setCalle(dom.getCalle());
                domDto.setNumero(dom.getNumero());
                domDto.setCp(dom.getCp());
                if (dom.getLocalidad() != null) {
                    LocalidadDTO locDto = new LocalidadDTO();
                    locDto.setId(dom.getLocalidad().getId());
                    locDto.setNombre(dom.getLocalidad().getNombre());
                    if (dom.getLocalidad().getProvincia() != null) {
                        ProvinciaDTO provDto = new ProvinciaDTO();
                        provDto.setId(dom.getLocalidad().getProvincia().getId());
                        provDto.setNombre(dom.getLocalidad().getProvincia().getNombre());
                        if (dom.getLocalidad().getProvincia().getPais() != null) {
                            PaisDTO paisDto = new PaisDTO();
                            paisDto.setId(dom.getLocalidad().getProvincia().getPais().getId());
                            paisDto.setNombre(dom.getLocalidad().getProvincia().getPais().getNombre());
                            provDto.setPais(paisDto);
                        }
                        locDto.setProvincia(provDto);
                    }
                    domDto.setLocalidad(locDto);
                }
                return domDto;
            }).collect(Collectors.toList()));
        } else {
            dto.setDomicilios(new ArrayList<>());
        }
        // No incluir cliente.getPassword() en el DTO de respuesta.
        return dto;
    }
}