package ElBuenSabor.ProyectoFinal.Repositories;

import ElBuenSabor.ProyectoFinal.Entities.Factura;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate; // Para búsquedas por fecha
import java.util.List;
import java.util.Optional;

@Repository
public interface FacturaRepository extends JpaRepository<Factura, Long> {

    // Para buscar incluyendo las 'baja = true' (anuladas)
    @Query("SELECT f FROM Factura f WHERE f.id = :id")
    Optional<Factura> findByIdRaw(@Param("id") Long id);

    @Query("SELECT f FROM Factura f")
    List<Factura> findAllRaw();

    // Opcional: Búsquedas adicionales que podrían ser útiles
    List<Factura> findByFechaFacturacionBetween(LocalDate fechaDesde, LocalDate fechaHasta); // Afectado por @Where

    @Query("SELECT f FROM Factura f WHERE f.fechaFacturacion BETWEEN :fechaDesde AND :fechaHasta")
    List<Factura> findByFechaFacturacionBetweenRaw(@Param("fechaDesde") LocalDate fechaDesde, @Param("fechaHasta") LocalDate fechaHasta);

    // No hay un findByPedidoId directo aquí porque la FK está en Pedido.
    // Se buscaría el Pedido y luego se accedería a su Factura.
}
