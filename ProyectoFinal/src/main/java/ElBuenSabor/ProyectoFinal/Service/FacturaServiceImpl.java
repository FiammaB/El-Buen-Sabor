package ElBuenSabor.ProyectoFinal.Service;

import ElBuenSabor.ProyectoFinal.DTO.FacturaDTO;
import ElBuenSabor.ProyectoFinal.DTO.PedidoResponseDTO; // Para el convertToDTO
import ElBuenSabor.ProyectoFinal.Entities.Factura;
import ElBuenSabor.ProyectoFinal.Entities.Pedido;
import ElBuenSabor.ProyectoFinal.Repositories.FacturaRepository;
import ElBuenSabor.ProyectoFinal.Repositories.PedidoRepository; // Para buscar pedido por factura
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class FacturaServiceImpl extends BaseServiceImpl<Factura, Long> implements FacturaService {

    private final FacturaRepository facturaRepository;
    private final PedidoRepository pedidoRepository;
    private final PedidoService pedidoService; // Para la lógica de reposición de stock al anular

    @Autowired
    public FacturaServiceImpl(FacturaRepository facturaRepository,
                              PedidoRepository pedidoRepository,
                              PedidoService pedidoService) {
        super(facturaRepository);
        this.facturaRepository = facturaRepository;
        this.pedidoRepository = pedidoRepository;
        this.pedidoService = pedidoService;
    }

    @Override
    @Transactional(readOnly = true)
    public FacturaDTO findFacturaById(Long id) throws Exception {
        return convertToDTO(super.findById(id).orElse(null)); // Devuelve activas
    }

    @Override
    @Transactional(readOnly = true)
    public List<FacturaDTO> findAllFacturas() throws Exception {
        return super.findAll().stream().map(this::convertToDTO).collect(Collectors.toList()); // Devuelve activas
    }

    @Override
    @Transactional(readOnly = true)
    public FacturaDTO findFacturaByPedidoId(Long pedidoId) throws Exception {
        Pedido pedido = pedidoRepository.findById(pedidoId) // findById de Pedido ya filtra por activos
                .orElseThrow(() -> new Exception("Pedido activo no encontrado con ID: " + pedidoId));
        if (pedido.getFactura() == null || pedido.getFactura().isBaja()) {
            // Si la factura del pedido está anulada (baja=true), no la devolvemos aquí
            // o devolvemos null indicando que no hay factura activa.
            return null;
        }
        return convertToDTO(pedido.getFactura());
    }

    @Override
    @Transactional(readOnly = true)
    public List<FacturaDTO> findFacturasByDateRange(LocalDate fechaDesde, LocalDate fechaHasta) throws Exception {
        // facturaRepository.findByFechaFacturacionBetween ya filtra por baja=false debido a @Where
        return facturaRepository.findByFechaFacturacionBetween(fechaDesde, fechaHasta)
                .stream().map(this::convertToDTO).collect(Collectors.toList());
    }


    @Override
    @Transactional
    public void anularFactura(Long facturaId) throws Exception {
        Factura factura = this.findByIdIncludingDeleted(facturaId) // Necesitamos encontrarla incluso si ya está "baja" para evitar error
                .orElseThrow(() -> new Exception("Factura no encontrada con ID: " + facturaId + " para anular."));

        if (factura.isBaja()) {
            throw new Exception("La factura ya está anulada.");
        }

        // Lógica de negocio para anulación:
        // 1. Marcar la factura como 'baja = true' (anulada)
        factura.setBaja(true);
        facturaRepository.save(factura); // O usar super.softDelete(facturaId) si está implementado para llamar a este.

        // 2. Encontrar el pedido asociado a esta factura
        Pedido pedidoAsociado = pedidoRepository.findByFacturaIdRaw(facturaId) // Necesita este método en PedidoRepository
                .orElseThrow(() -> new Exception("No se encontró pedido asociado a la factura ID: " + facturaId + " para la anulación."));

        // 3. Reponer stock de los ingredientes del pedido
        // Esta lógica ya la tenemos en PedidoServiceImpl.reponerStockPorPedido
        // ¡CUIDADO! reponerStockPorPedido podría estar diseñado para cuando el pedido se CANCELA,
        // no necesariamente cuando se ANULA una factura de un pedido ya entregado.
        // La consigna dice: "Al momento de emisión de la nota de crédito, se tendrán que agregar al stock los ingredientes..."
        // Esto implica que la anulación de factura SÍ repone stock.
        pedidoService.reponerStockPorPedido(pedidoAsociado); // Asumiendo que PedidoService tiene este método público

        // 4. Opcional: Cambiar el estado del pedido asociado si es necesario (ej. a "ANULADO" o similar)
        // pedidoAsociado.setEstado(Estado.ANULADO); // Si tienes un estado ANULADO
        // pedidoRepository.save(pedidoAsociado);

        // 5. Opcional: Generar una Nota de Crédito (esto sería una nueva entidad o un tipo de Factura)
        // NotaCredito nc = generarNotaDeCreditoParaFactura(factura, pedidoAsociado);
        // notaCreditoRepository.save(nc);
        // Por ahora, solo anulamos la factura marcándola como baja.
    }


    // --- Implementación de métodos de BaseService ---
    @Override
    @Transactional(readOnly = true)
    public List<Factura> findAllIncludingDeleted() throws Exception { // Renombrado para claridad
        return facturaRepository.findAllRaw();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Factura> findByIdIncludingDeleted(Long id) throws Exception { // Renombrado para claridad
        return facturaRepository.findByIdRaw(id);
    }

    // softDelete es ahora 'anularFactura'
    @Override
    @Transactional
    public Factura softDelete(Long id) throws Exception {
        this.anularFactura(id); // Llama a la lógica de anulación completa
        // anularFactura no devuelve la factura, pero softDelete en BaseService sí.
        // Para cumplir la firma, volvemos a buscarla (ya estará 'baja=true')
        return this.findByIdIncludingDeleted(id)
                .orElseThrow(() -> new Exception("Factura no encontrada después de la anulación, ID: " + id));
    }

    // Reactivar una factura anulada podría no tener sentido contable si se emitió nota de crédito.
    // Si se permite, la lógica es simple.
    @Override
    @Transactional
    public Factura reactivate(Long id) throws Exception {
        Factura factura = this.findByIdIncludingDeleted(id)
                .orElseThrow(() -> new Exception("Factura no encontrada con ID: " + id + " para reactivar."));
        if (!factura.isBaja()) {
            throw new Exception("La factura no está anulada (baja), no se puede reactivar.");
        }
        // Aquí se necesitaría lógica compleja si se generó una Nota de Crédito (anular la NC, etc.)
        // Por simplicidad, solo cambiamos el flag.
        factura.setBaja(false);
        return facturaRepository.save(factura);
    }

    // --- Implementación de métodos de FacturaService que devuelven DTO ---
    @Override
    @Transactional(readOnly = true)
    public List<FacturaDTO> findAllFacturasIncludingAnuladas() throws Exception {
        return this.findAllIncludingDeleted().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public FacturaDTO findFacturaByIdIncludingAnulada(Long id) throws Exception {
        return convertToDTO(this.findByIdIncludingDeleted(id).orElse(null));
    }

    private FacturaDTO convertToDTO(Factura factura) {
        if (factura == null) return null;
        FacturaDTO dto = new FacturaDTO();
        dto.setId(factura.getId());
        dto.setFechaFacturacion(factura.getFechaFacturacion());
        dto.setMpPaymentId(factura.getMpPaymentId());
        dto.setMpMerchantOrderId(factura.getMpMerchantOrderId());
        dto.setMpPreferenceId(factura.getMpPreferenceId());
        dto.setMpPaymentType(factura.getMpPaymentType());
        dto.setFormaPago(factura.getFormaPago());
        dto.setTotalVenta(factura.getTotalVenta());
        dto.setBaja(factura.isBaja()); // true si está anulada

        // Para obtener el pedidoId, necesitamos una query en PedidoRepository
        try {
            Pedido pedidoAsociado = pedidoRepository.findByFacturaIdRaw(factura.getId()).orElse(null);
            if (pedidoAsociado != null) {
                dto.setPedidoId(pedidoAsociado.getId());
            }
        } catch (Exception e) {
            // Loggear error, pero no impedir la conversión del DTO de factura
            System.err.println("Error al buscar pedido para factura ID " + factura.getId() + ": " + e.getMessage());
        }
        return dto;
    }
}
