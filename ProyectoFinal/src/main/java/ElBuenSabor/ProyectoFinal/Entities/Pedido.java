package ElBuenSabor.ProyectoFinal.Entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "pedido")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Pedido extends BaseEntity {

    private LocalTime horaEstimadaFinalizacion;
    private Double total;
    private Double totalCosto;

    @Enumerated(EnumType.STRING)
    private Estado estado;

    @Enumerated(EnumType.STRING)
    private TipoEnvio tipoEnvio;

    @Enumerated(EnumType.STRING)
    private FormaPago formaPago;

    private LocalDate fechaPedido;

    // Relaciones
    @ManyToOne
    @JoinColumn(name = "cliente_id")
    private Cliente cliente;

    @ManyToOne
    @JoinColumn(name = "domicilio_id")
    private Domicilio domicilioEntrega;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "factura_id", referencedColumnName = "id")
    private Factura factura;

    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<DetallePedido> detallesPedidos = new HashSet<>();

    // ¡ESTA ES LA ADICIÓN!
    @ManyToOne
    @JoinColumn(name = "sucursal_id")
    private Sucursal sucursal; // Un pedido se realiza en una sucursal
    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;
}