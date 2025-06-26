package ElBuenSabor.ProyectoFinal.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class ReporteMonetarioDTO {
    private BigDecimal totalIngresos;
    private BigDecimal totalCostos;
    private BigDecimal ganancia;
}