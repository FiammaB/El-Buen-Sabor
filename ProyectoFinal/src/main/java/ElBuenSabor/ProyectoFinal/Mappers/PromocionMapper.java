package ElBuenSabor.ProyectoFinal.Mappers;

import ElBuenSabor.ProyectoFinal.DTO.PromocionCreateDTO;
import ElBuenSabor.ProyectoFinal.DTO.PromocionDTO;
import ElBuenSabor.ProyectoFinal.Entities.Promocion;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", uses = {ImagenMapper.class})
public interface PromocionMapper {

    // Este método ahora mapeará la imagen automáticamente gracias a 'uses = {ImagenMapper.class}'
    PromocionDTO toDTO(Promocion entity);

    Promocion toEntity(PromocionDTO dto);

    @Mapping(target = "articulosManufacturados", ignore = true)
    @Mapping(target = "sucursales", ignore = true)
    Promocion toEntity(PromocionCreateDTO dto);

    List<PromocionDTO> toDTOList(List<Promocion> promociones);
}
