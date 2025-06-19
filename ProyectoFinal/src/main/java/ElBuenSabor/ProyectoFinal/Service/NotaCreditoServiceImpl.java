package ElBuenSabor.ProyectoFinal.Service;



import ElBuenSabor.ProyectoFinal.Entities.NotaCredito;
import ElBuenSabor.ProyectoFinal.Repositories.NotaCreditoRepository;
import org.springframework.stereotype.Service;

@Service
public class NotaCreditoServiceImpl extends BaseServiceImpl<NotaCredito, Long> implements NotaCreditoService {

    public NotaCreditoServiceImpl(NotaCreditoRepository notaCreditoRepository) {
        super(notaCreditoRepository);
    }
}