package ElBuenSabor.ProyectoFinal.Repositories;

import ElBuenSabor.ProyectoFinal.Entities.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    // Afectados por @Where(clause="baja=false")
    Optional<Cliente> findByEmail(String email); // Cambiado a Optional
    boolean existsByEmail(String email);

    // Para buscar incluyendo los 'baja = true'
    @Query("SELECT c FROM Cliente c WHERE c.id = :id")
    Optional<Cliente> findByIdRaw(@Param("id") Long id);

    @Query("SELECT c FROM Cliente c")
    List<Cliente> findAllRaw();

    @Query("SELECT c FROM Cliente c WHERE c.email = :email")
    Optional<Cliente> findByEmailRaw(@Param("email") String email);

    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END FROM Cliente c WHERE c.email = :email")
    boolean existsByEmailRaw(@Param("email") String email);

    // Para verificar si un domicilio está en uso por un cliente activo
    // (si Cliente tiene una colección de domicilios)
    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END FROM Cliente c JOIN c.domicilios d WHERE d.id = :domicilioId AND c.baja = false")
    boolean existsActiveClienteWithDomicilio(@Param("domicilioId") Long domicilioId);

    // Para verificar si una imagen está en uso por un cliente activo
    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END FROM Cliente c WHERE c.imagen.id = :imagenId AND c.baja = false")
    boolean countByImagenIdAndBajaFalse(@Param("imagenId") Long imagenId); // Renombrado para claridad, devuelve boolean
}
