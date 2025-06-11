package ElBuenSabor.ProyectoFinal.Mapper;

import ElBuenSabor.ProyectoFinal.DTO.ArticuloInsumoDTO;
import ElBuenSabor.ProyectoFinal.Entities.ArticuloInsumo;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ArticuloInsumoMapper extends BaseMapper <ArticuloInsumo, ArticuloInsumoDTO> {

    ArticuloInsumoDTO articuloInsumoToArticuloInsumoDto(ArticuloInsumo articuloInsumo);
    ArticuloInsumo articuloInsumoDtoToArticuloInsumo(ArticuloInsumoDTO articuloInsumoDTO);

    List<ArticuloInsumoDTO> articuloInsumotoToArticuloInsumoDtoList(List<ArticuloInsumo> articulos);
    List<ArticuloInsumo> articuloInsumoDtoToArticuloInsumoList(List<ArticuloInsumoDTO> articulosDTO);

}
