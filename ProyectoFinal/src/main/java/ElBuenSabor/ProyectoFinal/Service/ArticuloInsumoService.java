package ElBuenSabor.ProyectoFinal.Service;

import ElBuenSabor.ProyectoFinal.Entities.ArticuloInsumo;

import java.util.List;

public interface ArticuloInsumoService extends BaseService<ArticuloInsumo, Long>{
    // Nuevo método: Obtener todos los artículos insumo por sucursal
    List<ArticuloInsumo> findAllBySucursalId(Long sucursalId) throws Exception;

    // Nuevo método: Filtrar insumos con stock bajo por sucursal
    List<ArticuloInsumo> findByStockActualLessThanEqualAndSucursalId(Double stockMinimo, Long sucursalId) throws Exception;

}
