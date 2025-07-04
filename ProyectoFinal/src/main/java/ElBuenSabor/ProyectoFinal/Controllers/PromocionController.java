package ElBuenSabor.ProyectoFinal.Controllers;

import ElBuenSabor.ProyectoFinal.DTO.PromocionCreateDTO;
import ElBuenSabor.ProyectoFinal.DTO.PromocionDTO;
import ElBuenSabor.ProyectoFinal.Entities.ArticuloManufacturado;
import ElBuenSabor.ProyectoFinal.Entities.Promocion;
import ElBuenSabor.ProyectoFinal.Entities.Sucursal;
import ElBuenSabor.ProyectoFinal.Exceptions.ResourceNotFoundException;
import ElBuenSabor.ProyectoFinal.Mappers.PromocionMapper;
import ElBuenSabor.ProyectoFinal.Repositories.ArticuloManufacturadoRepository;
import ElBuenSabor.ProyectoFinal.Repositories.ImagenRepository;
import ElBuenSabor.ProyectoFinal.Repositories.SucursalRepository;
import ElBuenSabor.ProyectoFinal.Service.PromocionService; // Usar la interfaz específica
// Ya no es necesario si se inyecta por constructor explícito al padre
// import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate; // Importar LocalDate si es necesario
import java.time.LocalTime; // Importar LocalTime si es necesario
import java.util.List;
import java.util.Optional; // Importar Optional si es necesario
import java.util.stream.Collectors; // Importar Collectors si es necesario

@RestController
@RequestMapping("/api/promociones") // Define la URL base para este controlador
// PromocionController ahora extiende BaseController
public class PromocionController extends BaseController<Promocion, Long> {

    private final PromocionMapper promocionMapper;

    // Repositorios necesarios para resolver relaciones en el controlador
    private final ImagenRepository imagenRepository;
    private final ArticuloManufacturadoRepository articuloRepo;
    private final SucursalRepository sucursalRepository;

    // El constructor inyecta el servicio específico de Promocion y todas las dependencias adicionales
    public PromocionController(
            PromocionService promocionService, // Servicio específico
            PromocionMapper promocionMapper,
            ImagenRepository imagenRepository,
            ArticuloManufacturadoRepository articuloRepo,
            SucursalRepository sucursalRepository) {
        super(promocionService); // Pasa el servicio al constructor del BaseController
        this.promocionMapper = promocionMapper;
        this.imagenRepository = imagenRepository;
        this.articuloRepo = articuloRepo;
        this.sucursalRepository = sucursalRepository;
    }

    // Sobrescribir getAll para devolver DTOs y manejar excepciones
    @GetMapping
    @Override // Sobrescribe el getAll del BaseController
    public ResponseEntity<?> getAll() {
        try {
            List<Promocion> promociones = baseService.findAll(); // Llama al findAll del padre
            List<PromocionDTO> dtos = promociones.stream()
                    .map(promocionMapper::toDTO)
                    .toList();
            return ResponseEntity.ok(dtos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    // Sobrescribir getOne para devolver un DTO y manejar excepciones
    @GetMapping("/{id}")
    @Override // Sobrescribe el getOne del BaseController
    public ResponseEntity<?> getOne(@PathVariable Long id) {
        try {
            Promocion promocion = baseService.findById(id); // Llama al findById del padre
            return ResponseEntity.ok(promocionMapper.toDTO(promocion));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    // Sobrescribir create para aceptar un DTO de entrada, mapear y manejar excepciones
    @PostMapping(consumes = "application/json")
    // @Override // <<--- Quitar @Override aquí, ya que la firma del método es diferente (recibe DTO)
    public ResponseEntity<?> create(@RequestBody PromocionCreateDTO dto) {
        try {
            Promocion promocion = promocionMapper.toEntity(dto); // Mapea el DTO a la entidad Promocion

            // Asignar relaciones por ID
            if (dto.getImagenId() != null) {
                promocion.setImagen(imagenRepository.findById(dto.getImagenId())
                        .orElseThrow(() -> new ResourceNotFoundException("Imagen no encontrada")));
            }

            // Asignar colecciones (ArticulosManufacturados, Sucursales)
            if (dto.getArticuloManufacturadoIds() != null) {
                List<ArticuloManufacturado> articulos = articuloRepo.findAllById(dto.getArticuloManufacturadoIds());
                promocion.setArticulosManufacturados(articulos);
            }

            if (dto.getSucursalIds() != null) {
                List<Sucursal> sucursales = sucursalRepository.findAllById(dto.getSucursalIds());
                promocion.setSucursales(sucursales);
            }
            promocion.setBaja(false); // Por defecto, una nueva promoción no está dada de baja

            Promocion saved = baseService.save(promocion); // Llama al save del padre
            return ResponseEntity.status(HttpStatus.CREATED).body(promocionMapper.toDTO(saved)); // Convierte a DTO para la respuesta
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    // Sobrescribir update para aceptar un DTO de entrada, mapear y manejar excepciones
    @PutMapping(value = "/{id}", consumes = "application/json")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody PromocionCreateDTO dto) {
        try {
            // Obtener la entidad existente
            Promocion existingPromocion = baseService.findById(id);

            // Mapear las propiedades del DTO a la entidad existente
            existingPromocion.setDenominacion(dto.getDenominacion());
            existingPromocion.setFechaDesde(dto.getFechaDesde());
            existingPromocion.setFechaHasta(dto.getFechaHasta());
            existingPromocion.setHoraDesde(dto.getHoraDesde());
            existingPromocion.setHoraHasta(dto.getHoraHasta());
            existingPromocion.setDescripcionDescuento(dto.getDescripcionDescuento());
            existingPromocion.setPrecioPromocional(dto.getPrecioPromocional());
            existingPromocion.setTipoPromocion(dto.getTipoPromocion());

            // Actualizar relaciones
            if (dto.getImagenId() != null) {
                existingPromocion.setImagen(imagenRepository.findById(dto.getImagenId())
                        .orElseThrow(() -> new ResourceNotFoundException("Imagen no encontrada")));
            } else {
                existingPromocion.setImagen(null); // Si el DTO no trae imagenId, la eliminamos
            }

            // Sincronizar colecciones (ArticulosManufacturados, Sucursales)
            if (dto.getArticuloManufacturadoIds() != null) {
                List<ArticuloManufacturado> articulos = articuloRepo.findAllById(dto.getArticuloManufacturadoIds());
                existingPromocion.getArticulosManufacturados().clear();
                existingPromocion.getArticulosManufacturados().addAll(articulos);
            } else {
                existingPromocion.getArticulosManufacturados().clear(); // Si no se envían IDs, limpiar la colección
            }

            if (dto.getSucursalIds() != null) {
                List<Sucursal> sucursales = sucursalRepository.findAllById(dto.getSucursalIds());
                existingPromocion.getSucursales().clear();
                existingPromocion.getSucursales().addAll(sucursales);
            } else {
                existingPromocion.getSucursales().clear(); // Si no se envían IDs, limpiar la colección
            }

            Promocion updated = baseService.update(id, existingPromocion); // Llama al update del padre con la entidad EXISTENTE
            return ResponseEntity.ok(promocionMapper.toDTO(updated)); // Convierte a DTO para la respuesta
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    // Los métodos DELETE, ACTIVATE, DEACTIVATE pueden heredarse directamente de BaseController
    // si la lógica de borrado/activación/desactivación ya implementada en BaseController
    // es suficiente y no necesitas una respuesta con DTOs específicos.
    // @DeleteMapping("/{id}") ya está cubierto por BaseController
    // @PatchMapping("/{id}/activate") ya está cubierto por BaseController
    // @PatchMapping("/{id}/deactivate") ya está cubierto por BaseController
}