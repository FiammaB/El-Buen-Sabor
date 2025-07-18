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

    /*
    @PostMapping(consumes = "application/json")
    public ResponseEntity<?> create(@RequestBody PromocionCreateDTO dto) {
        try {
            Promocion promocion = promocionMapper.toEntity(dto);

            if (dto.getImagenId() != null) {
                promocion.setImagen(imagenRepository.findById(dto.getImagenId())
                        .orElseThrow(() -> new ResourceNotFoundException("Imagen no encontrada con ID: " + dto.getImagenId())));
            } else {
                promocion.setImagen(null);
            }

            if (dto.getArticuloManufacturadoIds() != null && !dto.getArticuloManufacturadoIds().isEmpty()) {
                List<ArticuloManufacturado> articulos = articuloManufacturadoRepo.findAllById(dto.getArticuloManufacturadoIds());
              //  if (articulos.size() != dto.getArticuloManufacturadoIds().size()) {
               //     throw new ResourceNotFoundException("Algunos artículos manufacturados no fueron encontrados.");
              //  }
                promocion.setArticulosManufacturados(articulos);
            } else {
                promocion.setArticulosManufacturados(List.of());
            }

            if (dto.getArticuloInsumoIds() != null && !dto.getArticuloInsumoIds().isEmpty()) {
                List<ArticuloInsumo> insumos = articuloInsumoRepo.findAllById(dto.getArticuloInsumoIds());
               // if (insumos.size() != dto.getArticuloInsumoIds().size()) {
                //    throw new ResourceNotFoundException("Algunos artículos insumos no fueron encontrados.");
               // }
                promocion.setArticulosInsumos(insumos);
            } else {
                promocion.setArticulosInsumos(List.of());
            }

//            if (dto.getSucursalIds() != null && !dto.getSucursalIds().isEmpty()) {
//                List<Sucursal> sucursales = sucursalRepository.findAllById(dto.getSucursalIds());
//                if (sucursales.size() != dto.getSucursalIds().size()) {
//                    throw new ResourceNotFoundException("Algunas sucursales no fueron encontradas.");
//                }
//                promocion.setSucursales(sucursales);
//            } else {
//                Sucursal defaultSucursal = sucursalRepository.findById(1L)
//                        .orElseThrow(() -> new ResourceNotFoundException("No se encontró la sucursal por defecto (ID 1L)."));
//                promocion.setSucursales(List.of(defaultSucursal));
//            }

            promocion.setBaja(false);

            Promocion saved = baseService.save(promocion);
            return ResponseEntity.status(HttpStatus.CREATED).body(promocionMapper.toDTO(saved));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }
*/

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

    /*
    @PutMapping(value = "/{id}", consumes = "application/json")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody PromocionCreateDTO dto) {
        try {
            System.out.println("--- INICIO PromocionController.update ---");
            System.out.println("DTO recibido para actualizar Promoción (ID: " + id + "): " + dto);

            Promocion existingPromocion = baseService.findById(id);
            System.out.println("Promoción existente obtenida del servicio (ID: " + existingPromocion.getId() + "): " + existingPromocion);
            System.out.println("Artículos manufacturados existentes en existingPromocion (antes de actualizar): " + existingPromocion.getArticulosManufacturados().size() + " items.");
            System.out.println("Artículos insumos existentes en existingPromocion (antes de actualizar): " + existingPromocion.getArticulosInsumos().size() + " items.");


            existingPromocion.setDenominacion(dto.getDenominacion());
            existingPromocion.setFechaDesde(dto.getFechaDesde());
            existingPromocion.setFechaHasta(dto.getFechaHasta());
            existingPromocion.setHoraDesde(dto.getHoraDesde());
            existingPromocion.setHoraHasta(dto.getHoraHasta());
            existingPromocion.setDescripcionDescuento(dto.getDescripcionDescuento());
            existingPromocion.setPrecioPromocional(dto.getPrecioPromocional());
            existingPromocion.setTipoPromocion(dto.getTipoPromocion());

            if (dto.getImagenId() != null) {
                existingPromocion.setImagen(imagenRepository.findById(dto.getImagenId())
                        .orElseThrow(() -> new ResourceNotFoundException("Imagen no encontrada con ID: " + dto.getImagenId())));
            } else if (existingPromocion.getImagen() != null) {
                // No se hace nada si imagenId es null para mantener la imagen existente
            }


            // Sincronizar ArticulosManufacturados
            if (dto.getArticuloManufacturadoIds() != null) {
                System.out.println("IDs de artículos manufacturados recibidos en DTO: " + dto.getArticuloManufacturadoIds());
                List<ArticuloManufacturado> articulos = articuloManufacturadoRepo.findAllById(dto.getArticuloManufacturadoIds());
                System.out.println("Artículos manufacturados recuperados de Repo: " + articulos.size() + " items. Contenido: " + articulos.stream().map(a -> a.getDenominacion() + "(ID:" + a.getId() + ")").collect(Collectors.joining(", ")));
              //  if (articulos.size() != dto.getArticuloManufacturadoIds().size()) {
                //    throw new ResourceNotFoundException("No se encontraron todos los artículos manufacturados para los IDs proporcionados.");
               // }
                existingPromocion.getArticulosManufacturados().clear();
                existingPromocion.getArticulosManufacturados().addAll(articulos);
                System.out.println("Artículos manufacturados en existingPromocion DESPUÉS de addAll: " + existingPromocion.getArticulosManufacturados().size() + " items. Contenido: " + existingPromocion.getArticulosManufacturados().stream().map(a -> a.getDenominacion() + "(ID:" + a.getId() + ")").collect(Collectors.joining(", ")));
            } else {
                existingPromocion.getArticulosManufacturados().clear(); // Si no se envían IDs, limpiar la colección
                System.out.println("No se proporcionaron IDs de artículos manufacturados en el DTO. Lista vaciada.");
            }

            // Sincronizar ArticuloInsumo en la actualización
            if (dto.getArticuloInsumoIds() != null && !dto.getArticuloInsumoIds().isEmpty()) {
                System.out.println("IDs de artículos insumos recibidos en DTO: " + dto.getArticuloInsumoIds());
                List<ArticuloInsumo> insumos = articuloInsumoRepo.findAllById(dto.getArticuloInsumoIds());
                System.out.println("Artículos insumos recuperados de Repo: " + insumos.size() + " items. Contenido: " + insumos.stream().map(i -> i.getDenominacion() + "(ID:" + i.getId() + ")").collect(Collectors.joining(", ")));
                //if (insumos.size() != dto.getArticuloInsumoIds().size()) {
                   // throw new ResourceNotFoundException("No se encontraron todos los artículos insumos para los IDs proporcionados.");
               // }
                existingPromocion.getArticulosInsumos().clear();
                existingPromocion.getArticulosInsumos().addAll(insumos);
                System.out.println("Artículos insumos en existingPromocion DESPUÉS de addAll: " + existingPromocion.getArticulosInsumos().size() + " items. Contenido: " + existingPromocion.getArticulosInsumos().stream().map(i -> i.getDenominacion() + "(ID:" + i.getId() + ")").collect(Collectors.joining(", ")));
            } else {
                existingPromocion.getArticulosInsumos().clear(); // Si no se envían IDs, limpiar la colección
                System.out.println("No se proporcionaron IDs de artículos insumos en el DTO o lista vacía. Lista vaciada.");
            }

//            // <-- CAMBIO IMPORTANTE AQUÍ para SucursalIds en UPDATE (ya lo tenías)
//            if (dto.getSucursalIds() != null && !dto.getSucursalIds().isEmpty()) {
//                List<Sucursal> sucursales = sucursalRepository.findAllById(dto.getSucursalIds());
//                if (sucursales.isEmpty() && !dto.getSucursalIds().isEmpty()) {
//                    throw new ResourceNotFoundException("No se encontraron sucursales para los IDs proporcionados.");
//                }
//                existingPromocion.getSucursales().clear();
//                existingPromocion.getSucursales().addAll(sucursales);
//            } else {
//                Sucursal defaultSucursal = sucursalRepository.findById(1L)
//                        .orElseThrow(() -> new ResourceNotFoundException("No se encontró la sucursal por defecto (ID 1L)."));
//                existingPromocion.getSucursales().clear();
//                existingPromocion.getSucursales().add(defaultSucursal);
//            }

            System.out.println("Promoción ANTES de llamar a baseService.update: " + existingPromocion.getId());
            System.out.println("  - Artículos Manufacturados: " + existingPromocion.getArticulosManufacturados().size());
            System.out.println("  - Artículos Insumos: " + existingPromocion.getArticulosInsumos().size());

            Promocion updated = promocionService.update(id, existingPromocion); // Llama al update del servicio
            System.out.println("Promoción DESPUÉS de baseService.update (entidad 'updated'): " + updated.getId());
            System.out.println("  - Artículos Manufacturados en 'updated': " + updated.getArticulosManufacturados().size());
            System.out.println("  - Artículos Insumos en 'updated': " + updated.getArticulosInsumos().size());

            PromocionDTO responseDto = promocionMapper.toDTO(updated);
            System.out.println("PromocionDTO FINAL de respuesta (Artículos Manufacturados): " + responseDto.getArticulosManufacturados().size());
            System.out.println("PromocionDTO FINAL de respuesta (Artículos Insumos): " + responseDto.getArticulosInsumos().size());
            System.out.println("--- FIN PromocionController.update ---");

            return ResponseEntity.ok(responseDto);
        } catch (Exception e) {
            System.err.println("Error en PromocionController.update: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }
*/
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