package ElBuenSabor.ProyectoFinal.DTO;

import ElBuenSabor.ProyectoFinal.Entities.TipoPromocion;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;

@Getter
@Setter
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
    private Long imagenId; // For create/update
    private List<Long> articuloManufacturadoIds; // For request
    private List<ArticuloManufacturadoDTO> articulosManufacturados; // For response
    private Set<Long> sucursalIds; // For request: IDs of sucursales where this promo applies
    // For response, you might list sucursales if needed
    // private Set<SucursalSimpleDTO> sucursales;
}