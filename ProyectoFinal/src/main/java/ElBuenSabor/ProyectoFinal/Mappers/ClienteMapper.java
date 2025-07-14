package ElBuenSabor.ProyectoFinal.Mappers;

import ElBuenSabor.ProyectoFinal.DTO.ClienteCreateDTO;
import ElBuenSabor.ProyectoFinal.DTO.ClienteDTO;
import ElBuenSabor.ProyectoFinal.Entities.Cliente;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", uses = {
        DomicilioMapper.class,
        LocalidadMapper.class,
        ProvinciaMapper.class,
        PaisMapper.class
})
public interface ClienteMapper {

    @Mapping(source = "usuario.username", target = "nombreUsuario")
    @Mapping(source = "usuario.email", target = "emailUsuario")
    ClienteDTO toDTO(Cliente cliente);

    @InheritInverseConfiguration
    @Mapping(target = "nombre", source = "nombreUsuario") // Agregá esto para el campo de Cliente
    @Mapping(target = "usuario.username", source = "nombreUsuario") // También para el Usuario
    Cliente toEntity(ClienteDTO dto);

    Cliente toEntity(ClienteCreateDTO createDTO);

    List<ClienteDTO> toDTOList(List<Cliente> clientes);
}
