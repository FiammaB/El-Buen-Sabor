package ElBuenSabor.ProyectoFinal.DTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ArticuloSimpleBaseDTO {
    private Long id;
    private String denominacion;
    private Double precioVenta;
    private boolean baja;
    private CategoriaDTO categoria;    // Usar CategoriaDTO simple
    private UnidadMedidaDTO unidadMedida; // Usar UnidadMedidaDTO simple
    private ImagenDTO imagen;          // Usar ImagenDTO simple
    private String tipo;
}