package ElBuenSabor.ProyectoFinal.Mappers;

import ElBuenSabor.ProyectoFinal.DTO.ArticuloManufacturadoCreateDTO;
import ElBuenSabor.ProyectoFinal.DTO.ArticuloManufacturadoDTO;
import ElBuenSabor.ProyectoFinal.Entities.ArticuloManufacturado;
import ElBuenSabor.ProyectoFinal.Entities.ArticuloManufacturadoDetalle;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring", uses = {
        ImagenMapper.class,
        UnidadMedidaMapper.class,
        CategoriaMapper.class,
        ArticuloManufacturadoDetalleMapper.class
})
public interface ArticuloManufacturadoMapper {

    @Mapping(source = "categoria.id", target = "categoriaId")
    ArticuloManufacturadoDTO toDTO(ArticuloManufacturado entity);

    @Mapping(target = "detalles", source = "detalles")
    ArticuloManufacturado toEntity(ArticuloManufacturadoDTO dto);

    ArticuloManufacturado toEntity(ArticuloManufacturadoCreateDTO dto);

    @AfterMapping
    default void setParentArticulo(@MappingTarget ArticuloManufacturado entity) {
        if (entity.getDetalles() != null) {
            for (ArticuloManufacturadoDetalle detalle : entity.getDetalles()) {
                detalle.setArticuloManufacturado(entity);
            }
        }
    }
}
