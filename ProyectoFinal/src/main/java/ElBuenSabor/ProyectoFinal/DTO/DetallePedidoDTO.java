package ElBuenSabor.ProyectoFinal.DTO;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL) // No mostrar campos nulos (ej. articuloInsumo si es manufacturado)
public class DetallePedidoDTO {
    private Long id;
    private Integer cantidad;
    private Double subTotal;
    private boolean baja; // Heredado, refleja si el detalle fue "anulado" con el pedido

    // Solo uno de estos estará presente en la respuesta, el otro será null
    private ArticuloManufacturadoSimpleDTO articuloManufacturado; // Usar DTO simple
    private ArticuloInsumoSimpleDTO articuloInsumo;           // Usar DTO simple

    // No incluir PedidoDTO aquí para evitar referencias circulares en la respuesta JSON
}
