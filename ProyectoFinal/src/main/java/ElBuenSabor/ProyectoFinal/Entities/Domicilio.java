package ElBuenSabor.ProyectoFinal.Entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.util.ArrayList; // Para inicializar la lista de pedidos
import java.util.List;

@Entity
@Table(name = "domicilio") // Nombre de tabla correcto
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@SQLDelete(sql = "UPDATE domicilio SET baja = true WHERE id = ?") // SQLDelete para tabla 'domicilio'
@Where(clause = "baja = false") // Filtrar por baja = false
public class Domicilio extends BaseEntity {

    private String calle;
    private Integer numero;
    private Integer cp; // Código Postal

    @ManyToOne // Un domicilio pertenece a una Localidad
    @JoinColumn(name = "localidad_id")
    private Localidad localidad;

    // Un domicilio puede estar asociado a muchos pedidos (como domicilio de entrega)
    // mappedBy="domicilioEntrega" si en Pedido el campo se llama domicilioEntrega
    // Considerar FetchType.LAZY para no cargar todos los pedidos innecesariamente.
    // CascadeType: Generalmente no queremos que al borrar un domicilio se borren sus pedidos.
    // Los pedidos tienen su propio ciclo de vida y borrado lógico.
    @OneToMany(mappedBy = "domicilioEntrega", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
    @Builder.Default // Para que Lombok Builder inicialice la lista
    private List<Pedido> pedidos = new ArrayList<>();

    // Un domicilio puede estar asociado a muchos clientes (un cliente puede tener varios domicilios)
    // Esta relación se maneja desde Cliente (Cliente OneToMany Domicilio).
    // Si necesitas navegar desde Domicilio a Cliente, necesitarías un @ManyToOne aquí.
    // Por ahora, tu modelo tiene Cliente @OneToMany Domicilio @JoinColumn(name="cliente_id")
    // lo que significa que la FK está en la tabla domicilio.
    // Si un domicilio puede pertenecer a UN SOLO cliente:
    // @ManyToOne(fetch = FetchType.LAZY)
    // @JoinColumn(name = "cliente_id") // Esta columna ya la tienes por la relación desde Cliente
    // private Cliente cliente;
    // Sin embargo, el modelo actual (Cliente tiene una List<Domicilio> con @JoinColumn)
    // implica que un Domicilio está asociado a un Cliente a través de la FK en la tabla Domicilio.
    // Esto está bien. No necesitamos un campo 'cliente' aquí si la navegación es unidireccional desde Cliente.

    // Un domicilio puede estar asociado a UNA Sucursal (Sucursal OneToOne Domicilio)
    // Esta relación se maneja desde Sucursal.
    // @OneToOne(mappedBy = "domicilio")
    // private Sucursal sucursal;
    // No es necesario si la navegación es unidireccional desde Sucursal.
}
