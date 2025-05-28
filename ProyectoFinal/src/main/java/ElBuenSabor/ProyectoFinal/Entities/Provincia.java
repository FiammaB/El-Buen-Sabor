package ElBuenSabor.ProyectoFinal.Entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

@Entity
@Table(name = "provincia")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@SQLDelete(sql = "UPDATE provincia SET baja = true WHERE id = ?") // SQLDelete para tabla 'provincia'
@Where(clause = "baja = false") // Filtrar por baja = false
public class Provincia extends BaseEntity {

    private String nombre;

    @ManyToOne
    @JoinColumn(name = "pais_id")
    private Pais pais;
}
