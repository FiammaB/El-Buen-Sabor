package ElBuenSabor.ProyectoFinal.Entities;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Entity
@Table(name = "usuario")
@Inheritance(strategy = InheritanceType.JOINED) // Usamos JOINED para separar las tablas por tipo de usuario
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Usuario extends BaseEntity {

    @Column(unique = true)
    private String auth0Id;
    @Column(unique = true)
    private String username;

}
