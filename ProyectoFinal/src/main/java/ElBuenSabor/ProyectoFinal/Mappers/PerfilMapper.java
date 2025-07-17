package ElBuenSabor.ProyectoFinal.Mappers;

import ElBuenSabor.ProyectoFinal.DTO.DomicilioDTO;
import ElBuenSabor.ProyectoFinal.DTO.PerfilDTO;
import ElBuenSabor.ProyectoFinal.Entities.Domicilio;
import ElBuenSabor.ProyectoFinal.Entities.Persona;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring", uses = {UsuarioMapper.class, DomicilioMapper.class, ImagenMapper.class})
public interface PerfilMapper {

    PerfilMapper INSTANCE = Mappers.getMapper(PerfilMapper.class);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "nombre", target = "nombre")
    @Mapping(source = "apellido", target = "apellido")
    @Mapping(source = "telefono", target = "telefono")
    @Mapping(source = "fechaNacimiento", target = "fechaNacimiento")
    @Mapping(source = "domicilios", target = "domicilio")
    @Mapping(source = "imagen", target = "imagen")
    @Mapping(source = "usuario", target = "usuario")
    PerfilDTO toPerfilDTO(Persona persona);

    // ✅ Devuelve solo el primer domicilio
    default DomicilioDTO mapPrimerDomicilio(List<Domicilio> domicilios) {
        if (domicilios != null && !domicilios.isEmpty()) {
            return DomicilioMapper.INSTANCE.toDTO(domicilios.get(0));
        }
        return null;
    }
}
