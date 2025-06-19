package ElBuenSabor.ProyectoFinal.Service;

import ElBuenSabor.ProyectoFinal.DTO.PedidoCreateDTO;

import ElBuenSabor.ProyectoFinal.Entities.Pedido;



public interface PedidoService extends BaseService<Pedido, Long> {
    Pedido crearPedidoPreferenciaMP(PedidoCreateDTO dto) throws Exception;


    void procesarNotificacionPagoMercadoPago(String paymentId) throws Exception;

    // Métodos para las nuevas consultas (si ya los habías añadido, no hace falta repetirlos)
    //List<Pedido> findPedidosByClienteId(Long clienteId) throws Exception;
    //List<Pedido> findPedidosByEstado(Estado estado) throws Exception;
    //List<Pedido> findPedidosBetweenFechas(LocalDate fechaInicio, LocalDate fechaFin) throws Exception;
   // List<Pedido> findPedidosByClienteIdAndEstado(Long clienteId, Estado estado) throws Exception;
    //List<Pedido> findPedidosByFechaAndEstado(LocalDate fechaInicio, LocalDate fechaFin, Estado estado) throws Exception;
    //List<Pedido> findPedidosByEstadoOrderByFechaPedidoDesc(Estado estado) throws Exception;
   // List<Pedido> findPedidosByClienteExcludingEstado(Long clienteId, Estado estadoExcluido) throws Exception;
    //long countPedidosByEstado(Estado estado) throws Exception;
    //List<Pedido> findPedidosBySucursalIdAndFechaBetween(Long sucursalId, LocalDate fechaInicio, LocalDate fechaFin) throws Exception;
   // List<Pedido> findPedidosByArticuloManufacturadoId(Long articuloManufacturadoId) throws Exception;
}
