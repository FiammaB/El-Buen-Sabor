package ElBuenSabor.ProyectoFinal.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ClientePedidosDTO {
    private Long idCliente;
    private String nombre;
    private String apellido;
    private Integer cantidadPedidos;
    private Double totalGastado;
}
