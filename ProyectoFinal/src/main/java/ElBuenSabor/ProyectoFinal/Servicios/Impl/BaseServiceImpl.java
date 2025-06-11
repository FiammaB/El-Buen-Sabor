package ElBuenSabor.ProyectoFinal.Servicios.Impl;
import ElBuenSabor.ProyectoFinal.Entities.BaseEntity;
import ElBuenSabor.ProyectoFinal.Repositories.BaseRepository;
import ElBuenSabor.ProyectoFinal.Servicios.BaseService;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

public abstract class BaseServiceImpl<E extends BaseEntity, ID extends Serializable> implements BaseService<E, ID> {

    protected BaseRepository<E, ID> baseRepository;

    public BaseServiceImpl(BaseRepository<E, ID> baseRepository) {
        this.baseRepository = baseRepository;
    }

    @Override
    @Transactional
    public List<E> findAll() throws Exception {
        try {
            return baseRepository.getAll();
        } catch (Exception e) {
            throw new Exception("Error al obtener todos los registros.", e);
        }
    }

    @Override
    @Transactional
    public E findById(ID id) throws Exception {
        try {
            return baseRepository.getById(id);
        } catch (Exception e) {
            throw new Exception("Error al obtener el registro por ID.", e);
        }
    }

    @Override
    @Transactional
    public E save(E entity) throws Exception {
        try {
            return baseRepository.save(entity);
        } catch (Exception e) {
            throw new Exception("Error al guardar el registro.", e);
        }
    }

    @Override
    @Transactional
    public E update(ID id, E entity) throws Exception {
        try {
            if (!baseRepository.existsById(id)) {
                throw new Exception("No se encontró el registro con ID: " + id);
            }
            return baseRepository.save(entity);
        } catch (Exception e) {
        throw new Exception("Error al actualizar el registro.", e);}
    }

    @Override
    @Transactional
    public boolean delete(ID id) throws Exception {
        try {
            Optional<E> entityOptional = baseRepository.findById(id);
            if (entityOptional.isPresent()) {
                baseRepository.delete(entityOptional.get());
                return true;
            } else {
                throw new Exception("No se encontró el registro con ID: " + id);
            }
        } catch (Exception e) {
            throw new Exception("Error al eliminar el registro.", e);
        }
    }


}