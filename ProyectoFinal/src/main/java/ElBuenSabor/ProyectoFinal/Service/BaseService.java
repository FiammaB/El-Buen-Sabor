package ElBuenSabor.ProyectoFinal.Service;

import ElBuenSabor.ProyectoFinal.Entities.BaseEntity;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

public interface BaseService<E extends BaseEntity, ID extends Serializable> {
    List<E> findAll() throws Exception;
    Optional<E> findById(ID id) throws Exception;
    E save(E entity) throws Exception;
    E update(ID id, E entity) throws Exception;
    boolean delete(ID id) throws Exception;
    boolean existsById(ID id) throws Exception;
}