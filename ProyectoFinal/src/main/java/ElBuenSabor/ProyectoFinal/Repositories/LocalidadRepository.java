package ElBuenSabor.ProyectoFinal.Repositories;

import ElBuenSabor.ProyectoFinal.Entities.Localidad;
import ElBuenSabor.ProyectoFinal.Entities.Provincia; // Importar Provincia
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LocalidadRepository extends JpaRepository<Localidad, Long> {
    Localidad findByNombre(String nombre); // Afectado por @Where

    // Para buscar incluyendo los 'baja = true'
    @Query("SELECT loc FROM Localidad loc WHERE loc.nombre = :nombre")
    Optional<Localidad> findByNombreRaw(@Param("nombre") String nombre);

    @Query("SELECT loc FROM Localidad loc WHERE loc.id = :id")
    Optional<Localidad> findByIdRaw(@Param("id") Long id);

    @Query("SELECT loc FROM Localidad loc")
    List<Localidad> findAllRaw();

    // Para buscar localidades de una provincia (activas por defecto debido a @Where en Localidad)
    List<Localidad> findByProvincia(Provincia provincia);
    List<Localidad> findByProvinciaId(Long provinciaId);

    // Para buscar localidades de una provincia incluyendo las dadas de baja
    @Query("SELECT loc FROM Localidad loc WHERE loc.provincia.id = :provinciaId")
    List<Localidad> findByProvinciaIdRaw(@Param("provinciaId") Long provinciaId);

    // Para la validación en SucursalServiceImpl (crearOEncontrarLocalidadPorNombre)
    Localidad findByNombreAndProvincia(String nombre, Provincia provincia); // Afectado por @Where

    @Query("SELECT loc FROM Localidad loc WHERE loc.nombre = :nombre AND loc.provincia = :provincia")
    Optional<Localidad> findByNombreAndProvinciaRaw(@Param("nombre") String nombre, @Param("provincia") Provincia provincia);
}