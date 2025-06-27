package ElBuenSabor.ProyectoFinal.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PerfilDTO {
    private Long id;
    private String nombre;
    private String apellido;
    private String telefono;
    private UsuarioDTO usuario;      // Contiene el email y rol
    private DomicilioDTO domicilio;  // Solo un domicilio (principal)
    private ImagenDTO imagen;        // Si querés mostrarlo o editarlo desde perfil
}
