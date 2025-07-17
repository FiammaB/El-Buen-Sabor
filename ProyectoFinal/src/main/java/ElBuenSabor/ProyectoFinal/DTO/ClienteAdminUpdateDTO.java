package ElBuenSabor.ProyectoFinal.DTO;

import lombok.Data;

@Data // O usa @Getter y @Setter
public class ClienteAdminUpdateDTO {
    private String telefono;
    private boolean activo; // Usamos 'activo' que es más intuitivo que 'baja'
}