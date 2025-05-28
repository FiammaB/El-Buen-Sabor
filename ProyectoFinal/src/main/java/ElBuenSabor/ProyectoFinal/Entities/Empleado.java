package ElBuenSabor.ProyectoFinal.Entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.time.LocalDate; // Para fecha de nacimiento o alta
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "empleado") // Nombre de la tabla para empleados
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
// No usar @Builder directamente en clases con herencia y @MappedSuperclass si el builder del padre no se maneja.
// Es mejor tener constructores específicos o un builder personalizado si es necesario.
// O usar @SuperBuilder de Lombok si está configurado para funcionar con la jerarquía.
// Por ahora, usaremos constructores y setters.
@SQLDelete(sql = "UPDATE empleado SET baja = true WHERE id = ?")
@Where(clause = "baja = false")
public class Empleado extends Usuario { // Hereda id, baja, auth0Id, username, pedidos (de Usuario)

    private String nombre;
    private String apellido;
    private String telefono;

    @Column(unique = true, nullable = false)
    private String email; // Email del empleado, debe ser único

    @Column(nullable = false)
    private String password; // Contraseña encriptada

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Rol rol; // Rol del empleado (CAJERO, COCINERO, DELIVERY, ADMINISTRADOR)

    // Campos adicionales que podrían ser útiles para un empleado
    private LocalDate fechaNacimiento;
    private LocalDate fechaAlta;
    // private LocalDate fechaBaja; // El campo 'baja' de BaseEntity ya maneja esto lógicamente

    // Un empleado puede tener domicilios, similar a un cliente.
    // Si un empleado puede tener múltiples domicilios (ej. personal y de trabajo si fuera relevante)
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "empleado_id") // Esto creará una columna empleado_id en la tabla Domicilio
    private List<Domicilio> domicilios = new ArrayList<>();

    // Un empleado puede tener una imagen de perfil
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "imagen_id")
    private Imagen imagen;

    // Constructor específico para facilitar la creación
    public Empleado(String auth0Id, String username, String nombre, String apellido, String telefono, String email, String password, Rol rol, LocalDate fechaAlta) {
        super(); // Llama al constructor de Usuario (que llama al de BaseEntity)
        this.setAuth0Id(auth0Id); // Heredado de Usuario
        this.setUsername(username); // Heredado de Usuario
        this.nombre = nombre;
        this.apellido = apellido;
        this.telefono = telefono;
        this.email = email;
        this.password = password; // Ya debería estar encriptada al llegar aquí
        this.rol = rol;
        this.fechaAlta = fechaAlta != null ? fechaAlta : LocalDate.now();
        this.setBaja(false); // Heredado de BaseEntity
    }
}
