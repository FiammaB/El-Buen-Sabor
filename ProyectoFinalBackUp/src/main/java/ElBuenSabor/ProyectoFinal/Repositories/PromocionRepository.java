package ElBuenSabor.ProyectoFinal.Repositories;

import ElBuenSabor.ProyectoFinal.Entities.Promocion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Repository
public interface PromocionRepository extends JpaRepository<Promocion, Long> {
    // Ejemplo: buscar promociones activas en un rango de fechas y horas
    List<Promocion> findByFechaDesdeLessThanEqualAndFechaHastaGreaterThanEqualAndHoraDesdeLessThanEqualAndHoraHastaGreaterThanEqual(
            LocalDate fechaActual, LocalDate fechaActual2, LocalTime horaActual, LocalTime horaActual2
    );
    List<Promocion> findBySucursalesId(Long sucursalId); // Para promociones de una sucursal específica
}
