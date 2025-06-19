package ElBuenSabor.ProyectoFinal.DTO;

import lombok.*;
import lombok.experimental.SuperBuilder;


@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class ArticuloInsumoShortDTO extends ArticuloDTO {

    private Double precioCompra;
    private Double stockActual;
    private Double stockMinimo;
    private Boolean esParaElaborar;
}