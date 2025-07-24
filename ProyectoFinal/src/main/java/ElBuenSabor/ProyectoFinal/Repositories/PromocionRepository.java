package ElBuenSabor.ProyectoFinal.Repositories;

import ElBuenSabor.ProyectoFinal.Entities.Promocion;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PromocionRepository extends JpaRepository<Promocion, Long> {
    // 👇 SOLUCIÓN PARA LA LISTA: Usamos un Query explícito
    @Query("SELECT p FROM Promocion p " +
            "LEFT JOIN FETCH p.promocionDetalles pd " +
            "LEFT JOIN FETCH pd.articuloManufacturado " +
            "LEFT JOIN FETCH p.promocionInsumoDetalles pid " +
            "LEFT JOIN FETCH pid.articuloInsumo " +
            "WHERE p.baja = false")
    List<Promocion> findByBajaFalse();

    // 👇 SOLUCIÓN PARA EDITAR: Usamos un Query explícito también aquí
    @Query("SELECT p FROM Promocion p " +
            "LEFT JOIN FETCH p.promocionDetalles pd " +
            "LEFT JOIN FETCH pd.articuloManufacturado " +
            "LEFT JOIN FETCH p.promocionInsumoDetalles pid " +
            "LEFT JOIN FETCH pid.articuloInsumo " +
            "WHERE p.id = :id")
    @Override
    Optional<Promocion> findById(@Param("id") Long id);
}