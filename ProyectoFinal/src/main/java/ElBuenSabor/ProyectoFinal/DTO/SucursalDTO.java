package ElBuenSabor.ProyectoFinal.DTO;

import lombok.*;

import java.time.LocalTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SucursalDTO {

    private Long id;
    private String nombre;
    private LocalTime horarioApertura;
    private LocalTime horarioCierre;

    private DomicilioDTO domicilio;
    private EmpresaDTO empresa;
    private List<CategoriaDTO> categorias;
    private List<PromocionDTO> promociones;
}