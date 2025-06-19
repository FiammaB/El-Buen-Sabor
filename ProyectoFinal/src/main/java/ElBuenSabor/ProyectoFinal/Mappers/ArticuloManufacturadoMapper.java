package ElBuenSabor.ProyectoFinal.Mappers;

import ElBuenSabor.ProyectoFinal.DTO.ArticuloManufacturadoCreateDTO;
import ElBuenSabor.ProyectoFinal.DTO.ArticuloManufacturadoDTO;
import ElBuenSabor.ProyectoFinal.Entities.ArticuloManufacturado;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {
        ImagenMapper.class,
        UnidadMedidaMapper.class,
        CategoriaMapper.class,
        ArticuloManufacturadoDetalleMapper.class
})
public interface ArticuloManufacturadoMapper {

    ArticuloManufacturadoDTO toDTO(ArticuloManufacturado entity);

    ArticuloManufacturado toEntity(ArticuloManufacturadoDTO dto);

    ArticuloManufacturado toEntity(ArticuloManufacturadoCreateDTO dto);
}
