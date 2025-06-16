package ElBuenSabor.ProyectoFinal.DTO;

import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class ArticuloManufacturadoCreateDTO extends ArticuloDTO {

    private String denominacion;
    private Double precioVenta;
    private Long imagenId;
    private Long unidadMedidaId;
    private Long categoriaId;

    private String descripcion;
    private Integer tiempoEstimadoMinutos;
    private String preparacion;

    private List<ArticuloManufacturadoDetalleCreateDTO> detalles;
}
