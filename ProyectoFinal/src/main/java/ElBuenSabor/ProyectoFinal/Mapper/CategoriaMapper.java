package ElBuenSabor.ProyectoFinal.Mapper;

import ElBuenSabor.ProyectoFinal.Entities.Categoria;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CategoriaMapper {

    CategoriaFullDTO categoriaToCategoriaFullDto(Categoria categoria);
    Categoria categoriaFullDtoToCategoria(CategoriaFullDTO categoriaFullDto);

    CategoriaShortDTO categoriaToCategoriaShortDto(Categoria categoria);

}
