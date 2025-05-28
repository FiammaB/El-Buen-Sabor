package ElBuenSabor.ProyectoFinal.Repositories;

import ElBuenSabor.ProyectoFinal.Entities.DetallePedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface DetallePedidoRepository extends JpaRepository<DetallePedido, Long> {

    @Query("SELECT CASE WHEN COUNT(dp) > 0 THEN true ELSE false END FROM DetallePedido dp WHERE dp.articuloInsumo.id = :articuloId AND dp.pedido.baja = false AND dp.pedido.estado NOT IN (ElBuenSabor.ProyectoFinal.Entities.Estado.ENTREGADO, ElBuenSabor.ProyectoFinal.Entities.Estado.CANCELADO, ElBuenSabor.ProyectoFinal.Entities.Estado.FACTURADO)")
    boolean existsByArticuloInsumoIdAndPedidoActivo(@Param("articuloId") Long articuloId);

    @Query("SELECT CASE WHEN COUNT(dp) > 0 THEN true ELSE false END FROM DetallePedido dp WHERE dp.articuloManufacturado.id = :articuloId AND dp.pedido.baja = false AND dp.pedido.estado NOT IN (ElBuenSabor.ProyectoFinal.Entities.Estado.ENTREGADO, ElBuenSabor.ProyectoFinal.Entities.Estado.CANCELADO, ElBuenSabor.ProyectoFinal.Entities.Estado.FACTURADO)")
    boolean existsByArticuloManufacturadoIdAndPedidoActivo(@Param("articuloId") Long articuloId);
}