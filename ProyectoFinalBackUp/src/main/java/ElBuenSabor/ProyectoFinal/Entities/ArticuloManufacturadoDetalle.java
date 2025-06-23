package ElBuenSabor.ProyectoFinal.Entities;


import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "articulo_manufacturado_detalle")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class ArticuloManufacturadoDetalle extends BaseEntity {

    private Double cantidad; // Cantidad de insumo para este producto

    @ManyToOne
    @JoinColumn(name = "articulo_insumo_id")
    private ArticuloInsumo articuloInsumo;

    @ManyToOne
    @JoinColumn(name = "articulo_manufacturado_id",nullable = false) // Esta columna almacenará el ID del ArticuloManufacturado
    private ArticuloManufacturado articuloManufacturado;
}
