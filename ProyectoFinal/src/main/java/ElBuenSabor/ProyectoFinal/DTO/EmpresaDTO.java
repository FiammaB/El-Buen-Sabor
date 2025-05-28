package ElBuenSabor.ProyectoFinal.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EmpresaDTO {
    private Long id;
    private String nombre;
    private String razonSocial;
    private Integer cuil; // Manteniendo Integer por consistencia con tu entidad
    private boolean baja;
    private List<SucursalSimpleDTO> sucursales; // Usar un DTO simple para sucursales para evitar ciclos
}
