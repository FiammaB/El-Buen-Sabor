package ElBuenSabor.ProyectoFinal.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProvinciaDTO {
    private Long id;
    private String nombre;
    private PaisDTO pais; // DTO del país asociado para la respuesta
    private boolean baja; // Para reflejar el estado de borrado lógico
}