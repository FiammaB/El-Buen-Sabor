package ElBuenSabor.ProyectoFinal.Repositories;

import ElBuenSabor.ProyectoFinal.Entities.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> { // ID es Long

    // Afectado por @Where en las entidades hijas (Cliente, Empleado)
    Optional<Usuario> findByUsername(String username);
    boolean existsByUsername(String username);

    // Para buscar incluyendo los 'baja = true'
    @Query("SELECT u FROM Usuario u WHERE u.id = :id")
    Optional<Usuario> findByIdRaw(@Param("id") Long id);

    @Query("SELECT u FROM Usuario u")
    List<Usuario> findAllRaw();

    @Query("SELECT u FROM Usuario u WHERE u.username = :username")
    Optional<Usuario> findByUsernameRaw(@Param("username") String username);

    @Query("SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END FROM Usuario u WHERE u.username = :username")
    boolean existsByUsernameRaw(@Param("username") String username);

    // Para buscar por auth0Id (si se implementa login social)
    Optional<Usuario> findByAuth0Id(String auth0Id); // Afectado por @Where

    @Query("SELECT u FROM Usuario u WHERE u.auth0Id = :auth0Id")
    Optional<Usuario> findByAuth0IdRaw(@Param("auth0Id") String auth0Id);
}
