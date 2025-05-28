package ElBuenSabor.ProyectoFinal.Entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.util.HashSet;
import java.util.ArrayList; // Para la lista de promociones
import java.util.List;    // Para la lista de promociones
import java.util.Set;

@Entity
@Table(name = "articulo_manufacturado") // Nombre de tabla correcto
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@SQLDelete(sql = "UPDATE articulo_manufacturado SET baja = true WHERE id = ?")
@Where(clause = "baja = false")
public class ArticuloManufacturado extends Articulo { // Hereda campos base y 'baja'

    private String descripcion;
    private Integer tiempoEstimadoMinutos; // HU#12: Tiempo estimado de los artículos manufacturados
    private String preparacion; // Receta

    // Relación con ArticuloManufacturadoDetalle: Un manufacturado tiene muchos detalles (ingredientes)
    // CascadeType.ALL: Si se borra un manufacturado, se borran sus detalles.
    // orphanRemoval=true: Si un detalle se quita de la colección 'detalles' y se guarda el manufacturado,
    // el detalle huérfano se elimina de la BD.
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "articulo_manufacturado_id") // FK en la tabla ArticuloManufacturadoDetalle
    @Builder.Default // Para que Lombok Builder inicialice el Set
    private Set<ArticuloManufacturadoDetalle> detalles = new HashSet<>();

    // Relación con Promocion: Un manufacturado puede estar en muchas promociones
    // mappedBy="articulosManufacturados" indica que la entidad Promocion es la dueña de la relación.
    @ManyToMany(mappedBy = "articulosManufacturados", fetch = FetchType.LAZY)
    @Builder.Default
    private List<Promocion> promociones = new ArrayList<>();

    // Helper methods para la relación bidireccional con ArticuloManufacturadoDetalle (opcional pero recomendado)
    public void addDetalle(ArticuloManufacturadoDetalle detalle) {
        detalles.add(detalle);
        // Si ArticuloManufacturadoDetalle tuviera un campo @ManyToOne ArticuloManufacturado articuloManufacturado,
        // aquí se setearía: detalle.setArticuloManufacturado(this);
        // Pero con @JoinColumn en el @OneToMany, la FK está en la tabla detalle y se maneja por la persistencia de la colección.
    }

    public void removeDetalle(ArticuloManufacturadoDetalle detalle) {
        detalles.remove(detalle);
        // Si fuera bidireccional con campo en Detalle: detalle.setArticuloManufacturado(null);
    }
}
