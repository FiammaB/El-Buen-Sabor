package ElBuenSabor.ProyectoFinal.Entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "usuario") // Tabla base para la estrategia JOINED
@Inheritance(strategy = InheritanceType.JOINED) // Estrategia de herencia
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
// No @SQLDelete ni @Where aquí; irán en las clases concretas (Cliente, Empleado)
public abstract class Usuario extends BaseEntity { // Hereda 'id' y 'baja'

    // auth0Id es opcional, si se implementa login con Auth0 o Google Sign-In
    // Si se usa Google Sign-In, este campo podría almacenar el 'sub' (subject ID de Google).
    @Column(unique = true, nullable = true) // Puede ser null si no usan login social, único si lo usan.
    private String auth0Id;

    @Column(unique = true, nullable = false) // El username (email) es el identificador único y obligatorio.
    private String username; // Según consignas, para Cliente es su email. Para Empleado también.

    // Un Usuario (sea Cliente o Empleado con rol Cliente) puede tener Pedidos.
    // Esta relación es más natural desde Pedido ManyToOne Usuario/Cliente.
    // Si se mantiene aquí, debe ser mappedBy.
    // La consigna indica que el Pedido tiene el nombre del Cliente.
    // Pedido tiene @ManyToOne Cliente cliente.
    // Por lo tanto, esta colección en Usuario no es estrictamente necesaria si la navegación
    // principal es Pedido -> Cliente. Si se quiere Usuario -> Pedidos, se necesita mappedBy.
    // Por ahora, la comentamos para evitar complejidad si no se usa activamente.
    // @OneToMany(mappedBy = "cliente", fetch = FetchType.LAZY) // Asumiendo que Pedido.cliente es el dueño
    // @Builder.Default
    // private List<Pedido> pedidos = new ArrayList<>();
}
