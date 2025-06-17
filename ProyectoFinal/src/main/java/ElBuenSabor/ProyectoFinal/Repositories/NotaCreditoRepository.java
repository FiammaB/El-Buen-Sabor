package ElBuenSabor.ProyectoFinal.Repositories;


import ElBuenSabor.ProyectoFinal.Entities.NotaCredito;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NotaCreditoRepository extends JpaRepository<NotaCredito, Long> {
}