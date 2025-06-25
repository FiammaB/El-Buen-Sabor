package ElBuenSabor.ProyectoFinal.Repositories;

import ElBuenSabor.ProyectoFinal.Entities.ArticuloManufacturado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface ArticuloManufacturadoRepository extends JpaRepository<ArticuloManufacturado, Long> {
    // Nuevo método: Encontrar todos los ArticuloManufacturado de una sucursal específica
    List<ArticuloManufacturado> findBySucursalId(Long sucursalId);

    // Modificado: Incluir el filtrado por sucursal en el método existente 'filtrar'
    @Query("SELECT a FROM ArticuloManufacturado a " +
            "WHERE a.sucursal.id = :sucursalId " + // Agregamos la condición de sucursal
            "AND (:categoriaId IS NULL OR a.categoria.id = :categoriaId) " +
            "AND (:denominacion IS NULL OR LOWER(a.denominacion) LIKE LOWER(CONCAT('%', :denominacion, '%'))) " +
            "AND (:baja IS NULL OR a.baja = :baja)")
    List<ArticuloManufacturado> filtrar(
            @Param("sucursalId") Long sucursalId, // Nuevo parámetro
            @Param("categoriaId") Long categoriaId,
            @Param("denominacion") String denominacion,
            @Param("baja") Boolean baja
    );
}
