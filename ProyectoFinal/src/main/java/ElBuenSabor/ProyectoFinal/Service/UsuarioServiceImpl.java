package ElBuenSabor.ProyectoFinal.Service;

import ElBuenSabor.ProyectoFinal.Entities.Usuario;
import ElBuenSabor.ProyectoFinal.Exceptions.ResourceNotFoundException; // Posiblemente ya no sea necesaria
import ElBuenSabor.ProyectoFinal.Repositories.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // Importar Transactional

import java.util.List; // Importar List si no se usara el findAll del padre

@Service
// UsuarioServiceImpl ahora extiende BaseServiceImpl
// y la interfaz UsuarioService (que debe extender BaseService)
public class UsuarioServiceImpl extends BaseServiceImpl<Usuario, Long> implements UsuarioService {

    // El repositorio se inyecta y se pasa al constructor del padre.
    // Ya no necesitas 'private final UsuarioRepository usuarioRepository;' aquí
    public UsuarioServiceImpl(UsuarioRepository usuarioRepository) {
        super(usuarioRepository); // Llama al constructor de la clase base
    }

    // Los métodos findAll(), findById(), save(), deleteById(), toggleBaja()
    // ya están implementados en BaseServiceImpl y se heredan automáticamente.

    @Override
    @Transactional // Asegúrate de que los métodos de modificación sean transaccionales
    // La firma debe ser consistente con BaseService: update(ID id, E entity)
    public Usuario update(Long id, Usuario updatedUsuario) throws Exception { // <<-- Añadir throws Exception
        try {
            // Usamos findById del padre (BaseServiceImpl) para obtener la entidad actual
            Usuario existing = findById(id);

            existing.setAuth0Id(updatedUsuario.getAuth0Id());
            existing.setUsername(updatedUsuario.getUsername());
            // Si Usuario tiene colecciones o otras relaciones,
            // necesitarías lógica adicional aquí para sincronizarlas.

            // Llamamos a save del baseRepository (heredado del padre) para persistir los cambios
            return baseRepository.save(existing);
        } catch (Exception e) {
            // Re-lanzamos cualquier excepción, manteniendo la consistencia con BaseService.
            throw new Exception("Error al actualizar el usuario: " + e.getMessage());
        }
    }
}