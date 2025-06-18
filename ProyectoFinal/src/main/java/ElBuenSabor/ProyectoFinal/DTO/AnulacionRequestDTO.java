// ProyectoFinal/src/main/java/ElBuenSabor/ProyectoFinal/DTO/AnulacionRequestDTO.java
package ElBuenSabor.ProyectoFinal.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AnulacionRequestDTO {
    private String motivoAnulacion;
    private Long usuarioAnuladorId; // ID del usuario que realiza la anulación (ej. el cajero/administrador)
}