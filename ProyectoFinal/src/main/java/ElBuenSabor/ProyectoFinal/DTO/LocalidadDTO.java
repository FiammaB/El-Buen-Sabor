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
    private Long provinciaId; // For request
    private ProvinciaDTO provincia; // For response
}