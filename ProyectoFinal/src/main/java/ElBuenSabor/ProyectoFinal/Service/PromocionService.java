package ElBuenSabor.ProyectoFinal.Service;

import ElBuenSabor.ProyectoFinal.Entities.Promocion;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface PromocionService extends BaseService<Promocion, Long> {
    List<Promocion> getPromocionesActivas();

    @Transactional(readOnly = true)
    List<Promocion> getPromocionesActivas(Long sucursalId);

    @Transactional(readOnly = true)
    List<Promocion> findAllBySucursalId(Long sucursalId) throws Exception;
}
