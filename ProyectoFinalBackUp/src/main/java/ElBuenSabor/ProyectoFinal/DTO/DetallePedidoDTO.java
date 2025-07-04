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
public class DetallePedidoDTO {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Integer cantidad;
    private Double subTotal;

    // For request, you'd send one of these IDs
    private Long articuloManufacturadoId;
    private Long articuloInsumoId;

    // For response, you might embed the DTOs
    private ArticuloManufacturadoDTO articuloManufacturado;
    private ArticuloInsumoDTO articuloInsumo;
}