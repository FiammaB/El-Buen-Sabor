package ElBuenSabor.ProyectoFinal.Mappers;

import ElBuenSabor.ProyectoFinal.DTO.ArticuloManufacturadoDetalleCreateDTO;
import ElBuenSabor.ProyectoFinal.DTO.ArticuloManufacturadoDetalleDTO;
import ElBuenSabor.ProyectoFinal.Entities.ArticuloManufacturadoDetalle;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", uses = ArticuloInsumoMapper.class)
public interface ArticuloManufacturadoDetalleMapper {

    ArticuloManufacturadoDetalleDTO toDTO(ArticuloManufacturadoDetalle entity);

    ArticuloManufacturadoDetalle toEntity(ArticuloManufacturadoDetalleCreateDTO dto);
}
