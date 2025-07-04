package ElBuenSabor.ProyectoFinal.Mappers;

import ElBuenSabor.ProyectoFinal.DTO.PromocionCreateDTO;
import ElBuenSabor.ProyectoFinal.DTO.PromocionDTO;
import ElBuenSabor.ProyectoFinal.Entities.Promocion;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PromocionMapper {

    PromocionDTO toDTO(Promocion entity);

    Promocion toEntity(PromocionDTO dto);

    Promocion toEntity(PromocionCreateDTO dto);

    List<PromocionDTO> toDTOList(List<Promocion> promociones);
}
