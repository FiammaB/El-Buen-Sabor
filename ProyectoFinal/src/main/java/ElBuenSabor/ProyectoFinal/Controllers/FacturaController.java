package ElBuenSabor.ProyectoFinal.Controllers;

import ElBuenSabor.ProyectoFinal.DTO.FacturaCreateDTO;
import ElBuenSabor.ProyectoFinal.DTO.FacturaDTO;
import ElBuenSabor.ProyectoFinal.Entities.Factura;
import ElBuenSabor.ProyectoFinal.Mappers.FacturaMapper;
import ElBuenSabor.ProyectoFinal.Service.FacturaService; // Usar la interfaz específica
// Ya no es necesario si se inyecta por constructor explícito al padre
// import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.awt.print.Pageable; // No parece usarse, eliminar si no es necesario
import java.util.List;

@RestController
@RequestMapping("/api/facturas") // Define la URL base para este controlador
// FacturaController ahora extiende BaseController
public class FacturaController extends BaseController<Factura, Long> {

    private final FacturaMapper facturaMapper;

    // El constructor inyecta el servicio específico de Factura y el mapper
    public FacturaController(
            FacturaService facturaService, // Servicio específico
            FacturaMapper facturaMapper) {
        super(facturaService); // Pasa el servicio al constructor del BaseController
        this.facturaMapper = facturaMapper;
    }

    // Sobrescribir getAll para devolver DTOs y manejar excepciones
    @GetMapping
    @Override // Sobrescribe el getAll del BaseController
    public ResponseEntity<?> getAll() {
        try {
            List<Factura> facturas = baseService.findAll(); // Llama al findAll del padre
            List<FacturaDTO> dtos = facturas.stream()
                    .map(facturaMapper::toDTO)
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
            Factura factura = baseService.findById(id); // Llama al findById del padre
            return ResponseEntity.ok(facturaMapper.toDTO(factura));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    // Sobrescribir create para aceptar un DTO de entrada, mapear y manejar excepciones
    @PostMapping(consumes = "application/json")
    // @Override // <<--- Quitar @Override aquí, ya que la firma del método es diferente (recibe DTO)
    public ResponseEntity<?> create(@RequestBody FacturaCreateDTO dto) {
        try {
            Factura factura = facturaMapper.toEntity(dto);
            factura.setBaja(false); // Por defecto, una nueva factura no está dada de baja

            Factura saved = baseService.save(factura); // Llama al save del padre
            return ResponseEntity.status(HttpStatus.CREATED).body(facturaMapper.toDTO(saved)); // Convierte a DTO para la respuesta
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    // Sobrescribir update para aceptar un DTO de entrada, mapear y manejar excepciones
    @PutMapping(value = "/{id}", consumes = "application/json")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody FacturaCreateDTO dto) {
        try {
            // Obtener la entidad existente y actualizar sus propiedades
            Factura existingFactura = baseService.findById(id);

            // Mapear las propiedades del DTO a la entidad existente
            // La entidad del DTO no tiene un ID, por lo que es mejor copiar las propiedades
            // desde el DTO a la entidad existente.
            existingFactura.setFechaFacturacion(dto.getFechaFacturacion());
            existingFactura.setMpPaymentId(dto.getMpPaymentId());
            existingFactura.setMpMerchantOrderId(dto.getMpMerchantOrderId());
            existingFactura.setMpPreferenceId(dto.getMpPreferenceId());
            existingFactura.setMpPaymentType(dto.getMpPaymentType());
            existingFactura.setFormaPago(dto.getFormaPago());
            existingFactura.setTotalVenta(dto.getTotalVenta());
            // La propiedad 'baja' se mantendrá o actualizará según la lógica de BaseServiceImpl.update
            // o puedes establecerla explícitamente si tu DTO lo soporta.

            Factura updated = baseService.update(id, existingFactura); // Llama al update del padre con la entidad EXISTENTE
            return ResponseEntity.ok(facturaMapper.toDTO(updated)); // Convierte a DTO para la respuesta
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