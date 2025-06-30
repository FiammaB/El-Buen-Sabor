package ElBuenSabor.ProyectoFinal.Service;

import ElBuenSabor.ProyectoFinal.Entities.ArticuloInsumo;
import ElBuenSabor.ProyectoFinal.Entities.ArticuloManufacturado;
import ElBuenSabor.ProyectoFinal.Exceptions.ResourceNotFoundException;
import ElBuenSabor.ProyectoFinal.Repositories.ArticuloInsumoRepository;
import ElBuenSabor.ProyectoFinal.Repositories.ArticuloManufacturadoRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ArticuloInsumoServiceImpl extends BaseServiceImpl<ArticuloInsumo, Long> implements ArticuloInsumoService {

    private final ArticuloInsumoRepository articuloInsumoRepository;
    private final ArticuloManufacturadoRepository articuloManufacturadoRepository;

    public ArticuloInsumoServiceImpl(
            ArticuloInsumoRepository articuloInsumoRepository,
            ArticuloManufacturadoRepository articuloManufacturadoRepository
    ) {
        super(articuloInsumoRepository);
        this.articuloInsumoRepository = articuloInsumoRepository;
        this.articuloManufacturadoRepository = articuloManufacturadoRepository;
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

    @Transactional
    public ArticuloInsumo actualizarPrecioYPropagar(Long id, Double nuevoPrecioCompra) throws Exception {
        ArticuloInsumo insumo = findById(id);
        insumo.setPrecioCompra(nuevoPrecioCompra);
        ArticuloInsumo actualizado = articuloInsumoRepository.save(insumo);

        // Buscar manufacturados que usen este insumo
        List<ArticuloManufacturado> manufacturadosAfectados =
                articuloManufacturadoRepository.findAllByDetalles_ArticuloInsumo_Id(insumo.getId());

        double margenPorcentual = 0.7;

        for (ArticuloManufacturado manufacturado : manufacturadosAfectados) {
            double costoTotal = manufacturado.getDetalles().stream()
                    .mapToDouble(det -> {
                        ArticuloInsumo ing = det.getArticuloInsumo();
                        return (ing.getPrecioCompra() != null ? ing.getPrecioCompra() : 0.0) * det.getCantidad();
                    })
                    .sum();

            double precioVenta = costoTotal * (1 + margenPorcentual);
            manufacturado.setPrecioVenta(precioVenta);
            articuloManufacturadoRepository.save(manufacturado);
        }

        return actualizado;
    }

}
