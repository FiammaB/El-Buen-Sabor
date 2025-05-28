package ElBuenSabor.ProyectoFinal.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UnidadMedidaDTO {
    private Long id;

    @NotBlank(message = "La denominación es obligatoria")
    @Size(min = 1, max = 50, message = "La denominación debe tener entre 1 y 50 caracteres")
    private String denominacion;

    private boolean baja; // Para reflejar el estado de borrado lógico
}
