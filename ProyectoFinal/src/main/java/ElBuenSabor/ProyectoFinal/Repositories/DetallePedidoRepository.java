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

// En DetallePedidoRepository.java

    @Query(value = """
    SELECT
        sub.nombreProducto,
        CAST(SUM(sub.cantidadVendida) AS SIGNED) as cantidadVendida,
        sub.precioVenta,
        sub.fechaVenta
    FROM (
        -- Ventas de Artículos Manufacturados Individuales
        SELECT a.denominacion AS nombreProducto, SUM(dp.cantidad) AS cantidadVendida, a.precio_venta as precioVenta, p.fecha_pedido as fechaVenta
        FROM detalle_pedido dp
        JOIN articulo a ON dp.articulo_manufacturado_id = a.id
        JOIN pedido p ON dp.id_pedido = p.id
        WHERE p.fecha_pedido BETWEEN :desde AND :hasta AND p.estado = 'PAGADO'
        GROUP BY a.denominacion, a.precio_venta, p.fecha_pedido

        UNION ALL

        -- Ventas de Artículos Insumo Individuales
        SELECT a.denominacion AS nombreProducto, SUM(dp.cantidad) AS cantidadVendida, a.precio_venta as precioVenta, p.fecha_pedido as fechaVenta
        FROM detalle_pedido dp
        JOIN articulo a ON dp.id_articulo = a.id
        JOIN articulo_insumo ai ON a.id = ai.id
        JOIN pedido p ON dp.id_pedido = p.id
        WHERE p.fecha_pedido BETWEEN :desde AND :hasta AND p.estado = 'PAGADO' AND ai.es_para_elaborar = false
        GROUP BY a.denominacion, a.precio_venta, p.fecha_pedido

        UNION ALL

        -- Ventas de Artículos Manufacturados DENTRO de Promociones
        SELECT a.denominacion AS nombreProducto, SUM(dp.cantidad * prd.cantidad) AS cantidadVendida, a.precio_venta as precioVenta, p.fecha_pedido as fechaVenta
        FROM detalle_pedido dp
        JOIN promocion_detalle prd ON dp.id_promocion = prd.promocion_id
        JOIN articulo a ON prd.articulo_manufacturado_id = a.id
        JOIN pedido p ON dp.id_pedido = p.id
        WHERE p.fecha_pedido BETWEEN :desde AND :hasta AND p.estado = 'PAGADO'
        GROUP BY a.denominacion, a.precio_venta, p.fecha_pedido

        UNION ALL

        -- Ventas de Artículos Insumo DENTRO de Promociones
        SELECT a.denominacion AS nombreProducto, SUM(dp.cantidad) AS cantidadVendida, a.precio_venta as precioVenta, p.fecha_pedido as fechaVenta
        FROM detalle_pedido dp
        JOIN promocion_articulo_insumo pai ON dp.id_promocion = pai.promocion_id
        JOIN articulo a ON pai.articulo_insumo_id = a.id
        JOIN articulo_insumo ai ON a.id = ai.id
        JOIN pedido p ON dp.id_pedido = p.id
        WHERE p.fecha_pedido BETWEEN :desde AND :hasta AND p.estado = 'PAGADO' AND ai.es_para_elaborar = false
        GROUP BY a.denominacion, a.precio_venta, p.fecha_pedido
    ) AS sub
    GROUP BY sub.nombreProducto, sub.precioVenta, sub.fechaVenta
    ORDER BY sub.fechaVenta ASC, cantidadVendida DESC
    """,
            nativeQuery = true)
    List<ProductoRankingDTO> rankingProductosMasVendidos(@Param("desde") LocalDate desde, @Param("hasta") LocalDate hasta);}