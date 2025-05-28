package ElBuenSabor.ProyectoFinal.DTO;

import ElBuenSabor.ProyectoFinal.Entities.Rol;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDate;
// import java.util.List; // Si se manejan domicilios aquí
// import java.util.ArrayList; // Si se manejan domicilios aquí

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EmpleadoCreateUpdateDTO {

    // Campos comunes para creación y actualización
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
    private String email; // Usado como username. Generalmente no se actualiza una vez creado.

    private LocalDate fechaNacimiento; // Opcional

    private Long imagenId; // Opcional, ID de una imagen existente

    // Campos específicos para la creación por Administrador (HU#04)
    // y para el cambio de contraseña por el empleado o admin (HU#06, HU#08)
    // Para creación, 'password' es la clave provisoria.
    // Para actualización, 'newPassword' es la nueva clave si se cambia.
    @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres")
    // La validación de fortaleza completa se hace en el servicio.
    private String password; // Usado para la contraseña provisoria en creación, o newPassword en actualización

    private String confirmPassword; // Para confirmar la contraseña provisoria en creación, o newPassword en actualización

    // Campo para la contraseña actual, solo relevante en el flujo de actualización por el propio empleado
    private String currentPassword; // Necesario si el empleado cambia su propia contraseña

    // Campo Rol: Requerido en creación. En actualización, solo el admin puede cambiarlo (HU#156)
    @NotNull(message = "El rol es obligatorio para la creación")
    private Rol rol;

    // Campo Baja: No se setea en creación (es false por defecto).
    // En actualización, solo el admin puede cambiarlo (HU#156)
    private Boolean baja; // Usar Boolean para que pueda ser null y no se actualice si no se envía

    // Consideraciones para Domicilios:
    // Si se quiere permitir la gestión de UN domicilio principal a través de este DTO:
    // @Valid
    // private DomicilioCreateUpdateDTO domicilioPrincipal;
    // Si se quieren manejar MÚLTIPLES domicilios, sería mejor un endpoint dedicado.
    // Por ahora, se omite para mantener este DTO enfocado en el perfil del empleado.
}
