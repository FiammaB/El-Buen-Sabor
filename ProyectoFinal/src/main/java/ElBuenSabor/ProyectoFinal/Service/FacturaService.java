package ElBuenSabor.ProyectoFinal.Service;

import ElBuenSabor.ProyectoFinal.DTO.FacturaDTO;
import ElBuenSabor.ProyectoFinal.Entities.Factura; // Para Optional
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface FacturaService extends BaseService<Factura, Long> {
    // La creación de Factura se maneja principalmente a través de PedidoService.
    // Este servicio se enfoca en consulta y anulación.

    FacturaDTO findFacturaById(Long id) throws Exception; // Devuelve DTO de activa (no anulada)
    List<FacturaDTO> findAllFacturas() throws Exception; // Devuelve DTOs de activas
    FacturaDTO findFacturaByPedidoId(Long pedidoId) throws Exception; // Busca la factura de un pedido
    List<FacturaDTO> findFacturasByDateRange(LocalDate fechaDesde, LocalDate fechaHasta) throws Exception; // Activas

    // Anulación de factura (implica borrado lógico y lógica de negocio adicional)
    void anularFactura(Long facturaId) throws Exception;
    // El método reactivate de BaseService podría no tener sentido para Factura si anular es definitivo.
    // Si se permite "des-anular", entonces reactivate tendría sentido.

    // Heredados de BaseService y a implementar en FacturaServiceImpl para devolver DTOs
    List<FacturaDTO> findAllFacturasIncludingAnuladas() throws Exception;
    FacturaDTO findFacturaByIdIncludingAnulada(Long id) throws Exception;
}
