package ElBuenSabor.ProyectoFinal.DTO;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ArticuloInsumoDTO implements ArticuloBaseDTO { // Implementar interfaz para el helper
    private Long id;

    @NotBlank(message = "La denominación es obligatoria")
    @Size(max = 255, message = "La denominación no debe exceder los 255 caracteres")
    private String denominacion;

    @NotNull(message = "El precio de venta es obligatorio")
    @PositiveOrZero(message = "El precio de venta no puede ser negativo")
    private Double precioVenta;

    @NotNull(message = "El ID de la categoría es obligatorio")
    private Long categoriaId;
    private CategoriaDTO categoria; // Para respuesta

    @NotNull(message = "El ID de la unidad de medida es obligatorio")
    private Long unidadMedidaId;
    private UnidadMedidaDTO unidadMedida; // Para respuesta

    private Long imagenId; // ID de una imagen existente
    private ImagenDTO imagen; // Para respuesta, o para enviar URL/datos de nueva imagen

    private boolean baja; // Para reflejar estado y permitir modificarlo al admin

    // Campos específicos de ArticuloInsumo
    @NotNull(message = "El precio de compra es obligatorio")
    @PositiveOrZero(message = "El precio de compra no puede ser negativo")
    private Double precioCompra;

    @NotNull(message = "El stock actual es obligatorio")
    @Min(value = 0, message = "El stock actual no puede ser negativo") // Min 0 es más apropiado que PositiveOrZero para stock
    private Double stockActual;

    @NotNull(message = "El stock mínimo es obligatorio")
    @Min(value = 0, message = "El stock mínimo no puede ser negativo")
    private Double stockMinimo;

    @NotNull(message = "Debe indicar si es para elaborar")
    private Boolean esParaElaborar;
}
