// ProyectoFinal/src/main/java/ElBuenSabor/ProyectoFinal/DTO/PersonaCreateDTO.java
package ElBuenSabor.ProyectoFinal.DTO;

import ElBuenSabor.ProyectoFinal.Validations.EsMayorDeEdad;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List; // Importa List

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PersonaCreateDTO {

    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 2, max = 50, message = "El nombre debe tener entre 2 y 50 caracteres")
    @Pattern(regexp = "^[a-zA-ZáéíóúÁÉÍÓÚñÑüÜ\\s]+$", message = "El nombre solo puede contener letras y espacios") // <--- AGREGAR ESTA LÍNEA
    private String nombre;

    @NotBlank(message = "El apellido es obligatorio")
    @Size(min = 2, max = 50, message = "El apellido debe tener entre 2 y 50 caracteres")
    @Pattern(regexp = "^[a-zA-ZáéíóúÁÉÍÓÚñÑüÜ\\s]+$", message = "El apellido solo puede contener letras y espacios") // <--- AGREGAR ESTA LÍNEA
    private String apellido;

    @NotBlank(message = "El teléfono es obligatorio")
    @Pattern(regexp = "^\\+?[0-9\\s\\-()]+$", message = "El teléfono no es válido")
    private String telefono;

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El email debe tener un formato válido")
    private String email;

    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*(),.?\":{}|<>_\\-+=]).*$",
            message = "La contraseña debe contener al menos una mayúscula, una minúscula, un número y un símbolo")
    private String password;

    @NotNull(message = "La fecha de nacimiento es obligatoria")
    @Past(message = "La fecha de nacimiento debe ser una fecha pasada")
    private LocalDate fechaNacimiento;

    @EsMayorDeEdad(min = 13, max = 99, message = "Debes tener entre {min} y {max} años para registrarte")
    private LocalDate fechaNacimientoValidacionEdad; // Campo auxiliar para la validación de edad

    private Long usuarioId;
    private Long imagenId;

    private List<Long> domicilioIds;

    public LocalDate getFechaNacimientoValidacionEdad() {
        return this.fechaNacimiento;
    }
}