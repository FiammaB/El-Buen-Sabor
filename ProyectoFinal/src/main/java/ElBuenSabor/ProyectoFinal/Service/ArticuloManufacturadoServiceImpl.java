package ElBuenSabor.ProyectoFinal.Service;

import ElBuenSabor.ProyectoFinal.Entities.ArticuloManufacturado;
import ElBuenSabor.ProyectoFinal.Exceptions.ResourceNotFoundException;
import ElBuenSabor.ProyectoFinal.Repositories.ArticuloManufacturadoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ArticuloManufacturadoServiceImpl implements ArticuloManufacturadoService {

    private final ArticuloManufacturadoRepository repository;

    @Override
    public ArticuloManufacturado findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Artículo manufacturado no encontrado"));
    }

    @Override
    public List<ArticuloManufacturado> findAll() {
        return repository.findAll();
    }

    @Override
    public ArticuloManufacturado save(ArticuloManufacturado articulo) {
        if (articulo.getDetalles() != null) {
            articulo.getDetalles().forEach(detalle -> detalle.setArticuloManufacturado(articulo));
        }
        return repository.save(articulo);
    }

    @Override
    public ArticuloManufacturado update(Long id, ArticuloManufacturado updated) {
        ArticuloManufacturado actual = findById(id);

        actual.setDenominacion(updated.getDenominacion());
        actual.setPrecioVenta(updated.getPrecioVenta());
        actual.setDescripcion(updated.getDescripcion());
        actual.setTiempoEstimadoMinutos(updated.getTiempoEstimadoMinutos());
        actual.setPreparacion(updated.getPreparacion());
        actual.setCategoria(updated.getCategoria());
        actual.setUnidadMedida(updated.getUnidadMedida());
        actual.setImagen(updated.getImagen());
        actual.setDetalles(updated.getDetalles());

        return repository.save(actual);
    }

    @Override
    public void deleteById(Long id) {
        repository.deleteById(id);
    }
}
