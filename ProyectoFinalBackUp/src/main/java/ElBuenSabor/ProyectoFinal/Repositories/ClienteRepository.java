package ElBuenSabor.ProyectoFinal.Repositories;



import ElBuenSabor.ProyectoFinal.Entities.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    // Spring Data JPA automáticamente implementará métodos CRUD básicos para Cliente
    // Podemos añadir métodos de consulta personalizados aquí si los necesitamos más adelante.
    Cliente findByEmail(String email); // Para buscar un cliente por su email
    boolean existsByEmail(String email); // Para verificar si un email ya existe
}
