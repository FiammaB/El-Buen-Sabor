package ElBuenSabor.ProyectoFinal.Repositories;

import ElBuenSabor.ProyectoFinal.Entities.Promocion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface PromocionRepository extends JpaRepository<Promocion, Long> {

    // Nuevo método: Encontrar todas las promociones de una sucursal específica
    // Dado que Promocion tiene una relación ManyToMany con Sucursal, la consulta es diferente.
    @Query("SELECT p FROM Promocion p JOIN p.sucursales s WHERE s.id = :sucursalId")
    List<Promocion> findBySucursalesId(@Param("sucursalId") Long sucursalId);

    // Modificado: Encontrar promociones activas para una sucursal específica
    @Query("SELECT p FROM Promocion p JOIN p.sucursales s " +
            "WHERE s.id = :sucursalId AND p.baja = FALSE AND p.fechaDesde <= :fechaActual AND p.fechaHasta >= :fechaActual")
    List<Promocion> findActivePromotionsBySucursalId(
            @Param("sucursalId") Long sucursalId,
            @Param("fechaActual") LocalDate fechaActual
    );

}
