package ElBuenSabor.ProyectoFinal.Service;
import ElBuenSabor.ProyectoFinal.Entities.BaseEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

public abstract class BaseServiceImpl<E extends BaseEntity, ID extends Serializable> implements BaseService<E, ID> {

    protected JpaRepository<E, ID> baseRepository;

    public BaseServiceImpl(JpaRepository<E, ID> baseRepository) {
        this.baseRepository = baseRepository;
    }



    @Override
    public boolean existsById(ID id) throws Exception {
        try {
            return baseRepository.existsById(id);
        } catch (Exception e) {
            throw new Exception("Error al verificar existencia por ID: " + id, e);
        }
    }



    @Override
    @Transactional
    public E save(E entity) throws Exception {
        try {
            return baseRepository.save(entity);
        } catch (Exception e) {
            throw new Exception("Error al guardar el registro: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public E update(ID id, E entity) throws Exception {
        try {
            Optional<E> entityOptional = baseRepository.findById(id);
            if (entityOptional.isPresent()) {
                E existingEntity = entityOptional.get();
                // Aquí puedes copiar propiedades específicas si necesitas
                // Aunque para update completo, baseRepository.save(entity) suele funcionar si el ID está presente
                // Para un update parcial o selectivo, tendrías que sobrescribir este método en subclases
                return baseRepository.save(entity);
            } else {
                throw new Exception("No se encontró el registro con ID: " + id);
            }
        } catch (Exception e) {
            throw new Exception("Error al actualizar el registro: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public boolean delete(ID id) throws Exception {
        try {
            if (baseRepository.existsById(id)) {
                baseRepository.deleteById(id);
                return true;
            } else {
                throw new Exception("No se encontró el registro con ID: " + id + " para eliminar.");
            }
        } catch (Exception e) {
            throw new Exception("Error al eliminar el registro: " + e.getMessage());
        }
    }


}