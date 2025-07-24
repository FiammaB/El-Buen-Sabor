package ElBuenSabor.ProyectoFinal.Mappers;

import ElBuenSabor.ProyectoFinal.DTO.PromocionDTO;
import ElBuenSabor.ProyectoFinal.Entities.Promocion;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;


// AÑADIMOS EL NUEVO MAPPER A LA LISTA DE 'uses'
@Mapper(componentModel = "spring", uses = {
        ImagenMapper.class,
        ArticuloInsumoMapper.class,
        SucursalMapper.class,
        PromocionDetalleMapper.class, // <-- ¡LA CLAVE ESTÁ AQUÍ!
        PromocionInsumoDetalleMapper.class // <-- ¡AÑADIR ESTA LÍNEA!
})
public interface PromocionMapper {

    // Este método ahora mapeará la imagen automáticamente gracias a 'uses = {ImagenMapper.class}'
    PromocionDTO toDTO(Promocion entity);

    @Mapping(target = "promocionDetalles", ignore = true)
    Promocion toEntity(PromocionDTO dto);
//
//    @Mapping(target = "articulosManufacturados", ignore = true)
//    @Mapping(target = "sucursales", ignore = true)
//    Promocion toEntity(PromocionCreateDTO dto);

    List<PromocionDTO> toDTOList(List<Promocion> promociones);
}
