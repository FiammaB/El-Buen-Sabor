package ElBuenSabor.ProyectoFinal.Controllers;

import ElBuenSabor.ProyectoFinal.DTO.CategoriaCreateUpdateDTO;
import ElBuenSabor.ProyectoFinal.DTO.CategoriaDTO;
import ElBuenSabor.ProyectoFinal.Entities.Categoria; // Para el tipo de retorno de reactivate
import ElBuenSabor.ProyectoFinal.Entities.Sucursal; // Para el Optional de Sucursal
import ElBuenSabor.ProyectoFinal.Entities.TipoRubro; // Importar Enum
import ElBuenSabor.ProyectoFinal.Service.CategoriaService;
import ElBuenSabor.ProyectoFinal.Service.SucursalService; // Para validar sucursalId
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional; // Importar Optional
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/categorias")
@CrossOrigin(origins = "*")
public class CategoriaController {

    @Autowired
    private CategoriaService categoriaService;

    @Autowired
    private SucursalService sucursalService; // Para validar sucursalId

    @PostMapping("")
    public ResponseEntity<?> createCategoria(@Valid @RequestBody CategoriaCreateUpdateDTO categoriaDTO) {
        try {
            CategoriaDTO nuevaCategoria = categoriaService.createCategoria(categoriaDTO);
            return new ResponseEntity<>(nuevaCategoria, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getCategoriaById(@PathVariable Long id) {
        try {
            CategoriaDTO dto = categoriaService.findCategoriaById(id); // Devuelve activas
            if (dto != null) {
                return ResponseEntity.ok(dto);
            } else {
                return new ResponseEntity<>("Categoría activa no encontrada con ID: " + id, HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>("Error al obtener la categoría: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("")
    public ResponseEntity<?> listarCategoriasActivas(
            @RequestParam(required = false) TipoRubro tipoRubro, // Filtrar por tipo
            @RequestParam(required = false) Long sucursalId,
            @RequestParam(required = false) Long padreId, // Para listar subcategorías de un padre
            @RequestParam(required = false, defaultValue = "false") boolean soloRaiz) {
        try {
            List<CategoriaDTO> dtos;
            if (sucursalId != null) {
                // Usar findById que devuelve Optional<Sucursal> y luego verificar isPresent y baja
                Optional<Sucursal> sucursalOpt = sucursalService.findById(sucursalId);
                if (!sucursalOpt.isPresent() || sucursalOpt.get().isBaja()) {
                    return new ResponseEntity<>("Sucursal activa no encontrada con ID: " + sucursalId, HttpStatus.NOT_FOUND);
                }
                dtos = categoriaService.findBySucursalesId(sucursalId); // Devuelve categorías activas de esa sucursal
                if (tipoRubro != null) { // Filtrar adicionalmente por tipo si se provee
                    dtos = dtos.stream().filter(c -> c.getTipoRubro() == tipoRubro).collect(Collectors.toList());
                }
            } else if (padreId != null) {
                // Validar que la categoría padre exista y esté activa
                CategoriaDTO padreDTO = categoriaService.findCategoriaById(padreId);
                if (padreDTO == null) {
                    return new ResponseEntity<>("Categoría padre activa no encontrada con ID: " + padreId, HttpStatus.NOT_FOUND);
                }
                dtos = categoriaService.findSubcategorias(padreId, true); // Solo subcategorías activas
                if (tipoRubro != null) {
                    dtos = dtos.stream().filter(c -> c.getTipoRubro() == tipoRubro).collect(Collectors.toList());
                }
            } else if (soloRaiz) {
                dtos = categoriaService.findByCategoriaPadreIsNull(tipoRubro); // tipoRubro puede ser null para todas las raíz activas
            } else if (tipoRubro != null) {
                dtos = categoriaService.findByTipoRubro(tipoRubro, true); // Solo activas de ese tipo
            } else {
                dtos = categoriaService.findAllCategorias(); // Todas las activas
            }
            return ResponseEntity.ok(dtos);
        } catch (Exception e) {
            return new ResponseEntity<>("Error al listar categorías activas: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateCategoria(@PathVariable Long id, @Valid @RequestBody CategoriaCreateUpdateDTO categoriaDTO) {
        try {
            CategoriaDTO categoriaActualizada = categoriaService.updateCategoria(id, categoriaDTO);
            return ResponseEntity.ok(categoriaActualizada);
        } catch (Exception e) {
            if (e.getMessage().contains("no encontrada")) {
                return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> darBajaCategoria(@PathVariable Long id) {
        try {
            categoriaService.softDelete(id); // El servicio verifica si está en uso
            return ResponseEntity.ok("Categoría dada de baja correctamente (borrado lógico).");
        } catch (Exception e) {
            if (e.getMessage().contains("no encontrada") || e.getMessage().contains("ya está dada de baja")) {
                return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
            }
            if (e.getMessage().contains("tiene artículos activos o subcategorías activas")) {
                return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
            }
            return new ResponseEntity<>("Error al dar de baja la categoría: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PatchMapping("/{id}/reactivar")
    public ResponseEntity<?> reactivarCategoria(@PathVariable Long id) {
        try {
            Categoria categoriaReactivadaEntity = categoriaService.reactivate(id);
            return ResponseEntity.ok(convertToDTO(categoriaReactivadaEntity)); // Helper para convertir
        } catch (Exception e) {
            if (e.getMessage().contains("no encontrada") || e.getMessage().contains("no está dada de baja") || e.getMessage().contains("categoría padre") ) {
                return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
            }
            return new ResponseEntity<>("Error al reactivar la categoría: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/admin/todos")
    // @PreAuthorize("hasRole('ADMIN') or hasRole('COCINERO')")
    public ResponseEntity<?> getAllCategoriasIncludingDeletedForAdmin(@RequestParam(required = false) TipoRubro tipoRubro) {
        try {
            List<CategoriaDTO> dtos = categoriaService.findAllCategoriasIncludingDeleted(tipoRubro);
            return ResponseEntity.ok(dtos);
        } catch (Exception e) {
            return new ResponseEntity<>("Error al obtener todas las categorías (incluyendo bajas): " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/admin/{id}")
    // @PreAuthorize("hasRole('ADMIN') or hasRole('COCINERO')")
    public ResponseEntity<?> getCategoriaByIdIncludingDeletedForAdmin(@PathVariable Long id) {
        try {
            CategoriaDTO dto = categoriaService.findCategoriaByIdIncludingDeleted(id);
            if (dto != null) {
                return ResponseEntity.ok(dto);
            } else {
                return new ResponseEntity<>("Categoría (activa o inactiva) no encontrada con ID: " + id, HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>("Error al obtener la categoría (incluyendo bajas): " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Helper para convertir Entidad a DTO (si el servicio devuelve la entidad, como en reactivate)
    private CategoriaDTO convertToDTO(Categoria categoria) {
        if (categoria == null) return null;
        CategoriaDTO dto = new CategoriaDTO();
        dto.setId(categoria.getId());
        dto.setDenominacion(categoria.getDenominacion());
        dto.setTipoRubro(categoria.getTipoRubro());
        dto.setBaja(categoria.isBaja());
        if (categoria.getCategoriaPadre() != null) {
            dto.setCategoriaPadreId(categoria.getCategoriaPadre().getId());
            dto.setCategoriaPadreDenominacion(categoria.getCategoriaPadre().getDenominacion());
        }
        if (categoria.getSubCategorias() != null && !categoria.getSubCategorias().isEmpty()) {
            dto.setSubCategorias(categoria.getSubCategorias().stream()
                    .map(this::convertToSimpleDTO) // Usar DTO simple para evitar recursión
                    .collect(Collectors.toSet()));
        }
        if (categoria.getSucursales() != null) {
            dto.setSucursalIds(categoria.getSucursales().stream().map(Sucursal::getId).collect(Collectors.toList()));
        }
        return dto;
    }

    private CategoriaDTO convertToSimpleDTO(Categoria categoria) {
        if (categoria == null) return null;
        CategoriaDTO dto = new CategoriaDTO();
        dto.setId(categoria.getId());
        dto.setDenominacion(categoria.getDenominacion());
        dto.setTipoRubro(categoria.getTipoRubro());
        dto.setBaja(categoria.isBaja());
        if (categoria.getCategoriaPadre() != null) { // Solo el ID del padre en el simple DTO
            dto.setCategoriaPadreId(categoria.getCategoriaPadre().getId());
        }
        // No incluir subCategorias ni sucursales aquí
        return dto;
    }
}
