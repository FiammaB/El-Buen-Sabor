package ElBuenSabor.ProyectoFinal.DTO;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.Set;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CategoriaDTO {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String denominacion;
    private Long categoriaPadreId; // For request, nullable
    private CategoriaDTO categoriaPadre; // For response, nullable
    private Set<CategoriaDTO> subCategorias; // For response
    private List<Long> sucursalIds; // For linking to sucursales on create/update

    // Agregar este constructor
    public CategoriaDTO(Long id, String denominacion, Long categoriaPadreId, List<Long> sucursalIds) {
        this.id = id;
        this.denominacion = denominacion;
        this.categoriaPadreId = categoriaPadreId;
        this.sucursalIds = sucursalIds;
    }

}