package ElBuenSabor.ProyectoFinal.Entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "cliente") // Nombre de tabla correcto
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
// No usar @Builder directamente en clases con herencia y @MappedSuperclass si el builder del padre no se maneja.
// Es mejor tener constructores específicos o un builder personalizado si es necesario.
// O usar @SuperBuilder de Lombok si está configurado para funcionar con la jerarquía.
@SQLDelete(sql = "UPDATE cliente SET baja = true WHERE id = ?")
@Where(clause = "baja = false")
public class Cliente extends Usuario { // Hereda id, baja, auth0Id, username

    private String nombre;
    private String apellido;
    private String telefono;

    @Column(unique = true, nullable = false) // El email del cliente, también es el username heredado.
    private String email;

    @Column(nullable = false)
    private String password; // Contraseña encriptada

    private LocalDate fechaNacimiento;

    // Relación con Domicilio: Un cliente puede tener muchos domicilios.
    // Cliente es el dueño de la relación. La FK 'cliente_id' estará en la tabla 'domicilio'.
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id") // Esto crea la columna cliente_id en la tabla Domicilio
    @Builder.Default // Si usas @Builder en Cliente, necesitas esto para inicializar
    private List<Domicilio> domicilios = new ArrayList<>();

    // Relación con Pedido: Un cliente puede tener muchos pedidos.
    // mappedBy="cliente" indica que la entidad Pedido es la dueña.
    @OneToMany(mappedBy = "cliente", cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Pedido> pedidos = new ArrayList<>();

    // Un cliente puede tener una imagen de perfil
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "imagen_id")
    private Imagen imagen;

    // El campo 'estaDadoDeBaja' se elimina, se usa 'baja' de BaseEntity.
    // El rol es implícitamente CLIENTE.

    // Constructor para facilitar la creación
    public Cliente(String auth0Id, String username, String nombre, String apellido, String telefono, String email, String password, LocalDate fechaNacimiento) {
        super();
        this.setAuth0Id(auth0Id);
        this.setUsername(username); // que será el email
        this.nombre = nombre;
        this.apellido = apellido;
        this.telefono = telefono;
        this.email = email;
        this.password = password; // Ya debería estar encriptada
        this.fechaNacimiento = fechaNacimiento;
        this.setBaja(false); // Heredado de BaseEntity
    }
}
