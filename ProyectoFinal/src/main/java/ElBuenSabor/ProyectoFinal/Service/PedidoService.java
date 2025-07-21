package ElBuenSabor.ProyectoFinal.Service;

import ElBuenSabor.ProyectoFinal.DTO.PersonaReporteDTO;
import ElBuenSabor.ProyectoFinal.DTO.PedidoCreateDTO;

import ElBuenSabor.ProyectoFinal.DTO.ProductoRankingDTO;
import ElBuenSabor.ProyectoFinal.Entities.Estado;
import ElBuenSabor.ProyectoFinal.Entities.NotaCredito;
import ElBuenSabor.ProyectoFinal.Entities.Pedido;
import ElBuenSabor.ProyectoFinal.Entities.Usuario;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;


public interface PedidoService extends BaseService<Pedido, Long> {
    Pedido crearPedidoPreferenciaMP(PedidoCreateDTO dto) throws Exception;
    void procesarNotificacionPagoMercadoPago(String paymentId) throws Exception;
    List<Pedido> findPedidosByClienteId(Long personaId)throws Exception;
    NotaCredito anularFacturaYGenerarNotaCredito(Long pedidoId, String motivoAnulacion, Usuario usuarioAnulador) throws Exception;
    LocalTime calcularTiempoEstimadoFinalizacion(Pedido pedido) throws Exception;
    List<Pedido> findPedidosByEstados(List<Estado> estados) throws Exception;
    List<ProductoRankingDTO> obtenerRankingProductosMasVendidos(LocalDate desde, LocalDate hasta);
    List<PersonaReporteDTO> obtenerReporteClientes(LocalDate desde, LocalDate hasta, String orden);
    Double calcularTotalCostoPedido(Pedido pedido);
    void descontarInsumosDelStock(Pedido pedido);
    boolean verificarStockParaPedido(Pedido pedido);
    Pedido marcarPedidoComoPagadoYFacturar(Long pedidoId) throws Exception;

}
