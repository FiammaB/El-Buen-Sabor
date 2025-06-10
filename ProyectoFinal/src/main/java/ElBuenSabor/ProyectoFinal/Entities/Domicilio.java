package ElBuenSabor.ProyectoFinal.Entities;

import jakarta.persistence.*;
import lombok.*;

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
    private Integer cp; // Codigo Postal

    @ManyToOne
    @JoinColumn(name = "localidad_id")
    private Localidad localidad;

}
