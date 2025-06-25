// Archivo: ElBuenSabor/ProyectoFinal/src/main/java/ElBuenSabor/ProyectoFinal/Service/ArticuloInsumoServiceImpl.java
package ElBuenSabor.ProyectoFinal.Service;

import ElBuenSabor.ProyectoFinal.Entities.ArticuloInsumo;
import ElBuenSabor.ProyectoFinal.Entities.Sucursal;
import ElBuenSabor.ProyectoFinal.Exceptions.ResourceNotFoundException;
import ElBuenSabor.ProyectoFinal.Repositories.ArticuloInsumoRepository;
import ElBuenSabor.ProyectoFinal.Repositories.SucursalRepository; // Importar SucursalRepository
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional; // Importar Optional

@Service
public class ArticuloInsumoServiceImpl extends BaseServiceImpl<ArticuloInsumo, Long> implements ArticuloInsumoService {

    private final ArticuloInsumoRepository articuloInsumoRepository;
    private final SucursalRepository sucursalRepository; // Inyectar SucursalRepository

    public ArticuloInsumoServiceImpl(ArticuloInsumoRepository articuloInsumoRepository,
                                     SucursalRepository sucursalRepository) { // Modificar constructor
        super(articuloInsumoRepository);
        this.articuloInsumoRepository = articuloInsumoRepository;
        this.sucursalRepository = sucursalRepository;
    }

    @Override
    @Transactional
    public ArticuloInsumo save(ArticuloInsumo newEntity) throws Exception {
        // Al guardar una nueva entidad, asegurarse de que la sucursal está establecida
        if (newEntity.getSucursal() == null && newEntity.getId() == null) {
            throw new Exception("El ArticuloInsumo debe estar asociado a una sucursal.");
        }
        return super.save(newEntity);
    }

    @Override
    @Transactional
    public ArticuloInsumo update(Long id, ArticuloInsumo updated) throws Exception {
        try {
            ArticuloInsumo actual = findById(id);

            // Asegurarse de que el insumo pertenece a la misma sucursal (o permitir cambio si la lógica lo permite)
            if (updated.getSucursal() != null && !updated.getSucursal().getId().equals(actual.getSucursal().getId())) {
                // CORRECCIÓN: Ahora findById devuelve Optional, por lo que usamos orElseThrow
                Sucursal nuevaSucursal = sucursalRepository.findById(updated.getSucursal().getId())
                        .orElseThrow(() -> new ResourceNotFoundException("Sucursal no encontrada con ID: " + updated.getSucursal().getId()));
                actual.setSucursal(nuevaSucursal);
            }

            actual.setDenominacion(updated.getDenominacion());
            actual.setPrecioVenta(updated.getPrecioVenta());
            actual.setPrecioCompra(updated.getPrecioCompra());
            actual.setStockActual(updated.getStockActual());
            actual.setStockMinimo(updated.getStockMinimo());
            actual.setEsParaElaborar(updated.getEsParaElaborar());
            actual.setCategoria(updated.getCategoria());
            actual.setUnidadMedida(updated.getUnidadMedida());
            actual.setImagen(updated.getImagen());

            return baseRepository.save(actual);
        } catch (Exception e) {
            throw new Exception("Error al actualizar el artículo insumo: " + e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<ArticuloInsumo> findAllBySucursalId(Long sucursalId) throws Exception {
        try {
            // CORRECCIÓN: Usar orElseThrow para verificar la existencia de la sucursal
            sucursalRepository.findById(sucursalId)
                    .orElseThrow(() -> new ResourceNotFoundException("Sucursal no encontrada con ID: " + sucursalId));
            return articuloInsumoRepository.findBySucursalId(sucursalId);
        } catch (Exception e) {
            throw new Exception("Error al obtener artículos insumo por sucursal: " + e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<ArticuloInsumo> findByStockActualLessThanEqualAndSucursalId(Double stockMinimo, Long sucursalId) throws Exception {
        try {
            // CORRECCIÓN: Usar orElseThrow para verificar la existencia de la sucursal
            sucursalRepository.findById(sucursalId)
                    .orElseThrow(() -> new ResourceNotFoundException("Sucursal no encontrada con ID: " + sucursalId));
            return articuloInsumoRepository.findByStockActualLessThanEqualAndSucursalId(stockMinimo, sucursalId);
        } catch (Exception e) {
            throw new Exception("Error al obtener insumos con stock bajo por sucursal: " + e.getMessage());
        }
    }
}
