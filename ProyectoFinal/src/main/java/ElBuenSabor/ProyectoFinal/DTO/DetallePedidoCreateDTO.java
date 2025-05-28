package ElBuenSabor.ProyectoFinal.DTO;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DetallePedidoCreateDTO {

    @NotNull(message = "La cantidad es obligatoria")
    @Min(value = 1, message = "La cantidad debe ser al menos 1")
    private Integer cantidad;

    // Solo uno de estos dos debe ser provisto
    private Long articuloManufacturadoId;
    private Long articuloInsumoId;

    // Validacion a nivel de clase para asegurar que solo uno de los IDs de artículo se provea
    // Esto se haría con una anotación de constraint personalizada o en la lógica del servicio.
    // Ejemplo: @AssertTrue(message = "Debe especificar un artículo manufacturado O un insumo, pero no ambos ni ninguno")
    // public boolean isArticuloExclusive() {
    //     return (articuloManufacturadoId != null ^ articuloInsumoId != null);
    // }
}
