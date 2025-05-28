package ElBuenSabor.ProyectoFinal.DTO;

import ElBuenSabor.ProyectoFinal.Entities.TipoPromocion;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PromocionSimpleDTO {
    private Long id;
    private String denominacion;
    private LocalDate fechaHasta;
    private TipoPromocion tipoPromocion;
    private Double precioPromocional;
    private boolean baja;
}
