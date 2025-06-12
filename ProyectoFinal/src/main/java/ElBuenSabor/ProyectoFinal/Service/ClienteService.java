package ElBuenSabor.ProyectoFinal.Service;

import ElBuenSabor.ProyectoFinal.DTO.ClienteActualizacionDTO;
import ElBuenSabor.ProyectoFinal.DTO.ClienteRegistroDTO;
import ElBuenSabor.ProyectoFinal.DTO.LoginDTO;
import ElBuenSabor.ProyectoFinal.Entities.Cliente;

public interface ClienteService extends BaseService<Cliente, Long> {
    Cliente registrarCliente(ClienteRegistroDTO registroDTO) throws Exception;
    Cliente loginCliente(LoginDTO loginDTO) throws Exception;
    Cliente actualizarCliente(Long id, ClienteActualizacionDTO actualizacionDTO) throws Exception;
    void darBajaCliente(Long id) throws Exception; //
    void darAltaCliente(Long id) throws Exception;
    // Considerar añadir un método que devuelva un ClienteResponseDTO si es necesario
    // ClienteResponseDTO findClienteByIdDTO(Long id) throws Exception;
}