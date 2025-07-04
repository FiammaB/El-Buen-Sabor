package ElBuenSabor.ProyectoFinal.Service;

import ElBuenSabor.ProyectoFinal.Entities.ArticuloManufacturado;
import java.util.List;

public interface ArticuloManufacturadoService extends BaseService<ArticuloManufacturado, Long> {
    List<ArticuloManufacturado> filtrar(Long categoriaId, String denominacion, Boolean baja);
}
