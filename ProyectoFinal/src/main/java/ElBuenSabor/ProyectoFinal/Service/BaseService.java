package ElBuenSabor.ProyectoFinal.Service;

import ElBuenSabor.ProyectoFinal.Entities.BaseEntity;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

public interface BaseService<E extends BaseEntity, ID extends Serializable> {
    List<E> findAll() throws Exception; // Ahora devuelve solo los activos (baja=false) debido a @Where

    boolean existsById(ID id) throws Exception;

    Optional<E> findById(ID id) throws Exception; // Ahora devuelve solo activos (baja=false) debido a @Where

    E save(E entity) throws Exception;
    E update(ID id, E entity) throws Exception;

    // delete ahora implica un borrado lógico gracias a @SQLDelete en la entidad
    boolean delete(ID id) throws Exception;

    // Nuevos métodos para borrado lógico explícito y reactivación
    E softDelete(ID id) throws Exception;
    E reactivate(ID id) throws Exception;

    // Para obtener todos, incluyendo los marcados como 'baja = true'
    List<E> findAllIncludingDeleted() throws Exception;
    Optional<E> findByIdIncludingDeleted(ID id) throws Exception;

    // Opcional: Si alguna vez se necesita un borrado físico (usar con extrema precaución)
    // boolean hardDelete(ID id) throws Exception;
}