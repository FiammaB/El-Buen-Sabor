package ElBuenSabor.ProyectoFinal.Entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.time.LocalTime;
import java.util.ArrayList; // Para inicializar listas
import java.util.HashSet;   // Para inicializar sets
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "sucursal") // Nombre de tabla correcto
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@SQLDelete(sql = "UPDATE sucursal SET baja = true WHERE id = ?")
@Where(clause = "baja = false")
public class Sucursal extends BaseEntity { // Hereda id y baja

    @Column(nullable = false)
    private String nombre;

    // Horarios de atención específicos de la sucursal.
    // Las consignas mencionan horarios generales, pero una sucursal podría tener los suyos.
    private LocalTime horarioApertura;
    private LocalTime horarioCierre;

    // Relación con Domicilio: Una sucursal tiene un domicilio.
    // CascadeType.ALL y orphanRemoval=true: Si se borra (físicamente) una sucursal, su domicilio también.
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "domicilio_id", referencedColumnName = "id", nullable = false)
    private Domicilio domicilio;

    // Relación con Empresa: Una sucursal pertenece a una empresa.
    @ManyToOne(fetch = FetchType.EAGER) // EAGER para tener siempre la info de la empresa
    @JoinColumn(name = "empresa_id", nullable = false)
    private Empresa empresa;

    // Relación con Categoria: Una sucursal ofrece ciertas categorías de productos.
    // Sucursal es la dueña de la relación ManyToMany con Categoria.
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "sucursal_categoria",
            joinColumns = @JoinColumn(name = "sucursal_id"),
            inverseJoinColumns = @JoinColumn(name = "categoria_id")
    )
    @Builder.Default
    private List<Categoria> categorias = new ArrayList<>(); // Cambiado a List según tu entidad original

    // Relación con Pedido: Una sucursal tiene muchos pedidos.
    // mappedBy="sucursal" indica que Pedido es el dueño de la relación.
    @OneToMany(mappedBy = "sucursal", cascade = CascadeType.PERSIST, fetch = FetchType.LAZY) // No CascadeType.ALL para no borrar pedidos con sucursal
    @Builder.Default
    private List<Pedido> pedidos = new ArrayList<>();

    // Relación con Promocion: Una sucursal puede tener varias promociones.
    // mappedBy="sucursales" indica que Promocion es la dueña de la relación.
    @ManyToMany(mappedBy = "sucursales", fetch = FetchType.LAZY)
    @Builder.Default
    private Set<Promocion> promociones = new HashSet<>();
}
