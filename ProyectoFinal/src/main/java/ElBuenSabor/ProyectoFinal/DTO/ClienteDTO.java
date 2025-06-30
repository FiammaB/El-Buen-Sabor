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
public class ClienteDTO {

    private Long id;
    private String nombreUsuario;
    private String apellido;
    private String telefono;
    private String emailUsuario;
    private Boolean baja;

    private ImagenDTO imagen;
    private UsuarioDTO usuario;

    private LocalDate fechaNacimiento;

    private List<DomicilioDTO> domicilios;

}
