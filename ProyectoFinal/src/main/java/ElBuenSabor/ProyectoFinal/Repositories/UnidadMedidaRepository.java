package ElBuenSabor.ProyectoFinal.Repositories;

import ElBuenSabor.ProyectoFinal.Entities.UnidadMedida;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List; // Importar List
import java.util.Optional; // Importar Optional

@Repository
public interface UnidadMedidaRepository extends JpaRepository<UnidadMedida, Long> {
    // Este método se verá afectado por @Where(clause="baja=false") en la entidad UnidadMedida
    UnidadMedida findByDenominacion(String denominacion);

    // Para buscar incluyendo los 'baja = true', útil para validaciones de unicidad o vistas de admin
    @Query("SELECT um FROM UnidadMedida um WHERE um.denominacion = :denominacion")
    Optional<UnidadMedida> findByDenominacionRaw(@Param("denominacion") String denominacion);

    @Query("SELECT um FROM UnidadMedida um WHERE um.id = :id")
    Optional<UnidadMedida> findByIdRaw(@Param("id") Long id);

    @Query("SELECT um FROM UnidadMedida um")
    List<UnidadMedida> findAllRaw();
}
