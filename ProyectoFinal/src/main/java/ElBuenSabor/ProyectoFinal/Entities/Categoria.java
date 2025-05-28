package ElBuenSabor.ProyectoFinal.Entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "categoria") // Nombre de tabla correcto
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@SQLDelete(sql = "UPDATE categoria SET baja = true WHERE id = ?")
@Where(clause = "baja = false")
public class Categoria extends BaseEntity { // Hereda 'id' y 'baja'

    @Column(nullable = false)
    private String denominacion;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoRubro tipoRubro; // Para diferenciar rubros de ingredientes o productos

    @ManyToOne(fetch = FetchType.LAZY) // Una categoría puede tener una categoría padre (para jerarquía)
    @JoinColumn(name = "categoria_padre_id")
    private Categoria categoriaPadre;

    @OneToMany(mappedBy = "categoriaPadre", cascade = CascadeType.ALL, orphanRemoval = false, fetch = FetchType.LAZY)
    @Builder.Default // Para que Lombok Builder inicialice el Set
    private Set<Categoria> subCategorias = new HashSet<>();

    // Relación con Articulo: Una categoría tiene muchos artículos
    // mappedBy="categoria" indica que la entidad Articulo es la dueña de la relación.
    // No usar CascadeType.ALL aquí, el ciclo de vida de los artículos es independiente.
    @OneToMany(mappedBy = "categoria", fetch = FetchType.LAZY)
    @Builder.Default
    private List<Articulo> articulos = new ArrayList<>();

    // Relación con Sucursal: Una categoría puede estar en muchas sucursales
    // La entidad Sucursal es la dueña de esta relación (tiene @JoinTable)
    @ManyToMany(mappedBy = "categorias", fetch = FetchType.LAZY)
    @Builder.Default
    private Set<Sucursal> sucursales = new HashSet<>();

    // Helper methods para la relación bidireccional con subCategorias (opcional)
    public void addSubCategoria(Categoria subCategoria) {
        subCategorias.add(subCategoria);
        subCategoria.setCategoriaPadre(this);
    }

    public void removeSubCategoria(Categoria subCategoria) {
        subCategorias.remove(subCategoria);
        subCategoria.setCategoriaPadre(null);
    }
}
