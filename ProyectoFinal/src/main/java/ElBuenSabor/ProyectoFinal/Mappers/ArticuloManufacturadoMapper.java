package ElBuenSabor.ProyectoFinal.Mappers;

import ElBuenSabor.ProyectoFinal.DTO.ArticuloManufacturadoCreateDTO;
import ElBuenSabor.ProyectoFinal.DTO.ArticuloManufacturadoDTO;
import ElBuenSabor.ProyectoFinal.Entities.ArticuloManufacturado;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {
        ImagenMapper.class,
        UnidadMedidaMapper.class,
        CategoriaMapper.class,
        ArticuloManufacturadoDetalleMapper.class
})
public interface ArticuloManufacturadoMapper {

    ArticuloManufacturadoDTO toDTO(ArticuloManufacturado entity);

    ArticuloManufacturado toEntity(ArticuloManufacturadoDTO dto);

    @Mapping(target = "detalles", source = "detalles") // <--- Agregado explícitamente
    ArticuloManufacturado toEntity(ArticuloManufacturadoCreateDTO dto);
}