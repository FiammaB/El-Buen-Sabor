// ProyectoFinal/src/main/java/ElBuenSabor/ProyectoFinal/DTO/ClienteDTO.java
package ElBuenSabor.ProyectoFinal.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDate;
import java.util.List; // Importa List

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ClienteDTO {

    private Long id;
    private String nombre;
    private String apellido;
    private String telefono;
    private String email;
    private LocalDate fechaNacimiento;
    private boolean estaDadoDeBaja;

    private ImagenDTO imagen;
    private UsuarioDTO usuario;
    // Esto ya estaba como List<DomicilioDTO>, lo cual es aceptable para un DTO de respuesta
    private List<DomicilioDTO> domicilios; // <-- Se mantiene así.

}