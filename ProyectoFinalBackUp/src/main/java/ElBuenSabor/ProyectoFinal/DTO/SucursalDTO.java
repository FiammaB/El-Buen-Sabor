package ElBuenSabor.ProyectoFinal.DTO;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SucursalDTO {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nombre;
    private LocalTime horarioApertura;
    private LocalTime horarioCierre;
    private DomicilioDTO domicilio; // For response
    private Long domicilioId; // For request (if creating/updating domicilio separately)
    // Or embed DomicilioCreateUpdateDTO for creating/updating with sucursal
    private Long empresaId;
    // For response, you might show basic empresa info
    // private EmpresaSimpleDTO empresa;
    private List<Long> categoriaIds; // For request: IDs of categories for this sucursal
    private List<CategoriaDTO> categorias; // For response
}