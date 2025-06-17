package ElBuenSabor.ProyectoFinal.DTO;

import lombok.*;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class ArticuloInsumoDTO extends ArticuloDTO {

    private String denominacion;
    private Double precioCompra;
    private Double stockActual;
    private Double stockMinimo;
    private Boolean esParaElaborar;

    private Double precioVenta;
    private Long imagenId;
    private Long unidadMedidaId;
    private Long categoriaId;

}
