package ElBuenSabor.ProyectoFinal.DTO;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProvinciaDTO {

    private Long id;
    private String nombre;
    private PaisDTO pais;

}
