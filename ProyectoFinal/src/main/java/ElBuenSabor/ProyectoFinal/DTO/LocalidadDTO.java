package ElBuenSabor.ProyectoFinal.DTO;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LocalidadDTO {

    private Long id;
    private String nombre;
    private ProvinciaDTO provincia;
}
