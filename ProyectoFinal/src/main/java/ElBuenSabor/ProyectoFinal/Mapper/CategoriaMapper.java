package ElBuenSabor.ProyectoFinal.Mapper;

import ElBuenSabor.ProyectoFinal.DTO.CategoriaDTO;
import ElBuenSabor.ProyectoFinal.Entities.Categoria;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CategoriaMapper extends BaseMapper <Categoria, CategoriaDTO>{

    CategoriaDTO categoriaToCategoriaFullDto(Categoria categoria);
    Categoria categoriaFullDtoToCategoria(CategoriaDTO categoriaFullDto);

    List<CategoriaDTO> categoriaToCategoriaDtoList(List<Categoria> categorias);
    List<Categoria> categoriaDtoListToCategoriaList(List<CategoriaDTO> categoriasDTO);

}
