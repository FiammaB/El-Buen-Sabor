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
    private Long id; // from BaseEntity, then Usuario
    private String nombre;
    private String apellido;
    private String telefono;
    private String email;
    private LocalDate fechaNacimiento;
    private String username; // from Usuario
    private String auth0Id;  // from Usuario
    private ImagenDTO imagen;
    private List<DomicilioDTO> domicilios;
    private boolean estaDadoDeBaja;
    private boolean baja;
    // Potentially roles or other user-specific info if not client-specific
}