package ElBuenSabor.ProyectoFinal.Entities;

import jakarta.persistence.*; // Asegúrate de tener todos los imports de persistence
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "empresa") // Nombre de tabla correcto
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@SQLDelete(sql = "UPDATE empresa SET baja = true WHERE id = ?") // SQLDelete para tabla 'empresa'
@Where(clause = "baja = false") // Filtrar por baja = false
public class Empresa extends BaseEntity {

    private String nombre;
    private String razonSocial;

    @Column(unique = true) // El CUIL de la empresa debería ser único
    private Integer cuil; // Las consignas no especifican el tipo, Integer o String son opciones. String es más flexible.

    // Relación con Sucursal: Una empresa tiene muchas sucursales.
    // mappedBy="empresa" indica que la entidad Sucursal es la dueña de la relación
    // y tiene un campo 'empresa' que la mapea.
    // CascadeType.ALL: Si se borra una empresa, se borran sus sucursales (considerar si esto es deseado para borrado físico).
    // Para borrado lógico, la cascada de borrado no se aplica de la misma manera.
    // orphanRemoval=true: Si una sucursal se quita de la lista 'sucursales' de una empresa y se guarda la empresa,
    // la sucursal huérfana se elimina de la base de datos. Usar con cuidado.
    @OneToMany(mappedBy = "empresa", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default // Para que Lombok Builder inicialice la lista
    private List<Sucursal> sucursales = new ArrayList<>();

    // Helper methods para la relación bidireccional con Sucursal (opcional pero recomendado)
    public void addSucursal(Sucursal sucursal) {
        sucursales.add(sucursal);
        sucursal.setEmpresa(this);
    }

    public void removeSucursal(Sucursal sucursal) {
        sucursales.remove(sucursal);
        sucursal.setEmpresa(null);
    }
}