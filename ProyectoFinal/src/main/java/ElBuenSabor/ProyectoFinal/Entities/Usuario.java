package ElBuenSabor.ProyectoFinal.Entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "usuario")
@Inheritance(strategy = InheritanceType.JOINED) // Usamos JOINED para separar las tablas por tipo de usuario
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public abstract class Usuario extends BaseEntity {

    @Column(unique = true)
    private String auth0Id; // ID de autenticación, por si usas Auth0 o similar
    @Column(unique = true)
    private String username;
    private Rol rol;

}
