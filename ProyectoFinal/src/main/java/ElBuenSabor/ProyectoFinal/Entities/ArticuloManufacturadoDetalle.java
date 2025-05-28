package ElBuenSabor.ProyectoFinal.Entities;

import jakarta.persistence.*; // Asegúrate de tener todos los imports de persistence
import lombok.*;

@Entity
@Table(name = "articulo_manufacturado_detalle") // Nombre de tabla correcto
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
// No necesita @SQLDelete ni @Where propios, su ciclo de vida está ligado al ArticuloManufacturado.
public class ArticuloManufacturadoDetalle extends BaseEntity { // Hereda 'id' y 'baja'

    @Column(nullable = false)
    private Double cantidad; // Cantidad de insumo para este producto

    // Relación con ArticuloInsumo: Un detalle usa un ArticuloInsumo
    @ManyToOne(fetch = FetchType.EAGER) // Cargar el insumo siempre con el detalle es útil
    @JoinColumn(name = "articulo_insumo_id", nullable = false) // Un detalle debe tener un insumo
    private ArticuloInsumo articuloInsumo;

    // La relación @ManyToOne con ArticuloManufacturado se gestiona desde ArticuloManufacturado
    // con el @JoinColumn(name = "articulo_manufacturado_id") en la colección @OneToMany.
    // Si quisieras navegación bidireccional explícita:
    // @ManyToOne(fetch = FetchType.LAZY)
    // @JoinColumn(name = "articulo_manufacturado_id", insertable = false, updatable = false) // Si la FK es manejada por el @JoinColumn del @OneToMany
    // private ArticuloManufacturado articuloManufacturado;
    // Por ahora, la mantenemos unidireccional desde ArticuloManufacturado.
}
