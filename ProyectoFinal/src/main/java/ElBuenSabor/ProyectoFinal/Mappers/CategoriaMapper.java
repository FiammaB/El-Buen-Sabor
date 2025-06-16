package ElBuenSabor.ProyectoFinal.Mappers;

import ElBuenSabor.ProyectoFinal.DTO.CategoriaDTO;
import ElBuenSabor.ProyectoFinal.DTO.CategoriaShortDTO;
import ElBuenSabor.ProyectoFinal.Entities.*;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CategoriaMapper {

    CategoriaDTO toDTO(Categoria entity);

    Categoria toEntity(CategoriaDTO dto);

    List<CategoriaDTO> toDTOList(List<Categoria> categorias);

    @Mapping(target = "categoriaPadre", source = "categoriaPadreId")
    Categoria toEntity(CategoriaShortDTO dto);

    default Categoria mapCategoriaPadre(Long id) {
        if (id == null) return null;
        return Categoria.builder().id(id).build();
    }
}