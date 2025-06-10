package ElBuenSabor.ProyectoFinal.Mapper;


import ElBuenSabor.ProyectoFinal.DTO.PromocionDTO;
import ElBuenSabor.ProyectoFinal.Entities.Promocion;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PromocionMapper extends BaseMapper<Promocion, PromocionDTO> {

    PromocionDTO promocionToPromocionDto(Promocion promocion);
    Promocion promocionDtoToPromocion(PromocionDTO promocionDTO);

    List<PromocionDTO> promocionListToPromocionDtoList(List<Promocion> promociones);
    List<Promocion> promocionDtoListToPromocionList(List<PromocionDTO> promocionesDTO);

}
