package ElBuenSabor.ProyectoFinal.Repositories;

import ElBuenSabor.ProyectoFinal.DTO.PersonaReporteDTO;
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
    List<Pedido> findByPersonaId(Long personaId);
    List<Pedido> findByEstado(Estado estado);
    List<Pedido> findByEstadoIn(List<Estado> estados);
    //--------------------------------------FILTRADO personaS-PEDIDOS-----------------------------------
    @Query("SELECT new ElBuenSabor.ProyectoFinal.DTO.PersonaReporteDTO(" +
            "p.persona.id, p.persona.nombre, p.persona.apellido, COUNT(p), SUM(p.total)) " +
            "FROM Pedido p " +
            "WHERE p.fechaPedido BETWEEN :desde AND :hasta AND p.estado = 'ENTREGADO' " +
            "GROUP BY p.persona.id, p.persona.nombre, p.persona.apellido " +
            "ORDER BY " +
            "CASE WHEN :orden = 'cantidad' THEN COUNT(p) END DESC, " +
            "CASE WHEN :orden = 'total' THEN SUM(p.total) END DESC")
    List<PersonaReporteDTO> obtenerReporteClientes(
            @Param("desde") LocalDate desde,
            @Param("hasta") LocalDate hasta,
            @Param("orden") String orden);



}
