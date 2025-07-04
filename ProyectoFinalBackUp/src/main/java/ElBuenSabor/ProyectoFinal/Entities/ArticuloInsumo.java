package ElBuenSabor.ProyectoFinal.Entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "articulo_insumo")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class ArticuloInsumo extends Articulo {

    private Double precioCompra;
    private Double stockActual; // Usamos Double para permitir cantidades no enteras (ej. 1.5 kg)
    private Double stockMinimo; // Usamos Double para permitir cantidades no enteras
    private Boolean esParaElaborar; // Indica si es un ingrediente para preparar otros productos

    @ManyToOne
    @JoinColumn(name = "unidad_medida_id")
    private UnidadMedida unidadMedida;

}