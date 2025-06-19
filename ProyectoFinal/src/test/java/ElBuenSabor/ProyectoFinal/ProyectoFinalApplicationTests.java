package ElBuenSabor.ProyectoFinal;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(properties = {
		"spring.security.enabled=false",
		"spring.jpa.hibernate.ddl-auto=create-drop",
		"spring.datasource.url=jdbc:mysql://localhost:3306/elbuensabor_test?createDatabaseIfNotExist=true",
		"app.data.loader.enabled=false",
		"spring.mail.host=false",
		"mercadopago.enabled=false"
})
class ProyectoFinalApplicationTests {

	@Test
	void contextLoads() {
	}
}