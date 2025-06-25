package ElBuenSabor.ProyectoFinal.Controllers;

import ElBuenSabor.ProyectoFinal.DTO.PaisDTO;
import ElBuenSabor.ProyectoFinal.Entities.Pais;
import ElBuenSabor.ProyectoFinal.Mappers.PaisMapper;
import ElBuenSabor.ProyectoFinal.Service.PaisService; // Usar la interfaz específica
// Ya no es necesario si se inyecta por constructor explícito al padre
// import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/paises")
public class PaisController extends BaseController<Pais, Long> {

    private final PaisMapper paisMapper;

    public PaisController(
            PaisService paisService,
            PaisMapper paisMapper) {
        super(paisService);
        this.paisMapper = paisMapper;
    }

    @GetMapping
    @Override
    public ResponseEntity<?> getAll() {
        try {
            List<Pais> paises = baseService.findAll();
            List<PaisDTO> dtos = paises.stream()
                    .map(paisMapper::toDTO)
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
            Pais pais = baseService.findById(id);
            return ResponseEntity.ok(paisMapper.toDTO(pais));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    @PostMapping(consumes = "application/json")
    public ResponseEntity<?> create(@RequestBody PaisDTO dto) {
        try {
            Pais pais = paisMapper.toEntity(dto);
            pais.setBaja(false);

            Pais saved = baseService.save(pais);
            return ResponseEntity.status(HttpStatus.CREATED).body(paisMapper.toDTO(saved));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    @PutMapping(value = "/{id}", consumes = "application/json")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody PaisDTO dto) {
        try {
            Pais existingPais = baseService.findById(id);

            existingPais.setNombre(dto.getNombre());

            Pais updated = baseService.update(id, existingPais);
            return ResponseEntity.ok(paisMapper.toDTO(updated));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }
}