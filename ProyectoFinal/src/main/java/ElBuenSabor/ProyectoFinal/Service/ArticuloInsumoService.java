package ElBuenSabor.ProyectoFinal.Service;

import ElBuenSabor.ProyectoFinal.Entities.ArticuloInsumo;

import java.util.List;

public interface ArticuloInsumoService extends BaseService<ArticuloInsumo, Long>{

    ArticuloInsumo actualizarPrecioYPropagar(Long id, Double nuevoPrecioCompra) throws Exception;
    void verificarYReactivarArticulosManufacturados(ArticuloInsumo insumoActualizado) throws Exception;
    void verificarYDarDeBajaRelacionadosPorStockBajo(ArticuloInsumo insumoActualizado) throws Exception;
}
