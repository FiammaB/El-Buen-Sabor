package ElBuenSabor.ProyectoFinal.Service;

import ElBuenSabor.ProyectoFinal.Entities.Imagen;
import ElBuenSabor.ProyectoFinal.Exceptions.ResourceNotFoundException; // Posiblemente ya no sea necesaria
import ElBuenSabor.ProyectoFinal.Repositories.ImagenRepository;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
// Ya no es necesario si se inyecta por constructor explícito al padre
// import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.transaction.annotation.Transactional; // Importar Transactional

import java.io.IOException;
import java.util.List; // Importar List si no se usara el findAll del padre
import java.util.Map;

@Service
// ImagenServiceImpl ahora extiende BaseServiceImpl
// y la interfaz ImagenService (que debe extender BaseService)
public class ImagenServiceImpl extends BaseServiceImpl<Imagen, Long> implements ImagenService {

    private final Cloudinary cloudinary; // Cloudinary sigue siendo una dependencia específica de este servicio

    // El constructor ahora inyecta tanto el repositorio (para el padre) como Cloudinary
    public ImagenServiceImpl(ImagenRepository imagenRepository, Cloudinary cloudinary) {
        super(imagenRepository); // Llama al constructor de la clase base
        this.cloudinary = cloudinary; // Inyecta Cloudinary
    }

    // Los métodos deleteById(), findAll(), findById() (y save() y update() genéricos)
    // ya están implementados en BaseServiceImpl y se heredan automáticamente.

    @Override
    @Transactional // Los métodos que modifican la BD deben ser transaccionales
    // Asegúrate de que declare 'throws Exception'
    public Imagen upload(MultipartFile file) throws Exception { // <<-- Añadir throws Exception
        try {
            Map<?, ?> result = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());

            Imagen imagen = new Imagen();
            imagen.setDenominacion((String) result.get("secure_url")); // o `url`
            imagen.setBaja(false); // Una imagen nueva no debería estar dada de baja por defecto

            // Usamos baseRepository.save() heredado del padre
            return baseRepository.save(imagen);

        } catch (IOException e) {
            // Envuelve la IOException en una Exception general para consistencia con BaseService
            throw new Exception("Error al subir la imagen a Cloudinary: " + e.getMessage(), e);
        }
    }

    // No necesitas sobreescribir el método 'update' si la lógica genérica de BaseServiceImpl
    // es suficiente para actualizar una Imagen (ej. solo cambiar su estado 'baja').
    // Si quisieras permitir actualizar la 'denominacion' de la imagen, sí necesitarías un @Override update.
}