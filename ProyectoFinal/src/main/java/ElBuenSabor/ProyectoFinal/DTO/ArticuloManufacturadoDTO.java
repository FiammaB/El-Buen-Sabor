package ElBuenSabor.ProyectoFinal.DTO;

import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class ArticuloManufacturadoDTO extends ArticuloDTO {

    private String descripcion;
    private Integer tiempoEstimadoMinutos;
    private String preparacion;

    private List<ArticuloManufacturadoDetalleDTO> detalles;
}
