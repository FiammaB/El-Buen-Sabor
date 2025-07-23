package ElBuenSabor.ProyectoFinal.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductoRankingDTO {
    private String nombreProducto;
    private Long cantidadVendida;
    private Double precioVenta; // <-- NUEVO CAMPO
    private Date fechaVenta; // <-- NUEVO CAMPO

}