package ElBuenSabor.ProyectoFinal.Entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "cliente")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Cliente extends BaseEntity {

    private String nombre;
    private String apellido;
    private String telefono;
    private LocalDate fechaNacimiento;


    @ManyToMany(cascade = CascadeType.PERSIST) // o PERSIST + MERGE
    @JoinTable(
            name = "cliente_domicilio",
            joinColumns = @JoinColumn(name = "cliente_id"),
            inverseJoinColumns = @JoinColumn(name = "domicilio_id")
    )
    private Set<Domicilio> domicilios = new HashSet<>();

    @OneToOne
    @MapsId
    @JoinColumn(name = "id")
    private Usuario usuario;

    //@OneToOne
    //@JoinColumn(name = "usuario_id", unique = true)
    //private Usuario usuario;

    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL)
    private List<Pedido> pedidos = new ArrayList<>();

    @OneToOne
    @JoinColumn(name = "imagen_id")
    private Imagen imagen;
}
