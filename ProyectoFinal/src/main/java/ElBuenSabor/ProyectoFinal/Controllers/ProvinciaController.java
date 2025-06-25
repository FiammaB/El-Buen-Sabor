package ElBuenSabor.ProyectoFinal.Controllers;

import ElBuenSabor.ProyectoFinal.DTO.ProvinciaDTO;
import ElBuenSabor.ProyectoFinal.Entities.Pais; // Importar Pais
import ElBuenSabor.ProyectoFinal.Entities.Provincia;
import ElBuenSabor.ProyectoFinal.Exceptions.ResourceNotFoundException;
import ElBuenSabor.ProyectoFinal.Mappers.ProvinciaMapper;
import ElBuenSabor.ProyectoFinal.Repositories.PaisRepository; // Se sigue necesitando para buscar Pais
import ElBuenSabor.ProyectoFinal.Service.ProvinciaService; // Usar la interfaz específica
// Importaciones que no se usan o se manejan de otra forma:
// import ElBuenSabor.ProyectoFinal.Service.LocalidadService; // Para listar localidades - si no la necesitas aquí, puedes quitarla
// import ElBuenSabor.ProyectoFinal.DTO.LocalidadDTO; // Para la respuesta de localidades - si no la necesitas aquí, puedes quitarla
// Ya no es necesario si se inyecta por constructor explícito al padre
// import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/provincias")
public class ProvinciaController extends BaseController<Provincia, Long> {

    private final ProvinciaMapper provinciaMapper;
    private final PaisRepository paisRepository;

    public ProvinciaController(
            ProvinciaService provinciaService,
            ProvinciaMapper provinciaMapper,
            PaisRepository paisRepository) {
        super(provinciaService);
        this.provinciaMapper = provinciaMapper;
        this.paisRepository = paisRepository;
    }

    @GetMapping
    @Override
    public ResponseEntity<?> getAll() {
        try {
            List<Provincia> provincias = baseService.findAll();
            List<ProvinciaDTO> dtos = provincias.stream()
                    .map(provinciaMapper::toDTO)
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
            Provincia provincia = baseService.findById(id);
            return ResponseEntity.ok(provinciaMapper.toDTO(provincia));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    @PostMapping(consumes = "application/json")
    public ResponseEntity<?> create(@RequestBody ProvinciaDTO dto) {
        try {
            Provincia provincia = provinciaMapper.toEntity(dto);

            if (dto.getPais() != null && dto.getPais().getId() != null) {
                Pais pais = paisRepository.findById(dto.getPais().getId())
                        .orElseThrow(() -> new ResourceNotFoundException("País no encontrado"));
                provincia.setPais(pais);
            }
            provincia.setBaja(false);

            Provincia saved = baseService.save(provincia);
            return ResponseEntity.status(HttpStatus.CREATED).body(provinciaMapper.toDTO(saved));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    @PutMapping(value = "/{id}", consumes = "application/json")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody ProvinciaDTO dto) {
        try {
            Provincia existingProvincia = baseService.findById(id);

            existingProvincia.setNombre(dto.getNombre());

            if (dto.getPais() != null && dto.getPais().getId() != null) {
                Pais pais = paisRepository.findById(dto.getPais().getId())
                        .orElseThrow(() -> new ResourceNotFoundException("País no encontrado"));
                existingProvincia.setPais(pais);
            } else {
                existingProvincia.setPais(null);
            }

            Provincia updated = baseService.update(id, existingProvincia);
            return ResponseEntity.ok(provinciaMapper.toDTO(updated));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }
}