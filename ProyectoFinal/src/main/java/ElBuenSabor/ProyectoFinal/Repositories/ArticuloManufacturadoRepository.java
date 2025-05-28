package ElBuenSabor.ProyectoFinal.Repositories;

import ElBuenSabor.ProyectoFinal.Entities.ArticuloManufacturado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ArticuloManufacturadoRepository extends JpaRepository<ArticuloManufacturado, Long> {

    @Query("SELECT am FROM ArticuloManufacturado am WHERE am.id = :id")
    Optional<ArticuloManufacturado> findByIdRaw(@Param("id") Long id);

    @Query("SELECT am FROM ArticuloManufacturado am")
    List<ArticuloManufacturado> findAllRaw();

    @Query("SELECT am FROM ArticuloManufacturado am WHERE lower(am.denominacion) = lower(:denominacion)")
    Optional<ArticuloManufacturado> findByDenominacionRaw(@Param("denominacion") String denominacion);

    // MÉTODO PARA VERIFICAR SI UN ArticuloInsumo ES PARTE DE ALGÚN ArticuloManufacturado ACTIVO
    @Query("SELECT CASE WHEN COUNT(am) > 0 THEN true ELSE false END " +
            "FROM ArticuloManufacturado am JOIN am.detalles detalle " +
            "WHERE detalle.articuloInsumo.id = :insumoId AND am.baja = false")
    boolean existsActiveWithInsumoId(@Param("insumoId") Long insumoId);
}
