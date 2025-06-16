// ProyectoFinal/src/main/java/ElBuenSabor/ProyectoFinal/Entities/Cliente.java
package ElBuenSabor.ProyectoFinal.Entities;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet; // Importa HashSet
import java.util.List;
import java.util.Set;     // Importa Set

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

    @Column(unique = true)
    private String email;

    private String password;
    private LocalDate fechaNacimiento;

    // CAMBIA ESTO:
    // @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL, orphanRemoval = true)
    // private List<Domicilio> domicilios = new ArrayList<>();

    // AÑADE ESTO:
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE}) // Usamos PERSIST y MERGE para ManyToMany
    @JoinTable(
            name = "cliente_domicilio", // Nombre de la tabla de unión
            joinColumns = @JoinColumn(name = "cliente_id"), // Columna para el ID del cliente
            inverseJoinColumns = @JoinColumn(name = "domicilio_id") // Columna para el ID del domicilio
    )
    private Set<Domicilio> domicilios = new HashSet<>(); // Un cliente puede tener varios domicilios

    @OneToOne
    @JoinColumn(name = "usuario_id", unique = true)
    private Usuario usuario;

    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL)
    private List<Pedido> pedidos = new ArrayList<>();

    @OneToOne
    @JoinColumn(name = "imagen_id")
    private Imagen imagen;

}