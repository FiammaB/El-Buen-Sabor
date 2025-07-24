package ElBuenSabor.ProyectoFinal.DTO;

import lombok.Data;

@Data
public class PromocionDetalleCreateDTO {
    private ArticuloManufacturadoDTO articuloManufacturado;
    private Integer cantidad;
}