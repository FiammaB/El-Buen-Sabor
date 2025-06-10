package ElBuenSabor.ProyectoFinal.Mapper;

import ElBuenSabor.ProyectoFinal.DTO.BaseDTO;
import ElBuenSabor.ProyectoFinal.Entities.BaseEntity;

import java.util.List;


public interface BaseMapper <Entidad extends BaseEntity,Dto extends BaseDTO>{

    Dto toDTO(Entidad source);
    Entidad toEntity(Dto source);
    List<Dto> toDTOsList(List<Entidad> source);
    List<Entidad> toEntitiesList(List<Dto> source);

}
