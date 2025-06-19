package ElBuenSabor.ProyectoFinal.Mappers;

import ElBuenSabor.ProyectoFinal.DTO.ArticuloManufacturadoDetalleCreateDTO;
import ElBuenSabor.ProyectoFinal.DTO.ArticuloManufacturadoDetalleDTO;
import ElBuenSabor.ProyectoFinal.Entities.ArticuloInsumo;
import ElBuenSabor.ProyectoFinal.Entities.ArticuloManufacturadoDetalle;
import ElBuenSabor.ProyectoFinal.Repositories.ArticuloInsumoRepository;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", uses = ArticuloInsumoMapper.class)
public interface ArticuloManufacturadoDetalleMapper {

    ArticuloManufacturadoDetalleDTO toDTO(ArticuloManufacturadoDetalle entity);

    @Mapping(target = "articuloManufacturado", ignore = true)
    @Mapping(target = "articuloInsumo", source = "articuloInsumoId")
    ArticuloManufacturadoDetalle toEntity(ArticuloManufacturadoDetalleCreateDTO dto, @Context ArticuloInsumoRepository articuloInsumoRepo);

    default ArticuloInsumo map(Long id, @Context ArticuloInsumoRepository articuloInsumoRepo) {
        return id == null ? null : articuloInsumoRepo.findById(id).orElse(null);
    }

}
