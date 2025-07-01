package ElBuenSabor.ProyectoFinal.Auth;

import ElBuenSabor.ProyectoFinal.Entities.Rol;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UsuarioResponse {
    private Long id;
    private String nombre;
    private String apellido;
    private String email;
    private String telefono;
    private Rol rol;
    private Boolean primerIngreso;

}
