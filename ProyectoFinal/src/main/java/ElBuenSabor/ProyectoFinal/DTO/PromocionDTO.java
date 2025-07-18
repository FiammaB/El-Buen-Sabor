package ElBuenSabor.ProyectoFinal.DTO;

import ElBuenSabor.ProyectoFinal.Entities.TipoPromocion;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PromocionDTO {

    private Long id;
    private String denominacion;
    private LocalDate fechaDesde;
    private LocalDate fechaHasta;
    private LocalTime horaDesde;
    private LocalTime horaHasta;
    private String descripcionDescuento;
    private Double precioPromocional;
    private TipoPromocion tipoPromocion;
    private ImagenDTO imagen;
    private List<ArticuloInsumoDTO> articulosInsumos;
    private List<SucursalDTO> sucursales;
    private Set<PromocionDetalleDTO> promocionDetalles;

    private  boolean baja;

    private Set<ArticuloManufacturadoDetalleDTO> detalles;
}
