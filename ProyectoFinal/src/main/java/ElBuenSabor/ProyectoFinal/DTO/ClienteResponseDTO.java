package ElBuenSabor.ProyectoFinal.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ClienteResponseDTO {
    private Long id;
    private String nombre;
    private String apellido;
    private String telefono;
    private String email;
    private LocalDate fechaNacimiento;
    private String username;
    private String auth0Id;
    private String rol; // convertido a string en el controller con .name()
    private ImagenDTO imagen;
    private List<DomicilioDTO> domicilios; // cada DomicilioDTO ya trae localidadNombre
    private boolean estaDadoDeBaja;
}
