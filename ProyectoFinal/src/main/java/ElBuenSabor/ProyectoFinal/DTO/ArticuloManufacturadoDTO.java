package ElBuenSabor.ProyectoFinal.DTO;

import lombok.*;

import java.util.HashSet;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ArticuloManufacturadoDTO extends ArticuloDTO {

    private String descripcion;
    private Integer tiempoEstimadoMinutos;
    private String preparacion;

    private Set<ArticuloManufacturadoDetalleDTO> articuloManufacturadoDetalles = new HashSet<>();

}
