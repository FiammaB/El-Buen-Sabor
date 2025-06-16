package ElBuenSabor.ProyectoFinal.Entities;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "provincia")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Provincia extends BaseEntity {

    private String nombre;

    @OneToMany(mappedBy = "provincia", cascade = CascadeType.ALL)
    private Set<Localidad> localidades = new HashSet<>();

    @ManyToOne
    @JoinColumn(name = "pais_id")
    private Pais pais;
}
