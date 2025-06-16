package ElBuenSabor.ProyectoFinal.Service;

import ElBuenSabor.ProyectoFinal.Entities.BaseEntity;

import java.util.List;

public interface BaseService<E extends BaseEntity, ID> { // E es el tipo de Entidad, ID el tipo de ID
    List<E> findAll() throws Exception;
    E findById(ID id) throws Exception;
    E save(E entity) throws Exception;
    E update(ID id, E entity) throws Exception;
    void deleteById(ID id) throws Exception;
    // Nuevo método para manejar el borrado lógico (cambiar estado 'baja')
    E toggleBaja(ID id, boolean estaDadoDeBaja) throws Exception;
}
