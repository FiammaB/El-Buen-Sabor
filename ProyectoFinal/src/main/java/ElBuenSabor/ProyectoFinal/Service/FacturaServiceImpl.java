package ElBuenSabor.ProyectoFinal.Service;

import ElBuenSabor.ProyectoFinal.DTO.FacturaDTO;
import ElBuenSabor.ProyectoFinal.Entities.Factura;
import ElBuenSabor.ProyectoFinal.Entities.Pedido; // Necesario para buscar por pedidoId
import ElBuenSabor.ProyectoFinal.Repositories.FacturaRepository;
import ElBuenSabor.ProyectoFinal.Repositories.PedidoRepository; // Necesario para buscar por pedidoId
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FacturaServiceImpl extends BaseServiceImpl<Factura, Long> implements FacturaService {

    private final FacturaRepository facturaRepository;
    private final PedidoRepository pedidoRepository; // Para buscar factura por pedido

    @Autowired
    public FacturaServiceImpl(FacturaRepository facturaRepository, PedidoRepository pedidoRepository) {
        super(facturaRepository);
        this.facturaRepository = facturaRepository;
        this.pedidoRepository = pedidoRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public FacturaDTO findFacturaById(Long id) throws Exception {
        Factura factura = facturaRepository.findById(id)
                .orElseThrow(() -> new Exception("Factura no encontrada con ID: " + id));
        return convertToDTO(factura);
    }

    @Override
    @Transactional(readOnly = true)
    public List<FacturaDTO> findAllFacturas() throws Exception {
        return facturaRepository.findAll().stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public FacturaDTO findByPedidoId(Long pedidoId) throws Exception {
        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new Exception("Pedido no encontrado con ID: " + pedidoId + " al buscar su factura."));
        Factura factura = pedido.getFactura();
        if (factura == null) {
            // Puedes decidir lanzar una excepción o devolver null/DTO vacío si el pedido no tiene factura
            // throw new Exception("El pedido con ID: " + pedidoId + " no tiene una factura asociada.");
            return null;
        }
        return convertToDTO(factura);
    }

    private FacturaDTO convertToDTO(Factura factura) {
        if (factura == null) return null;
        FacturaDTO dto = new FacturaDTO();
        dto.setId(factura.getId());
        dto.setFechaFacturacion(factura.getFechaFacturacion());
        dto.setMpPaymentId(factura.getMpPaymentId()); //
        dto.setMpMerchantOrderId(factura.getMpMerchantOrderId()); //
        dto.setMpPreferenceId(factura.getMpPreferenceId()); //
        dto.setMpPaymentType(factura.getMpPaymentType()); //
        dto.setFormaPago(factura.getFormaPago()); //
        dto.setTotalVenta(factura.getTotalVenta()); //

        // Para obtener el pedidoId, necesitamos la relación bidireccional o buscar el pedido que tenga esta factura.
        // Si Factura no tiene una referencia directa a Pedido (lo cual es común, Pedido tiene a Factura),
        // este campo podría ser llenado por quien llama al DTO si tiene el contexto del pedido.
        // O, si es necesario, hacer una query:
        // Pedido pedidoAsociado = pedidoRepository.findByFacturaId(factura.getId());
        // if (pedidoAsociado != null) dto.setPedidoId(pedidoAsociado.getId());
        // Por ahora, lo dejaremos así, asumiendo que el contexto del pedido_id se conoce por fuera o no es estrictamente necesario en este DTO.
        return dto;
    }
}