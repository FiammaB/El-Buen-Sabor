package ElBuenSabor.ProyectoFinal.Entities;

import jakarta.persistence.*; // Asegúrate de tener todos los imports de persistence
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "articulo_insumo") // Nombre de tabla correcto
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
// No usar @ToString.Exclude en relaciones que quieres que se carguen EAGER si usas Lombok @ToString
// o podrías tener problemas con la carga perezosa si no se inicializan.
// Es mejor controlar la serialización a través de DTOs.
@SQLDelete(sql = "UPDATE articulo_insumo SET baja = true WHERE id = ?")
@Where(clause = "baja = false")
public class ArticuloInsumo extends Articulo { // Hereda id, baja, denominacion, precioVenta, imagen, categoria, unidadMedida

    @Column(nullable = false)
    private Double precioCompra;

    @Column(nullable = false)
    private Double stockActual;

    @Column(nullable = false)
    private Double stockMinimo;

    @Column(nullable = false)
    private Boolean esParaElaborar; // Indica si es un ingrediente para preparar otros productos

    // La relación con UnidadMedida ya está en la clase Articulo.
    // No es necesario repetirla aquí a menos que sea una relación diferente o más específica.
    // @ManyToOne
    // @JoinColumn(name = "unidad_medida_id") // Esta ya está en Articulo
    // private UnidadMedida unidadMedida;

    // Relación con ArticuloManufacturadoDetalle: Un insumo puede estar en muchos detalles de manufacturados.
    // mappedBy="articuloInsumo" indica que ArticuloManufacturadoDetalle es la dueña de la relación.
    // CascadeType: Generalmente no queremos que al borrar un insumo se borren los detalles de las recetas.
    // FetchType.LAZY es apropiado.
    @OneToMany(mappedBy = "articuloInsumo", fetch = FetchType.LAZY)
    @Builder.Default // Para que Lombok Builder inicialice la lista si es necesario
    private List<ArticuloManufacturadoDetalle> detallesDondeEsIngrediente = new ArrayList<>();
}
