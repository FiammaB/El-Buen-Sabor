package ElBuenSabor.ProyectoFinal.DTO;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DetallePedidoCreateDTO {

    private Integer cantidad;
    private Double subTotal;
    private Long articuloId;
}