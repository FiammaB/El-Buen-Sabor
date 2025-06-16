package ElBuenSabor.ProyectoFinal.Mappers;

import ElBuenSabor.ProyectoFinal.DTO.SucursalCreateDTO;
import ElBuenSabor.ProyectoFinal.DTO.SucursalDTO;
import ElBuenSabor.ProyectoFinal.Entities.Sucursal;
import org.mapstruct.*;


import java.util.List;

@Mapper(componentModel = "spring")
public interface SucursalMapper {

    SucursalDTO toDTO(Sucursal entity);

    Sucursal toEntity(SucursalDTO dto);

    Sucursal toEntity(SucursalCreateDTO dto);

    List<SucursalDTO> toDTOList(List<Sucursal> sucursales);
}
