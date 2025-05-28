package ElBuenSabor.ProyectoFinal.Service;

import ElBuenSabor.ProyectoFinal.DTO.CategoriaCreateUpdateDTO;
import ElBuenSabor.ProyectoFinal.DTO.CategoriaDTO;
import ElBuenSabor.ProyectoFinal.Entities.Categoria; // Para Optional
import ElBuenSabor.ProyectoFinal.Entities.TipoRubro; // Importar Enum
import java.util.List;
import java.util.Optional;

public interface CategoriaService extends BaseService<Categoria, Long> {
    CategoriaDTO createCategoria(CategoriaCreateUpdateDTO dto) throws Exception;
    CategoriaDTO updateCategoria(Long id, CategoriaCreateUpdateDTO dto) throws Exception;

    CategoriaDTO findCategoriaById(Long id) throws Exception;
    List<CategoriaDTO> findAllCategorias() throws Exception; // Todas las activas
    List<CategoriaDTO> findByTipoRubro(TipoRubro tipoRubro, boolean activas) throws Exception; // Filtrar por tipo y opcionalmente solo activas
    List<CategoriaDTO> findBySucursalesId(Long sucursalId) throws Exception;
    List<CategoriaDTO> findByCategoriaPadreIsNull(TipoRubro tipoRubro) throws Exception; // Raíz por tipo
    List<CategoriaDTO> findSubcategorias(Long categoriaPadreId, boolean activas) throws Exception; // Subcategorías (opcionalmente solo activas)

    Optional<Categoria> findByDenominacionAndTipoRubroRaw(String denominacion, TipoRubro tipoRubro) throws Exception;

    // Heredados de BaseService y a implementar para devolver DTOs
    List<CategoriaDTO> findAllCategoriasIncludingDeleted(TipoRubro tipoRubro) throws Exception; // Filtrar por tipo
    CategoriaDTO findCategoriaByIdIncludingDeleted(Long id) throws Exception;

    boolean isCategoriaInUse(Long categoriaId) throws Exception;
}
