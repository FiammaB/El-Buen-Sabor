package ElBuenSabor.ProyectoFinal.DTO;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ClienteRegistroDTO {

    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 2, max = 50, message = "El nombre debe tener entre 2 y 50 caracteres")
    private String nombre;

    @NotBlank(message = "El apellido es obligatorio")
    @Size(min = 2, max = 50, message = "El apellido debe tener entre 2 y 50 caracteres")
    private String apellido;

    @NotBlank(message = "El teléfono es obligatorio")
    @Pattern(regexp = "^\\+?[0-9\\s\\-()]{7,20}$", message = "El formato del teléfono no es válido")
    private String telefono;

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El formato del email no es válido")
    @Size(max = 100, message = "El email no debe exceder los 100 caracteres")
    private String email;

    @NotBlank(message = "El nombre de usuario es obligatorio")
    @Size(min = 4, max = 50, message = "El nombre de usuario debe tener entre 4 y 50 caracteres")
    private String username;

    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres")
    private String password;

    @NotBlank(message = "La confirmación de contraseña es obligatoria")
    private String confirmPassword;

    @NotBlank(message = "La calle es obligatoria")
    @Size(max = 100, message = "La calle no debe exceder los 100 caracteres")
    private String calle;

    @NotNull(message = "El número de domicilio es obligatorio")
    @Positive(message = "El número de domicilio debe ser positivo")
    private Integer numero;

    @NotNull(message = "El código postal es obligatorio")
    @Positive(message = "El código postal debe ser positivo")
    private Integer cp;

    @NotBlank(message = "El nombre de la localidad es obligatorio")
    @Size(max = 100, message = "El nombre de la localidad no debe exceder los 100 caracteres")
    private String nombreLocalidad;

    @NotBlank(message = "El nombre de la provincia es obligatorio")
    @Size(max = 100, message = "El nombre de la provincia no debe exceder los 100 caracteres")
    private String nombreProvincia;

    @NotBlank(message = "El nombre del país es obligatorio")
    @Size(max = 100, message = "El nombre del país no debe exceder los 100 caracteres")
    private String nombrePais;

    @Past(message = "La fecha de nacimiento debe ser una fecha pasada")
    private LocalDate fechaNacimiento;

    private Long imagenId;
}
