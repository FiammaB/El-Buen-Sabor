package ElBuenSabor.ProyectoFinal.DTO;

import ElBuenSabor.ProyectoFinal.Entities.TipoRubro; // Importar Enum
import com.fasterxml.jackson.annotation.JsonInclude; // Para no incluir nulos
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.Set;
import java.util.List; // Para sucursalIds

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL) // Opcional: no serializar campos nulos
public class CategoriaDTO {
    private Long id;
    private String denominacion;
    private TipoRubro tipoRubro;
    private boolean baja;

    // Para la jerarquía
    private Long categoriaPadreId;
    private String categoriaPadreDenominacion; // Para visualización
    private Set<CategoriaDTO> subCategorias; // Lista de DTOs simples para evitar recursión profunda

    // Para la relación con Sucursal (solo IDs en el DTO de categoría)
    private List<Long> sucursalIds;
    // private List<SucursalSimpleDTO> sucursales; // Alternativa si se quiere más info de sucursal
}
