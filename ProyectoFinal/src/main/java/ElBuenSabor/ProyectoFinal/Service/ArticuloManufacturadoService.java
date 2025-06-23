package ElBuenSabor.ProyectoFinal.Service;

import ElBuenSabor.ProyectoFinal.Entities.ArticuloManufacturado;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface ArticuloManufacturadoService extends BaseService<ArticuloManufacturado, Long> {
    List<ArticuloManufacturado> filtrar(Long sucursalId,Long categoriaId, String denominacion, Boolean baja);

    @Transactional(readOnly = true)
    List<ArticuloManufacturado> findAllBySucursalId(Long sucursalId) throws Exception;


    ;
}
