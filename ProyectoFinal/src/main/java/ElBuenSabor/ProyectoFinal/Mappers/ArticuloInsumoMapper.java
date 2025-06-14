package ElBuenSabor.ProyectoFinal.Mappers;

import ElBuenSabor.ProyectoFinal.DTO.ArticuloInsumoDTO;
import ElBuenSabor.ProyectoFinal.DTO.ArticuloInsumoShortDTO;
import ElBuenSabor.ProyectoFinal.Entities.ArticuloInsumo;
import org.mapstruct.*;

@Mapper(componentModel = "spring", uses = {
        ImagenMapper.class,
        UnidadMedidaMapper.class,
        CategoriaMapper.class
})
public interface ArticuloInsumoMapper {

    @Mapping(target = "imagenUrl", expression = "java(entity.getImagen() != null ? entity.getImagen().getDenominacion() : null)")
    @Mapping(target = "imagenId", expression = "java(entity.getImagen() != null ? entity.getImagen().getId() : null)")
    @Mapping(target = "unidadMedidaId", source = "unidadMedida.id")
    @Mapping(target = "categoriaId", source = "categoria.id")
    ArticuloInsumoDTO toDTO(ArticuloInsumo entity);

    @Mapping(target = "detalles", ignore = true)
    ArticuloInsumo toEntity(ArticuloInsumoDTO dto);

    ArticuloInsumoShortDTO toShortDTO(ArticuloInsumo entity);
}

