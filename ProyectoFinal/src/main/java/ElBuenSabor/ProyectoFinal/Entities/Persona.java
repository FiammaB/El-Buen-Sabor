package ElBuenSabor.ProyectoFinal.Entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "persona")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Persona extends BaseEntity {

    private String nombre;
    private String apellido;
    private String telefono;
    private LocalDate fechaNacimiento;


    @ManyToMany(cascade = CascadeType.PERSIST) // o PERSIST + MERGE
    @JoinTable(
            name = "persona_domicilio",
            joinColumns = @JoinColumn(name = "persona_id"),
            inverseJoinColumns = @JoinColumn(name = "domicilio_id")
    )
    private List<Domicilio> domicilios = new ArrayList<>();

    @OneToOne
    @MapsId
    @JoinColumn(name = "id")
    private Usuario usuario;

    //@OneToOne
    //@JoinColumn(name = "usuario_id", unique = true)
    //private Usuario usuario;

    @OneToMany(mappedBy = "persona", cascade = CascadeType.ALL)
    private List<Pedido> pedidos = new ArrayList<>();

    @OneToOne
    @JoinColumn(name = "imagen_id")
    private Imagen imagen;
}
