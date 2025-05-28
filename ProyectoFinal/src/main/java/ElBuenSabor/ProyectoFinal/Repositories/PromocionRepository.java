package ElBuenSabor.ProyectoFinal.Repositories;

import ElBuenSabor.ProyectoFinal.Entities.Promocion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PromocionRepository extends JpaRepository<Promocion, Long> {

    // Busca promociones activas por fecha y hora (afectado por @Where en Promocion)
    List<Promocion> findByFechaDesdeLessThanEqualAndFechaHastaGreaterThanEqualAndHoraDesdeLessThanEqualAndHoraHastaGreaterThanEqualAndBajaFalse(
            LocalDate fechaActual1, LocalDate fechaActual2, LocalTime horaActual1, LocalTime horaActual2
    );

    // Versión más simple para promociones activas hoy (considerando que horaDesde/Hasta pueden ser null)
    // Esta query es más compleja de escribir en JPQL por los nulls en hora. Se maneja mejor en el servicio.
    // List<Promocion> findActivasEnFecha(@Param("fecha") LocalDate fecha);

    // Busca promociones activas por ID de sucursal (afectado por @Where en Promocion)
    List<Promocion> findBySucursalesIdAndBajaFalse(Long sucursalId); // El _Id es para el campo 'id' de la entidad Sucursal en la colección 'sucursales'
    // y BajaFalse para asegurar que la promoción esté activa.

    // Para buscar incluyendo las 'baja = true' (anuladas o inactivas)
    @Query("SELECT p FROM Promocion p WHERE p.id = :id")
    Optional<Promocion> findByIdRaw(@Param("id") Long id);

    @Query("SELECT p FROM Promocion p")
    List<Promocion> findAllRaw();

    @Query("SELECT p FROM Promocion p WHERE p.denominacion = :denominacion")
    Optional<Promocion> findByDenominacionRaw(@Param("denominacion") String denominacion);

    // Para verificar si un artículo manufacturado está en alguna promoción activa
    @Query("SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END " +
            "FROM Promocion p JOIN p.articulosManufacturados am " +
            "WHERE am.id = :manufacturadoId AND p.baja = false " +
            "AND p.fechaHasta >= CURRENT_DATE") // Podría añadir chequeo de hora también
    boolean existsByArticulosManufacturadosIdAndActiva(@Param("manufacturadoId") Long manufacturadoId);

    // Para buscar promociones por ID de sucursal incluyendo las dadas de baja
    @Query("SELECT p FROM Promocion p JOIN p.sucursales s WHERE s.id = :sucursalId")
    List<Promocion> findBySucursalIdRaw(@Param("sucursalId") Long sucursalId);
}
