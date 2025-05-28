package ElBuenSabor.ProyectoFinal.Repositories;

import ElBuenSabor.ProyectoFinal.Entities.Categoria;
import ElBuenSabor.ProyectoFinal.Entities.TipoRubro; // Importar Enum
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoriaRepository extends JpaRepository<Categoria, Long> {

    // Métodos afectados por @Where(clause="baja=false")
    List<Categoria> findBySucursalesId(Long sucursalId);
    List<Categoria> findByCategoriaPadreIsNull();
    Optional<Categoria> findByDenominacionAndTipoRubro(String denominacion, TipoRubro tipoRubro); // Para unicidad por tipo
    List<Categoria> findByTipoRubro(TipoRubro tipoRubro); // Para listar por tipo (ej. solo rubros de ingredientes)
    List<Categoria> findByCategoriaPadreId(Long categoriaPadreId); // Para obtener subcategorías directas


    // Para buscar incluyendo los 'baja = true'
    @Query("SELECT c FROM Categoria c WHERE c.id = :id")
    Optional<Categoria> findByIdRaw(@Param("id") Long id);

    @Query("SELECT c FROM Categoria c")
    List<Categoria> findAllRaw();

    @Query("SELECT c FROM Categoria c WHERE c.denominacion = :denominacion AND c.tipoRubro = :tipoRubro")
    Optional<Categoria> findByDenominacionAndTipoRubroRaw(@Param("denominacion") String denominacion, @Param("tipoRubro") TipoRubro tipoRubro);

    @Query("SELECT c FROM Categoria c WHERE c.tipoRubro = :tipoRubro")
    List<Categoria> findByTipoRubroRaw(@Param("tipoRubro") TipoRubro tipoRubro);

    @Query("SELECT c FROM Categoria c WHERE c.categoriaPadre IS NULL")
    List<Categoria> findByCategoriaPadreIsNullRaw();

    @Query("SELECT c FROM Categoria c WHERE c.categoriaPadre.id = :categoriaPadreId")
    List<Categoria> findByCategoriaPadreIdRaw(@Param("categoriaPadreId") Long categoriaPadreId);

    // Para verificar si una categoría tiene artículos activos asociados
    @Query("SELECT CASE WHEN COUNT(a) > 0 THEN true ELSE false END FROM Articulo a WHERE a.categoria.id = :categoriaId AND a.baja = false")
    boolean existsActiveArticuloWithCategoria(@Param("categoriaId") Long categoriaId);

    // Para verificar si una categoría tiene subcategorías activas asociadas
    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END FROM Categoria c WHERE c.categoriaPadre.id = :categoriaPadreId AND c.baja = false")
    boolean existsActiveSubCategoria(@Param("categoriaPadreId") Long categoriaPadreId);
}
