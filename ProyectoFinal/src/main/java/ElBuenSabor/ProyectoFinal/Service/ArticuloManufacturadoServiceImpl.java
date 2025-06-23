package ElBuenSabor.ProyectoFinal.Service;

import ElBuenSabor.ProyectoFinal.Entities.ArticuloManufacturado;
import ElBuenSabor.ProyectoFinal.Entities.ArticuloManufacturadoDetalle;
import ElBuenSabor.ProyectoFinal.Exceptions.ResourceNotFoundException; // Todavía útil si findById no viene del padre
import ElBuenSabor.ProyectoFinal.Repositories.ArticuloManufacturadoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // Importar Transactional

import java.util.List; // Importar List

@Service
public class ArticuloManufacturadoServiceImpl extends BaseServiceImpl<ArticuloManufacturado, Long> implements ArticuloManufacturadoService {

    private final ArticuloManufacturadoRepository articuloManufacturadoRepository;

    public ArticuloManufacturadoServiceImpl(ArticuloManufacturadoRepository articuloManufacturadoRepository) {
        super(articuloManufacturadoRepository);
        this.articuloManufacturadoRepository = articuloManufacturadoRepository;
    }

    @Override
    @Transactional
    public ArticuloManufacturado update(Long id, ArticuloManufacturado updated) throws Exception {
        try {
            ArticuloManufacturado actual = findById(id);

            actual.setDenominacion(updated.getDenominacion());
            actual.setPrecioVenta(updated.getPrecioVenta());
            actual.setDescripcion(updated.getDescripcion());
            actual.setTiempoEstimadoMinutos(updated.getTiempoEstimadoMinutos());
            actual.setPreparacion(updated.getPreparacion());
            actual.setCategoria(updated.getCategoria());
            actual.setUnidadMedida(updated.getUnidadMedida());
            actual.setImagen(updated.getImagen());

            actual.getDetalles().clear();

            if (updated.getDetalles() != null) {
                for (ArticuloManufacturadoDetalle detalle : updated.getDetalles()) {
                    detalle.setArticuloManufacturado(actual);
                    actual.getDetalles().add(detalle);
                }
            }

            return baseRepository.save(actual);
        } catch (Exception e) {
            throw new Exception("Error al actualizar el artículo manufacturado: " + e.getMessage());
        }
    }

    @Override
    public List<ArticuloManufacturado> filtrar(Long categoriaId, String denominacion, Boolean baja) {
        return articuloManufacturadoRepository.filtrar(categoriaId, denominacion, baja);
    }
}
