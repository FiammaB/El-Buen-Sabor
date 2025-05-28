package ElBuenSabor.ProyectoFinal.Repositories;

import ElBuenSabor.ProyectoFinal.Entities.Pais; // Importar Pais
import ElBuenSabor.ProyectoFinal.Entities.Provincia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List; // Importar List
import java.util.Optional; // Importar Optional

@Repository
public interface ProvinciaRepository extends JpaRepository<Provincia, Long> {
    Provincia findByNombre(String nombre); // Afectado por @Where

    // Para buscar incluyendo los 'baja = true'
    @Query("SELECT p FROM Provincia p WHERE p.nombre = :nombre")
    Optional<Provincia> findByNombreRaw(@Param("nombre") String nombre);

    @Query("SELECT p FROM Provincia p WHERE p.id = :id")
    Optional<Provincia> findByIdRaw(@Param("id") Long id);

    // Para buscar provincias de un país (activas por defecto debido a @Where en Provincia)
    List<Provincia> findByPais(Pais pais); // Puedes usar el objeto Pais
    // o por ID del país, que es más común para parámetros de servicio/controlador
    List<Provincia> findByPaisId(Long paisId); // Spring Data JPA infiere la query por el nombre del método

    // Para buscar provincias de un país incluyendo las dadas de baja
    @Query("SELECT p FROM Provincia p WHERE p.pais.id = :paisId")
    List<Provincia> findByPaisIdRaw(@Param("paisId") Long paisId);

    @Query("SELECT p FROM Provincia p") // Esta query trae todas las provincias, sin importar el campo 'baja'
    List<Provincia> findAllRaw();
}