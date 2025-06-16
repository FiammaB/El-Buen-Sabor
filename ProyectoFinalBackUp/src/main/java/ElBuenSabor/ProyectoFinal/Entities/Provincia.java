package ElBuenSabor.ProyectoFinal.Entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "provincia")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Provincia extends BaseEntity {

    private String nombre;

    @ManyToOne
    @JoinColumn(name = "pais_id")
    private Pais pais;
}
