package ElBuenSabor.ProyectoFinal.Entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Table; // Asegúrate de tener este import si usas @Table
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

@Entity
@Table(name = "imagen") // Nombre de tabla correcto
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@SQLDelete(sql = "UPDATE imagen SET baja = true WHERE id = ?") // SQLDelete para tabla 'imagen'
@Where(clause = "baja = false") // Filtrar por baja = false
public class Imagen extends BaseEntity {
    // La denominacion podría ser la URL de la imagen o un identificador único del archivo.
    // Si es una URL, podría no ser única necesariamente si diferentes entidades pueden apuntar a la misma URL externa.
    // Si es un identificador de archivo gestionado por tu sistema, podría ser único.
    // Por ahora, no la marcamos como @Column(unique=true) a menos que sea un requisito específico.
    private String denominacion;
}
