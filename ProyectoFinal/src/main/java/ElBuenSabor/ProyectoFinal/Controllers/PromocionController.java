package ElBuenSabor.ProyectoFinal.Controllers;

import ElBuenSabor.ProyectoFinal.DTO.*;
import ElBuenSabor.ProyectoFinal.Entities.Promocion; // Para el tipo de retorno de reactivate
import ElBuenSabor.ProyectoFinal.Service.PromocionService;
import ElBuenSabor.ProyectoFinal.Service.SucursalService; // Para validar sucursal
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional; // Para el Optional de Sucursal
import java.util.stream.Collectors;

import ElBuenSabor.ProyectoFinal.Entities.Sucursal; // Para el Optional de Sucursal


@RestController
@RequestMapping("/api/v1/promociones")
@CrossOrigin(origins = "*")
public class PromocionController {

    @Autowired
    private PromocionService promocionService;

    @Autowired
    private SucursalService sucursalService; // Para validar sucursalId

    @PostMapping("")
    public ResponseEntity<?> createPromocion(@Valid @RequestBody PromocionCreateUpdateDTO promocionDTO) {
        try {
            PromocionDTO nuevaPromocion = promocionService.createPromocion(promocionDTO);
            return new ResponseEntity<>(nuevaPromocion, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerPromocionPorId(@PathVariable Long id) {
        try {
            PromocionDTO dto = promocionService.findPromocionById(id); // Devuelve activa
            if (dto != null) {
                return ResponseEntity.ok(dto);
            } else {
                return new ResponseEntity<>("Promoción activa no encontrada con ID: " + id, HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>("Error al obtener la promoción: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarPromocion(@PathVariable Long id, @Valid @RequestBody PromocionCreateUpdateDTO promocionDTO) {
        try {
            PromocionDTO promocionActualizada = promocionService.updatePromocion(id, promocionDTO);
            return ResponseEntity.ok(promocionActualizada);
        } catch (Exception e) {
            if (e.getMessage().contains("no encontrada")) {
                return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    // Endpoint para listar promociones, con filtros opcionales
    @GetMapping("")
    public ResponseEntity<?> listarPromociones(
            @RequestParam(required = false) Long sucursalId,
            @RequestParam(required = false, defaultValue = "true") boolean soloActivasHoy) { // Por defecto solo activas hoy
        try {
            List<PromocionDTO> dtos;
            if (soloActivasHoy) {
                // Validar sucursal si se provee
                if (sucursalId != null) {
                    Optional<Sucursal> sucOpt = sucursalService.findById(sucursalId);
                    if (!sucOpt.isPresent() || sucOpt.get().isBaja()) {
                        return new ResponseEntity<>("Sucursal activa no encontrada con ID: " + sucursalId, HttpStatus.NOT_FOUND);
                    }
                }
                dtos = promocionService.findActivePromocionesForDisplay(LocalDate.now(), LocalTime.now(), sucursalId);
            } else if (sucursalId != null) {
                // Validar sucursal si se provee
                Optional<Sucursal> sucOpt = sucursalService.findById(sucursalId);
                if (!sucOpt.isPresent() || sucOpt.get().isBaja()) { // Aunque aquí no filtremos por baja, la sucursal debe existir
                    return new ResponseEntity<>("Sucursal no encontrada con ID: " + sucursalId, HttpStatus.NOT_FOUND);
                }
                dtos = promocionService.findPromocionesBySucursalId(sucursalId, false); // Todas (activas e inactivas por fecha/hora) de la sucursal, pero no las 'baja=true'
            }
            else {
                dtos = promocionService.findAllPromociones(); // Todas las activas (baja=false)
            }
            return ResponseEntity.ok(dtos);
        } catch (Exception e) {
            return new ResponseEntity<>("Error al listar promociones: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> darBajaPromocion(@PathVariable Long id) {
        try {
            promocionService.softDelete(id);
            return ResponseEntity.ok("Promoción dada de baja correctamente (borrado lógico).");
        } catch (Exception e) {
            if (e.getMessage().contains("no encontrada") || e.getMessage().contains("ya está dada de baja")) {
                return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
            }
            if (e.getMessage().contains("está referenciada en pedidos")) { // Mensaje del servicio
                return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
            }
            return new ResponseEntity<>("Error al dar de baja la promoción: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PatchMapping("/{id}/reactivar")
    public ResponseEntity<?> reactivarPromocion(@PathVariable Long id) {
        try {
            Promocion promocionReactivadaEntity = promocionService.reactivate(id);
            return ResponseEntity.ok(convertToDTO(promocionReactivadaEntity)); // Helper para convertir
        } catch (Exception e) {
            if (e.getMessage().contains("no encontrada") || e.getMessage().contains("no está dada de baja") || e.getMessage().contains("no se puede reactivar")) {
                return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
            }
            return new ResponseEntity<>("Error al reactivar la promoción: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/admin/todos")
    // @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getAllPromocionesIncludingDeletedForAdmin() {
        try {
            List<PromocionDTO> dtos = promocionService.findAllPromocionesIncludingDeleted();
            return ResponseEntity.ok(dtos);
        } catch (Exception e) {
            return new ResponseEntity<>("Error al obtener todas las promociones (incluyendo bajas): " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/admin/{id}")
    // @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getPromocionByIdIncludingDeletedForAdmin(@PathVariable Long id) {
        try {
            PromocionDTO dto = promocionService.findPromocionByIdIncludingDeleted(id);
            if (dto != null) {
                return ResponseEntity.ok(dto);
            } else {
                return new ResponseEntity<>("Promoción (activa o inactiva) no encontrada con ID: " + id, HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>("Error al obtener la promoción (incluyendo bajas): " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Helper para convertir Entidad a DTO (si el servicio devuelve la entidad)
    private PromocionDTO convertToDTO(Promocion promocion) {
        if (promocion == null) return null;
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
        dto.setBaja(promocion.isBaja());

        if (promocion.getImagen() != null) {
            ImagenDTO imgDto = new ImagenDTO();
            imgDto.setId(promocion.getImagen().getId());
            imgDto.setDenominacion(promocion.getImagen().getDenominacion());
            imgDto.setBaja(promocion.getImagen().isBaja());
            dto.setImagen(imgDto);
        }

        if (promocion.getArticulosManufacturados() != null) {
            dto.setArticulosManufacturados(promocion.getArticulosManufacturados().stream()
                    .map(am -> {
                        ArticuloManufacturadoSimpleDTO amDto = new ArticuloManufacturadoSimpleDTO();
                        amDto.setId(am.getId());
                        amDto.setDenominacion(am.getDenominacion());
                        amDto.setPrecioVenta(am.getPrecioVenta());
                        amDto.setBaja(am.isBaja());
                        return amDto;
                    })
                    .collect(Collectors.toSet()));
        }

        if (promocion.getSucursales() != null) {
            dto.setSucursales(promocion.getSucursales().stream()
                    .map(s -> {
                        SucursalSimpleDTO sDto = new SucursalSimpleDTO();
                        sDto.setId(s.getId());
                        sDto.setNombre(s.getNombre());
                        sDto.setBaja(s.isBaja());
                        return sDto;
                    })
                    .collect(Collectors.toSet()));
        }
        return dto;
    }
}
