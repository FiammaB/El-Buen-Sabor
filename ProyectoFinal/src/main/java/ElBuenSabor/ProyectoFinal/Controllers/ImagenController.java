package ElBuenSabor.ProyectoFinal.Controllers;

import ElBuenSabor.ProyectoFinal.DTO.ImagenDTO;
import ElBuenSabor.ProyectoFinal.Entities.Imagen;
import ElBuenSabor.ProyectoFinal.Mappers.ImagenMapper;
import ElBuenSabor.ProyectoFinal.Service.ImagenService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap; // Importar HashMap
import java.util.List;
import java.util.Map;   // Importar Map

@RestController
@RequestMapping("/api/imagenes")
@CrossOrigin(origins = "http://localhost:5173")
public class ImagenController extends BaseController<Imagen, Long> {

    private final ImagenMapper imagenMapper;

    public ImagenController(
            ImagenService imagenService,
            ImagenMapper imagenMapper) {
        super(imagenService);
        this.imagenMapper = imagenMapper;
    }

    @GetMapping
    @Override
    public ResponseEntity<?> getAll() {
        try {
            List<Imagen> imagenes = baseService.findAll();
            List<ImagenDTO> dtos = imagenes.stream()
                    .map(imagenMapper::toDTO)
                    .toList();
            return ResponseEntity.ok(dtos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    @GetMapping("/{id}")
    @Override
    public ResponseEntity<?> getOne(@PathVariable Long id) {
        try {
            Imagen imagen = baseService.findById(id);
            return ResponseEntity.ok(imagenMapper.toDTO(imagen));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    @PostMapping("/upload")
    // <<--- CAMBIAR EL TIPO DE RETORNO A ResponseEntity<?>
    public ResponseEntity<?> upload(@RequestParam("file") MultipartFile file) {
        try {
            Imagen imagen = ((ImagenService) baseService).upload(file);
            return ResponseEntity.status(HttpStatus.CREATED).body(imagenMapper.toDTO(imagen));
        } catch (Exception e) {
            // <<--- DEVOLVER UN Map<String, String> COMO CUERPO DEL ERROR
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Error al subir la imagen: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
}