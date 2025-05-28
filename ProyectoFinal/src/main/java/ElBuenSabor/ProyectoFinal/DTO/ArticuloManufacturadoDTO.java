package ElBuenSabor.ProyectoFinal.DTO;

import jakarta.validation.Valid; // Para validar la lista de detalles
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.Set; // Mantener Set para detalles si la entidad usa Set
import java.util.HashSet; // Para inicializar

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ArticuloManufacturadoDTO implements ArticuloBaseDTO { // Implementar interfaz
    private Long id;

    @NotBlank(message = "La denominación es obligatoria")
    @Size(max = 255)
    private String denominacion;

    @NotNull(message = "El precio de venta es obligatorio")
    @PositiveOrZero
    private Double precioVenta;

    @NotNull(message = "El ID de la categoría es obligatorio")
    private Long categoriaId;
    private CategoriaDTO categoria; // Para respuesta

    @NotNull(message = "El ID de la unidad de medida es obligatorio")
    private Long unidadMedidaId;
    private UnidadMedidaDTO unidadMedida; // Para respuesta

    private Long imagenId;
    private ImagenDTO imagen;

    private boolean baja;

    // Campos específicos de ArticuloManufacturado
    @Size(max = 1000, message = "La descripción no debe exceder los 1000 caracteres")
    private String descripcion;

    @NotNull(message = "El tiempo estimado en minutos es obligatorio")
    @PositiveOrZero
    private Integer tiempoEstimadoMinutos;

    @NotBlank(message = "La preparación (receta) es obligatoria")
    @Size(max = 4000, message = "La preparación no debe exceder los 4000 caracteres")
    private String preparacion;

    @NotNull(message = "Los detalles (ingredientes) son obligatorios")
    @NotEmpty(message = "Debe especificar al menos un ingrediente para el artículo manufacturado")
    @Valid // Para que se validen los DTOs de detalle anidados
    private Set<ArticuloManufacturadoDetalleDTO> detalles = new HashSet<>();
}
