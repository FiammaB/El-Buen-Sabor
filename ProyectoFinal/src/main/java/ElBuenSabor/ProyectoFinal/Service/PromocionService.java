package ElBuenSabor.ProyectoFinal.Service;

import ElBuenSabor.ProyectoFinal.Entities.Promocion;
import org.springframework.transaction.annotation.Transactional;
import ElBuenSabor.ProyectoFinal.DTO.PromocionCreateDTO; // <-- Añadir import


import java.util.List;

public interface PromocionService extends BaseService<Promocion, Long> {
    List<Promocion> getPromocionesActivas();
    @Transactional
    Promocion toggleBaja(Long id, boolean baja) throws Exception;
    Promocion update(Long id, PromocionCreateDTO dto) throws Exception;
    Promocion save(PromocionCreateDTO dto) throws Exception;

}
