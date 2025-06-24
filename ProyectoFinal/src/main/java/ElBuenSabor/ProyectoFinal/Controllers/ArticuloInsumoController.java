// Archivo: ElBuenSabor/ProyectoFinal/src/main/java/ElBuenSabor/ProyectoFinal/Controllers/ArticuloInsumoController.java
package ElBuenSabor.ProyectoFinal.Controllers;

import ElBuenSabor.ProyectoFinal.DTO.ArticuloInsumoDTO;
import ElBuenSabor.ProyectoFinal.Entities.ArticuloInsumo;
import ElBuenSabor.ProyectoFinal.Entities.Sucursal;
import ElBuenSabor.ProyectoFinal.Exceptions.ResourceNotFoundException;
import ElBuenSabor.ProyectoFinal.Mappers.ArticuloInsumoMapper;
import ElBuenSabor.ProyectoFinal.Repositories.CategoriaRepository;
import ElBuenSabor.ProyectoFinal.Repositories.ImagenRepository;
import ElBuenSabor.ProyectoFinal.Repositories.UnidadMedidaRepository;
import ElBuenSabor.ProyectoFinal.Service.ArticuloInsumoService;
import ElBuenSabor.ProyectoFinal.Service.SucursalService; // Importar SucursalService
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/articuloInsumo")
public class ArticuloInsumoController extends BaseController<ArticuloInsumo, Long> {

    private final ArticuloInsumoMapper articuloInsumoMapper;
    private final CategoriaRepository categoriaRepository;
    private final UnidadMedidaRepository unidadMedidaRepository;
    private final ImagenRepository imagenRepository;
    private final ArticuloInsumoService articuloInsumoService;
    private final SucursalService sucursalService; // Inyectar SucursalService

    public ArticuloInsumoController(
            ArticuloInsumoService articuloInsumoService,
            ArticuloInsumoMapper articuloInsumoMapper,
            CategoriaRepository categoriaRepository,
            UnidadMedidaRepository unidadMedidaRepository,
            ImagenRepository imagenRepository,
            SucursalService sucursalService) { // Modificar constructor
        super(articuloInsumoService);
        this.articuloInsumoService = articuloInsumoService;
        this.articuloInsumoMapper = articuloInsumoMapper;
        this.categoriaRepository = categoriaRepository;
        this.unidadMedidaRepository = unidadMedidaRepository;
        this.imagenRepository = imagenRepository;
        this.sucursalService = sucursalService;
    }

    // Nuevo endpoint para obtener todos los artículos insumo de una sucursal específica
    @GetMapping("/sucursal/{sucursalId}/insumos")
    public ResponseEntity<?> getAllBySucursal(@PathVariable Long sucursalId) {
        try {
            List<ArticuloInsumo> insumos = articuloInsumoService.findAllBySucursalId(sucursalId);
            List<ArticuloInsumoDTO> dtos = insumos.stream()
                    .map(articuloInsumoMapper::toDTO)
                    .toList();
            return ResponseEntity.ok(dtos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    // Sobrescribir getAll para que, por defecto, pida el sucursalId
    @GetMapping("/insumos")
    @Override
    public ResponseEntity<?> getAll() {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"error\": \"Para obtener artículos insumo, debe especificar un ID de sucursal. Use /api/articuloInsumo/sucursal/{sucursalId}/insumos\"}");
        // Alternativamente, si quieres mostrar todos los artículos de todas las sucursales (con precaución por rendimiento):
        // try {
        //     List<ArticuloInsumo> insumos = articuloInsumoService.findAll();
        //     List<ArticuloInsumoDTO> dtos = insumos.stream()
        //             .map(articuloInsumoMapper::toDTO)
        //             .toList();
        //     return ResponseEntity.ok(dtos);
        // } catch (Exception e) {
        //     return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"error\": \"" + e.getMessage() + "\"}");
        // }
    }

    // Sobrescribir getOne para que también pueda validar por sucursal (opcional)
    @GetMapping("/{id}")
    @Override
    public ResponseEntity<?> getOne(@PathVariable Long id) {
        try {
            ArticuloInsumo insumo = articuloInsumoService.findById(id);
            return ResponseEntity.ok(articuloInsumoMapper.toDTO(insumo));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    // Modificado: create ahora requiere un sucursalId en la URL
    @PostMapping("/sucursal/{sucursalId}")
    public ResponseEntity<?> create(@RequestBody ArticuloInsumoDTO dto, @PathVariable Long sucursalId) {
        try {
            ArticuloInsumo entity = articuloInsumoMapper.toEntity(dto);

            // Cargar la entidad Sucursal y asignarla al insumo
            Sucursal sucursal = sucursalService.findById(sucursalId);
            if (sucursal == null) {
                throw new ResourceNotFoundException("Sucursal no encontrada con ID: " + sucursalId);
            }
            entity.setSucursal(sucursal);

            // Establecer las demás relaciones ManyToOne
            entity.setCategoria(categoriaRepository.findById(dto.getCategoriaId()).orElse(null));
            entity.setUnidadMedida(unidadMedidaRepository.findById(dto.getUnidadMedidaId()).orElse(null));
            entity.setImagen(imagenRepository.findById(dto.getImagenId()).orElse(null));
            entity.setBaja(false);

            ArticuloInsumo saved = articuloInsumoService.save(entity);
            return ResponseEntity.status(HttpStatus.CREATED).body(articuloInsumoMapper.toDTO(saved));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"error\": \"Error al crear el artículo insumo: " + e.getMessage() + "\"}");
        }
    }

    // Modificado: update ahora requiere un sucursalId en la URL
    @PutMapping("/sucursal/{sucursalId}/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody ArticuloInsumoDTO dto, @PathVariable Long sucursalId) {
        try {
            ArticuloInsumo existingEntity = articuloInsumoService.findById(id);

            // Validar que el artículo pertenece a la sucursal especificada en la URL
            if (!existingEntity.getSucursal().getId().equals(sucursalId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("{\"error\": \"El artículo insumo no pertenece a la sucursal especificada.\"}");
            }

            ArticuloInsumo entity = articuloInsumoMapper.toEntity(dto);

            // Mantener la sucursal existente
            entity.setSucursal(existingEntity.getSucursal());

            // Establecer las relaciones ManyToOne
            entity.setCategoria(categoriaRepository.findById(dto.getCategoriaId()).orElse(null));
            entity.setUnidadMedida(unidadMedidaRepository.findById(dto.getUnidadMedidaId()).orElse(null));
            entity.setImagen(imagenRepository.findById(dto.getImagenId()).orElse(null));

            ArticuloInsumo updated = articuloInsumoService.update(id, entity);
            return ResponseEntity.ok(articuloInsumoMapper.toDTO(updated));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    // Modificado: sumarStock ahora requiere un sucursalId
    @PutMapping("/sucursal/{sucursalId}/{id}/sumar-stock")
    public ResponseEntity<?> sumarStock(
            @PathVariable Long id,
            @PathVariable Long sucursalId,
            @RequestParam("cantidad") Integer cantidad
    ) {
        try {
            ArticuloInsumo insumo = articuloInsumoService.findById(id);
            // Validar que el insumo pertenece a la sucursal especificada
            if (!insumo.getSucursal().getId().equals(sucursalId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("{\"error\": \"El artículo insumo no pertenece a la sucursal especificada.\"}");
            }
            if (insumo.getBaja() != null && insumo.getBaja()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("No se puede actualizar stock de un insumo dado de baja.");
            }
            insumo.setStockActual(insumo.getStockActual() + cantidad);
            articuloInsumoService.save(insumo);
            return ResponseEntity.ok(articuloInsumoMapper.toDTO(insumo)); // Devolver DTO
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: " + e.getMessage());
        }
    }

    // Modificado: actualizarPrecioCompra ahora requiere un sucursalId
    @PutMapping("/sucursal/{sucursalId}/{id}/actualizar-precio")
    public ResponseEntity<?> actualizarPrecioCompra(
            @PathVariable Long id,
            @PathVariable Long sucursalId,
            @RequestParam("precioCompra") Double precioCompra
    ) {
        try {
            ArticuloInsumo insumo = articuloInsumoService.findById(id);
            // Validar que el insumo pertenece a la sucursal especificada
            if (!insumo.getSucursal().getId().equals(sucursalId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("{\"error\": \"El artículo insumo no pertenece a la sucursal especificada.\"}");
            }
            insumo.setPrecioCompra(precioCompra);
            articuloInsumoService.save(insumo);
            return ResponseEntity.ok(articuloInsumoMapper.toDTO(insumo)); // Devolver DTO
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: " + e.getMessage());
        }
    }

    // Modificado: toggleBaja ahora requiere un sucursalId en la URL
    @PatchMapping("/sucursal/{sucursalId}/{id}/baja")
    public ResponseEntity<?> toggleBaja(
            @PathVariable Long id,
            @PathVariable Long sucursalId,
            @RequestParam boolean baja
    ) {
        try {
            ArticuloInsumo existingEntity = articuloInsumoService.findById(id);
            // Validar que el artículo pertenece a la sucursal especificada
            if (!existingEntity.getSucursal().getId().equals(sucursalId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("{\"error\": \"El artículo insumo no pertenece a la sucursal especificada.\"}");
            }

            ArticuloInsumo actualizado = articuloInsumoService.toggleBaja(id, baja);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    // Nuevo: Endpoint para obtener insumos con stock bajo por sucursal
    @GetMapping("/sucursal/{sucursalId}/stock-bajo")
    public ResponseEntity<?> getInsumosConStockBajo(@PathVariable Long sucursalId,
                                                    @RequestParam("stockMinimo") Double stockMinimo) {
        try {
            List<ArticuloInsumo> insumos = articuloInsumoService.findByStockActualLessThanEqualAndSucursalId(stockMinimo, sucursalId);
            List<ArticuloInsumoDTO> dtos = insumos.stream()
                    .map(articuloInsumoMapper::toDTO)
                    .toList();
            return ResponseEntity.ok(dtos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    // Métodos DELETE, ACTIVATE, DEACTIVATE (si los usas, asegúrate de que también validan la sucursal)
    @DeleteMapping("/sucursal/{sucursalId}/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id, @PathVariable Long sucursalId) {
        try {
            ArticuloInsumo existingEntity = articuloInsumoService.findById(id);
            if (!existingEntity.getSucursal().getId().equals(sucursalId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("{\"error\": \"El artículo insumo no pertenece a la sucursal especificada.\"}");
            }
            articuloInsumoService.deleteById(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"error\": \"Error al eliminar el artículo insumo: " + e.getMessage() + "\"}");
        }
    }

    @PatchMapping("/sucursal/{sucursalId}/{id}/activate")
    public ResponseEntity<?> activate(@PathVariable Long id, @PathVariable Long sucursalId) {
        try {
            ArticuloInsumo existingEntity = articuloInsumoService.findById(id);
            if (!existingEntity.getSucursal().getId().equals(sucursalId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("{\"error\": \"El artículo insumo no pertenece a la sucursal especificada.\"}");
            }
            articuloInsumoService.toggleBaja(id, false); // false = no está dado de baja, es decir, está activo
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"error\": \"Error al activar el artículo insumo: " + e.getMessage() + "\"}");
        }
    }

    @PatchMapping("/sucursal/{sucursalId}/{id}/deactivate")
    public ResponseEntity<?> deactivate(@PathVariable Long id, @PathVariable Long sucursalId) {
        try {
            ArticuloInsumo existingEntity = articuloInsumoService.findById(id);
            if (!existingEntity.getSucursal().getId().equals(sucursalId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("{\"error\": \"El artículo insumo no pertenece a la sucursal especificada.\"}");
            }
            articuloInsumoService.toggleBaja(id, true); // true = está dado de baja, es decir, está inactivo
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"error\": \"Error al desactivar el artículo insumo: " + e.getMessage() + "\"}");
        }
    }
}
