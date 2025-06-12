package ElBuenSabor.ProyectoFinal.Repositories;

import ElBuenSabor.ProyectoFinal.Entities.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoriaRepository extends JpaRepository<Categoria, Long> {
    List<Categoria> findBySucursalesId(Long sucursalId); // Para obtener categorías de una sucursal específica
    List<Categoria> findByCategoriaPadreIsNull(); // Para obtener categorías de nivel superior
}
