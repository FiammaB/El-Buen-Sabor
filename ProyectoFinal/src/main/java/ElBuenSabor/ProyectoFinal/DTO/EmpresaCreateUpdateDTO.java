package ElBuenSabor.ProyectoFinal.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
// Para validación de CUIL/CUIT, podrías usar @Pattern si tienes un formato específico.
// import org.hibernate.validator.constraints.AR.CUIT; // Si usas esta dependencia específica
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EmpresaCreateUpdateDTO {

    @NotBlank(message = "El nombre de la empresa es obligatorio")
    @Size(min = 2, max = 100, message = "El nombre debe tener entre 2 y 100 caracteres")
    private String nombre;

    @NotBlank(message = "La razón social es obligatoria")
    @Size(min = 2, max = 150, message = "La razón social debe tener entre 2 y 150 caracteres")
    private String razonSocial;

    @NotNull(message = "El CUIL/CUIT es obligatorio")
    // @CUIT // Si decides usar la validación específica de CUIT de Hibernate Validator AR
    // O un @Pattern para el formato numérico si es Integer.
    // Si es String, @Pattern(regexp = "^[0-9]{2}-[0-9]{8}-[0-9]$") para formato con guiones.
    @Positive(message = "El CUIL debe ser un número positivo") // Si se mantiene como Integer
    private Integer cuil;
}