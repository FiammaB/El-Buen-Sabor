package ElBuenSabor.ProyectoFinal.DTO;

import jakarta.validation.constraints.NotBlank; // Para validaciones futuras
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

    // Este campo es para cuando se conoce el ID exacto de la localidad y ya existe.
    // Si se proporcionan los nombres de país/provincia/localidad, estos podrían usarse
    // para buscar o crear, y localidadId podría ser opcional o ignorado en ese caso.
    // O, si localidadId está presente, podría tener precedencia.
    private Long localidadId;

    // Campos añadidos para permitir la búsqueda o creación por nombre,
    // consistentes con la lógica de ClienteServiceImpl y la actual de SucursalServiceImpl.
    @NotBlank(message = "El nombre de la localidad es obligatorio para el domicilio")
    private String nombreLocalidad;

    @NotBlank(message = "El nombre de la provincia es obligatorio para el domicilio")
    private String nombreProvincia;

    @NotBlank(message = "El nombre del país es obligatorio para el domicilio")
    private String nombrePais;
}