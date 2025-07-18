package ElBuenSabor.ProyectoFinal.DTO;

import lombok.Data;

@Data
public class PromocionDetalleDTO {
    private Long id;
    private Integer cantidad;
    private ArticuloManufacturadoDTO articuloManufacturado;

}