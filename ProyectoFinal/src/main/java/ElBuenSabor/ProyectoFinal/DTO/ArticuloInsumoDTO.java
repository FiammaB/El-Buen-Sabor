package ElBuenSabor.ProyectoFinal.DTO;

import lombok.*;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor

public class ArticuloInsumoDTO extends ArticuloDTO{


    private Double precioCompra;
    private Double stockActual;
    private Double stockMinimo;
    private Boolean esParaElaborar;

    private Long imagenId;
    private Long unidadMedidaId;
    private Long categoriaId;

    @Override
    public String toString() {
        return "ArticuloInsumoDTO{" +
                "precioCompra=" + precioCompra +
                ", stockActual=" + stockActual +
                ", stockMinimo=" + stockMinimo +
                ", esParaElaborar=" + esParaElaborar +
                ", imagenId=" + imagenId +
                ", unidadMedidaId=" + unidadMedidaId +
                ", categoriaId=" + categoriaId +
                '}';
    }
}