package ElBuenSabor.ProyectoFinal.Controllers;

import ElBuenSabor.ProyectoFinal.DTO.DomicilioDTO;
import ElBuenSabor.ProyectoFinal.Entities.Persona;
import ElBuenSabor.ProyectoFinal.Entities.Domicilio;
import ElBuenSabor.ProyectoFinal.Entities.Localidad; // Importar Localidad
import ElBuenSabor.ProyectoFinal.Exceptions.ResourceNotFoundException;
import ElBuenSabor.ProyectoFinal.Mappers.DomicilioMapper;
import ElBuenSabor.ProyectoFinal.Repositories.LocalidadRepository;
import ElBuenSabor.ProyectoFinal.Service.PersonaService;
import ElBuenSabor.ProyectoFinal.Service.DomicilioService; // Usar la interfaz específica
// Ya no es necesario si se inyecta por constructor explícito al padre
// import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;

@RestController
@RequestMapping("/api/domicilios")
public class DomicilioController extends BaseController<Domicilio, Long> {

    private final DomicilioMapper domicilioMapper;
    private final LocalidadRepository localidadRepository;
    private final PersonaService personaService;
    private final DomicilioService domicilioService;


    public DomicilioController(
            DomicilioService domicilioService,
            DomicilioMapper domicilioMapper,
            LocalidadRepository localidadRepository,
            PersonaService personaService // ⬅️ inyectado
) {
        super(domicilioService);
        this.domicilioMapper = domicilioMapper;
        this.localidadRepository = localidadRepository;
        this.personaService = personaService; // ⬅️ guardado
        this.domicilioService = domicilioService;
    }

    @GetMapping
    @Override
    public ResponseEntity<?> getAll() {
        try {
            List<Domicilio> domicilios = baseService.findAll();
            List<DomicilioDTO> dtos = domicilios.stream()
                    .map(domicilioMapper::toDTO)
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
            Domicilio domicilio = baseService.findById(id);
            return ResponseEntity.ok(domicilioMapper.toDTO(domicilio));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    @PostMapping(consumes = "application/json")
    public ResponseEntity<?> create(@RequestBody DomicilioDTO dto) {
        try {
            System.out.println("Entra: " + dto.toString());
            Domicilio domicilio = domicilioMapper.toEntity(dto);

            System.out.println("Nuevo dom:" + domicilio);

            if (dto.getLocalidad() != null && dto.getLocalidad().getId() != null) {
                Localidad localidad = localidadRepository.findById(dto.getLocalidad().getId())
                        .orElseThrow(() -> new ResourceNotFoundException("Localidad no encontrada"));
                domicilio.setLocalidad(localidad);
            }
            domicilio.setBaja(false);

            Domicilio saved = domicilioService.save(domicilio);
            return ResponseEntity.status(HttpStatus.CREATED).body(domicilioMapper.toDTO(saved));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    // DomicilioController.java
    @PostMapping("/persona/{personaId}")
    public ResponseEntity<?> createForPersona(@PathVariable Long personaId, @RequestBody DomicilioDTO dto) {
        try {
            System.out.println("DTO recibido: " + dto);
            System.out.println("DTO localidad: " + dto.getLocalidad());

            Domicilio domicilio = domicilioMapper.toEntity(dto);
            System.out.println("Entidad domicilio mapeada: " + domicilio);

            if (dto.getLocalidad() != null && dto.getLocalidad().getId() != null) {
                Localidad localidad = localidadRepository.findById(dto.getLocalidad().getId())
                        .orElseThrow(() -> new ResourceNotFoundException("Localidad no encontrada"));
                domicilio.setLocalidad(localidad);
            }

            Persona persona = personaService.findById(personaId);

            if (domicilio.getPersonas() == null) {
                domicilio.setPersonas(new HashSet<>());
            }

            persona.getDomicilios().add(domicilio);
            domicilio.getPersonas().add(persona);
            domicilio.setBaja(false);

            Domicilio saved = domicilioService.save(domicilio);
            return ResponseEntity.status(HttpStatus.CREATED).body(domicilioMapper.toDTO(saved));
        } catch (Exception e) {
            e.printStackTrace(); // 👈 esto mostrará el error real en consola
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    @GetMapping("/persona/{personaId}")
    public ResponseEntity<?> getDomiciliosByPersonaId(@PathVariable Long personaId) {
        try {
            List<Domicilio> domicilios = baseService.findAll(); // o un método filtrado por persona
            List<DomicilioDTO> dtos = domicilios.stream()
                    .filter(d -> d.getPersonas().stream().anyMatch(c -> c.getId().equals(personaId)))
                    .map(domicilioMapper::toDTO)
                    .toList();

            return ResponseEntity.ok(dtos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }




    @PutMapping(value = "/{id}", consumes = "application/json")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody DomicilioDTO dto) {
        try {

            Domicilio existingDomicilio = baseService.findById(id);

            existingDomicilio.setCalle(dto.getCalle());
            existingDomicilio.setNumero(dto.getNumero());
            existingDomicilio.setCp(dto.getCp());


            if (dto.getLocalidad() != null && dto.getLocalidad().getId() != null) {
                Localidad localidad = localidadRepository.findById(dto.getLocalidad().getId())
                        .orElseThrow(() -> new ResourceNotFoundException("Localidad no encontrada"));
                existingDomicilio.setLocalidad(localidad);
            } else {
                existingDomicilio.setLocalidad(null);
            }


            Domicilio updated = baseService.update(id, existingDomicilio);
            return ResponseEntity.ok(domicilioMapper.toDTO(updated));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }
}