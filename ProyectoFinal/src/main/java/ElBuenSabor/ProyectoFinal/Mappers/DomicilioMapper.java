package ElBuenSabor.ProyectoFinal.Mappers;

import ElBuenSabor.ProyectoFinal.DTO.DomicilioDTO;
import ElBuenSabor.ProyectoFinal.Entities.Domicilio;
import org.mapstruct.Mapper;
import java.util.List;

@Mapper(componentModel = "spring", uses = {LocalidadMapper.class})
public interface DomicilioMapper {

    // ✅ Necesario para usarlo en métodos default de otros mappers
    DomicilioMapper INSTANCE = org.mapstruct.factory.Mappers.getMapper(DomicilioMapper.class);

    DomicilioDTO toDTO(Domicilio domicilio);

    Domicilio toEntity(DomicilioDTO dto);

    List<DomicilioDTO> toDTOList(List<Domicilio> domicilios);
}
