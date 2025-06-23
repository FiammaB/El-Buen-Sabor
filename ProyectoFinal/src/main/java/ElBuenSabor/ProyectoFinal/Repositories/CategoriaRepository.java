package ElBuenSabor.ProyectoFinal.Repositories;

import ElBuenSabor.ProyectoFinal.Entities.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoriaRepository extends JpaRepository<Categoria, Long> {

    // Modificado: Encontrar categorías raíz asociadas a una sucursal específica
    // Dado que Categoria tiene una relación ManyToMany con Sucursal, la consulta es diferente.
    @Query("SELECT c FROM Categoria c JOIN c.sucursales s WHERE s.id = :sucursalId AND c.categoriaPadre IS NULL")
    List<Categoria> findBySucursalesIdAndCategoriaPadreIsNull(@Param("sucursalId") Long sucursalId);

    // Nuevo método: Encontrar todas las categorías asociadas a una sucursal específica (raíz y subcategorías)
    @Query("SELECT c FROM Categoria c JOIN c.sucursales s WHERE s.id = :sucursalId")
    List<Categoria> findBySucursalesId(@Param("sucursalId") Long sucursalId);
}
