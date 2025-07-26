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
public class PerfilDTO {
    private Long id;
    private String nombre;
    private String apellido;
    private String telefono;
    private LocalDate fechaNacimiento;  // ✅ Para mostrar en el perfil
    private UsuarioDTO usuario;         // ✅ Contiene email, username, rol y baja
    private List<DomicilioDTO> domicilios;     // ✅ Solo un domicilio (principal)
    private ImagenDTO imagen;           // ✅ Imagen de perfil (si existe)
}
