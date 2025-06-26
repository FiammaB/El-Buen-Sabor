package ElBuenSabor.ProyectoFinal.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClienteReporteDTO {
    private Long idCliente;
    private String nombre;
    private String apellido;
    private Long cantidadPedidos;
    private Double totalGastado;
}