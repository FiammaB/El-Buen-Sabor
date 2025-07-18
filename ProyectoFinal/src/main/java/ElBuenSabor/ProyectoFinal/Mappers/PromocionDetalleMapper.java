package ElBuenSabor.ProyectoFinal.Mappers;

import ElBuenSabor.ProyectoFinal.DTO.PromocionDetalleDTO;
import ElBuenSabor.ProyectoFinal.Entities.PromocionDetalle;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {ArticuloManufacturadoMapper.class})
public interface PromocionDetalleMapper extends BaseMapper<PromocionDetalle, PromocionDetalleDTO> {
}