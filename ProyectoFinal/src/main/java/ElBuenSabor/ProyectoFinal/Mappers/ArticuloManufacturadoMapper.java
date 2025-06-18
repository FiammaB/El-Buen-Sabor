package ElBuenSabor.ProyectoFinal.Mappers;

import ElBuenSabor.ProyectoFinal.DTO.ArticuloManufacturadoCreateDTO;
import ElBuenSabor.ProyectoFinal.DTO.ArticuloManufacturadoDTO;
import ElBuenSabor.ProyectoFinal.DTO.ArticuloManufacturadoDetalleCreateDTO;
import ElBuenSabor.ProyectoFinal.Entities.ArticuloManufacturado;
import ElBuenSabor.ProyectoFinal.Entities.ArticuloManufacturadoDetalle;
import ElBuenSabor.ProyectoFinal.Repositories.ArticuloInsumoRepository;
import org.mapstruct.*;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", uses = {
        ImagenMapper.class,
        UnidadMedidaMapper.class,
        CategoriaMapper.class,
        ArticuloManufacturadoDetalleMapper.class
})
public interface ArticuloManufacturadoMapper {

    @Mapping(source = "baja", target = "estaDadoDeBaja")
    @Mapping(source = "categoria.id", target = "categoriaId")
    ArticuloManufacturadoDTO toDTO(ArticuloManufacturado entity);

    @Mapping(source = "estaDadoDeBaja", target = "baja")
    ArticuloManufacturado toEntity(ArticuloManufacturadoDTO dto);

    @Mapping(target = "detalles", source = "detalles")
    @Mapping(source = "estaDadoDeBaja", target = "baja", ignore = true)
    ArticuloManufacturado toEntity(ArticuloManufacturadoCreateDTO dto, @Context ArticuloInsumoRepository articuloInsumoRepo);

    @Mapping(target = "detalles", ignore = true)
    @Mapping(source = "estaDadoDeBaja", target = "baja", ignore = true)
    ArticuloManufacturado toEntity(ArticuloManufacturadoCreateDTO dto);

    @AfterMapping
    default void setParentArticulo(@MappingTarget ArticuloManufacturado entity) {
        if (entity.getDetalles() != null) {
            for (ArticuloManufacturadoDetalle detalle : entity.getDetalles()) {
                detalle.setArticuloManufacturado(entity);
            }
        }
    }

    default List<ArticuloManufacturadoDetalle> mapDetalles(List<ArticuloManufacturadoDetalleCreateDTO> dtos, ArticuloInsumoRepository repo) {
        if (dtos == null) return null;
        return dtos.stream()
                .map(dto -> )
                .collect(Collectors.toList());
    }
}
