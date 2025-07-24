package ElBuenSabor.ProyectoFinal.DTO;

import ElBuenSabor.ProyectoFinal.Entities.TipoPromocion;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PromocionCreateDTO {

    private String denominacion;
    private LocalDate fechaDesde;
    private LocalDate fechaHasta;
    private LocalTime horaDesde;
    private LocalTime horaHasta;
    private String descripcionDescuento;
    private Double precioPromocional;
    private TipoPromocion tipoPromocion;
    private Long imagenId;

    @Builder.Default
    private List<PromocionDetalleCreateDTO> promocionDetalles = new ArrayList<>();

    @Builder.Default
    private List<Long> sucursalIds = new ArrayList<>();

    // 👇 LA CORRECCIÓN CLAVE ESTÁ AQUÍ
    @Builder.Default
    private List<PromocionInsumoDetalleCreateDTO> promocionInsumoDetalles = new ArrayList<>();
}