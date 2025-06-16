package ElBuenSabor.ProyectoFinal.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SucursalCreateUpdateDTO {
    //RequestDTO
    private String nombre;
    private LocalTime horarioApertura;
    private LocalTime horarioCierre;
    private DomicilioCreateUpdateDTO domicilio; // Create/Update Domicilio along with Sucursal
    private Long empresaId;
    private List<Long> categoriaIds;
}