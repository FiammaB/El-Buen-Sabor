package ElBuenSabor.ProyectoFinal.Entities;


import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "articulo_manufacturado_detalle")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class ArticuloManufacturadoDetalle extends BaseEntity {

    private Double cantidad;

    @ManyToOne
    @JoinColumn(name = "articulo_insumo_id")
    private ArticuloInsumo articuloInsumo;

    @ManyToOne
    @JoinColumn(name = "articulo_manufacturado_id",nullable = false) // Esta columna almacenará el ID del ArticuloManufacturado
    private ArticuloManufacturado articuloManufacturado;

}
