// ProyectoFinal/src/main/java/ElBuenSabor/ProyectoFinal/Entities/NotaCredito.java
package ElBuenSabor.ProyectoFinal.Entities;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "nota_credito") // Tabla dedicada para notas de crédito
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@SuperBuilder // Si hereda de BaseEntity
public class NotaCredito extends BaseEntity { // Extiende BaseEntity para id y baja

    private LocalDate fechaEmision;
    private Double total; // El mismo importe que la factura original
    private String motivo; // Motivo de la nota de crédito

    @OneToOne // Referencia a la factura que anula
    @JoinColumn(name = "factura_anulada_id")
    private Factura facturaAnulada; // <-- Referencia a la Factura (no FacturaVenta)

    @ManyToOne // Referencia al cliente original
    @JoinColumn(name = "cliente_id")
    private Cliente cliente; // <-- Cliente al que se emite la nota

    // Los detalles de la nota de crédito serán una COPIA de los del pedido original
    // Reutilizamos DetallePedido para la estructura, con FK a NotaCredito
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "nota_credito_id") // Esta FK se creará en la tabla DetallePedido
    private Set<DetallePedido> detalles = new HashSet<>(); // <-- Ítems de la nota de crédito

    // Opcional: URL del PDF de la Nota de Crédito si también se genera
    @Column(length = 500)
    private String urlPdfNotaCredito;

    @OneToOne // Referencia al pedido original de donde viene la factura
    @JoinColumn(name = "pedido_original_id") // Columna de clave foránea
    private Pedido pedidoOriginal;
}