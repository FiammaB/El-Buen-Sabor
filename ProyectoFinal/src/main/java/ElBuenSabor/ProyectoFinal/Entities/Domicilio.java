// ProyectoFinal/src/main/java/ElBuenSabor/ProyectoFinal/Entities/Domicilio.java
package ElBuenSabor.ProyectoFinal.Entities;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "domicilio")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Domicilio extends BaseEntity {

    private String calle;
    private Integer numero;
    private Integer cp; // Código Postal

    @ManyToOne
    @JoinColumn(name = "localidad_id")
    private Localidad localidad;

    @ManyToMany(mappedBy = "domicilios")
    private Set<Cliente> clientes = new HashSet<>();

    @OneToMany(mappedBy = "domicilioEntrega")
    private List<Pedido> pedidos;

    public Cliente getCliente() {
        return clientes.stream().findFirst().orElse(null);
    }

}