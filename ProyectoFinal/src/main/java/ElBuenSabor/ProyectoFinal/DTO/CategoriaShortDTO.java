package ElBuenSabor.ProyectoFinal.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CategoriaShortDTO {
    private String denominacion;
    private Long categoriaPadreId;
    private List<Long> sucursalIds;
    public Boolean baja;
}