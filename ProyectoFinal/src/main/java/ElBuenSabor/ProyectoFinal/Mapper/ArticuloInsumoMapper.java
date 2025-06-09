package ElBuenSabor.ProyectoFinal.Mapper;

import ElBuenSabor.ProyectoFinal.Entities.ArticuloInsumo;

public interface ArticuloInsumoMapper {

    ArticuloInsumoFullDTO articuloInsumoToArticuloInsumoFullDTO(ArticuloInsumo articuloInsumo);
    ArticuloInsumo articuloInsumoFullDtoToArticuloInsumo(ArticuloInsumo articuloInsumo);

    ArticuloInsumoShortDTO articuloInsumoToArticuloInsumoShortDTO(ArticuloInsumo articuloInsumo);

}
