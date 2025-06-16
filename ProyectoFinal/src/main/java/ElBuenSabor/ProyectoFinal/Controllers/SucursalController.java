package ElBuenSabor.ProyectoFinal.Controllers;

import ElBuenSabor.ProyectoFinal.DTO.SucursalCreateDTO;
import ElBuenSabor.ProyectoFinal.DTO.SucursalDTO;
import ElBuenSabor.ProyectoFinal.Entities.Categoria;
import ElBuenSabor.ProyectoFinal.Entities.Promocion;
import ElBuenSabor.ProyectoFinal.Entities.Sucursal;
import ElBuenSabor.ProyectoFinal.Exceptions.ResourceNotFoundException;
import ElBuenSabor.ProyectoFinal.Mappers.SucursalMapper;
import ElBuenSabor.ProyectoFinal.Repositories.CategoriaRepository;
import ElBuenSabor.ProyectoFinal.Repositories.DomicilioRepository;
import ElBuenSabor.ProyectoFinal.Repositories.EmpresaRepository;
import ElBuenSabor.ProyectoFinal.Repositories.PromocionRepository;
import ElBuenSabor.ProyectoFinal.Service.SucursalService; // Usar la interfaz específica
// Ya no es necesario si se inyecta por constructor explícito al padre
// import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.time.LocalDate; // Si aún se usan en alguna lógica específica aquí
import java.time.LocalTime; // Si aún se usan en alguna lógica específica aquí


@RestController
@RequestMapping("/api/sucursales") // Define la URL base para este controlador
// SucursalController ahora extiende BaseController
public class SucursalController extends BaseController<Sucursal, Long> {

    private final SucursalMapper sucursalMapper;

    // Repositorios necesarios para resolver relaciones en el controlador
    private final DomicilioRepository domicilioRepository;
    private final EmpresaRepository empresaRepository;
    private final CategoriaRepository categoriaRepository;
    private final PromocionRepository promocionRepository;

    // El constructor inyecta el servicio específico de Sucursal y todas las dependencias adicionales
    public SucursalController(
            SucursalService sucursalService, // Servicio específico
            SucursalMapper sucursalMapper,
            DomicilioRepository domicilioRepository,
            EmpresaRepository empresaRepository,
            CategoriaRepository categoriaRepository,
            PromocionRepository promocionRepository) {
        super(sucursalService); // Pasa el servicio al constructor del BaseController
        this.sucursalMapper = sucursalMapper;
        this.domicilioRepository = domicilioRepository;
        this.empresaRepository = empresaRepository;
        this.categoriaRepository = categoriaRepository;
        this.promocionRepository = promocionRepository;
    }

    // Sobrescribir getAll para devolver DTOs y manejar excepciones
    @GetMapping
    @Override // Sobrescribe el getAll del BaseController
    public ResponseEntity<?> getAll() {
        try {
            List<Sucursal> sucursales = baseService.findAll(); // Llama al findAll del padre
            List<SucursalDTO> dtos = sucursales.stream()
                    .map(sucursalMapper::toDTO)
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
            Sucursal sucursal = baseService.findById(id); // Llama al findById del padre
            return ResponseEntity.ok(sucursalMapper.toDTO(sucursal));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    // Sobrescribir create para aceptar un DTO de entrada, mapear y manejar excepciones
    @PostMapping(consumes = "application/json")
    // @Override // <<--- Quitar @Override aquí, ya que la firma del método es diferente (recibe DTO)
    public ResponseEntity<?> create(@RequestBody SucursalCreateDTO dto) {
        try {
            Sucursal sucursal = sucursalMapper.toEntity(dto); // Mapea el DTO a la entidad Sucursal

            // Asignar relaciones por ID
            sucursal.setDomicilio(domicilioRepository.findById(dto.getDomicilioId())
                    .orElseThrow(() -> new ResourceNotFoundException("Domicilio no encontrado")));

            sucursal.setEmpresa(empresaRepository.findById(dto.getEmpresaId())
                    .orElseThrow(() -> new ResourceNotFoundException("Empresa no encontrada")));

            // Asignar colecciones (Categorias, Promociones)
            if (dto.getCategoriaIds() != null) {
                List<Categoria> categorias = categoriaRepository.findAllById(dto.getCategoriaIds());
                sucursal.setCategorias(categorias);
            }

            if (dto.getPromocionIds() != null) {
                List<Promocion> promociones = promocionRepository.findAllById(dto.getPromocionIds());
                sucursal.setPromociones(promociones);
            }
            sucursal.setBaja(false); // Por defecto, una nueva sucursal no está dada de baja

            Sucursal saved = baseService.save(sucursal); // Llama al save del padre
            return ResponseEntity.status(HttpStatus.CREATED).body(sucursalMapper.toDTO(saved)); // Convierte a DTO para la respuesta
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    // Sobrescribir update para aceptar un DTO de entrada, mapear y manejar excepciones
    @PutMapping(value = "/{id}", consumes = "application/json")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody SucursalCreateDTO dto) {
        try {
            // Obtener la entidad existente
            Sucursal existingSucursal = baseService.findById(id);

            // Mapear las propiedades del DTO a la entidad existente
            existingSucursal.setNombre(dto.getNombre());
            existingSucursal.setHorarioApertura(dto.getHorarioApertura());
            existingSucursal.setHorarioCierre(dto.getHorarioCierre());

            // Actualizar relaciones OneToOne y ManyToOne
            existingSucursal.setDomicilio(domicilioRepository.findById(dto.getDomicilioId())
                    .orElseThrow(() -> new ResourceNotFoundException("Domicilio no encontrado")));

            existingSucursal.setEmpresa(empresaRepository.findById(dto.getEmpresaId())
                    .orElseThrow(() -> new ResourceNotFoundException("Empresa no encontrada")));

            // Sincronizar colecciones (Categorias, Promociones)
            if (dto.getCategoriaIds() != null) {
                List<Categoria> categorias = categoriaRepository.findAllById(dto.getCategoriaIds());
                existingSucursal.getCategorias().clear();
                existingSucursal.getCategorias().addAll(categorias);
            } else {
                existingSucursal.getCategorias().clear(); // Si no se envían IDs, limpiar la colección
            }

            if (dto.getPromocionIds() != null) {
                List<Promocion> promociones = promocionRepository.findAllById(dto.getPromocionIds());
                existingSucursal.getPromociones().clear();
                existingSucursal.getPromociones().addAll(promociones);
            } else {
                existingSucursal.getPromociones().clear(); // Si no se envían IDs, limpiar la colección
            }

            Sucursal updated = baseService.update(id, existingSucursal); // Llama al update del padre con la entidad EXISTENTE
            return ResponseEntity.ok(sucursalMapper.toDTO(updated)); // Convierte a DTO para la respuesta
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

}