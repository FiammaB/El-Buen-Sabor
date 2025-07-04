package ElBuenSabor.ProyectoFinal.Service;

import ElBuenSabor.ProyectoFinal.DTO.PedidoCreateDTO;

import ElBuenSabor.ProyectoFinal.Entities.Estado;
import ElBuenSabor.ProyectoFinal.Entities.NotaCredito;
import ElBuenSabor.ProyectoFinal.Entities.Pedido;
import ElBuenSabor.ProyectoFinal.Entities.Usuario;

import java.time.LocalTime;
import java.util.List;


public interface PedidoService extends BaseService<Pedido, Long> {
    Pedido crearPedidoPreferenciaMP(PedidoCreateDTO dto) throws Exception;


    void procesarNotificacionPagoMercadoPago(String paymentId) throws Exception;

    List<Pedido> findPedidosByClienteId(Long clienteId)throws Exception;

    /**
     * Anula una factura asociada a un pedido, genera una nota de crédito,
     * repone el stock de ingredientes y registra la anulación.
     * @param pedidoId ID del pedido cuya factura se anulará.
     * @param motivoAnulacion Motivo de la anulación.
     * @param usuarioAnulador Usuario que realiza la anulación.
     * @return La NotaCredito generada.
     * @throws Exception Si el pedido o la factura no se encuentran, o si ocurre un error en el proceso.
     */
    NotaCredito anularFacturaYGenerarNotaCredito(Long pedidoId, String motivoAnulacion, Usuario usuarioAnulador) throws Exception;
    LocalTime calcularTiempoEstimadoFinalizacion(Pedido pedido) throws Exception;

    // Métodos para las nuevas consultas (si ya los habías añadido, no hace falta repetirlos)

    List<Pedido> findPedidosByEstado(Estado estado) throws Exception;
    //List<Pedido> findPedidosBetweenFechas(LocalDate fechaInicio, LocalDate fechaFin) throws Exception;
   // List<Pedido> findPedidosByClienteIdAndEstado(Long clienteId, Estado estado) throws Exception;
    //List<Pedido> findPedidosByFechaAndEstado(LocalDate fechaInicio, LocalDate fechaFin, Estado estado) throws Exception;
    //List<Pedido> findPedidosByEstadoOrderByFechaPedidoDesc(Estado estado) throws Exception;
   // List<Pedido> findPedidosByClienteExcludingEstado(Long clienteId, Estado estadoExcluido) throws Exception;
    //long countPedidosByEstado(Estado estado) throws Exception;
    //List<Pedido> findPedidosBySucursalIdAndFechaBetween(Long sucursalId, LocalDate fechaInicio, LocalDate fechaFin) throws Exception;
   // List<Pedido> findPedidosByArticuloManufacturadoId(Long articuloManufacturadoId) throws Exception;
}
