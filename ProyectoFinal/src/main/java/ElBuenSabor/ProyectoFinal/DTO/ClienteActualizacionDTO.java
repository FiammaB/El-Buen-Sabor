package ElBuenSabor.ProyectoFinal.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ClienteActualizacionDTO {
    // Datos personales
    private String nombre;
    private String apellido;
    private String telefono;

    // Datos de domicilio (opcionales, solo si se actualizan)
    private String calle;
    private Integer numero;
    private Integer cp;
    private String nombreLocalidad;
    private String nombreProvincia;
    private String nombrePais;

    // Para cambio de contraseña (opcionales)
    private String currentPassword;
    private String newPassword;
    private String confirmNewPassword;

    private LocalDate fechaNacimiento;
    private long ImagenId;

    // Consider adding fechaNacimiento if it's updatable
    //private java.time.LocalDate fechaNacimiento;
}