package ElBuenSabor.ProyectoFinal.Entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;
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
    private TipoPromocion tipoPromocion; // happyHour, promocionGeneral [cite: 249]

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
    // También las promociones pueden estar relacionadas con sucursales
    @ManyToMany(mappedBy = "promociones") // Mapeado por el campo 'promociones' en la entidad Sucursal
    private Set<Sucursal> sucursales = new HashSet<>();
}
