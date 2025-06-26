package ElBuenSabor.ProyectoFinal.Repositories;

import ElBuenSabor.ProyectoFinal.DTO.ReporteMonetarioDTO;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface ReporteMonetarioRepository {
    ReporteMonetarioDTO obtenerTotales(LocalDate desde, LocalDate hasta);
}

@Repository
class ReporteMonetarioRepositoryImpl implements ReporteMonetarioRepository {

    @PersistenceContext
    private EntityManager em;

    @Override
    public ReporteMonetarioDTO obtenerTotales(LocalDate desde, LocalDate hasta) {
        // Ingresos: suma de totales de pedidos entregados
        Query ingresoQuery = em.createQuery("SELECT COALESCE(SUM(p.total), 0) FROM Pedido p WHERE p.fechaPedido BETWEEN :desde AND :hasta AND p.estado = 'ENTREGADO'");
        ingresoQuery.setParameter("desde", desde);
        ingresoQuery.setParameter("hasta", hasta);
        Object ingresoResult = ingresoQuery.getSingleResult();
        BigDecimal totalIngresos = ingresoResult != null ? BigDecimal.valueOf((Double) ingresoResult) : BigDecimal.ZERO;

        // Costos: suma de costos de fabricación
        Query costoQuery = em.createQuery("SELECT COALESCE(SUM(amd.cantidad * i.precioCompra), 0) " +
                "FROM Pedido p " +
                "JOIN p.detallesPedidos dp " +
                "JOIN dp.articuloManufacturado am " +
                "JOIN am.detalles amd " +
                "JOIN amd.articuloInsumo i " +
                "WHERE p.fechaPedido BETWEEN :desde AND :hasta AND p.estado = 'ENTREGADO'");
        costoQuery.setParameter("desde", desde);
        costoQuery.setParameter("hasta", hasta);
        Object costoResult = costoQuery.getSingleResult();
        BigDecimal totalCostos = costoResult != null ? BigDecimal.valueOf((Double) costoResult) : BigDecimal.ZERO;

        // Ganancia = ingresos - costos
        BigDecimal ganancia = totalIngresos.subtract(totalCostos);

        return new ReporteMonetarioDTO(totalIngresos, totalCostos, ganancia);
    }
}