package ElBuenSabor.ProyectoFinal.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProvinciaCreateUpdateDTO {

    @NotBlank(message = "El nombre de la provincia es obligatorio")
    @Size(min = 2, max = 100, message = "El nombre de la provincia debe tener entre 2 y 100 caracteres")
    private String nombre;

    @NotNull(message = "El ID del país es obligatorio")
    private Long paisId; // Se envía el ID del país al crear/actualizar
}