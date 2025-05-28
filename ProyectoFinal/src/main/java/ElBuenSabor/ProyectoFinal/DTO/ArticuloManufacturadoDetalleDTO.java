package ElBuenSabor.ProyectoFinal.DTO;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ArticuloManufacturadoDetalleDTO {
    private Long id; // Útil para actualizaciones si los detalles tienen sus propios IDs

    @NotNull(message = "La cantidad del ingrediente es obligatoria")
    @Positive(message = "La cantidad del ingrediente debe ser positiva")
    private Double cantidad;

    @NotNull(message = "El ID del artículo insumo (ingrediente) es obligatorio")
    private Long articuloInsumoId;

    // Para respuesta, podrías incluir un DTO simple del insumo
    private ArticuloInsumoDTO articuloInsumo; // Usar ArticuloInsumoDTO o uno más simple
    // No necesita campo 'baja' propio, ya que su existencia está ligada al manufacturado.
}
