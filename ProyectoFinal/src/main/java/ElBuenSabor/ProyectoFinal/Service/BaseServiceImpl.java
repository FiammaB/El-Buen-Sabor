package ElBuenSabor.ProyectoFinal.Service;

// ProyectoFinal/src/main/java/ElBuenSabor/ProyectoFinal/Service/BaseServiceImpl.java

import ElBuenSabor.ProyectoFinal.Entities.BaseEntity;
import ElBuenSabor.ProyectoFinal.Exceptions.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // Importar Transactional

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

@Service // Aunque es abstracta, Spring puede gestionarla para inyección
public abstract class BaseServiceImpl<E extends BaseEntity, ID extends Serializable> implements BaseService<E, ID> {

    protected JpaRepository<E, ID> baseRepository; // Repositorio genérico

    public BaseServiceImpl(JpaRepository<E, ID> baseRepository) {//me sigue apareciendo error q tendria q cambiar, tendria q hacer un base repository?Could not autowire. No beans of 'JpaRepository<E, ID>' type found.
        this.baseRepository = baseRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<E> findAll() throws Exception {
        try {
            return baseRepository.findAll();
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public E findById(ID id) throws Exception {
        try {
            Optional<E> entityOptional = baseRepository.findById(id);
            return entityOptional.orElseThrow(() -> new ResourceNotFoundException("Entidad no encontrada con ID: " + id));
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    @Override
    @Transactional
    public E save(E entity) throws Exception {
        try {
            // Por defecto, al guardar una nueva entidad, no debería estar dada de baja.
            // Si la entidad ya tiene un ID, es una actualización y su estado 'baja' se mantiene.
            if (entity.getId() == null) {
                entity.setBaja(false); // Asegúrate de que los nuevos registros no estén dados de baja
            }
            return baseRepository.save(entity);
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    @Override
    @Transactional
    public E update(ID id, E entity) throws Exception {
        try {
            E existingEntity = findById(id); // Usa findById para asegurar que exista
            entity.setId((Long)id); //
            entity.setBaja(existingEntity.getBaja()); // Mantener el estado de baja existente
            return baseRepository.save(entity);
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    @Override
    @Transactional
    public void deleteById(ID id) throws Exception {
        try {
            // Este método hace un borrado físico.
            // Para borrado lógico, usar toggleBaja.
            baseRepository.deleteById(id);
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    @Override
    @Transactional
    public E toggleBaja(ID id, boolean estaDadoDeBaja) throws Exception {
        try {
            E entity = findById(id);
            entity.setBaja(estaDadoDeBaja);
            return baseRepository.save(entity);
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }
}
