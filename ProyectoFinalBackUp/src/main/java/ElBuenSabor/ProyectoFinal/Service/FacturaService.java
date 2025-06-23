package ElBuenSabor.ProyectoFinal.Service;

import ElBuenSabor.ProyectoFinal.DTO.FacturaDTO; // Para respuestas
import ElBuenSabor.ProyectoFinal.Entities.Factura;
import ElBuenSabor.ProyectoFinal.Entities.Pedido;

import java.util.List; // Asegúrate de tener esta importación

public interface FacturaService extends BaseService<Factura, Long> {
    FacturaDTO findFacturaById(Long id) throws Exception;
    List<FacturaDTO> findAllFacturas() throws Exception;
    FacturaDTO findByPedidoId(Long pedidoId) throws Exception;

    void generarFacturaPorPedido(Pedido savedPedido);
    // Otros métodos de búsqueda que puedas necesitar (por fecha, por cliente a través del pedido, etc.)
}