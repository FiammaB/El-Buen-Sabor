package ElBuenSabor.ProyectoFinal.DTO;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class RegistrarCompraDTO {
    @NotNull(message = "La cantidad comprada es obligatoria")
    @Positive(message = "La cantidad comprada debe ser positiva")
    private Double cantidadComprada;

    @Positive(message = "El nuevo precio de costo no puede ser negativo")
    private Double nuevoPrecioCosto; // Opcional, si no se envía, no se actualiza
}