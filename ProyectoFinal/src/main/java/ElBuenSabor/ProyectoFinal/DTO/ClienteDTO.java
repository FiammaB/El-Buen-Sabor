package ElBuenSabor.ProyectoFinal.DTO;

import ElBuenSabor.ProyectoFinal.Entities.Usuario;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ClienteDTO extends BaseDTO{

    private String nombre;
    private String apellido;
    private String telefono;
    private String email;
    private Usuario usuario;
    private LocalDate fechaNacimiento;
    private ImagenDTO imagen;
    private List<PedidoDTO> pedidos = new ArrayList<>();
    private List<DomicilioDTO> domicilios = new ArrayList<>();

}