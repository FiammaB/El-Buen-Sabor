package ElBuenSabor.ProyectoFinal.DTO;

import ElBuenSabor.ProyectoFinal.Entities.Rol;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioDTO {
    private Long id;
    private String email;
    private String password;
    private String username;
    private Rol rol;
    private Boolean baja;
}
