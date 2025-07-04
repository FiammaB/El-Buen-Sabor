package ElBuenSabor.ProyectoFinal.Service;

import ElBuenSabor.ProyectoFinal.Entities.BaseEntity;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

public interface BaseService<E extends BaseEntity, ID extends Serializable> {
  ;
  List<E> findAll() throws Exception; // Obtener todos los registros
  Optional<E> findById(ID id) throws Exception; // Obtener un registro por ID
  E save(E entity) throws Exception; // Guardar un nuevo registro o actualizar uno existente
  E update(ID id, E entity) throws Exception; // Actualizar un registro por ID
  boolean delete(ID id) throws Exception; // Eliminar un registro por ID (borrado físico o lógico)

  // Método para verificar la existencia por ID (útil, aunque JpaRepository ya lo tiene)
  boolean existsById(ID id) throws Exception;
}