package ElBuenSabor.ProyectoFinal.Repositories;

import ElBuenSabor.ProyectoFinal.DTO.ClienteReporteDTO;
import ElBuenSabor.ProyectoFinal.Entities.Estado;
import ElBuenSabor.ProyectoFinal.Entities.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {
    List<Pedido> findByClienteId(Long clienteId);
    List<Pedido> findByEstado(Estado estado);
    List<Pedido> findByEstadoIn(List<Estado> estados);
    //--------------------------------------FILTRADO CLIENTES-PEDIDOS-----------------------------------
    @Query("SELECT new ElBuenSabor.ProyectoFinal.DTO.ClienteReporteDTO(" +
            "p.cliente.id, p.cliente.nombre, p.cliente.apellido, COUNT(p), SUM(p.total)) " +
            "FROM Pedido p " +
            "WHERE p.fechaPedido BETWEEN :desde AND :hasta AND p.estado = 'ENTREGADO' " +
            "GROUP BY p.cliente.id, p.cliente.nombre, p.cliente.apellido " +
            "ORDER BY " +
            "CASE WHEN :orden = 'cantidad' THEN COUNT(p) END DESC, " +
            "CASE WHEN :orden = 'total' THEN SUM(p.total) END DESC")
    List<ClienteReporteDTO> obtenerReporteClientes(
            @Param("desde") LocalDate desde,
            @Param("hasta") LocalDate hasta,
            @Param("orden") String orden);



}
