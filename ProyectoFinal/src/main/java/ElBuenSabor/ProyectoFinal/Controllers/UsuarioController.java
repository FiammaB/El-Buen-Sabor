package ElBuenSabor.ProyectoFinal.Controllers;

import ElBuenSabor.ProyectoFinal.DTO.*; // Importar todos los DTOs necesarios
import ElBuenSabor.ProyectoFinal.Entities.*;
import ElBuenSabor.ProyectoFinal.Service.ClienteService; // Para convertir a ClienteResponseDTO
import ElBuenSabor.ProyectoFinal.Service.EmpleadoService; // Para convertir a EmpleadoResponseDTO
import ElBuenSabor.ProyectoFinal.Service.UsuarioService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
// import org.springframework.security.access.prepost.PreAuthorize; // Para seguridad

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/api/v1/usuarios") // Ruta para gestión general de usuarios (admin)
@CrossOrigin(origins = "*")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private ClienteService clienteService; // Para la conversión DTO

    @Autowired
    private EmpleadoService empleadoService; // Para la conversión DTO y lógica específica

    // Endpoint para listar todos los usuarios (clientes, empleados) incluyendo los dados de baja
    // Generalmente para un rol de Administrador
    @GetMapping("/admin/todos")
    // @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<?> listarTodosLosUsuariosIncludingDeletedAdmin() {
        try {
            List<Usuario> usuarios = usuarioService.findAllIncludingDeleted();
            List<Object> dtos = usuarios.stream().map(this::convertToAppropriateUserDTO).collect(Collectors.toList());
            return ResponseEntity.ok(dtos);
        } catch (Exception e) {
            return new ResponseEntity<>("Error al listar todos los usuarios (admin): " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Endpoint para obtener un usuario por su ID genérico, incluyendo los dados de baja
    // Generalmente para un rol de Administrador
    @GetMapping("/admin/{id}")
    // @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<?> obtenerUsuarioPorIdIncludingDeletedAdmin(@PathVariable Long id) {
        try {
            Optional<Usuario> usuarioOptional = usuarioService.findByIdIncludingDeleted(id);
            if (usuarioOptional.isPresent()) {
                return ResponseEntity.ok(convertToAppropriateUserDTO(usuarioOptional.get()));
            } else {
                return new ResponseEntity<>("Usuario no encontrado (activo o inactivo) con ID: " + id, HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>("Error al obtener usuario por ID (admin): " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Endpoint para obtener un usuario por su username, incluyendo los dados de baja
    // Generalmente para un rol de Administrador
    @GetMapping("/admin/username/{username}")
    // @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<?> obtenerUsuarioPorUsernameIncludingDeletedAdmin(@PathVariable String username) {
        try {
            Optional<Usuario> usuarioOptional = usuarioService.findByUsernameRaw(username);
            if (usuarioOptional.isPresent()) {
                return ResponseEntity.ok(convertToAppropriateUserDTO(usuarioOptional.get()));
            } else {
                return new ResponseEntity<>("Usuario no encontrado (activo o inactivo) con username: " + username, HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>("Error al obtener usuario por username (admin): " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // El borrado lógico (dar de baja) y reactivación (dar de alta)
    // se manejan mejor a través de los controladores específicos (ClienteController, EmpleadoController)
    // ya que la lógica de "quién puede hacer qué" y las validaciones de "en uso" son específicas del tipo de usuario.
    // Por ejemplo, un admin da de baja un Cliente o un Empleado.

    // --- Helper para convertir Entidad Usuario a un DTO apropiado ---
    private Object convertToAppropriateUserDTO(Usuario usuario) {
        if (usuario == null) {
            return null;
        }
        if (usuario instanceof Cliente) {
            // Usar el helper de conversión de ClienteServiceImpl o uno local si es necesario
            return convertToClienteResponseDTO((Cliente) usuario);
        } else if (usuario instanceof Empleado) {
            // Usar el helper de conversión de EmpleadoServiceImpl o uno local
            return convertToEmpleadoResponseDTO((Empleado) usuario);
        }
        // Fallback a un DTO muy básico si no es un tipo conocido
        // (esto no debería pasar si solo tienes Cliente y Empleado como hijos de Usuario)
        var baseInfo = new java.util.HashMap<String, Object>();
        baseInfo.put("id", usuario.getId());
        baseInfo.put("username", usuario.getUsername());
        baseInfo.put("auth0Id", usuario.getAuth0Id());
        baseInfo.put("baja", usuario.isBaja());
        baseInfo.put("tipo", usuario.getClass().getSimpleName());
        return baseInfo;
    }

    // Estos helpers de conversión se replican aquí para el UsuarioController.
    // Idealmente, estarían en una clase Mapper o los servicios específicos devolverían DTOs.
    private ClienteResponseDTO convertToClienteResponseDTO(Cliente cliente) {
        if (cliente == null) return null;
        ClienteResponseDTO dto = new ClienteResponseDTO();
        dto.setId(cliente.getId());
        dto.setNombre(cliente.getNombre());
        dto.setApellido(cliente.getApellido());
        dto.setTelefono(cliente.getTelefono());
        dto.setEmail(cliente.getEmail());
        dto.setUsername(cliente.getUsername());
        dto.setAuth0Id(cliente.getAuth0Id());
        dto.setFechaNacimiento(cliente.getFechaNacimiento());
        dto.setBaja(cliente.isBaja()); // Cannot resolve method 'setBaja' in 'ClienteResponseDTO'
        if (cliente.getImagen() != null) {
            dto.setImagen(convertToImagenDTO(cliente.getImagen()));
        }
        if (cliente.getDomicilios() != null) {
            dto.setDomicilios(cliente.getDomicilios().stream()
                    .map(this::convertToDomicilioDTO).collect(Collectors.toList()));
        } else {
            dto.setDomicilios(new ArrayList<>());
        }
        return dto;
    }

    private EmpleadoResponseDTO convertToEmpleadoResponseDTO(Empleado empleado) {
        if (empleado == null) return null;
        EmpleadoResponseDTO dto = new EmpleadoResponseDTO();
        dto.setId(empleado.getId());
        dto.setNombre(empleado.getNombre());
        dto.setApellido(empleado.getApellido());
        dto.setTelefono(empleado.getTelefono());
        dto.setEmail(empleado.getEmail());
        dto.setUsername(empleado.getUsername());
        dto.setRol(empleado.getRol());
        dto.setFechaNacimiento(empleado.getFechaNacimiento());
        dto.setFechaAlta(empleado.getFechaAlta());
        dto.setBaja(empleado.isBaja());
        if (empleado.getImagen() != null) {
            dto.setImagen(convertToImagenDTO(empleado.getImagen()));
        }
        if (empleado.getDomicilios() != null) {
            dto.setDomicilios(empleado.getDomicilios().stream()
                    .map(this::convertToDomicilioDTO).collect(Collectors.toList()));
        } else {
            dto.setDomicilios(new ArrayList<>());
        }
        return dto;
    }

    // Helpers para DTOs anidados (simplificados)
    private ImagenDTO convertToImagenDTO(Imagen imagen) {
        if (imagen == null) return null;
        ImagenDTO dto = new ImagenDTO();
        dto.setId(imagen.getId());
        dto.setDenominacion(imagen.getDenominacion());
        dto.setBaja(imagen.isBaja());
        return dto;
    }
    private DomicilioDTO convertToDomicilioDTO(Domicilio dom) {
        if (dom == null) return null;
        DomicilioDTO dto = new DomicilioDTO();
        dto.setId(dom.getId());
        dto.setCalle(dom.getCalle());
        // ... otros campos necesarios para la vista de usuario ...
        dto.setBaja(dom.isBaja());
        if (dom.getLocalidad() != null) {
            LocalidadDTO locDto = new LocalidadDTO();
            locDto.setId(dom.getLocalidad().getId());
            locDto.setNombre(dom.getLocalidad().getNombre());
            // ... anidar provincia y país si es necesario ...
            dto.setLocalidad(locDto);
        }
        return dto;
    }
}
