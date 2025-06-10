package ElBuenSabor.ProyectoFinal.Mapper;

import ElBuenSabor.ProyectoFinal.DTO.ArticuloManufacturadoDetalleDTO;
import ElBuenSabor.ProyectoFinal.Entities.ArticuloManufacturadoDetalle;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ArticuloManufacturadoDetalleMapper extends BaseMapper <ArticuloManufacturadoDetalle, ArticuloManufacturadoDetalleDTO> {

    ArticuloManufacturadoDetalleDTO amDetalleToAmDetalleDTO(ArticuloManufacturadoDetalle articuloManufacturadoDetalle);
    ArticuloManufacturadoDetalle amDetalleDtoToAmDetalle(ArticuloManufacturadoDetalleDTO articuloManufacturadoDetalleDTO);

    List<ArticuloManufacturadoDetalleDTO> amDetalleToAmDetalleDtoList(List<ArticuloManufacturadoDetalle> detalles);
    List<ArticuloManufacturadoDetalle> amDetalleDtoToAmDetalleList(List<ArticuloManufacturadoDetalleDTO> detallesDTO);
}
