package ElBuenSabor.ProyectoFinal.Controllers;

import ElBuenSabor.ProyectoFinal.DTO.LocalidadCreateUpdateDTO;
import ElBuenSabor.ProyectoFinal.DTO.LocalidadDTO;
import ElBuenSabor.ProyectoFinal.Entities.Localidad;
import ElBuenSabor.ProyectoFinal.Entities.Provincia; // Importar Provincia
import ElBuenSabor.ProyectoFinal.Exceptions.ResourceNotFoundException;
import ElBuenSabor.ProyectoFinal.Mappers.LocalidadMapper;
import ElBuenSabor.ProyectoFinal.Repositories.ProvinciaRepository;
import ElBuenSabor.ProyectoFinal.Service.LocalidadService; // Usar la interfaz específica
// Ya no es necesario si se inyecta por constructor explícito al padre
// import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/localidades")
public class LocalidadController extends BaseController<Localidad, Long> {

    private final LocalidadMapper localidadMapper;
    private final ProvinciaRepository provinciaRepository;

    public LocalidadController(
            LocalidadService localidadService,
            LocalidadMapper localidadMapper,
            ProvinciaRepository provinciaRepository) {
        super(localidadService);
        this.localidadMapper = localidadMapper;
        this.provinciaRepository = provinciaRepository;
    }

    @GetMapping
    @Override
    public ResponseEntity<?> getAll() {
        try {
            List<Localidad> localidades = baseService.findAll();
            List<LocalidadDTO> dtos = localidades.stream()
                    .map(localidadMapper::toDTO)
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
            Localidad localidad = baseService.findById(id);
            return ResponseEntity.ok(localidadMapper.toDTO(localidad));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    @PostMapping(consumes = "application/json")
    public ResponseEntity<?> create(@RequestBody LocalidadCreateUpdateDTO dto) {
        try {
            Provincia provincia = provinciaRepository.findById(dto.getProvinciaId())
                    .orElseThrow(() -> new ResourceNotFoundException("Provincia no encontrada"));

            Localidad localidad = new Localidad();
            localidad.setNombre(dto.getNombre());
            localidad.setProvincia(provincia);
            localidad.setBaja(dto.isEstaDadoDeBaja());

            Localidad saved = baseService.save(localidad);
            return ResponseEntity.status(HttpStatus.CREATED).body(localidadMapper.toDTO(saved));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    @PutMapping(value = "/{id}", consumes = "application/json")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody LocalidadCreateUpdateDTO dto) {
        try {
            Localidad existingLocalidad = baseService.findById(id);

            Provincia provincia = provinciaRepository.findById(dto.getProvinciaId())
                    .orElseThrow(() -> new ResourceNotFoundException("Provincia no encontrada"));

            existingLocalidad.setNombre(dto.getNombre());
            existingLocalidad.setProvincia(provincia);
            existingLocalidad.setBaja(dto.isEstaDadoDeBaja());

            Localidad updated = baseService.update(id, existingLocalidad);
            return ResponseEntity.ok(localidadMapper.toDTO(updated));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

}