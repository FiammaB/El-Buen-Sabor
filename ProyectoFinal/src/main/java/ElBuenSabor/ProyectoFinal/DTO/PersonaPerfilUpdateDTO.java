package ElBuenSabor.ProyectoFinal.DTO;

import ElBuenSabor.ProyectoFinal.Validations.EsMayorDeEdad;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PersonaPerfilUpdateDTO {

    // Validaciones para campos que siempre deben ser válidos si se envían
    @Size(min = 2, max = 50, message = "El nombre debe tener entre 2 y 50 caracteres")
    @Pattern(regexp = "^[a-zA-ZáéíóúÁÉÍÓÚñÑüÜ\\s]+$", message = "El nombre solo puede contener letras y espacios")
    private String nombre;

    @Size(min = 2, max = 50, message = "El apellido debe tener entre 2 y 50 caracteres")
    @Pattern(regexp = "^[a-zA-ZáéíóúÁÉÍÓÚñÑüÜ\\s]+$", message = "El apellido solo puede contener letras y espacios")
    private String apellido;

    @Pattern(regexp = "^\\+?[0-9\\s-()]+$", message = "El teléfono no es válido")
    private String telefono;

    @Email(message = "El email debe tener un formato válido")
    private String email;

    @Past(message = "La fecha de nacimiento debe ser una fecha pasada")
    @EsMayorDeEdad(min = 13, max = 99, message = "Debes tener entre {min} y {max} años para registrarte")
    private LocalDate fechaNacimiento;

    private String passwordActual;
    private String nuevaPassword;
    private String repetirPassword;

    private Long imagenId;
    private List<Long> domicilioIds;

    // Getter para fechaNacimientoValidacionEdad que devuelve la misma fechaNacimiento
    // Necesario para aplicar la anotación de edad.
    public LocalDate getFechaNacimientoValidacionEdad() {
        return this.fechaNacimiento;
    }
    // --- NUEVOS MÉTODOS PARA VALIDACIÓN CONDICIONAL DE CONTRASEÑA ---
    @AssertTrue(message = "Debes ingresar tu contraseña actual para cambiarla")
    public boolean isPasswordActualProvidedIfNewPassword() {
        // Solo valida si se está intentando cambiar la contraseña
        return nuevaPassword == null || nuevaPassword.isEmpty() || (passwordActual != null && !passwordActual.isEmpty());
    }

    @AssertTrue(message = "La nueva contraseña debe tener al menos 8 caracteres, una mayúscula, una minúscula, un número y un símbolo")
    public boolean isNuevaPasswordValid() {
        // Solo valida si se ha proporcionado una nueva contraseña
        if (nuevaPassword == null || nuevaPassword.isEmpty()) {
            return true; // No hay nueva contraseña, no se valida este patrón
        }
        return nuevaPassword.length() >= 8 &&
                nuevaPassword.matches(".*[a-z].*") &&
                nuevaPassword.matches(".*[A-Z].*") &&
                nuevaPassword.matches(".*\\d.*") &&
                nuevaPassword.matches(".*[!@#$%^&*(),.?\":{}|<>_\\-+=].*");
    }

    @AssertTrue(message = "La nueva contraseña y la repetición no coinciden")
    public boolean isPasswordMatch() {
        // Solo valida si se han proporcionado ambas contraseñas nuevas
        if (nuevaPassword == null || nuevaPassword.isEmpty() || repetirPassword == null || repetirPassword.isEmpty()) {
            return true; // No hay nueva contraseña o repetición, no se valida la coincidencia
        }
        return nuevaPassword.equals(repetirPassword);
    }
}