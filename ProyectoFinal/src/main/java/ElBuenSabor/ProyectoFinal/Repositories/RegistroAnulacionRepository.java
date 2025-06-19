package ElBuenSabor.ProyectoFinal.Repositories;


import ElBuenSabor.ProyectoFinal.Entities.RegistroAnulacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RegistroAnulacionRepository extends JpaRepository<RegistroAnulacion, Long> {
}