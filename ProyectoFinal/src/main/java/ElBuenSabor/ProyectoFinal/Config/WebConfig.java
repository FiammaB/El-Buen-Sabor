package ElBuenSabor.ProyectoFinal.Config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Paths;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // aplica a todas las rutas
                .allowedOrigins("https://localhost:5173","https://localhost:5174", "https://localhost:5175", "https://localhost:5176") // permite tu frontend
                .allowedMethods("*") // permite todos los métodos HTTP
                .allowedHeaders("*")
                .allowCredentials(true);
    }
}