package ElBuenSabor.ProyectoFinal.Controllers;

import ElBuenSabor.ProyectoFinal.DTO.CategoriaShortDTO;
import ElBuenSabor.ProyectoFinal.DTO.CategoriaDTO;
import ElBuenSabor.ProyectoFinal.Entities.ArticuloInsumo;
import ElBuenSabor.ProyectoFinal.Entities.Categoria;
import ElBuenSabor.ProyectoFinal.Mappers.CategoriaMapper;
import ElBuenSabor.ProyectoFinal.Service.CategoriaService; // Usar la interfaz específica
// Ya no es necesario si se inyecta por constructor explícito al padre
// import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categorias")
public class CategoriaController extends BaseController<Categoria, Long> {

    private final CategoriaMapper categoriaMapper;
    private final CategoriaService categoriaService;

    public CategoriaController(
            CategoriaService categoriaService,
            CategoriaMapper categoriaMapper) {
        super(categoriaService);
        this.categoriaService = categoriaService;
        this.categoriaMapper = categoriaMapper;
    }

    @GetMapping
    @Override
    public ResponseEntity<?> getAll() {
        try {
            List<Categoria> categorias = baseService.findAll();
            List<CategoriaDTO> dtos = categorias.stream()
                    .map(categoriaMapper::toDTO)
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
            Categoria categoria = baseService.findById(id);
            return ResponseEntity.ok(categoriaMapper.toDTO(categoria));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    @PostMapping(consumes = "application/json")
    public ResponseEntity<?> create(@RequestBody CategoriaShortDTO dto) {
        try {
            System.out.println("Categoria a crear: " + dto.getDenominacion() +" - "+ dto.getBaja());
            Categoria categoria = categoriaMapper.toEntity(dto);
            categoria.setBaja(dto.getBaja() != null ? dto.getBaja() : false);

            if (dto.getCategoriaPadreId() != null) {
                Categoria padre = categoriaService.findById(dto.getCategoriaPadreId());
                categoria.setCategoriaPadre(padre);
            }

            Categoria saved = baseService.save(categoria);
            System.out.println("Categoria creada: " + saved.getDenominacion() +" - "+ saved.getBaja());
            return ResponseEntity.status(HttpStatus.CREATED).body(categoriaMapper.toDTO(saved));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    @PutMapping(value = "/{id}", consumes = "application/json")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody CategoriaShortDTO dto) {
        try {
            Categoria existingCategory = baseService.findById(id);

            existingCategory.setDenominacion(dto.getDenominacion());

            if (dto.getCategoriaPadreId() != null) {
                Categoria padre = categoriaService.findById(dto.getCategoriaPadreId());
                existingCategory.setCategoriaPadre(padre);
            } else {
                existingCategory.setCategoriaPadre(null);
            }

            Categoria updated = baseService.update(id, existingCategory);
            return ResponseEntity.ok(categoriaMapper.toDTO(updated));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    @PatchMapping("/{id}/baja")
    public ResponseEntity<?> toggleBaja(
            @PathVariable Long id,
            @RequestParam boolean baja
    ) {
        try {
            Categoria actualizado = baseService.toggleBaja(id, baja);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }


}