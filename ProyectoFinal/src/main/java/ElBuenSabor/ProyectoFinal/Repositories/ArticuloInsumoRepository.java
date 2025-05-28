package ElBuenSabor.ProyectoFinal.Repositories;

import ElBuenSabor.ProyectoFinal.Entities.ArticuloInsumo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ArticuloInsumoRepository extends JpaRepository<ArticuloInsumo, Long> {

    // Este método se verá afectado por @Where(clause="baja=false") en la entidad ArticuloInsumo
    List<ArticuloInsumo> findByStockActualLessThanEqual(Double stockMinimo);

    // Método para la consigna HU#25 (Control de Stock), considerando solo los activos.
    // El @Where ya filtra por baja=false.
    @Query("SELECT ai FROM ArticuloInsumo ai WHERE ai.stockActual <= ai.stockMinimo")
    List<ArticuloInsumo> findByStockActualLessThanEqualStockMinimoAndBajaFalse();
    // Alternativa si se quiere pasar un porcentaje para "cerca del stock mínimo":
    // @Query("SELECT ai FROM ArticuloInsumo ai WHERE ai.stockActual <= (ai.stockMinimo * (1 + :porcentajeMargen / 100.0)) AND ai.baja = false")
    // List<ArticuloInsumo> findByStockCercanoAlMinimo(@Param("porcentajeMargen") double porcentajeMargen);


    // Para buscar incluyendo los 'baja = true'
    @Query("SELECT ai FROM ArticuloInsumo ai WHERE ai.id = :id")
    Optional<ArticuloInsumo> findByIdRaw(@Param("id") Long id);

    @Query("SELECT ai FROM ArticuloInsumo ai")
    List<ArticuloInsumo> findAllRaw();

    // Opcional: si necesitas buscar por denominación específica de insumo incluyendo bajas
    @Query("SELECT ai FROM ArticuloInsumo ai WHERE lower(ai.denominacion) = lower(:denominacion)")
    Optional<ArticuloInsumo> findByDenominacionRaw(@Param("denominacion") String denominacion);
}
