package ElBuenSabor.ProyectoFinal.DTO;

import ElBuenSabor.ProyectoFinal.Entities.TipoPromocion;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.*; // Para validaciones si este DTO se usara como entrada
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;
import java.util.HashSet; // Para inicializar

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PromocionDTO {
    private Long id;

    @NotBlank
    @Size(max = 255)
    private String denominacion;

    @NotNull
    private LocalDate fechaDesde;

    @NotNull
    private LocalDate fechaHasta;

    private LocalTime horaDesde; // Puede ser null
    private LocalTime horaHasta; // Puede ser null

    @Size(max = 1000)
    private String descripcionDescuento;

    @NotNull
    @Positive
    private Double precioPromocional;

    @NotNull
    private TipoPromocion tipoPromocion;

    private ImagenDTO imagen; // Para respuesta
    private boolean baja;

    // Para respuesta, podríamos enviar DTOs simples de los artículos y sucursales
    private Set<ArticuloManufacturadoSimpleDTO> articulosManufacturados = new HashSet<>();
    private Set<SucursalSimpleDTO> sucursales = new HashSet<>();

    // Para entrada (si este DTO se usara también para crear/actualizar)
    private Long imagenId;
    private Set<Long> articuloManufacturadoIds = new HashSet<>();
    private Set<Long> sucursalIds = new HashSet<>();
}
