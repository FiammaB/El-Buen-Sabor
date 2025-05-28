package ElBuenSabor.ProyectoFinal.Controllers;

import ElBuenSabor.ProyectoFinal.DTO.FacturaDTO;
// import ElBuenSabor.ProyectoFinal.Entities.Factura; // No es necesario si el servicio devuelve DTO
import ElBuenSabor.ProyectoFinal.Entities.Factura;
import ElBuenSabor.ProyectoFinal.Service.FacturaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/facturas")
@CrossOrigin(origins = "*")
public class FacturaController {

    @Autowired
    private FacturaService facturaService;

    @GetMapping("/{id}")
    public ResponseEntity<?> getFacturaById(@PathVariable Long id) {
        try {
            FacturaDTO factura = facturaService.findFacturaById(id); // Devuelve activa (no anulada)
            if (factura != null) {
                return ResponseEntity.ok(factura);
            } else {
                return new ResponseEntity<>("Factura activa no encontrada con ID: " + id, HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>("Error al obtener la factura: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("")
    public ResponseEntity<?> getAllFacturasActivas(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaDesde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaHasta) {
        try {
            List<FacturaDTO> facturas;
            if (fechaDesde != null && fechaHasta != null) {
                facturas = facturaService.findFacturasByDateRange(fechaDesde, fechaHasta); // Activas en rango
            } else {
                facturas = facturaService.findAllFacturas(); // Todas las activas
            }
            return ResponseEntity.ok(facturas);
        } catch (Exception e) {
            return new ResponseEntity<>("Error al obtener facturas activas: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/pedido/{pedidoId}")
    public ResponseEntity<?> getFacturaByPedidoId(@PathVariable Long pedidoId) {
        try {
            FacturaDTO factura = facturaService.findFacturaByPedidoId(pedidoId); // Busca factura activa del pedido
            if (factura != null) {
                return ResponseEntity.ok(factura);
            } else {
                return new ResponseEntity<>("No se encontró factura activa para el pedido ID: " + pedidoId, HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            if (e.getMessage().contains("Pedido activo no encontrado")) {
                return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>("Error al obtener la factura del pedido: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Endpoint para anular una factura (borrado lógico)
    // Esto también debería generar una Nota de Crédito según las consignas.
    // La generación de la NC se manejaría dentro del servicio anularFactura.
    @DeleteMapping("/{id}/anular") // Usar DELETE o PATCH para anular
    public ResponseEntity<?> anularFactura(@PathVariable Long id) {
        try {
            facturaService.anularFactura(id);
            return ResponseEntity.ok("Factura ID: " + id + " anulada correctamente (y stock correspondiente repuesto).");
        } catch (Exception e) {
            if (e.getMessage().contains("no encontrada") || e.getMessage().contains("ya está anulada")) {
                return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND); // O CONFLICT si ya está anulada
            }
            return new ResponseEntity<>("Error al anular la factura: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    // Opcional: Reactivar una factura (si se permite "des-anular")
    @PatchMapping("/{id}/reactivar")
    // @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> reactivarFactura(@PathVariable Long id) {
        try {
            Factura facturaReactivadaEntity = facturaService.reactivate(id);
            return ResponseEntity.ok(convertToDTO(facturaReactivadaEntity)); // Helper para convertir
        } catch (Exception e) {
            if (e.getMessage().contains("no encontrada") || e.getMessage().contains("no está anulada")) {
                return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
            }
            return new ResponseEntity<>("Error al reactivar la factura: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/admin/todos")
    // @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getAllFacturasIncludingAnuladasForAdmin(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaDesde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaHasta) {
        try {
            List<FacturaDTO> dtos;
            // Necesitarías métodos en el servicio para filtrar por fecha incluyendo anuladas
            // if (fechaDesde != null && fechaHasta != null) {
            //     dtos = facturaService.findFacturasByDateRangeIncludingAnuladas(fechaDesde, fechaHasta);
            // } else {
            dtos = facturaService.findAllFacturasIncludingAnuladas();
            // }
            return ResponseEntity.ok(dtos);
        } catch (Exception e) {
            return new ResponseEntity<>("Error al obtener todas las facturas (incluyendo anuladas): " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/admin/{id}")
    // @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getFacturaByIdIncludingAnuladaForAdmin(@PathVariable Long id) {
        try {
            FacturaDTO dto = facturaService.findFacturaByIdIncludingAnulada(id);
            if (dto != null) {
                return ResponseEntity.ok(dto);
            } else {
                return new ResponseEntity<>("Factura (activa o anulada) no encontrada con ID: " + id, HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>("Error al obtener la factura (incluyendo anulada): " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Helper para convertir Entidad a DTO (si el servicio devuelve la entidad, como en reactivate)
    private FacturaDTO convertToDTO(Factura factura) {
        if (factura == null) return null;
        FacturaDTO dto = new FacturaDTO();
        dto.setId(factura.getId());
        dto.setFechaFacturacion(factura.getFechaFacturacion());
        dto.setMpPaymentId(factura.getMpPaymentId());
        dto.setMpMerchantOrderId(factura.getMpMerchantOrderId());
        dto.setMpPreferenceId(factura.getMpPreferenceId());
        dto.setMpPaymentType(factura.getMpPaymentType());
        dto.setFormaPago(factura.getFormaPago());
        dto.setTotalVenta(factura.getTotalVenta());
        dto.setBaja(factura.isBaja());
        // Lógica para obtener pedidoId si es necesario (podría venir del servicio)
        return dto;
    }
}
