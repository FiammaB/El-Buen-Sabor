package ElBuenSabor.ProyectoFinal.Entities;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "articulo")
@Inheritance(strategy = InheritanceType.JOINED)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public abstract class Articulo extends BaseEntity {

    protected String denominacion;
    protected Double precioVenta;

    @ManyToOne
    @JoinColumn(name = "unidad_medida_id")
    protected UnidadMedida unidadMedida;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "imagen_id", referencedColumnName = "id")
    protected Imagen imagen;

    @ManyToOne
    @JoinColumn(name = "categoria_id")
    protected Categoria categoria;

}
