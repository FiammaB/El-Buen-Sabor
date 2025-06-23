package ElBuenSabor.ProyectoFinal.Repositories;

import ElBuenSabor.ProyectoFinal.Entities.ArticuloInsumo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ArticuloInsumoRepository extends JpaRepository<ArticuloInsumo, Long> {
    List<ArticuloInsumo> findByStockActualLessThanEqual(Double stockMinimo);
    // Modificado: Encontrar insumos con stock bajo para una sucursal específica
    @Query("SELECT ai FROM ArticuloInsumo ai WHERE ai.stockActual <= :stockMinimo AND ai.sucursal.id = :sucursalId")
    List<ArticuloInsumo> findByStockActualLessThanEqualAndSucursalId(
            @Param("stockMinimo") Double stockMinimo,
            @Param("sucursalId") Long sucursalId
    );
    List<ArticuloInsumo> findBySucursalId(Long sucursalId);
}
