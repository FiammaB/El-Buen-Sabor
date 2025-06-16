package ElBuenSabor.ProyectoFinal.Mappers;

import ElBuenSabor.ProyectoFinal.DTO.ArticuloDTO;
import ElBuenSabor.ProyectoFinal.Entities.Articulo;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {
        ImagenMapper.class,
        UnidadMedidaMapper.class,
        CategoriaMapper.class
})
public interface ArticuloMapper {
    ArticuloDTO toDTO(Articulo articulo);
}

