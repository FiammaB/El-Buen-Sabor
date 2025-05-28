package ElBuenSabor.ProyectoFinal.Repositories;

import ElBuenSabor.ProyectoFinal.Entities.ArticuloManufacturadoDetalle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List; // Para otros métodos si los necesitas

@Repository
public interface ArticuloManufacturadoDetalleRepository extends JpaRepository<ArticuloManufacturadoDetalle, Long> {

    // Para verificar si un ArticuloInsumo es parte de algún ArticuloManufacturado ACTIVO
    // Se usa en ArticuloServiceImpl.isArticuloInActiveUse()
    @Query("SELECT CASE WHEN COUNT(amd) > 0 THEN true ELSE false END " +
            "FROM ArticuloManufacturadoDetalle amd " +
            "JOIN amd.articuloManufacturado am " + // Necesitas la relación bidireccional o ajustar la query
            "WHERE amd.articuloInsumo.id = :insumoId AND am.baja = false")
    boolean existsByArticuloInsumoIdAndArticuloManufacturado_BajaFalse(@Param("insumoId") Long insumoId);

    // Si ArticuloManufacturadoDetalle NO tiene un campo 'articuloManufacturado' para el JOIN:
    // Necesitarías una query que una a través de la tabla ArticuloManufacturado.
    // Esto es más complejo si la relación es unidireccional desde ArticuloManufacturado.
    // Una forma sería:
    // @Query("SELECT CASE WHEN COUNT(am) > 0 THEN true ELSE false END " +
    //        "FROM ArticuloManufacturado am JOIN am.detalles detalle " +
    //        "WHERE detalle.articuloInsumo.id = :insumoId AND am.baja = false")
    // boolean isArticuloInsumoUsedInActiveManufacturado(@Param("insumoId") Long insumoId);
    // La primera opción (con JOIN amd.articuloManufacturado) es más limpia si tienes la relación bidireccional.
    // Si mantienes la relación unidireccional desde ArticuloManufacturado (con @JoinColumn en el @OneToMany),
    // la segunda query es más apropiada para el repositorio de ArticuloManufacturadoDetalle,
    // o mejor aún, hacer esta verificación desde ArticuloManufacturadoRepository.
}
