package ElBuenSabor.ProyectoFinal.Mapper;


import ElBuenSabor.ProyectoFinal.DTO.SucursalDTO;
import ElBuenSabor.ProyectoFinal.Entities.Sucursal;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface SucursalMapper extends BaseMapper<Sucursal, SucursalDTO> {

    SucursalDTO sucursalToSucursalDto(Sucursal sucursal);
    Sucursal sucursalDtoToSucursal(SucursalDTO sucursalDTO);

    List<SucursalDTO> sucursalToSucursalDtoList(List<Sucursal> sucursal);
    List<Sucursal> sucursalDtoListToSucursalList(List<SucursalDTO> sucursalDTO);

}
