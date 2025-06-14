package ElBuenSabor.ProyectoFinal.Service;

import ElBuenSabor.ProyectoFinal.Entities.Imagen;
import ElBuenSabor.ProyectoFinal.Exceptions.ResourceNotFoundException;
import ElBuenSabor.ProyectoFinal.Repositories.ImagenRepository;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ImagenServiceImpl implements ImagenService {

    private final Cloudinary cloudinary;
    private final ImagenRepository imagenRepository;

    @Override
    public Imagen upload(MultipartFile file) {
        try {
            Map<?, ?> result = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());

            Imagen imagen = new Imagen();
            imagen.setDenominacion((String) result.get("secure_url")); // o `url`
            // Si querés guardar el public_id:
            // imagen.setPublicId((String) result.get("public_id"));
            return imagenRepository.save(imagen);

        } catch (IOException e) {
            throw new RuntimeException("Error al subir la imagen a Cloudinary", e);
        }
    }

    @Override
    public void deleteById(Long id) {
        imagenRepository.deleteById(id);
    }

    @Override
    public List<Imagen> findAll() {
        return imagenRepository.findAll();
    }

    @Override
    public Imagen findById(Long id) {
        return imagenRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Imagen no encontrada con id: " + id));
    }

    @PostConstruct
    public void testCloudinary() {
        System.out.println("⏳ Testeando conexión Cloudinary...");
        System.out.println(cloudinary.config); // debería imprimir las claves y cloud_name

        try {
            Map result = cloudinary.uploader().upload("https://res.cloudinary.com/demo/image/upload/sample.jpg", ObjectUtils.emptyMap());
            System.out.println("✅ Cloudinary conectado. URL de muestra: " + result.get("secure_url"));
        } catch (Exception e) {
            System.err.println("❌ Falló la conexión a Cloudinary: " + e.getMessage());
        }
    }
}
