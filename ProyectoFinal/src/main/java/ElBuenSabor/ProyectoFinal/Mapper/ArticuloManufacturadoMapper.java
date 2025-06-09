package ElBuenSabor.ProyectoFinal.Mapper;

import ElBuenSabor.ProyectoFinal.Entities.Articulo;
import ElBuenSabor.ProyectoFinal.Entities.ArticuloManufacturado;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ArticuloManufacturadoMapper {

    ArticuloManufacturadoFullDTO articuloManufacturadoToArticuloManufacturadoFullDTO(Articulo articulo);
    ArticuloManufacturado articuloManufacturadoFullDtoToArticuloManufacturado(ArticuloManufacturado articuloManufacturado);

    ArticuloManufacturadoShortDTO articuloManufacturadoToArticuloManufacturadoShortDTO(ArticuloManufacturado articuloManufacturado);

}
