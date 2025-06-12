package ElBuenSabor.ProyectoFinal.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DomicilioDTO {
    private Long id;
    private String calle;
    private Integer numero;
    private Integer cp;
    private Long localidadId;         // Para envío de datos (POST/PUT)
    private LocalidadDTO localidad;   // Para respuesta completa (GET)
    private String localidadNombre;   // Para respuesta simplificada
}
