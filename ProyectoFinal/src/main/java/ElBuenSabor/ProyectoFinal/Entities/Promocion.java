package ElBuenSabor.ProyectoFinal.Entities;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "promocion")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@SuperBuilder
public class Promocion extends BaseEntity {

    private String denominacion;
    private LocalDate fechaDesde;
    private LocalDate fechaHasta;
    private LocalTime horaDesde;
    private LocalTime horaHasta;
    private String descripcionDescuento;

    private Double precioPromocional;

    @Enumerated(EnumType.STRING)
    private TipoPromocion tipoPromocion; // happyHour, promocionGeneral

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "imagen_id") // Una promoción puede tener una imagen
    private Imagen imagen;

    @ManyToMany
    @JoinTable(
            name = "promocion_articulo_insumo",
            joinColumns = @JoinColumn(name = "promocion_id"),
            inverseJoinColumns = @JoinColumn(name = "articulo_insumo_id")

    )
    @Builder.Default
    private List<ArticuloInsumo> articulosInsumos = new ArrayList<>();

    @ManyToMany
    @JoinTable(
            name = "promocion_sucursal",
            joinColumns = @JoinColumn(name = "promocion_id"),
            inverseJoinColumns = @JoinColumn(name = "sucursal_id")
    )
    @Builder.Default
    private List<Sucursal> sucursales = new ArrayList<>();

    @OneToMany(mappedBy = "promocion", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private Set<PromocionDetalle> promocionDetalles = new HashSet<>();
}
