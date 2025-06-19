// ProyectoFinal/src/main/java/ElBuenSabor/ProyectoFinal/DTO/DomicilioDTO.java
package ElBuenSabor.ProyectoFinal.DTO;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DomicilioDTO {

    private Long id;
    private String calle;
    private Integer numero;
    private Integer cp;

    private LocalidadDTO localidad;
    // ASEGÚRATE DE QUE NO TIENE ESTO (y de hecho, no lo tiene):
    // private ClienteDTO cliente;
}