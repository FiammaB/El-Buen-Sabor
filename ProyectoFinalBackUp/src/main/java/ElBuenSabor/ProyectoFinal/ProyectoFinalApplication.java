package ElBuenSabor.ProyectoFinal;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean; // Asegúrate de tener este import
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder; // Importa la implementación
import org.springframework.security.crypto.password.PasswordEncoder; // Importa la interfaz

@SpringBootApplication
public class ProyectoFinalApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProyectoFinalApplication.class, args);
	}




}