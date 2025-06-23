package ElBuenSabor.ProyectoFinal.Entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "pais")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Pais extends BaseEntity {

    private String nombre;
}
