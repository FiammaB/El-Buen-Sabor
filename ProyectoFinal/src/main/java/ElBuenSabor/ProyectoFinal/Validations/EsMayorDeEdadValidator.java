package ElBuenSabor.ProyectoFinal.Validations;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.time.LocalDate;
import java.time.Period;

public class EsMayorDeEdadValidator implements ConstraintValidator<EsMayorDeEdad, LocalDate> {

    private int minAge;
    private int maxAge;

    @Override
    public void initialize(EsMayorDeEdad constraintAnnotation) {
        this.minAge = constraintAnnotation.min();
        this.maxAge = constraintAnnotation.max();
    }

    @Override
    public boolean isValid(LocalDate fechaNacimiento, ConstraintValidatorContext context) {
        if (fechaNacimiento == null) {
            return true; // @NotNull ya maneja los nulos. Aquí solo validamos si hay fecha.
        }

        LocalDate today = LocalDate.now();
        Period age = Period.between(fechaNacimiento, today);
        int years = age.getYears();

        if (years < minAge || years > maxAge) {
            // Personaliza el mensaje de error para incluir los valores min y max
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(
                    context.getDefaultConstraintMessageTemplate()
                            .replace("{min}", String.valueOf(minAge))
                            .replace("{max}", String.valueOf(maxAge))
            ).addConstraintViolation();
            return false;
        }
        return true;
    }
}