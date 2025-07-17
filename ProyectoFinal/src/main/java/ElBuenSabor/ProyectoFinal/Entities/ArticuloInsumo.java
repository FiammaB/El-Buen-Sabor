package ElBuenSabor.ProyectoFinal.Entities;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.HashSet;
import java.util.Set;
import java.util.List;
import java.util.ArrayList;

@Entity
@Table(name = "articulo_insumo")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@SuperBuilder
public class ArticuloInsumo extends Articulo {

    private Double precioCompra;
    private Double stockActual;
    private Double stockMinimo;
    private Boolean esParaElaborar;

    @OneToMany(mappedBy = "articuloInsumo", cascade = CascadeType.ALL, orphanRemoval = true,fetch = FetchType.EAGER)
    private Set<ArticuloManufacturadoDetalle> detalles = new HashSet<>();

    @ManyToMany(mappedBy = "articulosInsumos")
    private List<Promocion> promociones = new ArrayList<>();
}