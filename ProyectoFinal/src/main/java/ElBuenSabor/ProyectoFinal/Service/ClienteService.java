package ElBuenSabor.ProyectoFinal.Service;

import ElBuenSabor.ProyectoFinal.DTO.ClienteActualizacionDTO;
import ElBuenSabor.ProyectoFinal.DTO.ClienteRegistroDTO;
import ElBuenSabor.ProyectoFinal.DTO.ClienteResponseDTO; // Para respuestas
import ElBuenSabor.ProyectoFinal.DTO.LoginDTO;
import ElBuenSabor.ProyectoFinal.Entities.Cliente; // Para Optional
import ElBuenSabor.ProyectoFinal.Entities.Usuario; // Para el actor en actualización
import java.util.List;
import java.util.Optional;

public interface ClienteService extends BaseService<Cliente, Long> {
    ClienteResponseDTO registrarCliente(ClienteRegistroDTO registroDTO) throws Exception;
    ClienteResponseDTO loginCliente(LoginDTO loginDTO) throws Exception;
    // El actor es quien realiza la actualización (puede ser el mismo cliente o un admin)
    ClienteResponseDTO actualizarCliente(Long id, ClienteActualizacionDTO actualizacionDTO, Usuario actor) throws Exception;

    // Hereda softDelete (darBajaCliente) y reactivate (darAltaCliente) de BaseService.
    // Si se quieren nombres específicos en la interfaz ClienteService:
    // void darBajaClientePorAdmin(Long id, Usuario adminActor) throws Exception;
    // void darAltaClientePorAdmin(Long id, Usuario adminActor) throws Exception;

    ClienteResponseDTO findClienteByIdDTO(Long id) throws Exception; // Devuelve DTO de activo
    List<ClienteResponseDTO> findAllClientesDTO() throws Exception; // Devuelve DTOs de activos

    Optional<Cliente> findByEmailRaw(String email) throws Exception; // Para validación de unicidad
    boolean existsByEmailRaw(String email) throws Exception;

    // Para vistas de admin
    List<ClienteResponseDTO> findAllClientesIncludingDeleted() throws Exception;
    ClienteResponseDTO findClienteByIdIncludingDeletedDTO(Long id) throws Exception;
}
