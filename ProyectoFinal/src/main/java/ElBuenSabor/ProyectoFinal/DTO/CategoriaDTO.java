package ElBuenSabor.ProyectoFinal.DTO;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoriaDTO {

    private Long id;
    private String denominacion;
    private Long categoriaPadreId;
    private Boolean baja;

}