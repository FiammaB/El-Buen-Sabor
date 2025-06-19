package ElBuenSabor.ProyectoFinal.Entities;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "categoria")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@SuperBuilder
public class Categoria extends BaseEntity {
    private String denominacion;

    @ManyToOne
    @JoinColumn(name = "categoria_padre_id")
    private Categoria categoriaPadre;

    @Builder.Default
    @OneToMany(mappedBy = "categoriaPadre", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Categoria> subCategorias = new HashSet<>();

    @Builder.Default
    @OneToMany(mappedBy = "categoria")
    private List<Articulo> articulos = new ArrayList<>();
    // ¡ESTA ES LA MODIFICACIÓN CLAVE!
    @Builder.Default
    @ManyToMany(mappedBy = "categorias") // Mapeado por el campo 'categorias' en la entidad Sucursal
    private Set<Sucursal> sucursales = new HashSet<>(); // Una categoría puede estar en varias sucursales
    // ¡AÑADIR ESTE CAMPO PARA EL BORRADO LÓGICO DE CATEGORÍAS!
   // protected boolean estaDadoDeBaja = false;
}
