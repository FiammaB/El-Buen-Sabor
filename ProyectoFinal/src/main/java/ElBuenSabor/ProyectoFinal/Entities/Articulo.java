package ElBuenSabor.ProyectoFinal.Entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Builder
@Entity
@Table(name = "articulo") // Tabla base para la estrategia JOINED
@Inheritance(strategy = InheritanceType.JOINED) // Estrategia de herencia
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
// No añadir @SQLDelete ni @Where aquí, irán en las clases concretas.
public abstract class Articulo extends BaseEntity { // Hereda 'id' y 'baja'

    @Column(nullable = false) // La denominación no debería ser nula
    protected String denominacion;

    @Column(nullable = false) // El precio de venta no debería ser nulo
    protected Double precioVenta;

    // Relación con Imagen: Un artículo tiene una imagen (opcional o requerida según el caso)
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER) // EAGER para que se cargue siempre con el artículo
    @JoinColumn(name = "imagen_id", referencedColumnName = "id")
    protected Imagen imagen;

    // Relación con Categoria: Un artículo pertenece a una categoría
    @ManyToOne(fetch = FetchType.EAGER) // EAGER para cargar la categoría siempre
    @JoinColumn(name = "categoria_id", nullable = false) // Un artículo debe tener una categoría
    protected Categoria categoria;

    // Relación con UnidadMedida: Un artículo tiene una unidad de medida
    @ManyToOne(fetch = FetchType.EAGER) // EAGER para cargar la unidad de medida siempre
    @JoinColumn(name= "unidad_medida_id", nullable = false) // Un artículo debe tener una unidad de medida
    protected UnidadMedida unidadMedida;

    // Relación con DetallePedido: Un artículo puede estar en muchos detalles de pedido
    // Esta relación es para navegar desde Artículo a sus Detalles de Pedido, si fuera necesario.
    // Generalmente, se navega desde Pedido a DetallePedido y luego a Artículo.
    // CascadeType: No queremos que al borrar un artículo se borren los detalles de pedidos históricos.
    // FetchType.LAZY es apropiado aquí.
    @OneToMany(mappedBy = "articuloInsumo", fetch = FetchType.LAZY) // Si DetallePedido tiene un campo 'articuloInsumo'
    @Builder.Default
    private List<DetallePedido> detallesPedidoConInsumo = new ArrayList<>();

    @OneToMany(mappedBy = "articuloManufacturado", fetch = FetchType.LAZY) // Si DetallePedido tiene un campo 'articuloManufacturado'
    @Builder.Default
    private List<DetallePedido> detallesPedidoConManufacturado = new ArrayList<>();

    // NOTA: La entidad DetallePedido tiene dos campos ManyToOne:
    // private ArticuloManufacturado articuloManufacturado;
    // private ArticuloInsumo articuloInsumo;
    // Esto significa que un DetallePedido referencia o a un Insumo o a un Manufacturado, no a un 'Articulo' genérico.
    // Por lo tanto, la relación OneToMany desde Articulo a DetallePedido es un poco más compleja de modelar
    // directamente en la clase base Articulo si DetallePedido no tiene un campo 'Articulo articulo'.
    // Las listas 'detallesPedidoConInsumo' y 'detallesPedidoConManufacturado' son una forma de representarlo,
    // pero solo una de ellas estará poblada para una instancia dada de Articulo (Insumo o Manufacturado).
    // Una alternativa sería no tener esta relación inversa aquí y siempre consultar los detalles desde Pedido.
    // Para la consigna "Ranking comidas más pedidas", se necesitará agregar las ventas de artículos.
    // Por ahora, mantendremos estas relaciones inversas, pero su uso práctico dependerá de las queries.
}
