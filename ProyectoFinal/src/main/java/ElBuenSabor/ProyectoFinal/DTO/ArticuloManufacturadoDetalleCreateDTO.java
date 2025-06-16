package ElBuenSabor.ProyectoFinal.DTO;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ArticuloManufacturadoDetalleCreateDTO {

    private Double cantidad;
    private Long articuloInsumoId;
}
