package ElBuenSabor.ProyectoFinal.Service;

import ElBuenSabor.ProyectoFinal.Entities.Factura;
import ElBuenSabor.ProyectoFinal.Exceptions.ResourceNotFoundException; // Posiblemente ya no sea necesaria
import ElBuenSabor.ProyectoFinal.Repositories.FacturaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // Importar Transactional

import java.util.List; // Importar List si no se usara el findAll del padre

@Service

public class FacturaServiceImpl extends BaseServiceImpl<Factura, Long> implements FacturaService {
    public FacturaServiceImpl(FacturaRepository facturaRepository) {
        super(facturaRepository);
    }


    @Override
    @Transactional
    public Factura update(Long id, Factura updatedFactura) throws Exception {
        try {
            Factura actual = findById(id);

            actual.setFechaFacturacion(updatedFactura.getFechaFacturacion());
            actual.setMpPaymentId(updatedFactura.getMpPaymentId());
            actual.setMpMerchantOrderId(updatedFactura.getMpMerchantOrderId());
            actual.setMpPreferenceId(updatedFactura.getMpPreferenceId());
            actual.setMpPaymentType(updatedFactura.getMpPaymentType());
            actual.setFormaPago(updatedFactura.getFormaPago());
            actual.setTotalVenta(updatedFactura.getTotalVenta());

            return baseRepository.save(actual);
        } catch (Exception e) {

            throw new Exception("Error al actualizar la factura: " + e.getMessage());
        }
    }
}