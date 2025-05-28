package ElBuenSabor.ProyectoFinal.DTO;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalTime;
import java.util.List;
import java.util.Set; // Para promociones
import java.util.ArrayList; // Para inicializar
import java.util.HashSet; // Para inicializar

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SucursalDTO {
    private Long id;
    private String nombre;
    private LocalTime horarioApertura;
    private LocalTime horarioCierre;
    private boolean baja;

    private DomicilioDTO domicilio; // DTO completo del domicilio
    private EmpresaSimpleDTO empresa; // DTO simple de la empresa

    private List<CategoriaSimpleDTO> categorias = new ArrayList<>(); // DTOs simples
    private Set<PromocionSimpleDTO> promociones = new HashSet<>();   // DTOs simples
}
