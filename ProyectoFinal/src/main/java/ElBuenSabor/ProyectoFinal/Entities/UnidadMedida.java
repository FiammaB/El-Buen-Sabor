package ElBuenSabor.ProyectoFinal.Entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

@Entity
@Table(name = "unidad_medida") // Nombre de tabla correcto
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@SQLDelete(sql = "UPDATE unidad_medida SET baja = true WHERE id = ?") // SQLDelete para tabla 'unidad_medida'
@Where(clause = "baja = false") // Filtrar por baja = false
public class UnidadMedida extends BaseEntity {

    @Column(unique = true) // La denominación debe ser única
    private String denominacion; // Ej: "kg", "gr", "unidad", "litro"
}
