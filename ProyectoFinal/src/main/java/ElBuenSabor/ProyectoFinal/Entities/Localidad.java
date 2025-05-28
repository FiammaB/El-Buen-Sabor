package ElBuenSabor.ProyectoFinal.Entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

@Entity
@Table(name = "localidad") // Nombre de tabla correcto
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@SQLDelete(sql = "UPDATE localidad SET baja = true WHERE id = ?") // SQLDelete para tabla 'localidad'
@Where(clause = "baja = false") // Filtrar por baja = false
public class Localidad extends BaseEntity {

    private String nombre;

    @ManyToOne // Relación con Provincia
    @JoinColumn(name = "provincia_id") // Nombre de la columna de clave foránea
    private Provincia provincia;
}