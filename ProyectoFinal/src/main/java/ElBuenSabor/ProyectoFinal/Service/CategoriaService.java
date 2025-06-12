package ElBuenSabor.ProyectoFinal.Service;

import ElBuenSabor.ProyectoFinal.DTO.CategoriaCreateUpdateDTO;
import ElBuenSabor.ProyectoFinal.Entities.Categoria;
import java.util.List;

public interface CategoriaService extends BaseService<Categoria, Long> {
    List<Categoria> findBySucursalesId(Long sucursalId) throws Exception; //
    List<Categoria> findByCategoriaPadreIsNull() throws Exception; //
    Categoria createCategoria(CategoriaCreateUpdateDTO dto) throws Exception;
    Categoria updateCategoria(Long id, CategoriaCreateUpdateDTO dto) throws Exception;
    // Considerar métodos para añadir/remover subcategorías o artículos si la lógica es compleja
}