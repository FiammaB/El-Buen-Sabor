package ElBuenSabor.ProyectoFinal.Repositories;

import ElBuenSabor.ProyectoFinal.Entities.Pais;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Optional;


@Repository
public interface PaisRepository extends JpaRepository<Pais, Long> {
    Pais findByNombre(String nombre); // Afectado por @Where

    // Para buscar incluyendo los 'baja = true', necesario para validaciones de unicidad o vistas de admin
    @Query("SELECT p FROM Pais p WHERE p.nombre = :nombre")
    Optional<Pais> findByNombreRaw(@Param("nombre") String nombre);

    @Query("SELECT p FROM Pais p WHERE p.id = :id")
    Optional<Pais> findByIdRaw(@Param("id") Long id); // Para reactivar

    // No necesitas un findAllRaw si el servicio puede filtrar una lista completa,
    // o si el admin siempre ve una lista paginada.
    // Si se necesita:
    // @Query("SELECT p FROM Pais p")
    // List<Pais> findAllRaw();
}