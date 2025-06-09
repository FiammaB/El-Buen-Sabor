package ElBuenSabor.ProyectoFinal.Mapper;

import ElBuenSabor.ProyectoFinal.Entities.ArticuloManufacturadoDetalle;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ArticuloManufacturadoDetalleMapper {

    ArticuloManufacturadoDetalleDTO articuloManufacturadoDetalleToArticuloManufacturadoDetalleDTO(ArticuloManufacturadoDetalle articuloManufacturadoDetalle);
    ArticuloManufacturadoDetalle articuloManufactudadoDetalleDtoToArticuloManufacturadoDetalle(ArticuloManufacturadoDetalleDTO articuloManufacturadoDetalleDTO);

}
