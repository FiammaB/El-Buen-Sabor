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

    ArticuloInsumoDTO toDTO(ArticuloInsumo entity);

    @Mapping(target = "detalles", ignore = true)
    ArticuloInsumo toEntity(ArticuloInsumoDTO dto);

    ArticuloInsumoShortDTO toShortDTO(ArticuloInsumo entity);
}

