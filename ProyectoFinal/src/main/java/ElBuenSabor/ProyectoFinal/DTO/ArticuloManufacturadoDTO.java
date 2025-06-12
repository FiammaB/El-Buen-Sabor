package ElBuenSabor.ProyectoFinal.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ArticuloManufacturadoDTO {
    private Long id;
    private String denominacion;
    private Double precioVenta;
    private String descripcion;
    private Integer tiempoEstimadoMinutos;
    private String preparacion;
    private Long categoriaId;
    private CategoriaDTO categoria; // For response
    private Long unidadMedidaId; // Though often "unidad" for manufacturados
    private UnidadMedidaDTO unidadMedida; // For response
    private ImagenDTO imagen;
    private Long imagenId; // For create/update
    private Set<ArticuloManufacturadoDetalleDTO> detalles;
}