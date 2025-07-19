package ElBuenSabor.ProyectoFinal.Controllers;

import ElBuenSabor.ProyectoFinal.DTO.PromocionCreateDTO;
import ElBuenSabor.ProyectoFinal.DTO.PromocionDTO;
import ElBuenSabor.ProyectoFinal.Entities.ArticuloManufacturado;
import ElBuenSabor.ProyectoFinal.Entities.ArticuloInsumo;
import ElBuenSabor.ProyectoFinal.Entities.Promocion;
import ElBuenSabor.ProyectoFinal.Entities.Sucursal;
import ElBuenSabor.ProyectoFinal.Exceptions.ResourceNotFoundException;
import ElBuenSabor.ProyectoFinal.Mappers.PromocionMapper;
import ElBuenSabor.ProyectoFinal.Repositories.ArticuloManufacturadoRepository;
import ElBuenSabor.ProyectoFinal.Repositories.ArticuloInsumoRepository;
import ElBuenSabor.ProyectoFinal.Repositories.ImagenRepository;
import ElBuenSabor.ProyectoFinal.Repositories.SucursalRepository;
import ElBuenSabor.ProyectoFinal.Service.PromocionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/promociones")
@CrossOrigin(origins = "http://localhost:5173") // <-- ¡AÑADIR ESTA LÍNEA!
public class PromocionController extends BaseController<Promocion, Long> {
    private final PromocionService promocionService;
    private final PromocionMapper promocionMapper;

    private final ImagenRepository imagenRepository;
    private final ArticuloManufacturadoRepository articuloManufacturadoRepo;
    private final ArticuloInsumoRepository articuloInsumoRepo;
    private final SucursalRepository sucursalRepository;

    public PromocionController(
            PromocionService promocionService,
            PromocionMapper promocionMapper,
            ImagenRepository imagenRepository,
            ArticuloManufacturadoRepository articuloManufacturadoRepo,
            ArticuloInsumoRepository articuloInsumoRepo,
            SucursalRepository sucursalRepository) {
        super(promocionService);
        this.promocionMapper = promocionMapper;
        this.imagenRepository = imagenRepository;
        this.articuloManufacturadoRepo = articuloManufacturadoRepo;
        this.articuloInsumoRepo = articuloInsumoRepo;
        this.sucursalRepository = sucursalRepository;
        this.promocionService = promocionService;
    }

    @GetMapping
    @Override
    public ResponseEntity<?> getAll() {
        try {
            List<Promocion> promociones = baseService.findAll();
            List<PromocionDTO> dtos = promociones.stream()
                    .map(promocionMapper::toDTO)
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
            Promocion promocion = baseService.findById(id);
            return ResponseEntity.ok(promocionMapper.toDTO(promocion));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    @PostMapping(consumes = "application/json")
    public ResponseEntity<?> create(@RequestBody PromocionCreateDTO dto) {
        try {
            // Delegamos toda la lógica de creación al servicio
            Promocion savedPromocion = promocionService.save(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(promocionMapper.toDTO(savedPromocion));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    @PutMapping(value = "/{id}", consumes = "application/json")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody PromocionCreateDTO dto) {
        try {
            // Toda la lógica ahora está en el servicio. El controlador solo delega.
            Promocion updatedPromocion = promocionService.update(id, dto);
            return ResponseEntity.ok(promocionMapper.toDTO(updatedPromocion));
        } catch (Exception e) {
            System.err.println("Error en PromocionController.update: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    @PatchMapping("/{id}/activate")
    public ResponseEntity<?> activate(@PathVariable Long id) {
        try {
            promocionService.toggleBaja(id, false);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<?> deactivate(@PathVariable Long id) {
        try {
            promocionService.toggleBaja(id, true);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }
}