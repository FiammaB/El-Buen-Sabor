package ElBuenSabor.ProyectoFinal.Repositories;

import ElBuenSabor.ProyectoFinal.Entities.Empresa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmpresaRepository extends JpaRepository<Empresa, Long> {

    // Métodos afectados por @Where(clause="baja=false") en la entidad Empresa
    Optional<Empresa> findByNombre(String nombre);
    Optional<Empresa> findByCuil(Integer cuil); // Asumiendo que cuil es Integer

    // Para buscar incluyendo los 'baja = true'
    @Query("SELECT e FROM Empresa e WHERE e.nombre = :nombre")
    Optional<Empresa> findByNombreRaw(@Param("nombre") String nombre);

    @Query("SELECT e FROM Empresa e WHERE e.cuil = :cuil")
    Optional<Empresa> findByCuilRaw(@Param("cuil") Integer cuil); // Asumiendo cuil Integer

    @Query("SELECT e FROM Empresa e WHERE e.id = :id")
    Optional<Empresa> findByIdRaw(@Param("id") Long id);

    @Query("SELECT e FROM Empresa e")
    List<Empresa> findAllRaw();
}
