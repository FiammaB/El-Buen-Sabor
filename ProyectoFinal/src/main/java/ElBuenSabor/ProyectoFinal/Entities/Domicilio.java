// ProyectoFinal/src/main/java/ElBuenSabor/ProyectoFinal/Entities/Domicilio.java
package ElBuenSabor.ProyectoFinal.Entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

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
    @JsonIgnore
    private Set<Persona> personas = new HashSet<>();

    @OneToMany(mappedBy = "domicilioEntrega")
    private List<Pedido> pedidos;

    public Persona getCliente() {
        return personas.stream().findFirst().orElse(null);
    }

}