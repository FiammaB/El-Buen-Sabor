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
    private LocalidadDTO localidad; // DTO de la localidad asociada
    private boolean baja; // Para reflejar el estado de borrado lógico
    // Opcional: IDs del cliente o sucursal si este DomicilioDTO se usa en contextos donde se conoce esa asociación
    // private Long clienteId;
    // private Long sucursalId;
}
