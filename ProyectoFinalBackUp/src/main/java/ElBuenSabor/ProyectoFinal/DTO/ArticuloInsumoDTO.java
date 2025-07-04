package ElBuenSabor.ProyectoFinal.DTO;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ArticuloInsumoDTO {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String denominacion;
    private Double precioVenta;
    private Double precioCompra;
    private Double stockActual;
    private Double stockMinimo;
    private Boolean esParaElaborar;
    private Long unidadMedidaId;
    private UnidadMedidaDTO unidadMedida; // For response
    private Long categoriaId;
    private CategoriaDTO categoria; // For response
    private ImagenDTO imagen; // Could be just an ID for request, or a nested DTO
    private Long imagenId; // For create/update if imagen is separate
}