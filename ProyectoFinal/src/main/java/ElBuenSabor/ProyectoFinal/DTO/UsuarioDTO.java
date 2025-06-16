package ElBuenSabor.ProyectoFinal.DTO;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioDTO {
    private Long id;
    private String auth0Id;
    private String username;
}
