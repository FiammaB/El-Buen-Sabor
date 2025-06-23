package ElBuenSabor.ProyectoFinal.Controllers;

import ElBuenSabor.ProyectoFinal.DTO.PromocionDTO;
import ElBuenSabor.ProyectoFinal.Entities.ArticuloManufacturado;
import ElBuenSabor.ProyectoFinal.Entities.Promocion;
import ElBuenSabor.ProyectoFinal.Entities.Sucursal;
import ElBuenSabor.ProyectoFinal.Service.PromocionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

// Importaciones para el mapeo a DTOs de respuesta (si fueran diferentes o más complejos)
import ElBuenSabor.ProyectoFinal.DTO.ArticuloManufacturadoDTO;
import ElBuenSabor.ProyectoFinal.DTO.ImagenDTO;
import ElBuenSabor.ProyectoFinal.DTO.SucursalDTO;


@RestController
@RequestMapping("/api/v1/promociones")
@CrossOrigin(origins = "*") // Ajustar según necesidades
public class PromocionController {

    @Autowired
    private PromocionService promocionService;

    // Endpoint para crear una nueva promoción
    // Restringido a Administradores/Empleados con permisos
    @PostMapping("")
    public ResponseEntity<?> crearPromocion(@RequestBody PromocionDTO promocionDTO) {
        try {
            PromocionDTO nuevaPromocion = promocionService.createPromocion(promocionDTO);
            return new ResponseEntity<>(nuevaPromocion, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    // Endpoint para obtener una promoción por su ID
    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerPromocionPorId(@PathVariable Long id) {
        try {
            // El PromocionService ya devuelve PromocionDTO en sus métodos find
            // o podemos convertir la entidad si findById devuelve la entidad.
            // Asumiendo que el servicio devuelve PromocionDTO o tenemos un helper
            Optional<Promocion> promocionOptional = promocionService.findById(id);
            if (promocionOptional.isPresent()) {
                return ResponseEntity.ok(convertToPromocionDTO(promocionOptional.get()));
            } else {
                return new ResponseEntity<>("Promoción no encontrada.", HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Endpoint para actualizar una promoción
    // Restringido a Administradores/Empleados con permisos
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarPromocion(@PathVariable Long id, @RequestBody PromocionDTO promocionDTO) {
        try {
            PromocionDTO promocionActualizada = promocionService.updatePromocion(id, promocionDTO);
            return ResponseEntity.ok(promocionActualizada);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    // Endpoint para listar todas las promociones
    @GetMapping("")
    public ResponseEntity<?> listarPromociones(
            @RequestParam(required = false) Long sucursalId,
            @RequestParam(required = false) Boolean activas) {
        try {
            List<PromocionDTO> promociones;
            if (sucursalId != null) {
                promociones = promocionService.findPromocionesBySucursalId(sucursalId); //
                if (activas != null && activas) {
                    LocalDate hoy = LocalDate.now();
                    LocalTime ahora = LocalTime.now();
                    promociones = promociones.stream()
                            .filter(p -> !hoy.isBefore(p.getFechaDesde()) && !hoy.isAfter(p.getFechaHasta()))
                            .filter(p -> {
                                if (p.getHoraDesde() == null && p.getHoraHasta() == null) return true;
                                if (p.getHoraDesde() != null && p.getHoraHasta() == null) return !ahora.isBefore(p.getHoraDesde());
                                if (p.getHoraDesde() == null && p.getHoraHasta() != null) return !ahora.isAfter(p.getHoraHasta());
                                return !ahora.isBefore(p.getHoraDesde()) && !ahora.isAfter(p.getHoraHasta());
                            })
                            .collect(Collectors.toList());
                }
            } else if (activas != null && activas) {
                promociones = promocionService.findActivePromociones(LocalDate.now(), LocalTime.now()); //
            } else {
                List<Promocion> todas = promocionService.findAll();
                promociones = todas.stream().map(this::convertToPromocionDTO).collect(Collectors.toList());
            }
            return ResponseEntity.ok(promociones);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Endpoint para obtener promociones activas (en general, no por sucursal específica a menos que se filtre)
    @GetMapping("/activas")
    public ResponseEntity<?> listarPromocionesActivas() {
        try {
            List<PromocionDTO> promocionesActivas = promocionService.findActivePromociones(LocalDate.now(), LocalTime.now()); //
            return ResponseEntity.ok(promocionesActivas);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    // Endpoint para eliminar una promoción
    // Restringido a Administradores/Empleados con permisos
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarPromocion(@PathVariable Long id) {
        try {
            // Considerar si una promoción puede ser eliminada si ya fue usada en pedidos, etc.
            // Podría ser mejor un borrado lógico (campo 'activa' o 'baja').
            boolean eliminado = promocionService.delete(id);
            if (eliminado) {
                return ResponseEntity.ok("Promoción eliminada correctamente.");
            } else {
                return new ResponseEntity<>("Promoción no encontrada para eliminar.", HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    // --- Helper para convertir Entidad a DTO ---
    // El PromocionServiceImpl ya tiene un convertToDTO, idealmente lo usaríamos desde ahí.
    // Si no, lo replicamos o lo hacemos más simple aquí.
    private PromocionDTO convertToPromocionDTO(Promocion promocion) {
        if (promocion == null) return null;
        // Esta conversión ya está detallada en PromocionServiceImpl.
        // Si PromocionService.findById() devuelve PromocionDTO, no necesitas esto aquí.
        // Si devuelve la entidad, entonces sí.
        PromocionDTO dto = new PromocionDTO();
        dto.setId(promocion.getId());
        dto.setDenominacion(promocion.getDenominacion());
        dto.setFechaDesde(promocion.getFechaDesde());
        dto.setFechaHasta(promocion.getFechaHasta());
        dto.setHoraDesde(promocion.getHoraDesde());
        dto.setHoraHasta(promocion.getHoraHasta());
        dto.setDescripcionDescuento(promocion.getDescripcionDescuento());
        dto.setPrecioPromocional(promocion.getPrecioPromocional());
        dto.setTipoPromocion(promocion.getTipoPromocion());

        if (promocion.getImagen() != null) {
            ImagenDTO imgDto = new ImagenDTO();
            imgDto.setId(promocion.getImagen().getId());
            imgDto.setDenominacion(promocion.getImagen().getDenominacion());
            dto.setImagen(imgDto);
            dto.setImagenId(imgDto.getId());
        }

        if (promocion.getArticulosManufacturados() != null && !promocion.getArticulosManufacturados().isEmpty()) { //
            dto.setArticuloManufacturadoIds(promocion.getArticulosManufacturados().stream().map(ArticuloManufacturado::getId).collect(Collectors.toList())); //
            // Para DTOs completos de artículo:
            // dto.setArticulosManufacturados(promocion.getArticulosManufacturados().stream().map(am -> {
            //    ArticuloManufacturadoDTO amDto = new ArticuloManufacturadoDTO();
            //    amDto.setId(am.getId());
            //    amDto.setDenominacion(am.getDenominacion());
            //    // ...otros campos necesarios
            //    return amDto;
            // }).collect(Collectors.toList()));
        }

        if (promocion.getSucursales() != null && !promocion.getSucursales().isEmpty()) { //
            dto.setSucursalIds(promocion.getSucursales().stream().map(Sucursal::getId).collect(Collectors.toSet())); //
            // Para DTOs completos de sucursal:
            // dto.setSucursales(promocion.getSucursales().stream().map(s -> {
            //    SucursalDTO sDto = new SucursalDTO();
            //    sDto.setId(s.getId());
            //    sDto.setNombre(s.getNombre());
            //    // ...otros campos necesarios
            //    return sDto;
            // }).collect(Collectors.toSet()));
        }
        return dto;
    }
}