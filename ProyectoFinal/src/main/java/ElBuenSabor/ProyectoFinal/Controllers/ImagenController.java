package ElBuenSabor.ProyectoFinal.Controllers;

import ElBuenSabor.ProyectoFinal.DTO.ImagenDTO;
import ElBuenSabor.ProyectoFinal.Entities.Imagen;
import ElBuenSabor.ProyectoFinal.Mappers.ImagenMapper;
import ElBuenSabor.ProyectoFinal.Service.ImagenService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/api/imagenes")
@CrossOrigin(origins = { "http://localhost:5173", "http://localhost:5174" })
@RequiredArgsConstructor
public class ImagenController {

    private final ImagenService imagenService;
    private final ImagenMapper imagenMapper;

    @PostMapping("/upload")
    public ResponseEntity<ImagenDTO> upload(@RequestParam("file") MultipartFile file) {
        Imagen imagen = imagenService.upload(file);
        return ResponseEntity.status(HttpStatus.CREATED).body(imagenMapper.toDTO(imagen));
    }
}
