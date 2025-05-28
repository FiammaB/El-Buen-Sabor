package ElBuenSabor.ProyectoFinal.DTO;

import ElBuenSabor.ProyectoFinal.Entities.Rol;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EmpleadoResponseDTO {
    private Long id;
    private String nombre;
    private String apellido;
    private String telefono;
    private String email;
    private String username; // Heredado de Usuario
    // No incluir password
    private Rol rol;
    private LocalDate fechaNacimiento;
    private LocalDate fechaAlta;
    private boolean baja;
    private ImagenDTO imagen;
    private List<DomicilioDTO> domicilios = new ArrayList<>();
}
