package ElBuenSabor.ProyectoFinal.Entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList; // Para inicializar listas
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "promocion") // Nombre de tabla correcto
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@SQLDelete(sql = "UPDATE promocion SET baja = true WHERE id = ?")
@Where(clause = "baja = false")
public class Promocion extends BaseEntity { // Hereda 'id' y 'baja'

    @Column(nullable = false)
    private String denominacion;

    @Column(nullable = false)
    private LocalDate fechaDesde;

    @Column(nullable = false)
    private LocalDate fechaHasta;

    // HoraDesde y HoraHasta pueden ser nulas si la promoción aplica todo el día
    private LocalTime horaDesde;
    private LocalTime horaHasta;

    @Column(length = 1000) // Aumentar longitud si es necesario
    private String descripcionDescuento;

    @Column(nullable = false)
    private Double precioPromocional; // O podría ser un porcentaje de descuento, o un monto fijo a descontar. La consigna sugiere un precio.

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoPromocion tipoPromocion; // HAPPY_HOUR, PROMOCION_GENERAL [cite: fiammab/el-buen-sabor/El-Buen-Sabor-5c97c909d4285672be03d563e2fa229f5e7921e4/ProyectoFinal/src/main/java/ElBuenSabor/ProyectoFinal/Entities/TipoPromocion.java]

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "imagen_id")
    private Imagen imagen;

    // Relación con ArticuloManufacturado: Una promoción puede aplicar a varios artículos manufacturados.
    // Y un artículo manufacturado puede estar en varias promociones.
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "promocion_articulo_manufacturado", // Nombre de la tabla de unión
            joinColumns = @JoinColumn(name = "promocion_id"),
            inverseJoinColumns = @JoinColumn(name = "articulo_manufacturado_id")
    )
    @Builder.Default
    private Set<ArticuloManufacturado> articulosManufacturados = new HashSet<>();

    // Relación con Sucursal: Una promoción puede estar disponible en varias sucursales.
    // Y una sucursal puede tener varias promociones.
    // La entidad Sucursal tiene `mappedBy = "promociones"` en su colección de promociones,
    // lo que significa que Promocion es la dueña de esta relación y define el @JoinTable.
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "promocion_sucursal", // Nombre de la tabla de unión
            joinColumns = @JoinColumn(name = "promocion_id"),
            inverseJoinColumns = @JoinColumn(name = "sucursal_id")
    )
    @Builder.Default
    private Set<Sucursal> sucursales = new HashSet<>();
}
