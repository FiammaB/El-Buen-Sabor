package ElBuenSabor.ProyectoFinal.Service;

import ElBuenSabor.ProyectoFinal.Entities.Promocion;
import ElBuenSabor.ProyectoFinal.Entities.TipoPromocion;
import ElBuenSabor.ProyectoFinal.Repositories.PromocionRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PromocionServiceImpl extends BaseServiceImpl<Promocion, Long> implements PromocionService {

    private final PromocionRepository promocionRepository;

    public PromocionServiceImpl(PromocionRepository promocionRepository) {
        super(promocionRepository);
        this.promocionRepository = promocionRepository;
    }

    @Override
    public List<Promocion> getPromocionesActivas() {
        return promocionRepository.findAll()
                .stream()
                .filter(p -> !p.isBaja())
                .collect(Collectors.toList());
    }


    @Override
    public Promocion update(Long id, Promocion updatedPromocion) throws Exception {
        Promocion promocion = promocionRepository.findById(id)
                .orElseThrow(() -> new Exception("Promoción no encontrada con ID: " + id));

        promocion.setDenominacion(updatedPromocion.getDenominacion());
        promocion.setFechaDesde(updatedPromocion.getFechaDesde());
        promocion.setFechaHasta(updatedPromocion.getFechaHasta());
        promocion.setHoraDesde(updatedPromocion.getHoraDesde());
        promocion.setHoraHasta(updatedPromocion.getHoraHasta());
        promocion.setPrecioPromocional(updatedPromocion.getPrecioPromocional());
        promocion.setTipoPromocion(updatedPromocion.getTipoPromocion());
        promocion.setArticulosManufacturados(updatedPromocion.getArticulosManufacturados());
        promocion.setSucursales(updatedPromocion.getSucursales());

        return promocionRepository.save(promocion);
    }
}
