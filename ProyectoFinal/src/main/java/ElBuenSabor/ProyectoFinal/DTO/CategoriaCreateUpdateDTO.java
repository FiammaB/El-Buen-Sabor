package ElBuenSabor.ProyectoFinal.DTO;

import ElBuenSabor.ProyectoFinal.Entities.TipoRubro; // Importar Enum
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CategoriaCreateUpdateDTO {

    @NotBlank(message = "La denominación es obligatoria")
    @Size(min = 2, max = 100, message = "La denominación debe tener entre 2 y 100 caracteres")
    private String denominacion;

    @NotNull(message = "El tipo de rubro (INGREDIENTE o PRODUCTO) es obligatorio")
    private TipoRubro tipoRubro;

    private Long categoriaPadreId; // Null si es una categoría de nivel superior

    // IDs de sucursales a las que pertenece esta categoría.
    // La gestión de la relación ManyToMany se hace en el servicio.
    private List<Long> sucursalIds;

    // El estado de 'baja' se maneja por endpoints específicos (softDelete/reactivate),
    // no usualmente en el DTO de creación/actualización general, a menos que un admin lo modifique directamente.
    // private Boolean baja;
}
