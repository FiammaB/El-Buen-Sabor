package ElBuenSabor.ProyectoFinal.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
// import org.hibernate.validator.constraints.URL; // Opcional, si 'denominacion' siempre es una URL
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ImagenDTO {
    private Long id;

    @NotBlank(message = "La denominación (URL/path) de la imagen es obligatoria")
    @Size(max = 2048, message = "La denominación no debe exceder los 2048 caracteres")
    // @URL(message = "La denominación debe ser una URL válida") // Descomentar si siempre es una URL
    private String denominacion;

    private boolean baja; // Para reflejar el estado de borrado lógico
}
