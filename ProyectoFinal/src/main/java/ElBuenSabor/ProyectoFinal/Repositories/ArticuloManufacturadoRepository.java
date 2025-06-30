package ElBuenSabor.ProyectoFinal.Repositories;

import ElBuenSabor.ProyectoFinal.Entities.ArticuloManufacturado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface ArticuloManufacturadoRepository extends JpaRepository<ArticuloManufacturado, Long> {
    @Query("SELECT a FROM ArticuloManufacturado a " +
            "WHERE (:categoriaId IS NULL OR a.categoria.id = :categoriaId) " +
            "AND (:denominacion IS NULL OR LOWER(a.denominacion) LIKE LOWER(CONCAT('%', :denominacion, '%'))) " +
            "AND (:baja IS NULL OR a.baja = :baja)")
    List<ArticuloManufacturado> filtrar(
            @Param("categoriaId") Long categoriaId,
            @Param("denominacion") String denominacion,
            @Param("baja") Boolean baja
    );
    List<ArticuloManufacturado> findAllByDetalles_ArticuloInsumo_Id(Long articuloInsumoId);
}
