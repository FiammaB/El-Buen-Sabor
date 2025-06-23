package ElBuenSabor.ProyectoFinal.Controllers;

import ElBuenSabor.ProyectoFinal.DTO.CategoriaShortDTO;
import ElBuenSabor.ProyectoFinal.DTO.CategoriaDTO;
import ElBuenSabor.ProyectoFinal.Entities.Categoria;
import ElBuenSabor.ProyectoFinal.Entities.Sucursal;
import ElBuenSabor.ProyectoFinal.Service.CategoriaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/categorias")
@CrossOrigin(origins = "*") // Ajustar según necesidades de seguridad
public class CategoriaController {

    @Autowired
    private CategoriaService categoriaService;

    // Endpoint para crear una nueva categoría
    @PostMapping("")
    public ResponseEntity<?> crearCategoria(@RequestBody CategoriaShortDTO categoriaCreateUpdateDTO) {
        try {
            Categoria nuevaCategoria = categoriaService.createCategoria(categoriaCreateUpdateDTO);
            return new ResponseEntity<>(convertToCategoriaDTO(nuevaCategoria), HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    // Endpoint para obtener una categoría por su ID
    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerCategoriaPorId(@PathVariable Long id) {
        try {
            Optional<Categoria> categoriaOptional = categoriaService.findById(id); //
            if (categoriaOptional.isPresent()) {
                return ResponseEntity.ok(convertToCategoriaDTO(categoriaOptional.get()));
            } else {
                return new ResponseEntity<>("Categoría no encontrada.", HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Endpoint para actualizar una categoría
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarCategoria(@PathVariable Long id, @RequestBody CategoriaShortDTO categoriaCreateUpdateDTO) {
        try {
            Categoria categoriaActualizada = categoriaService.updateCategoria(id, categoriaCreateUpdateDTO);
            return ResponseEntity.ok(convertToCategoriaDTO(categoriaActualizada));
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    // Endpoint para listar todas las categorías (podría devolver una estructura jerárquica)
    @GetMapping("")
    public ResponseEntity<?> listarCategorias(
            @RequestParam(required = false) Long sucursalId,
            @RequestParam(required = false, defaultValue = "false") boolean soloRaiz) {
        try {
            List<Categoria> categorias;
            if (sucursalId != null) {
                categorias = categoriaService.findBySucursalesId(sucursalId); //
            } else if (soloRaiz) {
                categorias = categoriaService.findByCategoriaPadreIsNull(); //
            } else {
                categorias = categoriaService.findAll(); //
            }
            List<CategoriaDTO> dtos = categorias.stream()
                    .map(this::convertToCategoriaDTO) // Usar el helper que incluye subcategorías
                    .collect(Collectors.toList());
            return ResponseEntity.ok(dtos);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Endpoint para obtener las categorías de nivel superior (raíz)
    @GetMapping("/raiz")
    public ResponseEntity<?> listarCategoriasRaiz() {
        try {
            List<Categoria> categoriasRaiz = categoriaService.findByCategoriaPadreIsNull(); //
            List<CategoriaDTO> dtos = categoriasRaiz.stream()
                    .map(this::convertToCategoriaDTO)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(dtos);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Endpoint para obtener las subcategorías de una categoría padre
    @GetMapping("/{id}/subcategorias")
    public ResponseEntity<?> listarSubcategorias(@PathVariable Long id) {
        try {
            Optional<Categoria> categoriaPadreOpt = categoriaService.findById(id); //
            if (!categoriaPadreOpt.isPresent()) {
                return new ResponseEntity<>("Categoría padre no encontrada.", HttpStatus.NOT_FOUND);
            }
            Categoria categoriaPadre = categoriaPadreOpt.get();
            List<CategoriaDTO> dtos = categoriaPadre.getSubCategorias().stream() //
                    .map(this::convertToCategoriaDTOSimple) // Usar DTO simple para subcategorías
                    .collect(Collectors.toList());
            return ResponseEntity.ok(dtos);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    // Endpoint para eliminar una categoría
    // Considerar restricciones: no eliminar si tiene subcategorías o artículos asociados.
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarCategoria(@PathVariable Long id) {
        try {
            // Lógica de validación en el servicio (ej. no eliminar si tiene artículos o subcategorías)
            // Optional<Categoria> catOpt = categoriaService.findById(id);
            // if(catOpt.isPresent()){
            //     Categoria cat = catOpt.get();
            //     if(cat.getArticulos() != null && !cat.getArticulos().isEmpty()){
            //         return new ResponseEntity<>("No se puede eliminar la categoría porque tiene artículos asociados.", HttpStatus.BAD_REQUEST);
            //     }
            //     if(cat.getSubCategorias() != null && !cat.getSubCategorias().isEmpty()){
            //         return new ResponseEntity<>("No se puede eliminar la categoría porque tiene subcategorías asociadas.", HttpStatus.BAD_REQUEST);
            //     }
            // }

            boolean eliminado = categoriaService.delete(id); //
            if (eliminado) {
                return ResponseEntity.ok("Categoría eliminada correctamente.");
            } else {
                return new ResponseEntity<>("Categoría no encontrada para eliminar.", HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    // --- Helper para convertir Entidad a DTO ---
    private CategoriaDTO convertToCategoriaDTO(Categoria categoria) {
        if (categoria == null) return null;
        CategoriaDTO dto = new CategoriaDTO();
        dto.setId(categoria.getId());
        dto.setDenominacion(categoria.getDenominacion());

        if (categoria.getCategoriaPadre() != null) {
            dto.setCategoriaPadreId(categoria.getCategoriaPadre().getId());
            // Para evitar ciclos infinitos, no convertimos el padre completo aquí,
            // o usamos un CategoriaSimpleDTO para el padre.
            dto.setCategoriaPadre(convertToCategoriaDTOSimple(categoria.getCategoriaPadre()));
        }

        if (categoria.getSubCategorias() != null && !categoria.getSubCategorias().isEmpty()) { //
            dto.setSubCategorias(categoria.getSubCategorias().stream() //
                    .map(this::convertToCategoriaDTOSimple) // Usar DTO simple para subcategorías
                    .collect(Collectors.toSet()));
        }

        if (categoria.getSucursales() != null && !categoria.getSucursales().isEmpty()) { //
            dto.setSucursalIds(categoria.getSucursales().stream() //
                    .map(Sucursal::getId)
                    .collect(Collectors.toList()));
        }
        // No incluimos la lista de artículos aquí para mantener el DTO de categoría más enfocado.
        // Los artículos se obtendrían a través del ArticuloController por categoría ID.
        return dto;
    }

    // DTO más simple para evitar recursividad infinita o DTOs muy pesados
    private CategoriaDTO convertToCategoriaDTOSimple(Categoria categoria) {
        if (categoria == null) return null;
        CategoriaDTO dto = new CategoriaDTO();
        dto.setId(categoria.getId());
        dto.setDenominacion(categoria.getDenominacion());
        if (categoria.getCategoriaPadre() != null) {
            dto.setCategoriaPadreId(categoria.getCategoriaPadre().getId());
        }
        // No incluir subcategorías ni sucursales en la versión simple
        return dto;
    }
}