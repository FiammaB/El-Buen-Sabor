package ElBuenSabor.ProyectoFinal.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SucursalSimpleDTO {
    private Long id;
    private String nombre;
    private LocalTime horarioApertura;
    private LocalTime horarioCierre;
    private boolean baja; // Importante para saber el estado de la sucursal
    // No incluir Domicilio completo aquí para mantenerlo simple, quizás solo la calle o nombre de localidad.
    private String domicilioCalle; // Ejemplo
    private String domicilioLocalidadNombre; // Ejemplo
}
