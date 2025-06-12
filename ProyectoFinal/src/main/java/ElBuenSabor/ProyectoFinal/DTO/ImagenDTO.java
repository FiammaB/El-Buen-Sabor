package ElBuenSabor.ProyectoFinal.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ImagenDTO {
    private Long id;
    private String denominacion; // Se usa como "url" o nombre del archivo
}
