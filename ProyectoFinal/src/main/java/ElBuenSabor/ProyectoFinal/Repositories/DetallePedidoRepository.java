package ElBuenSabor.ProyectoFinal.Repositories;

import ElBuenSabor.ProyectoFinal.DTO.ProductoRankingDTO;
import ElBuenSabor.ProyectoFinal.Entities.DetallePedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface DetallePedidoRepository extends JpaRepository<DetallePedido, Long> {

    @Query("SELECT new ElBuenSabor.ProyectoFinal.DTO.ProductoRankingDTO(d.articuloManufacturado.denominacion, SUM(d.cantidad)) " +
            "FROM DetallePedido d " +
            "WHERE d.pedido.fechaPedido BETWEEN :desde AND :hasta AND d.pedido.estado = 'ENTREGADO' " +
            "GROUP BY d.articuloManufacturado.denominacion " +
            "ORDER BY SUM(d.cantidad) DESC")
    List<ProductoRankingDTO> rankingProductosMasVendidos(@Param("desde") LocalDate desde, @Param("hasta") LocalDate hasta);


    /*@Query("SELECT new ElBuenSabor.ProyectoFinal.DTO.ProductoRankingDTO(dp.articuloManufacturado.denominacion, SUM(dp.cantidad))\n" +
            "FROM DetallePedido dp\n" +
            "WHERE dp.pedido.fechaPedido BETWEEN :desde AND :hasta\n" +
            "GROUP BY dp.articuloManufacturado.denominacion\n" +
            "ORDER BY SUM(dp.cantidad)DESC\n")
    List<ProductoRankingDTO> obtenerRankingProductos(LocalDate desde, LocalDate hasta);

     */
}
