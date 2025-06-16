package ElBuenSabor.ProyectoFinal.DTO;

import lombok.*;

import java.time.LocalTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SucursalCreateDTO {

    private String nombre;
    private LocalTime horarioApertura;
    private LocalTime horarioCierre;

    private Long domicilioId;
    private Long empresaId;

    private List<Long> categoriaIds;
    private List<Long> promocionIds;
}
