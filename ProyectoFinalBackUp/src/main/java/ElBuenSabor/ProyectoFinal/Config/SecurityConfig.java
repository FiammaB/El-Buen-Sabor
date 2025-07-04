package ElBuenSabor.ProyectoFinal.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder; // Necesario para el bean passwordEncoder
import org.springframework.security.crypto.password.PasswordEncoder; // Necesario para el bean passwordEncoder

// Importaciones CORS si las tienes (déjalas comentadas si no se usan)
// import org.springframework.web.cors.CorsConfiguration;
// import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
// import java.util.Arrays;

@Configuration
@EnableWebSecurity // Mantener esta anotación
public class SecurityConfig {

    // Mantén el bean passwordEncoder, aunque no se use activamente si la seguridad está deshabilitada
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // ¡TEMPORALMENTE: DESHABILITAR TODA LA SEGURIDAD PARA SIMPLIFICAR!
        http
                .csrf(csrf -> csrf.disable()) // Deshabilitar CSRF
                // No configurar CORS aquí, lo haremos globalmente más tarde o en controladores
                .authorizeHttpRequests(authorize -> authorize.anyRequest().permitAll()); // Permitir todo
        return http.build();
    }

    // Puedes comentar o eliminar el bean de corsConfigurationSource por ahora si no lo necesitas globalmente
    // @Bean
    // public CorsConfigurationSource corsConfigurationSource() {
    //    // ... (tu configuración CORS) ...
    // }
}