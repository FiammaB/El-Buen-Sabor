// Archivo: ElBuenSabor/ProyectoFinal/src/main/java/ElBuenSabor/ProyectoFinal/Controllers/PromocionController.java
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
import ElBuenSabor.ProyectoFinal.Service.PromocionService;
import ElBuenSabor.ProyectoFinal.Service.SucursalService; // Importar SucursalService
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/promociones")
public class PromocionController extends BaseController<Promocion, Long> {

    private final PromocionMapper promocionMapper;
    private final ImagenRepository imagenRepository;
    private final ArticuloManufacturadoRepository articuloRepo;
    private final SucursalRepository sucursalRepository; // Usaremos este para buscar Sucursales por ID
    private final PromocionService promocionService; // Declaración correcta
    private final SucursalService sucursalService; // Inyectar SucursalService

    public PromocionController(
            PromocionService promocionService,
            PromocionMapper promocionMapper,
            ImagenRepository imagenRepository,
            ArticuloManufacturadoRepository articuloRepo,
            SucursalRepository sucursalRepository,
            SucursalService sucursalService) { // Modificar constructor
        super(promocionService);
        this.promocionService = promocionService; // Asignación correcta
        this.promocionMapper = promocionMapper;
        this.imagenRepository = imagenRepository;
        this.articuloRepo = articuloRepo;
        this.sucursalRepository = sucursalRepository;
        this.sucursalService = sucursalService;
    }

    // Nuevo endpoint para obtener todas las promociones de una sucursal específica
    @GetMapping("/sucursal/{sucursalId}")
    public ResponseEntity<?> getAllBySucursal(@PathVariable Long sucursalId) {
        try {
            List<Promocion> promociones = promocionService.findAllBySucursalId(sucursalId);
            List<PromocionDTO> dtos = promociones.stream()
                    .map(promocionMapper::toDTO)
                    .toList();
            return ResponseEntity.ok(dtos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    // Nuevo endpoint para obtener promociones activas por sucursal
    @GetMapping("/sucursal/{sucursalId}/activas")
    public ResponseEntity<?> getPromocionesActivasBySucursal(@PathVariable Long sucursalId) {
        try {
            List<Promocion> promociones = promocionService.getPromocionesActivas(sucursalId);
            List<PromocionDTO> dtos = promociones.stream()
                    .map(promocionMapper::toDTO)
                    .toList();
            return ResponseEntity.ok(dtos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    // Sobrescribir getAll para devolver DTOs y manejar excepciones
    @GetMapping
    @Override
    public ResponseEntity<?> getAll() {
        // En un escenario de múltiples sucursales, rara vez querrías todas las promociones de todas las sucursales
        // sin un filtro. Considera eliminar este método o hacer que requiera un sucursalId.
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"error\": \"Para obtener promociones, debe especificar un ID de sucursal. Use /api/promociones/sucursal/{sucursalId}\"}");
        // Alternativamente, si quieres mostrar todas las promociones de todas las sucursales:
        // try {
        //     List<Promocion> promociones = promocionService.findAll();
        //     List<PromocionDTO> dtos = promociones.stream()
        //             .map(promocionMapper::toDTO)
        //             .toList();
        //     return ResponseEntity.ok(dtos);
        // } catch (Exception e) {
        //     return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"error\": \"" + e.getMessage() + "\"}");
        // }
    }

    // Sobrescribir getOne para devolver un DTO y manejar excepciones
    // Aquí puedes decidir si quieres que el getOne de una promoción también valide que pertenece a una sucursal específica.
    // Para simplificar, lo mantendremos como una búsqueda global por ID.
    @GetMapping("/{id}")
    @Override
    public ResponseEntity<?> getOne(@PathVariable Long id) {
        try {
            Promocion promocion = promocionService.findById(id);
            return ResponseEntity.ok(promocionMapper.toDTO(promocion));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    // Modificado: create ahora podría recibir sucursalIds en el DTO
    @PostMapping(consumes = "application/json")
    public ResponseEntity<?> create(@RequestBody PromocionCreateDTO dto) {
        try {
            Promocion promocion = promocionMapper.toEntity(dto);

            // Asignar relaciones por ID
            if (dto.getImagenId() != null) {
                promocion.setImagen(imagenRepository.findById(dto.getImagenId())
                        .orElseThrow(() -> new ResourceNotFoundException("Imagen no encontrada")));
            }

            // Asignar colecciones (ArticulosManufacturados, Sucursales)
            if (dto.getArticuloManufacturadoIds() != null && !dto.getArticuloManufacturadoIds().isEmpty()) {
                List<ArticuloManufacturado> articulos = articuloRepo.findAllById(dto.getArticuloManufacturadoIds());
                promocion.setArticulosManufacturados(articulos);
            } else {
                promocion.setArticulosManufacturados(List.of()); // Asegura que no sea null
            }

            if (dto.getSucursalIds() != null && !dto.getSucursalIds().isEmpty()) {
                List<Sucursal> sucursales = sucursalRepository.findAllById(dto.getSucursalIds());
                if (sucursales.isEmpty() && !dto.getSucursalIds().isEmpty()) { // Si se enviaron IDs pero no se encontraron sucursales
                    throw new ResourceNotFoundException("No se encontraron sucursales para los IDs proporcionados.");
                }
                promocion.setSucursales(sucursales);
            } else {
                throw new ResourceNotFoundException("Una promoción debe estar asociada al menos a una sucursal.");
            }
            promocion.setBaja(false); // Por defecto, una nueva promoción no está dada de baja

            Promocion saved = promocionService.save(promocion);
            return ResponseEntity.status(HttpStatus.CREATED).body(promocionMapper.toDTO(saved));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"error\": \"Error al crear la promoción: " + e.getMessage() + "\"}");
        }
    }

    // Modificado: update ahora recibe sucursalIds en el DTO
    @PutMapping(value = "/{id}", consumes = "application/json")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody PromocionCreateDTO dto) {
        try {
            Promocion existingPromocion = promocionService.findById(id);

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
                existingPromocion.setImagen(null);
            }

            // Sincronizar colecciones (ArticulosManufacturados)
            if (dto.getArticuloManufacturadoIds() != null) {
                List<ArticuloManufacturado> articulos = articuloRepo.findAllById(dto.getArticuloManufacturadoIds());
                existingPromocion.getArticulosManufacturados().clear();
                existingPromocion.getArticulosManufacturados().addAll(articulos);
            } else {
                existingPromocion.getArticulosManufacturados().clear();
            }

            // Sincronizar colecciones (Sucursales)
            if (dto.getSucursalIds() != null && !dto.getSucursalIds().isEmpty()) {
                List<Sucursal> sucursales = sucursalRepository.findAllById(dto.getSucursalIds());
                if (sucursales.isEmpty() && !dto.getSucursalIds().isEmpty()) {
                    throw new ResourceNotFoundException("No se encontraron sucursales para los IDs proporcionados.");
                }
                existingPromocion.getSucursales().clear();
                existingPromocion.getSucursales().addAll(sucursales);
            } else {
                throw new ResourceNotFoundException("Una promoción debe estar asociada al menos a una sucursal.");
            }

            Promocion updated = promocionService.update(id, existingPromocion);
            return ResponseEntity.ok(promocionMapper.toDTO(updated));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    // Modificado: toggleBaja ahora requiere un sucursalId en la URL
    // (Opcional, dado que Promocion es ManyToMany con Sucursal, la baja podría ser global o por sucursal)
    // Si la baja es GLOBAL para la promoción, no necesitas sucursalId aquí.
    // Si la baja es por SUCURSAL, necesitarías una tabla intermedia para el estado de baja por sucursal.
    // Por simplicidad, asumimos que toggleBaja es global para la Promoción.
    @PatchMapping("/{id}/baja")
    public ResponseEntity<?> toggleBaja(
            @PathVariable Long id,
            @RequestParam boolean baja
    ) {
        try {
            Promocion actualizado = promocionService.toggleBaja(id, baja);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    // Métodos DELETE, ACTIVATE, DEACTIVATE (si los usas, asegúrate de que también validan la sucursal)
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        try {
            promocionService.deleteById(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"error\": \"Error al eliminar la promoción: " + e.getMessage() + "\"}");
        }
    }

    @PatchMapping("/{id}/activate")
    public ResponseEntity<?> activate(@PathVariable Long id) {
        try {
            promocionService.toggleBaja(id, false);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"error\": \"Error al activar la promoción: " + e.getMessage() + "\"}");
        }
    }

    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<?> deactivate(@PathVariable Long id) {
        try {
            promocionService.toggleBaja(id, true);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"error\": \"Error al desactivar la promoción: " + e.getMessage() + "\"}");
        }
    }
}
