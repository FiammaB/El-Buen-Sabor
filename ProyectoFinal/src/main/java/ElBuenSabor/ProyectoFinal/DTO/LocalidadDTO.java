package ElBuenSabor.ProyectoFinal.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LocalidadDTO {
    private Long id;
    private String nombre;
    private ProvinciaDTO provincia; // DTO de la provincia asociada
    private boolean baja; // Para reflejar el estado de borrado lógico
}