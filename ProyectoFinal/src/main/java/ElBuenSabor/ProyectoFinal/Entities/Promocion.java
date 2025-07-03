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
@Builder
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
            name = "promocion_articulo_manufacturado",
            joinColumns = @JoinColumn(name = "promocion_id"),
            inverseJoinColumns = @JoinColumn(name = "articulo_manufacturado_id")
    )
    private List<ArticuloManufacturado> articulosManufacturados;

    @ManyToMany
    @JoinTable(
            name = "promocion_articulo_insumo",
            joinColumns = @JoinColumn(name = "promocion_id"),
            inverseJoinColumns = @JoinColumn(name = "articulo_insumo_id")

    )
    private List<ArticuloInsumo> articulosInsumos;

    @ManyToMany
    @JoinTable(
            name = "promocion_sucursal",
            joinColumns = @JoinColumn(name = "promocion_id"),
            inverseJoinColumns = @JoinColumn(name = "sucursal_id")
    )
    private List<Sucursal> sucursales;

    @OneToMany(mappedBy = "promocion", cascade = CascadeType.ALL, orphanRemoval = true,fetch = FetchType.EAGER)
    private Set<ArticuloManufacturadoDetalle> detalles = new HashSet<>();
}
