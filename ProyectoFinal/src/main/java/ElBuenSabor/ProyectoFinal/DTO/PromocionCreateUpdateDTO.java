package ElBuenSabor.ProyectoFinal.DTO;

import ElBuenSabor.ProyectoFinal.Entities.TipoPromocion;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Set;
import java.util.HashSet;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PromocionCreateUpdateDTO {

    @NotBlank(message = "La denominación es obligatoria")
    @Size(max = 255)
    private String denominacion;

    @NotNull(message = "La fecha desde es obligatoria")
    private LocalDate fechaDesde;

    @NotNull(message = "La fecha hasta es obligatoria")
    private LocalDate fechaHasta;

    // Opcionales
    private LocalTime horaDesde;
    private LocalTime horaHasta;

    @Size(max = 1000, message = "La descripción no debe exceder los 1000 caracteres")
    private String descripcionDescuento;

    @NotNull(message = "El precio promocional es obligatorio")
    @Positive(message = "El precio promocional debe ser positivo")
    private Double precioPromocional;

    @NotNull(message = "El tipo de promoción es obligatorio")
    private TipoPromocion tipoPromocion;

    private Long imagenId; // ID de una imagen existente
    // private String imagenDenominacion; // O para crear una nueva imagen con URL/path

    @NotEmpty(message = "Debe seleccionar al menos un artículo manufacturado para la promoción")
    private Set<Long> articuloManufacturadoIds = new HashSet<>();

    @NotEmpty(message = "Debe seleccionar al menos una sucursal para la promoción")
    private Set<Long> sucursalIds = new HashSet<>();

    // AssertTrue para validar que fechaDesde no sea posterior a fechaHasta
    @AssertTrue(message = "La fecha 'desde' no puede ser posterior a la fecha 'hasta'")
    public boolean isDateOrderValid() {
        if (fechaDesde == null || fechaHasta == null) {
            return true; // Se valida con @NotNull si son obligatorias
        }
        return !fechaDesde.isAfter(fechaHasta);
    }

    // AssertTrue para validar que horaDesde no sea posterior a horaHasta si ambas están presentes
    @AssertTrue(message = "La hora 'desde' no puede ser posterior a la hora 'hasta' si ambas están especificadas")
    public boolean isTimeOrderValid() {
        if (horaDesde == null || horaHasta == null) {
            return true; // Válido si una o ambas no están especificadas
        }
        return !horaDesde.isAfter(horaHasta);
    }
}
