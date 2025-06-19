package ElBuenSabor.ProyectoFinal.Controllers;

import ElBuenSabor.ProyectoFinal.DTO.ArticuloCantidadDTO;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/promociones")
public class PromocionController extends BaseController<Promocion, Long> {

    private final PromocionMapper promocionMapper;
    private final ImagenRepository imagenRepository;
    private final ArticuloManufacturadoRepository articuloRepo;
    private final SucursalRepository sucursalRepository;

    public PromocionController(
            PromocionService promocionService,
            PromocionMapper promocionMapper,
            ImagenRepository imagenRepository,
            ArticuloManufacturadoRepository articuloRepo,
            SucursalRepository sucursalRepository) {
        super(promocionService);
        this.promocionMapper = promocionMapper;
        this.imagenRepository = imagenRepository;
        this.articuloRepo = articuloRepo;
        this.sucursalRepository = sucursalRepository;
    }

    @GetMapping
    @Override
    public ResponseEntity<?> getAll() {
        try {
            List<Promocion> promociones = baseService.findAll();
            List<PromocionDTO> dtos = promociones.stream().map(promocionMapper::toDTO).toList();
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
            Promocion promocion = promocionMapper.toEntity(dto);

            if (dto.getArticulos() != null) {
                List<ArticuloManufacturado> articulos = dto.getArticulos().stream()
                        .map(articuloDTO -> articuloRepo.findById(articuloDTO.getArticuloId())
                                .orElseThrow(() -> new ResourceNotFoundException("Artículo no encontrado con ID: " + articuloDTO.getArticuloId())))
                        .toList();
                promocion.setArticulosManufacturados(articulos);
            }

            promocion.setBaja(false);
            Promocion saved = baseService.save(promocion);
            return ResponseEntity.status(HttpStatus.CREATED).body(promocionMapper.toDTO(saved));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    @PutMapping(value = "/{id}", consumes = "application/json")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody PromocionCreateDTO dto) {
        try {
            Promocion promocion = baseService.findById(id);

            promocion.setDenominacion(dto.getDenominacion());
            promocion.setFechaDesde(dto.getFechaDesde());
            promocion.setFechaHasta(dto.getFechaHasta());
            promocion.setHoraDesde(dto.getHoraDesde());
            promocion.setHoraHasta(dto.getHoraHasta());
            promocion.setPrecioPromocional(dto.getPrecioPromocional());
            promocion.setTipoPromocion(dto.getTipoPromocion());

            if (dto.getArticulos() != null) {
                List<ArticuloManufacturado> articulos = dto.getArticulos().stream()
                        .map(articuloDTO -> articuloRepo.findById(articuloDTO.getArticuloId())
                                .orElseThrow(() -> new ResourceNotFoundException("Artículo no encontrado con ID: " + articuloDTO.getArticuloId())))
                        .toList();
                promocion.getArticulosManufacturados().clear();
                promocion.getArticulosManufacturados().addAll(articulos);
            }

            Promocion updated = baseService.update(id, promocion);
            return ResponseEntity.ok(promocionMapper.toDTO(updated));
        } catch (Exception e) {
            e.printStackTrace(); // 👈 para ver el error en consola
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("{\"error\": \"" + e.getMessage() + "\"}");
        }

    }
}
