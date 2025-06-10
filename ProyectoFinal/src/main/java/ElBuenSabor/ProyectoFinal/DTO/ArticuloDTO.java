package ElBuenSabor.ProyectoFinal.DTO;

import lombok.*;
import java.util.HashSet;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ArticuloDTO extends BaseDTO{

    protected String denominacion;
    protected Double precioVenta;
    private ImagenDTO imagen;
    private UnidadMedidaDTO unidadMedida;

}
