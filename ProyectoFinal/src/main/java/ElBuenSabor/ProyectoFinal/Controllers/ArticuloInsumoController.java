package ElBuenSabor.ProyectoFinal.Controllers;

import ElBuenSabor.ProyectoFinal.DTO.ArticuloInsumoDTO;

import ElBuenSabor.ProyectoFinal.Entities.ArticuloInsumo;
import ElBuenSabor.ProyectoFinal.Mappers.ArticuloInsumoMapper;
import ElBuenSabor.ProyectoFinal.Repositories.CategoriaRepository;
import ElBuenSabor.ProyectoFinal.Repositories.ImagenRepository;
import ElBuenSabor.ProyectoFinal.Repositories.UnidadMedidaRepository;
import ElBuenSabor.ProyectoFinal.Service.ArticuloInsumoService; // Usar la interfaz específica

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/articuloInsumo") // Define la URL base para este controlador
// ArticuloInsumoController ahora extiende BaseController
public class ArticuloInsumoController extends BaseController<ArticuloInsumo, Long> {

    private final ArticuloInsumoMapper articuloInsumoMapper;
    private final CategoriaRepository categoriaRepository;
    private final UnidadMedidaRepository unidadMedidaRepository;
    private final ImagenRepository imagenRepository;

    // El constructor inyecta el servicio específico de ArticuloInsumo
    public ArticuloInsumoController(
            ArticuloInsumoService articuloInsumoService, // Servicio específico
            ArticuloInsumoMapper articuloInsumoMapper,
            CategoriaRepository categoriaRepository,
            UnidadMedidaRepository unidadMedidaRepository,
            ImagenRepository imagenRepository) {
        super(articuloInsumoService); // Pasa el servicio al constructor del BaseController
        this.articuloInsumoMapper = articuloInsumoMapper;
        this.categoriaRepository = categoriaRepository;
        this.unidadMedidaRepository = unidadMedidaRepository;
        this.imagenRepository = imagenRepository;
    }

    // Sobrescribir getAll para devolver DTOs y manejar excepciones
    @GetMapping("/insumos") // Puedes mantener tu endpoint específico si quieres
    @Override // Sobrescribe el getAll del BaseController
    public ResponseEntity<?> getAll() {
        try {
            List<ArticuloInsumo> insumos = baseService.findAll(); // Llama al findAll del padre
            List<ArticuloInsumoDTO> dtos = insumos.stream()
                    .map(articuloInsumoMapper::toDTO)
                    .toList();
            return ResponseEntity.ok(dtos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    // Sobrescribir getOne para devolver un DTO y manejar excepciones
    @GetMapping("/{id}") // Puedes mantener tu endpoint específico si quieres
    @Override // Sobrescribe el getOne del BaseController
    public ResponseEntity<?> getOne(@PathVariable Long id) {
        try {
            ArticuloInsumo insumo = baseService.findById(id); // Llama al findById del padre
            return ResponseEntity.ok(articuloInsumoMapper.toDTO(insumo));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    @PostMapping(consumes = "application/json")

    public ResponseEntity<?> create(@RequestBody ArticuloInsumoDTO dto) {
        try {
            ArticuloInsumo entity = articuloInsumoMapper.toEntity(dto);

            // Establecer las relaciones ManyToOne
            entity.setCategoria(categoriaRepository.findById(dto.getCategoriaId()).orElse(null));
            entity.setUnidadMedida(unidadMedidaRepository.findById(dto.getUnidadMedidaId()).orElse(null));
            entity.setImagen(imagenRepository.findById(dto.getImagenId()).orElse(null));
            entity.setBaja(false); // Por defecto, un nuevo artículo está activo

            ArticuloInsumo saved = baseService.save(entity); // Llama al save del padre
            return ResponseEntity.status(HttpStatus.CREATED).body(articuloInsumoMapper.toDTO(saved)); // Convierte a DTO para la respuesta
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    // Sobrescribir update para aceptar un DTO de entrada, mapear y manejar excepciones
    @PutMapping(value = "/{id}", consumes = "application/json")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody ArticuloInsumoDTO dto) {
        try {
            ArticuloInsumo entity = articuloInsumoMapper.toEntity(dto);

            // Establecer las relaciones ManyToOne
            entity.setCategoria(categoriaRepository.findById(dto.getCategoriaId()).orElse(null));
            entity.setUnidadMedida(unidadMedidaRepository.findById(dto.getUnidadMedidaId()).orElse(null));
            entity.setImagen(imagenRepository.findById(dto.getImagenId()).orElse(null));

            ArticuloInsumo updated = baseService.update(id, entity); // Llama al update del padre
            return ResponseEntity.ok(articuloInsumoMapper.toDTO(updated)); // Convierte a DTO para la respuesta
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }


}