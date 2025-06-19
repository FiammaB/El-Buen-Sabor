package ElBuenSabor.ProyectoFinal.DTO;

import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClientePerfilUpdateDTO {
    private String nombre;
    private String apellido;
    private String telefono;
    private String email;
    private LocalDate fechaNacimiento;

    private String passwordActual;
    private String nuevaPassword;
    private String repetirPassword;

    private Long imagenId;
    private List<Long> domicilioIds;
}