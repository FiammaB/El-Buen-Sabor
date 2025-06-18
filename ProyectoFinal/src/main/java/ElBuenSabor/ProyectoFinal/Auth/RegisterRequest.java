package ElBuenSabor.ProyectoFinal.Auth;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class RegisterRequest {
    private String nombre;
    private String apellido;
    private String username; // en vez de "email"
    private String password;
    private String telefono;
    private LocalDate fechaNacimiento;
}
