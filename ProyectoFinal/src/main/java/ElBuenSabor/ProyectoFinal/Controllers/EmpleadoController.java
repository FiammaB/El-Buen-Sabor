package ElBuenSabor.ProyectoFinal.Controllers;

import ElBuenSabor.ProyectoFinal.DTO.EmpleadoCreateUpdateDTO; // Unificado
import ElBuenSabor.ProyectoFinal.DTO.EmpleadoResponseDTO;
import ElBuenSabor.ProyectoFinal.DTO.LoginDTO;
import ElBuenSabor.ProyectoFinal.Entities.Rol;
import ElBuenSabor.ProyectoFinal.Entities.Usuario; // Para el actor
import ElBuenSabor.ProyectoFinal.Entities.Empleado; // Para castear el actor
import ElBuenSabor.ProyectoFinal.Service.EmpleadoService;
// import ElBuenSabor.ProyectoFinal.Service.UsuarioService; // Para obtener el actor genérico
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
// Importar UserDetails si tu Principal es UserDetails
// import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional; // Para el Optional de Empleado

@RestController
@RequestMapping("/api/v1/empleados")
@CrossOrigin(origins = "*")
public class EmpleadoController {

    @Autowired
    private EmpleadoService empleadoService;

    // @Autowired
    // private UsuarioService usuarioService; // Si necesitas cargar el 'actor' de forma genérica

    // Endpoint para que un Administrador registre un nuevo empleado (HU#04, HU#124)
    @PostMapping("/registrar")
    // @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<?> registrarEmpleado(@Valid @RequestBody EmpleadoCreateUpdateDTO empleadoCreateUpdateDTO) {
        try {
            EmpleadoResponseDTO nuevoEmpleado = empleadoService.registrarEmpleado(empleadoCreateUpdateDTO);
            return new ResponseEntity<>(nuevoEmpleado, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    // Endpoint para login de empleado (HU#05)
    @PostMapping("/login")
    public ResponseEntity<?> loginEmpleado(@Valid @RequestBody LoginDTO loginDTO) {
        try {
            EmpleadoResponseDTO empleado = empleadoService.loginEmpleado(loginDTO);
            // Aquí se generaría y devolvería un token JWT
            return ResponseEntity.ok(empleado);
        } catch (Exception e) {
            return new ResponseEntity<>("Credenciales de empleado inválidas.", HttpStatus.UNAUTHORIZED);
        }
    }

    // Endpoint para que un empleado (o admin) vea un perfil de empleado por ID
    @GetMapping("/{id}")
    // @PreAuthorize("hasRole('ADMINISTRADOR') or (hasRole('EMPLEADO') and #id == authentication.principal.id)")
    public ResponseEntity<?> obtenerEmpleadoPorId(@PathVariable Long id) {
        try {
            // Validar si el que consulta es el mismo empleado o un admin
            // Esta lógica de autorización es mejor con Spring Security @PreAuthorize
            // Por ahora, el servicio findEmpleadoByIdDTO devuelve solo activos.
            EmpleadoResponseDTO dto = empleadoService.findEmpleadoByIdDTO(id);
            if (dto != null) {
                return ResponseEntity.ok(dto);
            } else {
                return new ResponseEntity<>("Empleado activo no encontrado con ID: " + id, HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Endpoint para que el empleado actualice su propio perfil (HU#06)
    // O para que un admin actualice el perfil de un empleado (HU#08)
    @PutMapping("/{id}/perfil")
    // @PreAuthorize("hasRole('ADMINISTRADOR') or (authentication.principal instanceof T(ElBuenSabor.ProyectoFinal.Entities.Empleado) and #id == authentication.principal.id)")
    public ResponseEntity<?> actualizarPerfilEmpleado(@PathVariable Long id, @Valid @RequestBody EmpleadoCreateUpdateDTO empleadoUpdateDTO) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            Usuario actor = null;

            // Lógica para obtener el actor. Esto dependerá de tu implementación de UserDetails.
            // Si tu UserDetails es directamente tu entidad Empleado o Usuario:
            if (authentication != null && authentication.getPrincipal() instanceof Usuario) {
                actor = (Usuario) authentication.getPrincipal();
            } else if (authentication != null && authentication.getPrincipal() instanceof org.springframework.security.core.userdetails.User) {
                // Si usas el User de Spring Security, necesitas cargar tu entidad Usuario/Empleado
                // String username = ((org.springframework.security.core.userdetails.User) authentication.getPrincipal()).getUsername();
                // Optional<Empleado> optActor = empleadoService.findByEmailRaw(username); // Asumiendo que email es username
                // if(optActor.isPresent()) actor = optActor.get();
                // else { /* Podría ser un Cliente u otro tipo, o no encontrado */ }
                System.out.println("Advertencia: El principal es UserDetails de Spring, se necesita lógica para mapear a entidad Usuario/Empleado.");
                // Para pruebas sin seguridad completa, y si el servicio lo permite, actor puede ser null
                // o simularlo como antes.
            }
            else {
                System.out.println("Advertencia: No se pudo obtener el actor autenticado para actualizar perfil. La lógica de permisos podría ser laxa.");
                // Para pruebas sin seguridad, si el servicio lo maneja, actor puede ser null.
                // O simular:
                // if (id.equals(1L)) { // Simula que el empleado 1 se actualiza a sí mismo
                //    Optional<Empleado> selfOpt = empleadoService.findByIdIncludingDeleted(id);
                //    if (selfOpt.isPresent()) actor = selfOpt.get();
                // }
            }

            EmpleadoResponseDTO empleadoActualizado = empleadoService.actualizarPerfilEmpleado(id, empleadoUpdateDTO, actor);
            return ResponseEntity.ok(empleadoActualizado);
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

    // --- Endpoints solo para Administrador ---
    @GetMapping("/admin")
    // @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<?> listarTodosLosEmpleadosAdmin(@RequestParam(required = false) Rol rol) {
        try {
            List<EmpleadoResponseDTO> dtos = empleadoService.findAllEmpleadosIncludingDeleted(rol);
            return ResponseEntity.ok(dtos);
        } catch (Exception e) {
            return new ResponseEntity<>("Error al listar empleados para admin: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/admin/{id}")
    // @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<?> obtenerEmpleadoPorIdAdmin(@PathVariable Long id) {
        try {
            EmpleadoResponseDTO dto = empleadoService.findEmpleadoByIdIncludingDeletedDTO(id);
            if (dto != null) {
                return ResponseEntity.ok(dto);
            } else {
                return new ResponseEntity<>("Empleado (activo o inactivo) no encontrado con ID: " + id, HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>("Error al obtener empleado para admin: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PatchMapping("/admin/{id}/dar-baja")
    // @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<?> darBajaEmpleadoAdmin(@PathVariable Long id) {
        try {
            // Lógica para evitar que el admin se de baja a sí mismo
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.getPrincipal() instanceof Usuario) {
                Usuario adminActor = (Usuario) authentication.getPrincipal();
                if (adminActor.getId().equals(id)) {
                    return new ResponseEntity<>("Un administrador no puede darse de baja a sí mismo.", HttpStatus.FORBIDDEN);
                }
            } // else: Si no hay autenticación, esta validación no se puede hacer aquí de forma segura.

            empleadoService.darBajaEmpleado(id);
            return ResponseEntity.ok("Empleado ID: " + id + " dado de baja correctamente.");
        } catch (Exception e) {
            if (e.getMessage().contains("no encontrado") || e.getMessage().contains("ya está dado de baja")) {
                return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>("Error al dar de baja empleado: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PatchMapping("/admin/{id}/dar-alta")
    // @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<?> darAltaEmpleadoAdmin(@PathVariable Long id) {
        try {
            empleadoService.darAltaEmpleado(id);
            return ResponseEntity.ok("Empleado ID: " + id + " dado de alta correctamente.");
        } catch (Exception e) {
            if (e.getMessage().contains("no encontrado") || e.getMessage().contains("no está dado de baja")) {
                return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
            }
            return new ResponseEntity<>("Error al dar de alta empleado: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
