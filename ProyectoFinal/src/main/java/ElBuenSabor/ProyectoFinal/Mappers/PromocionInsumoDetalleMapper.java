package ElBuenSabor.ProyectoFinal.Mappers;

import ElBuenSabor.ProyectoFinal.DTO.PromocionInsumoDetalleDTO;
import ElBuenSabor.ProyectoFinal.Entities.PromocionInsumoDetalle;
import org.mapstruct.Mapper;

// Le decimos a MapStruct cómo convertir un detalle de insumo de Entidad a DTO
@Mapper(componentModel = "spring", uses = {ArticuloInsumoMapper.class})
public interface PromocionInsumoDetalleMapper extends BaseMapper<PromocionInsumoDetalle, PromocionInsumoDetalleDTO> {
}