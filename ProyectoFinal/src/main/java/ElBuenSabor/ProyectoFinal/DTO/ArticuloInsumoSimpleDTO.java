package ElBuenSabor.ProyectoFinal.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ArticuloInsumoSimpleDTO {
    private Long id;
    private String denominacion;
    private Double precioVenta; // Precio unitario para el detalle
    private boolean baja;
    private UnidadMedidaDTO unidadMedida; // Para mostrar la unidad
}
