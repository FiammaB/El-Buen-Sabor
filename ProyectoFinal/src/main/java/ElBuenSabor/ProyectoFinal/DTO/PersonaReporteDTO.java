package ElBuenSabor.ProyectoFinal.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PersonaReporteDTO {
    private Long idPersona;
    private String nombre;
    private String apellido;
    private Long cantidadPedidos;
    private Double totalGastado;
}