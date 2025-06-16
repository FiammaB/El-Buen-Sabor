package ElBuenSabor.ProyectoFinal.Entities;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "imagen")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Imagen extends BaseEntity {

    @Column(length = 500)
    private String denominacion;

}