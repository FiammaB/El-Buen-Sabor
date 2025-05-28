package ElBuenSabor.ProyectoFinal.Repositories;

import ElBuenSabor.ProyectoFinal.Entities.Articulo;
import ElBuenSabor.ProyectoFinal.Entities.Categoria; // Para buscar por categoría
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ArticuloRepository extends JpaRepository<Articulo, Long> {

    // Estos métodos se verán afectados por @Where(clause="baja=false") en las entidades concretas.
    List<Articulo> findByDenominacionContainingIgnoreCase(String denominacion);
    List<Articulo> findByCategoria(Categoria categoria); // Buscar por objeto Categoria
    List<Articulo> findByCategoriaId(Long categoriaId);  // Buscar por ID de Categoria

    // Para buscar incluyendo los 'baja = true'
    @Query("SELECT a FROM Articulo a WHERE a.id = :id")
    Optional<Articulo> findByIdRaw(@Param("id") Long id);

    @Query("SELECT a FROM Articulo a")
    List<Articulo> findAllRaw();

    // Para buscar por denominación incluyendo los 'baja = true'
    @Query("SELECT a FROM Articulo a WHERE lower(a.denominacion) LIKE lower(concat('%', :denominacion, '%'))")
    List<Articulo> findByDenominacionContainingIgnoreCaseRaw(@Param("denominacion") String denominacion);

    // Para buscar por categoría incluyendo los 'baja = true'
    @Query("SELECT a FROM Articulo a WHERE a.categoria.id = :categoriaId")
    List<Articulo> findByCategoriaIdRaw(@Param("categoriaId") Long categoriaId);

    // Para verificar si una imagen está en uso por algún artículo activo
    @Query("SELECT CASE WHEN COUNT(a) > 0 THEN true ELSE false END FROM Articulo a WHERE a.imagen.id = :imagenId AND a.baja = false")
    boolean existsByImagenIdAndBajaFalse(@Param("imagenId") Long imagenId);

    // Para verificar si una categoría tiene artículos activos asociados
    @Query("SELECT CASE WHEN COUNT(a) > 0 THEN true ELSE false END FROM Articulo a WHERE a.categoria.id = :categoriaId AND a.baja = false")
    boolean existsByCategoriaIdAndBajaFalse(@Param("categoriaId") Long categoriaId);

    // Para verificar si una unidad de medida tiene artículos activos asociados
    @Query("SELECT CASE WHEN COUNT(a) > 0 THEN true ELSE false END FROM Articulo a WHERE a.unidadMedida.id = :unidadMedidaId AND a.baja = false")
    boolean existsByUnidadMedidaIdAndBajaFalse(@Param("unidadMedidaId") Long unidadMedidaId);

    @Query("SELECT COUNT(a) FROM Articulo a WHERE a.imagen.id = :imagenId AND a.baja = false")
    long countByImagenIdAndBajaFalse(@Param("imagenId") Long imagenId);
}
