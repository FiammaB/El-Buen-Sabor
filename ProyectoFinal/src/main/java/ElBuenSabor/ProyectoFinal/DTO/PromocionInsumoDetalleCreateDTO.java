package ElBuenSabor.ProyectoFinal.DTO;

import lombok.Data;

@Data
public class PromocionInsumoDetalleCreateDTO {
    private Long articuloInsumoId; // <-- Contiene el ID
    private Integer cantidad;
}