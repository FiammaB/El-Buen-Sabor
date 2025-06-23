package ElBuenSabor.ProyectoFinal.Entities;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "empresa")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Empresa extends BaseEntity {

    private String nombre;
    private String razonSocial;
    private int cuil;
    @OneToMany(mappedBy = "empresa", cascade = CascadeType.ALL, orphanRemoval = true) // Añadir orphanRemoval si deseas que las sucursales se eliminen con la empresa
    private List<Sucursal> sucursales = new ArrayList<>();

}