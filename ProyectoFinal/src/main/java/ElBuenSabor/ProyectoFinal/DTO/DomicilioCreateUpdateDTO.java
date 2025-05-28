package ElBuenSabor.ProyectoFinal.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DomicilioCreateUpdateDTO {

    @NotBlank(message = "La calle es obligatoria")
    @Size(max = 100)
    private String calle;

    @NotNull(message = "El número de domicilio es obligatorio")
    @Positive
    private Integer numero;

    @NotNull(message = "El código postal es obligatorio")
    @Positive
    private Integer cp;

    // Para creación/actualización independiente, la localidad debe existir.
    @NotNull(message = "El ID de la localidad es obligatorio")
    private Long localidadId;

    // Estos campos son más para cuando se crea un domicilio junto con Cliente/Sucursal
    // y se quiere buscar/crear la jerarquía geográfica por nombre.
    // Para un DomicilioCreateUpdateDTO usado por DomicilioController, podrían ser opcionales
    // o eliminados si se asume que la localidad (y su jerarquía) ya existe y se referencia por localidadId.
    // Por ahora, los mantenemos para consistencia con su uso en SucursalServiceImpl.
    private String nombreLocalidad; // Si se usa, localidadId podría ser ignorado o usado para verificar.
    private String nombreProvincia;
    private String nombrePais;
}
