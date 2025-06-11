package ElBuenSabor.ProyectoFinal.Mapper;

import ElBuenSabor.ProyectoFinal.DTO.ArticuloManufacturadoDTO;
import ElBuenSabor.ProyectoFinal.Entities.ArticuloManufacturado;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ArticuloManufacturadoMapper extends BaseMapper <ArticuloManufacturado, ArticuloManufacturadoDTO>{

    @Override
    @Mapping(target = "detalles", source = "articuloManufacturadoDetalles") // Mapea 'articuloManufacturadoDetalles' del DTO a 'detalles' de la entidad
    ArticuloManufacturado toEntity(ArticuloManufacturadoDTO dto);

    @Override
    @Mapping(target = "articuloManufacturadoDetalles", source = "detalles") // Mapea 'detalles' de la entidad a 'articuloManufacturadoDetalles' del DTO
    ArticuloManufacturadoDTO toDTO(ArticuloManufacturado entity);

    List<ArticuloManufacturadoDTO> amToAmDto(List<ArticuloManufacturado> articulosManufacturados);
    List<ArticuloManufacturado> amDtoToAm(List<ArticuloManufacturadoDTO> articulosManufacturadosDTO);

}
