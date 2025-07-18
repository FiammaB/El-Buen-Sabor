package ElBuenSabor.ProyectoFinal.Repositories;

import ElBuenSabor.ProyectoFinal.Entities.Promocion;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PromocionRepository extends JpaRepository<Promocion, Long> {
    List<Promocion> findByBajaFalse();
    @EntityGraph(attributePaths = {"promocionDetalles.articuloManufacturado", "articulosInsumos", "sucursales", "imagen"})
    Optional<Promocion> findById(Long id);
}
