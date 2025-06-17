// ProyectoFinal/src/main/java/ElBuenSabor/ProyectoFinal/Entities/RegistroAnulacion.java
package ElBuenSabor.ProyectoFinal.Entities;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Entity
@Table(name = "registro_anulacion")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@SuperBuilder
public class RegistroAnulacion extends BaseEntity {

    private LocalDateTime fechaHoraAnulacion;
    private String motivoAnulacion;

    @ManyToOne // El usuario que realizó la anulación
    @JoinColumn(name = "usuario_anulador_id")
    private Usuario usuarioAnulador;

    @OneToOne // Referencia a la factura que fue anulada
    @JoinColumn(name = "factura_anulada_id")
    private Factura facturaAnulada; // <-- Apunta a Factura

    @OneToOne // Referencia a la nota de crédito generada
    @JoinColumn(name = "nota_credito_id")
    private NotaCredito notaCreditoGenerada; // <-- Apunta a NotaCredito
}
