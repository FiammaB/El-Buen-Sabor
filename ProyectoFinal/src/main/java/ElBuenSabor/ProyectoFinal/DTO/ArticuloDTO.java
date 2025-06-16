package ElBuenSabor.ProyectoFinal.DTO;

import lombok.*;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class ArticuloDTO {

    private Long id;
    private String denominacion;
    private Double precioVenta;
    private ImagenDTO imagen;
    private UnidadMedidaDTO unidadMedida;
    private CategoriaDTO categoria;
    private boolean estaDadoDeBaja;
}