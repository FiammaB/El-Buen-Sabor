package ElBuenSabor.ProyectoFinal.Entities;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "articulo_manufacturado")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@SuperBuilder
public class ArticuloManufacturado extends Articulo {

    private String descripcion;
    private Integer tiempoEstimadoMinutos;

    @Column(length = 1000)
    private String preparacion; // Receta

    @OneToMany(mappedBy = "articuloManufacturado", cascade = CascadeType.ALL, orphanRemoval = true,fetch = FetchType.EAGER) // CASCADE.ALL para guardar/actualizar detalles
    private Set<ArticuloManufacturadoDetalle> detalles = new HashSet<>(); // Los ingredientes de la receta


}