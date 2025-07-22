package ElBuenSabor.ProyectoFinal.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReporteMonetarioDiarioDTO {
    private LocalDate fecha;
    private Double ingresos;
    private Double costos;
    private Double ganancia;
}