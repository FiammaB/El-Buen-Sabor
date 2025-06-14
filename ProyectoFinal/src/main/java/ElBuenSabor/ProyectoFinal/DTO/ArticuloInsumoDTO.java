package ElBuenSabor.ProyectoFinal.DTO;

import lombok.*;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class ArticuloInsumoDTO extends ArticuloDTO {
    private Double precioCompra;
    private Double stockActual;
    private Double stockMinimo;
    private Boolean esParaElaborar;

    private String imagenUrl;
    private Long imagenId;
    private Long unidadMedidaId;
    private Long categoriaId;

}