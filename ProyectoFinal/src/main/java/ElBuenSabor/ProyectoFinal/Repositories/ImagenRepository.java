package ElBuenSabor.ProyectoFinal.Repositories;

import ElBuenSabor.ProyectoFinal.Entities.Imagen;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ImagenRepository extends JpaRepository<Imagen, Long> {

    // Opcional: si necesitas buscar imágenes por su denominación (URL/path)
    // Imagen findByDenominacion(String denominacion); // Afectado por @Where

    // Para buscar incluyendo los 'baja = true'
    @Query("SELECT img FROM Imagen img WHERE img.denominacion = :denominacion")
    Optional<Imagen> findByDenominacionRaw(@Param("denominacion") String denominacion);

    @Query("SELECT img FROM Imagen img WHERE img.id = :id")
    Optional<Imagen> findByIdRaw(@Param("id") Long id);

    @Query("SELECT img FROM Imagen img")
    List<Imagen> findAllRaw();
}
