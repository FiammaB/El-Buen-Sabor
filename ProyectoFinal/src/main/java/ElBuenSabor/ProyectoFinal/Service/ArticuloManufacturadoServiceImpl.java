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

    public ArticuloManufacturadoServiceImpl(ArticuloManufacturadoRepository articuloManufacturadoRepository) {
        super(articuloManufacturadoRepository); // Llama al constructor de la clase base
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

            // Limpiar detalles y agregar los nuevos
            System.out.println("Detalles antes de limpiar: " + actual.getDetalles().size());
            actual.getDetalles().clear();
            System.out.println("Detalles después de limpiar y antes de agregar nuevos: " + actual.getDetalles().size());

            if (updated.getDetalles() != null) {
                for (ArticuloManufacturadoDetalle detalle : updated.getDetalles()) {
                    detalle.setArticuloManufacturado(actual); // Relación inversa
                    actual.getDetalles().add(detalle);
                }
            }
            System.out.println("Detalles después de agregar nuevos: " + actual.getDetalles().size());

            return baseRepository.save(actual);
        } catch (Exception e) {
            throw new Exception("Error al actualizar el artículo manufacturado: " + e.getMessage());
        }
    }
}