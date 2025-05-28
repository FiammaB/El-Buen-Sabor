package ElBuenSabor.ProyectoFinal.Repositories;

import ElBuenSabor.ProyectoFinal.Entities.Empresa; // Para buscar por empresa
import ElBuenSabor.ProyectoFinal.Entities.Sucursal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SucursalRepository extends JpaRepository<Sucursal, Long> {

    // Afectados por @Where(clause="baja=false")
    List<Sucursal> findByEmpresa(Empresa empresa);
    List<Sucursal> findByEmpresaId(Long empresaId);
    Optional<Sucursal> findByNombreAndEmpresaId(String nombre, Long empresaId); // Para unicidad de nombre dentro de una empresa

    // Para buscar incluyendo los 'baja = true'
    @Query("SELECT s FROM Sucursal s WHERE s.id = :id")
    Optional<Sucursal> findByIdRaw(@Param("id") Long id);

    @Query("SELECT s FROM Sucursal s")
    List<Sucursal> findAllRaw();

    @Query("SELECT s FROM Sucursal s WHERE s.empresa.id = :empresaId")
    List<Sucursal> findByEmpresaIdRaw(@Param("empresaId") Long empresaId);

    @Query("SELECT s FROM Sucursal s WHERE s.nombre = :nombre AND s.empresa.id = :empresaId")
    Optional<Sucursal> findByNombreAndEmpresaIdRaw(@Param("nombre") String nombre, @Param("empresaId") Long empresaId);

    // Para verificar si una empresa tiene sucursales activas
    boolean existsByEmpresaIdAndBajaFalse(Long empresaId);

    // Para verificar si un domicilio está en uso por una sucursal activa
    @Query("SELECT CASE WHEN COUNT(s) > 0 THEN true ELSE false END FROM Sucursal s WHERE s.domicilio.id = :domicilioId AND s.baja = false")
    boolean existsActiveSucursalWithDomicilio(@Param("domicilioId") Long domicilioId);
}
