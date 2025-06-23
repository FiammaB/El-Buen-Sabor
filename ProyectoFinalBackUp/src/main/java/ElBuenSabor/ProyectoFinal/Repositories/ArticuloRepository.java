package ElBuenSabor.ProyectoFinal.Repositories;

import ElBuenSabor.ProyectoFinal.Entities.Articulo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ArticuloRepository extends JpaRepository<Articulo, Long> {
    // Métodos de consulta para el buscador de productos [cite: 94, 95, 104]
    List<Articulo> findByDenominacionContainingIgnoreCase(String denominacion);
    List<Articulo> findByCategoriaId(Long categoriaId); // Para navegar por categorías [cite: 91]
    // Métodos para borrado lógico: filtrar por estaDadoDeBaja=false
    List<Articulo> findByEstaDadoDeBajaFalse();
    List<Articulo> findByDenominacionContainingIgnoreCaseAndEstaDadoDeBajaFalse(String denominacion);
    List<Articulo> findByCategoriaIdAndEstaDadoDeBajaFalse(Long categoriaId);
}
