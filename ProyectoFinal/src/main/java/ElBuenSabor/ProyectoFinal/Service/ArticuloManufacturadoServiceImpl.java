package ElBuenSabor.ProyectoFinal.Service;

import ElBuenSabor.ProyectoFinal.Entities.ArticuloManufacturado;
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
    public ArticuloManufacturado update(Long id, ArticuloManufacturado updated) throws Exception { // <<-- Añadir throws Exception
        try {
            // Usamos findById del padre (BaseServiceImpl) para obtener la entidad actual
            ArticuloManufacturado actual = findById(id);

            actual.setDenominacion(updated.getDenominacion());
            actual.setPrecioVenta(updated.getPrecioVenta());
            actual.setDescripcion(updated.getDescripcion());
            actual.setTiempoEstimadoMinutos(updated.getTiempoEstimadoMinutos());
            actual.setPreparacion(updated.getPreparacion());
            actual.setCategoria(updated.getCategoria());
            actual.setUnidadMedida(updated.getUnidadMedida());
            actual.setImagen(updated.getImagen());

            // Lógica específica para manejar la colección 'detalles'
            // Limpiamos los detalles existentes y añadimos los nuevos para asegurar la sincronización
            if (updated.getDetalles() != null) {
                actual.getDetalles().clear(); // Limpia los detalles existentes
                updated.getDetalles().forEach(detalle -> {
                    detalle.setArticuloManufacturado(actual); // Asegura la relación inversa
                    actual.getDetalles().add(detalle);
                });
            }

            // Llamamos a save del baseRepository (heredado del padre) para persistir los cambios
            return baseRepository.save(actual);
        } catch (Exception e) {
            // Re-lanzamos cualquier excepción, manteniendo la consistencia con BaseService.
            throw new Exception("Error al actualizar el artículo manufacturado: " + e.getMessage());
        }
    }
}