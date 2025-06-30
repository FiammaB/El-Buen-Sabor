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

    @Mapping(source = "usuario.nombre", target = "nombreUsuario")
    @Mapping(source = "usuario.email", target = "emailUsuario")
    ClienteDTO toDTO(Cliente cliente);

    @InheritInverseConfiguration
    Cliente toEntity(ClienteDTO dto);

    Cliente toEntity(ClienteCreateDTO createDTO);

    List<ClienteDTO> toDTOList(List<Cliente> clientes);
}
