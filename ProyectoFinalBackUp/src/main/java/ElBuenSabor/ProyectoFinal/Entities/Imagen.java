package ElBuenSabor.ProyectoFinal.Entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "imagen")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Imagen extends BaseEntity {
    @Column(length = 500)
    private String denominacion; // Por ejemplo, la URL de la imagen


}