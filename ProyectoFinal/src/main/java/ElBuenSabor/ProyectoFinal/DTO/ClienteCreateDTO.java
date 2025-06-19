// ProyectoFinal/src/main/java/ElBuenSabor/ProyectoFinal/DTO/ClienteCreateDTO.java
package ElBuenSabor.ProyectoFinal.DTO;

import lombok.*;

import java.time.LocalDate;
import java.util.List; // Importa List

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClienteCreateDTO {

    private String nombre;
    private String apellido;
    private String telefono;
    private String email;
    private String password;
    private LocalDate fechaNacimiento;

    private Long usuarioId;
    private Long imagenId;

    private List<Long> domicilioIds;
}