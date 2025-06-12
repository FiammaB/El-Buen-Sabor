package ElBuenSabor.ProyectoFinal.Controllers;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@CrossOrigin(origins = "http://localhost:5173")
public class ImagenController {

    @GetMapping("/uploads/{filename:.+}")
    public ResponseEntity<Resource> getImage(@PathVariable String filename) {
        try {
            // Buscar el archivo en la carpeta uploads
            Path filePath = Paths.get("uploads").resolve(filename).normalize();
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists() && resource.isReadable()) {
                String contentType = "image/png";
                if (filename.toLowerCase().endsWith(".jpg") || filename.toLowerCase().endsWith(".jpeg")) {
                    contentType = "image/jpeg";
                }

                return ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType(contentType))
                        .header(HttpHeaders.CACHE_CONTROL, "no-cache")
                        .body(resource);
            } else {
                System.out.println("Archivo no encontrado: " + filePath.toAbsolutePath());
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            System.out.println("Error al servir imagen: " + e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
}