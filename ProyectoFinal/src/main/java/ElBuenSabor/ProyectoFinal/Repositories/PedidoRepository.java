package ElBuenSabor.ProyectoFinal.Repositories;

import ElBuenSabor.ProyectoFinal.Entities.Estado;
import ElBuenSabor.ProyectoFinal.Entities.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    // Afectados por @Where(clause="baja=false") en Pedido
    List<Pedido> findByClienteId(Long clienteId);
    List<Pedido> findByEstado(Estado estado);
    List<Pedido> findBySucursalIdAndEstado(Long sucursalId, Estado estado); // Para cajero/cocinero
    List<Pedido> findByFechaPedidoBetween(LocalDate fechaDesde, LocalDate fechaHasta);
    boolean existsActivePedidoWithCliente(Long id);


    // Para buscar incluyendo los 'baja = true' (anulados/borrados lógicamente)
    @Query("SELECT p FROM Pedido p WHERE p.id = :id")
    Optional<Pedido> findByIdRaw(@Param("id") Long id);

    @Query("SELECT p FROM Pedido p")
    List<Pedido> findAllRaw();

    @Query("SELECT p FROM Pedido p WHERE p.cliente.id = :clienteId")
    List<Pedido> findByClienteIdRaw(@Param("clienteId") Long clienteId);

    @Query("SELECT p FROM Pedido p WHERE p.estado = :estado")
    List<Pedido> findByEstadoRaw(@Param("estado") Estado estado);

    // Para encontrar el pedido asociado a una factura (incluso si el pedido está 'baja=true')
    @Query("SELECT p FROM Pedido p WHERE p.factura.id = :facturaId")
    Optional<Pedido> findByFacturaIdRaw(@Param("facturaId") Long facturaId);

    // Para calcular tiempo estimado (HU#10) - pedidos en cocina de una sucursal específica
    // Se asume que el estado "EN_COCINA" implica que no están 'baja=true' debido al @Where en Pedido.
    @Query("SELECT p FROM Pedido p WHERE p.sucursal.id = :sucursalId AND p.estado = :estadoCocina AND p.baja = false")
    List<Pedido> findPedidosEnCocinaPorSucursal(@Param("sucursalId") Long sucursalId, @Param("estadoCocina") Estado estadoCocina);

    // Para verificar si un domicilio está en uso en pedidos activos (no finales)
    @Query("SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END FROM Pedido p WHERE p.domicilioEntrega.id = :domicilioId AND p.baja = false AND p.estado NOT IN (ElBuenSabor.ProyectoFinal.Entities.Estado.ENTREGADO, ElBuenSabor.ProyectoFinal.Entities.Estado.CANCELADO, ElBuenSabor.ProyectoFinal.Entities.Estado.FACTURADO)")
    boolean existsActivePedidoWithDomicilio(@Param("domicilioId") Long domicilioId);


}
