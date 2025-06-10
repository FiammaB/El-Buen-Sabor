package ElBuenSabor.ProyectoFinal.Mapper;

import ElBuenSabor.ProyectoFinal.DTO.ArticuloInsumoDTO;
import ElBuenSabor.ProyectoFinal.Entities.ArticuloInsumo;

public interface ArticuloInsumoMapper {

    ArticuloInsumoDTO articuloInsumoToArticuloInsumoFullDTO(ArticuloInsumo articuloInsumo);
    ArticuloInsumo articuloInsumoFullDtoToArticuloInsumo(ArticuloInsumoDTO articuloInsumo);

}
