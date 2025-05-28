package ElBuenSabor.ProyectoFinal.DTO;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalTime;
import java.util.List;
import java.util.Set; // Para IDs de promocion y categoria
import java.util.ArrayList; // Para inicializar
import java.util.HashSet;   // Para inicializar


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SucursalCreateUpdateDTO {

    @NotBlank(message = "El nombre de la sucursal es obligatorio")
    @Size(min = 2, max = 100)
    private String nombre;

    private LocalTime horarioApertura; // Opcional, puede usar horario general
    private LocalTime horarioCierre;   // Opcional

    @NotNull(message = "El domicilio es obligatorio para la sucursal")
    @Valid // Para validar el DomicilioCreateUpdateDTO anidado
    private DomicilioCreateUpdateDTO domicilio;

    @NotNull(message = "El ID de la empresa es obligatorio")
    private Long empresaId;

    // IDs de categorías y promociones a asociar.
    // La lógica de asociación se maneja en el servicio.
    private List<Long> categoriaIds = new ArrayList<>();
    private Set<Long> promocionIds = new HashSet<>();

    // El estado 'baja' se maneja por endpoints específicos (softDelete/reactivate)
    // private Boolean baja;
}
