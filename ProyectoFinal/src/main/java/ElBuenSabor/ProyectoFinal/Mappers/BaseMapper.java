package ElBuenSabor.ProyectoFinal.Mappers;

import java.util.List;

public interface BaseMapper<E, D> {

    D toDTO(E entity);

    E toEntity(D dto);

    List<D> toDTOList(List<E> entities);

    List<E> toEntityList(List<D> dtos);
}