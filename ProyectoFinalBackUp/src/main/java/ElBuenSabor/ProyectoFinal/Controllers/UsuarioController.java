package ElBuenSabor.ProyectoFinal.Controllers;

import ElBuenSabor.ProyectoFinal.DTO.ClienteResponseDTO; // Usaremos este para clientes
import ElBuenSabor.ProyectoFinal.DTO.DomicilioDTO;
import ElBuenSabor.ProyectoFinal.DTO.ImagenDTO;
import ElBuenSabor.ProyectoFinal.DTO.LocalidadDTO;
import ElBuenSabor.ProyectoFinal.DTO.PaisDTO;
import ElBuenSabor.ProyectoFinal.DTO.ProvinciaDTO;
// Considerar un UsuarioBaseResponseDTO para información muy genérica si hay muchos tipos de usuario
// import ElBuenSabor.ProyectoFinal.DTO.UsuarioBaseResponseDTO;
import ElBuenSabor.ProyectoFinal.Entities.Cliente;
import ElBuenSabor.ProyectoFinal.Entities.Usuario;
import ElBuenSabor.ProyectoFinal.Service.UsuarioService;
import ElBuenSabor.ProyectoFinal.Service.ClienteService; // Para obtener detalles del cliente

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    private ClienteService clienteService; // Inyectar para convertir Cliente a ClienteResponseDTO

    // Endpoint para listar todos los usuarios (clientes, y futuros empleados, etc.)
    // Generalmente para un rol de Administrador
    @GetMapping("")
    public ResponseEntity<?> listarTodosLosUsuarios() {
        try {
            List<Usuario> usuarios = usuarioService.findAll();
            List<Object> dtos = usuarios.stream().map(this::convertToAppropriateUserDTO).collect(Collectors.toList());
            return ResponseEntity.ok(dtos);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Endpoint para obtener un usuario por su ID genérico
    // Generalmente para un rol de Administrador
    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerUsuarioPorId(@PathVariable Long id) {
        try {
            Optional<Usuario> usuarioOptional = usuarioService.findById(id);
            if (usuarioOptional.isPresent()) {
                return ResponseEntity.ok(convertToAppropriateUserDTO(usuarioOptional.get()));
            } else {
                return new ResponseEntity<>("Usuario no encontrado.", HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Endpoint para obtener un usuario por su username
    // Generalmente para un rol de Administrador o sistema interno
    @GetMapping("/username/{username}")
    public ResponseEntity<?> obtenerUsuarioPorUsername(@PathVariable String username) {
        try {
            Optional<Usuario> usuarioOptional = usuarioService.findByUsername(username);
            if (usuarioOptional.isPresent()) {
                return ResponseEntity.ok(convertToAppropriateUserDTO(usuarioOptional.get()));
            } else {
                return new ResponseEntity<>("Usuario no encontrado con username: " + username, HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Endpoint para eliminar un usuario (cualquier tipo)
    // ¡OPERACIÓN DELICADA! Generalmente para un rol de Administrador.
    // Considerar borrado lógico a nivel de subtipo (ej. Cliente.estaDadoDeBaja)
    // en lugar de un borrado físico aquí, o que el servicio maneje la lógica
    // de qué hacer si es un Cliente (marcarlo como dado de baja).
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarUsuario(@PathVariable Long id) {
        try {
            // Aquí es donde la lógica se complica. Si es un Cliente, ¿debería llamar a clienteService.darBajaCliente?
            // O ¿el usuarioService.delete(id) es suficiente si las cascadas están bien configuradas?
            // Por simplicidad, el BaseService.delete intentará borrarlo.
            // Sería mejor que el servicio determinara el tipo y aplicara lógica de borrado específica.

            // Alternativa: Si es un cliente, marcarlo como dado de baja.
            Optional<Usuario> usuarioOpt = usuarioService.findById(id);
            if (!usuarioOpt.isPresent()) {
                return new ResponseEntity<>("Usuario no encontrado.", HttpStatus.NOT_FOUND);
            }
            Usuario usuario = usuarioOpt.get();
            if (usuario instanceof Cliente) {
                clienteService.darBajaCliente(id); // Usar el método de soft delete de ClienteService
                return ResponseEntity.ok("Cliente marcado como dado de baja.");
            } else {
                // Para otros tipos de usuario, podrías implementar un borrado físico o lógico diferente.
                // O si el objetivo es borrado físico general:
                // boolean eliminado = usuarioService.delete(id);
                // if (eliminado) {
                //    return ResponseEntity.ok("Usuario eliminado físicamente.");
                // } else {
                //    return new ResponseEntity<>("No se pudo eliminar el usuario o no fue encontrado.", HttpStatus.NOT_FOUND);
                // }
                return new ResponseEntity<>("Tipo de usuario no soportado para esta operación de 'eliminación' genérica o es un cliente que debería ser 'dado de baja'.", HttpStatus.BAD_REQUEST);
            }

        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }


    // --- Helper para convertir Entidad Usuario a un DTO apropiado ---
    private Object convertToAppropriateUserDTO(Usuario usuario) {
        if (usuario == null) {
            return null;
        }
        if (usuario instanceof Cliente) {
            return convertToClienteResponseDTO((Cliente) usuario);
        }
        // } else if (usuario instanceof Empleado) { // Si tuvieras Empleado
        // return convertToEmpleadoResponseDTO((Empleado) usuario);
        // }

        // Fallback a un DTO muy básico si no es un tipo conocido con DTO específico
        // Esto es opcional y depende de cómo quieras manejar otros tipos de usuarios.
        // Podrías crear un UsuarioBaseResponseDTO.
        // Por ahora, si no es Cliente, devolvemos un objeto simple (no ideal para producción).
        // Lo ideal sería tener un DTO base o lanzar error si el tipo no es manejado.
        // Para este ejemplo, si no es cliente, devolvemos la info básica.
        // Esto NO se recomienda para producción sin un DTO definido.
        var baseInfo = new java.util.HashMap<String, Object>();
        baseInfo.put("id", usuario.getId());
        baseInfo.put("username", usuario.getUsername());
        baseInfo.put("auth0Id", usuario.getAuth0Id());
        baseInfo.put("tipo", usuario.getClass().getSimpleName()); // Indica el tipo de entidad
        return baseInfo;
    }

    // Copiado de ClienteController para convertir Cliente a ClienteResponseDTO
    // Idealmente, este tipo de mappers estarían en una clase dedicada o el servicio devolvería el DTO.
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
        dto.setUsername(cliente.getUsername()); // Heredado de Usuario
        dto.setAuth0Id(cliente.getAuth0Id());   // Heredado de Usuario
        dto.setFechaNacimiento(cliente.getFechaNacimiento());
        dto.setEstaDadoDeBaja(cliente.isEstaDadoDeBaja());

        if (cliente.getImagen() != null) {
            ImagenDTO imagenDTO = new ImagenDTO();
            imagenDTO.setId(cliente.getImagen().getId());
            imagenDTO.setDenominacion(cliente.getImagen().getDenominacion());
            dto.setImagen(imagenDTO);
        }

        if (cliente.getDomicilios() != null) { //
            dto.setDomicilios(cliente.getDomicilios().stream().map(dom -> { //
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
        return dto;
    }
}