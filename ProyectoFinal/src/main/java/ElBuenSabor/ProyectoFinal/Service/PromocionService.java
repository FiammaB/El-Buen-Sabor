package ElBuenSabor.ProyectoFinal.Service;

import ElBuenSabor.ProyectoFinal.Entities.Promocion;
import java.util.List;

public interface PromocionService extends BaseService<Promocion, Long> {
    // Nuevos métodos para manejar las promociones
    List<Promocion> getPromocionesActivas();
}