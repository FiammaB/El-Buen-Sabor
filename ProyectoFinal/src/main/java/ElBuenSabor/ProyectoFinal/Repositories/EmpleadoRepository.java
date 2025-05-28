package ElBuenSabor.ProyectoFinal.Repositories;

import ElBuenSabor.ProyectoFinal.Entities.Empleado;
import ElBuenSabor.ProyectoFinal.Entities.Rol; // Para buscar por rol
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmpleadoRepository extends JpaRepository<Empleado, Long> {

    // Métodos afectados por @Where(clause="baja=false") en la entidad Empleado
    Optional<Empleado> findByEmail(String email);
    boolean existsByEmail(String email);
    List<Empleado> findByRol(Rol rol);

    // Para buscar incluyendo los 'baja = true'
    @Query("SELECT e FROM Empleado e WHERE e.id = :id")
    Optional<Empleado> findByIdRaw(@Param("id") Long id);

    @Query("SELECT e FROM Empleado e")
    List<Empleado> findAllRaw();

    @Query("SELECT e FROM Empleado e WHERE e.email = :email")
    Optional<Empleado> findByEmailRaw(@Param("email") String email);

    @Query("SELECT CASE WHEN COUNT(e) > 0 THEN true ELSE false END FROM Empleado e WHERE e.email = :email")
    boolean existsByEmailRaw(@Param("email") String email);

    @Query("SELECT e FROM Empleado e WHERE e.rol = :rol")
    List<Empleado> findByRolRaw(@Param("rol") Rol rol);
}
