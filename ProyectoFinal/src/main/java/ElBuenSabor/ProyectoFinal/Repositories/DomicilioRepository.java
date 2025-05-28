package ElBuenSabor.ProyectoFinal.Repositories;

import ElBuenSabor.ProyectoFinal.Entities.Domicilio;
import ElBuenSabor.ProyectoFinal.Entities.Localidad; // Para buscar por localidad
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DomicilioRepository extends JpaRepository<Domicilio, Long> {

    // Para buscar incluyendo los 'baja = true'
    @Query("SELECT d FROM Domicilio d WHERE d.id = :id")
    Optional<Domicilio> findByIdRaw(@Param("id") Long id);

    @Query("SELECT d FROM Domicilio d")
    List<Domicilio> findAllRaw();

    // Métodos para verificar si un domicilio está en uso (activos)
    // (Estos ya estarían filtrados por @Where en Pedido si se accede desde PedidoRepository)
    // Si se necesita una query directa desde DomicilioRepository:
    @Query("SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END FROM Pedido p WHERE p.domicilioEntrega.id = :domicilioId AND p.baja = false")
    boolean existsActivePedidoWithDomicilio(@Param("domicilioId") Long domicilioId);

    // Para listar domicilios activos de una localidad (afectado por @Where en Domicilio)
    List<Domicilio> findByLocalidadAndBajaFalse(Localidad localidad); // O findByLocalidad(Localidad localidad)
    List<Domicilio> findByLocalidadIdAndBajaFalse(Long localidadId); // O findByLocalidadId(Long localidadId)

    // Para listar TODOS los domicilios de una localidad (incluyendo los 'baja = true')
    @Query("SELECT d FROM Domicilio d WHERE d.localidad.id = :localidadId")
    List<Domicilio> findByLocalidadIdRaw(@Param("localidadId") Long localidadId);
}
