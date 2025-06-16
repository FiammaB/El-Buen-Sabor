package ElBuenSabor.ProyectoFinal.Configuration;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CloudinaryConfig {

    @Bean
    public Cloudinary cloudinary() {
        return new Cloudinary(ObjectUtils.asMap(
                "cloud_name", "deagcdoak",
                "api_key", "472886418379767",
                "api_secret", "lDslxb-eGE6ofgq-s_elbJpKhv4",
                "secure", true
        ));
    }
}
