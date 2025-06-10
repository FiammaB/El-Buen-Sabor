package ElBuenSabor.ProyectoFinal.DTO;

import ElBuenSabor.ProyectoFinal.Entities.Categoria;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CategoriaDTO extends BaseDTO{

    private String denominacion;
    private CategoriaDTO categoria;
    private List<ArticuloDTO> articulos = new ArrayList<>();
    private Set<CategoriaDTO> subCategorias = new HashSet<>();

}