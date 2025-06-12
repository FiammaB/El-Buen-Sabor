package ElBuenSabor.ProyectoFinal.Repositories;

import ElBuenSabor.ProyectoFinal.Entities.ArticuloManufacturadoDetalle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ArticuloManufacturadoDetalleRepository extends JpaRepository<ArticuloManufacturadoDetalle, Long> {
    // Puedes añadir métodos personalizados aquí si necesitas buscar detalles específicos
    // por ArticuloManufacturado o ArticuloInsumo, por ejemplo:
    // List<ArticuloManufacturadoDetalle> findByArticuloManufacturadoId(Long articuloManufacturadoId);
    // List<ArticuloManufacturadoDetalle> findByArticuloInsumoId(Long articuloInsumoId);
}
