package ElBuenSabor.ProyectoFinal.DTO;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EmpresaDTO extends BaseDTO{

    private String nombre;
    private String razonSocial;
    private Integer cuil;
    private List <SucursalDTO> sucursales = new ArrayList<>();

}
