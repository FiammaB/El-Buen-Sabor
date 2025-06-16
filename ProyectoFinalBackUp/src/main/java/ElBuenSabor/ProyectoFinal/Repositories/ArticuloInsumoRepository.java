package ElBuenSabor.ProyectoFinal.Repositories;

import ElBuenSabor.ProyectoFinal.Entities.ArticuloInsumo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ArticuloInsumoRepository extends JpaRepository<ArticuloInsumo, Long> {
    // Puedes añadir métodos específicos para insumos si los necesitas (ej. por stock)
    List<ArticuloInsumo> findByStockActualLessThanEqual(Double stockMinimo); // Para el control de stock bajo [cite: 222]
    List<ArticuloInsumo> findByEstaDadoDeBajaFalse(); // ¡AÑADIDO!

}
