package ElBuenSabor.ProyectoFinal.DTO;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nombre;
    private String razonSocial;
    private Integer cuil;
    // For response, you might include a list of SucursalSimpleDTO
    private List<SucursalDTO> sucursales; // Or SucursalSimpleDTO to avoid cycles if SucursalDTO contains EmpresaDTO
}