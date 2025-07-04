package ElBuenSabor.ProyectoFinal.Entities;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.HashSet;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;
@Entity
@Table(name = "sucursal")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Sucursal extends BaseEntity {

    private String nombre;
    private LocalTime horarioApertura;
    private LocalTime horarioCierre;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "domicilio_id", referencedColumnName = "id")
    private Domicilio domicilio;

    @ManyToOne
    @JoinColumn(name = "empresa_id")
    private Empresa empresa;

    @ManyToMany
    @JoinTable(
            name = "sucursal_categoria",
            joinColumns = @JoinColumn(name = "sucursal_id"),
            inverseJoinColumns = @JoinColumn(name = "categoria_id")
    )
    private List<Categoria> categorias;

    @OneToMany(mappedBy = "sucursal")
    private List<Pedido> pedidos;

    @ManyToMany
    @JoinTable(
            name = "sucursal_promocion",
            joinColumns = @JoinColumn(name = "sucursal_id"),
            inverseJoinColumns = @JoinColumn(name = "promocion_id")
    )
    private List<Promocion> promociones = new ArrayList<>();
}
