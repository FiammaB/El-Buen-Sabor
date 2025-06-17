package ElBuenSabor.ProyectoFinal.Service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.itextpdf.io.source.ByteArrayOutputStream;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CloudinaryService {

    private final Cloudinary cloudinary;

    public String uploadFile(MultipartFile file) throws IOException {
        Map uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());
        return uploadResult.get("url").toString(); // URL de la imagen
    }


    /**
     * Sube un ByteArrayOutputStream (ej. un PDF) a Cloudinary.
     * @param outputStream El ByteArrayOutputStream que contiene los bytes del archivo.
     * @param publicId El ID público deseado para el archivo en Cloudinary (ej. "factura_123").
     * @return La URL segura del archivo subido.
     * @throws IOException Si ocurre un error durante la subida.
     */
    public String uploadByteArray(ByteArrayOutputStream outputStream, String publicId) throws IOException {
        // Convierte el ByteArrayOutputStream a un array de bytes
        byte[] fileBytes = outputStream.toByteArray();
        // Prepara los parámetros para la subida (ej. public_id, folder)
        Map params = ObjectUtils.asMap(
                "public_id", publicId,
                "resource_type", "raw", // O 'auto' o 'pdf' si solo subes PDFs
                "folder", "facturas_pdf" // Opcional: una carpeta específica en Cloudinary
        );
        Map uploadResult = cloudinary.uploader().upload(fileBytes, params);
        return uploadResult.get("secure_url").toString();
    }
}
