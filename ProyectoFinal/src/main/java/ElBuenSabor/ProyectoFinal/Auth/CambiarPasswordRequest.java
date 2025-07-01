package ElBuenSabor.ProyectoFinal.Auth;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CambiarPasswordRequest {
    private String email;
    private String nuevaPassword;
}
