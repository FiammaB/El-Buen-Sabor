package ElBuenSabor.ProyectoFinal.Entities;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "cliente")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Cliente extends Usuario {

    private String nombre;
    private String apellido;
    private String telefono;
    @Column(unique = true) // El email del cliente debe ser único [cite: 19]
    private String email;
    private String password; // La contraseña encriptada [cite: 20]
    private LocalDate fechaNacimiento; // Según tu diagrama [cite: 251]

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "cliente_id")
    private List<Domicilio> domicilios;
    @OneToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;
    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL)
    private List<Pedido> pedidos = new ArrayList<>();
    // Puedes agregar un campo para manejar el estado de baja del cliente [cite: 33]
    private boolean estaDadoDeBaja = false; // Por defecto, no está dado de baja

    @OneToOne
    @JoinColumn(name ="imagen_id")
    private Imagen imagen;

}

