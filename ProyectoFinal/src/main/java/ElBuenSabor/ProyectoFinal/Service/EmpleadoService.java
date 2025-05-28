package ElBuenSabor.ProyectoFinal.Service;

import ElBuenSabor.ProyectoFinal.DTO.EmpleadoCreateUpdateDTO; // Unificado
import ElBuenSabor.ProyectoFinal.DTO.EmpleadoResponseDTO;
import ElBuenSabor.ProyectoFinal.DTO.LoginDTO; // Para el login de empleado
import ElBuenSabor.ProyectoFinal.Entities.Empleado;
import ElBuenSabor.ProyectoFinal.Entities.Rol; // Para buscar por rol
import ElBuenSabor.ProyectoFinal.Entities.Usuario; // Para el actor en la actualización
import java.util.List;
import java.util.Optional;

public interface EmpleadoService extends BaseService<Empleado, Long> {

    // Creación por Administrador
    EmpleadoResponseDTO registrarEmpleado(EmpleadoCreateUpdateDTO dto) throws Exception;

    // Login de Empleado
    EmpleadoResponseDTO loginEmpleado(LoginDTO loginDTO) throws Exception;

    // Actualización de perfil (puede ser por el propio empleado o por un admin)
    // Usará el mismo DTO que la creación, pero el servicio diferenciará la lógica.
    EmpleadoResponseDTO actualizarPerfilEmpleado(Long empleadoId, EmpleadoCreateUpdateDTO dto, Usuario actor) throws Exception;

    // Obtener perfil del empleado actual (o por ID si es admin)
    EmpleadoResponseDTO findEmpleadoByIdDTO(Long id) throws Exception; // Devuelve DTO de activo

    // Listar empleados (para admin)
    List<EmpleadoResponseDTO> findAllEmpleados() throws Exception; // Activos
    List<EmpleadoResponseDTO> findByRol(Rol rol, boolean soloActivos) throws Exception;

    // Dar de baja y alta por Administrador (usa softDelete y reactivate de BaseService)
    void darBajaEmpleado(Long id) throws Exception;
    void darAltaEmpleado(Long id) throws Exception;

    // Para validaciones y búsquedas internas que ignoran el estado 'baja'
    Optional<Empleado> findByEmailRaw(String email) throws Exception;

    // Para vistas de admin
    List<EmpleadoResponseDTO> findAllEmpleadosIncludingDeleted(Rol rol) throws Exception; // Filtrar por rol opcional
    EmpleadoResponseDTO findEmpleadoByIdIncludingDeletedDTO(Long id) throws Exception;
}
