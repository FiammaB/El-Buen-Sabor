package ElBuenSabor.ProyectoFinal.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CategoriaCreateUpdateDTO {
    //RequestDTO
    private String denominacion;
    private Long categoriaPadreId; // Null if it's a top-level category
    private List<Long> sucursalIds; // IDs of sucursales this category belongs to
}