package ElBuenSabor.ProyectoFinal.DTO;


import lombok.*;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegistroAnulacionDTO {
    private Long id;
    private LocalDateTime fechaHoraAnulacion;
    private String motivoAnulacion;
    private Long usuarioAnuladorId; // Solo el ID del usuario
    private Long facturaAnuladaId; // Solo el ID de la factura
    private Long notaCreditoGeneradaId; // Solo el ID de la nota de crédito
}
