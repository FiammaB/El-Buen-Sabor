package ElBuenSabor.ProyectoFinal.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ArticuloManufacturadoSimpleDTO {
    private Long id;
    private String denominacion;
    private Double precioVenta; // Precio original para referencia
    private boolean baja;
    // No incluir detalles ni otras relaciones complejas aquí
}
