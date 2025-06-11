package ElBuenSabor.ProyectoFinal.Mapper;

import ElBuenSabor.ProyectoFinal.DTO.ArticuloManufacturadoDTO;
import ElBuenSabor.ProyectoFinal.Entities.ArticuloManufacturado;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ArticuloManufacturadoMapper extends BaseMapper <ArticuloManufacturado, ArticuloManufacturadoDTO>{

    ArticuloManufacturadoDTO articuloManufacturadoToArticuloManufacturadoDto(ArticuloManufacturado articuloManufacturado);
    ArticuloManufacturado articuloManufacturadoDtoToArticuloManufacturado(ArticuloManufacturadoDTO articuloManufacturadoDTO);

    List<ArticuloManufacturadoDTO> amToAmDto(List<ArticuloManufacturado> articulosManufacturados);
    List<ArticuloManufacturado> amDtoToAm(List<ArticuloManufacturadoDTO> articulosManufacturadosDTO);

}
