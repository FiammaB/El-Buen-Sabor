package ElBuenSabor.ProyectoFinal.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DetallePedidoCreateDTO {
    private Integer cantidad;
    private Long articuloManufacturadoId; // Can be null if it's an insumo
    private Long articuloInsumoId;      // Can be null if it's a manufacturado
    // Add validation: one of them must be non-null
}