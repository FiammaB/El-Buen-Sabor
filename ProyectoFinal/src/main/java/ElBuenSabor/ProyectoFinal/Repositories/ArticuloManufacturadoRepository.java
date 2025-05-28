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
    // Los métodos heredados de JpaRepository y ArticuloRepository
    // se verán afectados por @Where(clause="baja=false") en la entidad ArticuloManufacturado.

    // Para buscar incluyendo los 'baja = true'
    @Query("SELECT am FROM ArticuloManufacturado am WHERE am.id = :id")
    Optional<ArticuloManufacturado> findByIdRaw(@Param("id") Long id);

    @Query("SELECT am FROM ArticuloManufacturado am")
    List<ArticuloManufacturado> findAllRaw();

    // Opcional: si necesitas buscar por denominación específica de manufacturado incluyendo bajas
    @Query("SELECT am FROM ArticuloManufacturado am WHERE lower(am.denominacion) = lower(:denominacion)")
    Optional<ArticuloManufacturado> findByDenominacionRaw(@Param("denominacion") String denominacion);
}
