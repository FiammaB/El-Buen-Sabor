package ElBuenSabor.ProyectoFinal.DTO;

import lombok.Data;

@Data
public class PromocionInsumoDetalleDTO {
    private Long id;
    private Integer cantidad;
    private ArticuloInsumoDTO articuloInsumo;
}