package ElBuenSabor.ProyectoFinal.Service;

import ElBuenSabor.ProyectoFinal.Entities.ArticuloInsumo;
import ElBuenSabor.ProyectoFinal.Entities.ArticuloManufacturado;
import ElBuenSabor.ProyectoFinal.Exceptions.ResourceNotFoundException;
import ElBuenSabor.ProyectoFinal.Repositories.ArticuloInsumoRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ArticuloInsumoServiceImpl extends BaseServiceImpl<ArticuloInsumo, Long> implements ArticuloInsumoService {


    public ArticuloInsumoServiceImpl(ArticuloInsumoRepository articuloInsumoRepository) {
        super(articuloInsumoRepository); // ¡ESTO ES CRUCIAL!
    }


    @Override
    @Transactional
    public ArticuloInsumo update(Long id, ArticuloInsumo updated) throws Exception {
        // Lógica de actualización específica para ArticuloManufacturado
        // Puedes llamar a super.update(id, updated) o implementar la lógica
        // que involucre los detalles aquí, como ya te había mostrado.
        ArticuloInsumo actual = findById(id); // Usa el findById del padre

        actual.setDenominacion(updated.getDenominacion());
        actual.setPrecioVenta(updated.getPrecioVenta());
        actual.setPrecioCompra(updated.getPrecioCompra());
        actual.setStockActual(updated.getStockActual());
        actual.setStockMinimo(updated.getStockMinimo());
        actual.setEsParaElaborar(updated.getEsParaElaborar());
        actual.setCategoria(updated.getCategoria());
        actual.setUnidadMedida(updated.getUnidadMedida());
        actual.setImagen(updated.getImagen());


        return baseRepository.save(actual); // <-- AQUÍ ES DONDE DEBE IR
    }
}
