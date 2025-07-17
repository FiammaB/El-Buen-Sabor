package ElBuenSabor.ProyectoFinal.Entities;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "usuario")
@Inheritance(strategy = InheritanceType.JOINED)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@SuperBuilder
public class Usuario extends BaseEntity {

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    private String username;

    @Enumerated(EnumType.STRING)
    private Rol rol;

    @Column(nullable = false)
    private boolean primerInicio = true;

    private boolean baja;
    // 👇 AÑADÍ ESTE MÉTODO
    public boolean isBaja() {
        return baja;
    };


}
