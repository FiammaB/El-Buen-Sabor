package ElBuenSabor.ProyectoFinal.Mapper;


import ElBuenSabor.ProyectoFinal.DTO.ClienteDTO;
import ElBuenSabor.ProyectoFinal.Entities.Cliente;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ClienteMapper extends BaseMapper<Cliente, ClienteDTO> {

    ClienteDTO clienteToClienteDto(Cliente cliente);
    Cliente clienteDtoToCliente(ClienteDTO clienteDTO);

    List<ClienteDTO> clienteListToClienteDtoList(List<Cliente> clientes);
    List<Cliente> clienteDtoListToClienteList(List<ClienteDTO> clienteDTO);

}
