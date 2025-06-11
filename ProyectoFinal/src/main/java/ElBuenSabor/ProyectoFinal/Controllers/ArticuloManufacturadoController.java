package ElBuenSabor.ProyectoFinal.Controllers;


import ElBuenSabor.ProyectoFinal.DTO.ArticuloManufacturadoDTO;
import ElBuenSabor.ProyectoFinal.Entities.ArticuloManufacturado;
import ElBuenSabor.ProyectoFinal.Mapper.ArticuloManufacturadoMapper;
import ElBuenSabor.ProyectoFinal.Servicios.ArticuloManufacturadoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController // Indica que esta clase es un controlador REST
@RequestMapping("/api/v1/articulos") // Define la ruta base para todos los endpoints de este controlador
@CrossOrigin(origins = "*") // Permite solicitudes desde cualquier origen. Considera restringirlo en producción.
public class ArticuloManufacturadoController {

    private final ArticuloManufacturadoService articuloManufacturadoService;
    @Autowired
    private final ArticuloManufacturadoMapper articuloManufacturadoMapper;

    @Autowired // Inyecta las dependencias necesarias
    public ArticuloManufacturadoController(
            ArticuloManufacturadoService articuloManufacturadoService,
            ArticuloManufacturadoMapper articuloManufacturadoMapper) {
        this.articuloManufacturadoService = articuloManufacturadoService;
        this.articuloManufacturadoMapper = articuloManufacturadoMapper;
    }

    /**
     * Endpoint para crear un nuevo ArticuloManufacturado.
     * Recibe un DTO, lo convierte a entidad, lo guarda y devuelve el DTO del ArticuloManufacturado creado.
     * POST /api/v1/articulos-manufacturados
     * @param articuloManufacturadoDTO El DTO del artículo manufacturado a crear.
     * @return ResponseEntity con el DTO del artículo creado y el estado HTTP 201 CREATED.
     */
    @PostMapping
    public ResponseEntity<ArticuloManufacturadoDTO> create(@RequestBody ArticuloManufacturadoDTO articuloManufacturadoDTO) {
        // Convierte el DTO de entrada a la entidad
        ArticuloManufacturado articuloManufacturado = articuloManufacturadoMapper.toEntity(articuloManufacturadoDTO);
        // Llama al servicio para guardar la entidad
        ArticuloManufacturado savedArticulo = articuloManufacturadoService.save(articuloManufacturado);
        // Convierte la entidad guardada de nuevo a DTO para la respuesta
        ArticuloManufacturadoDTO responseDTO = articuloManufacturadoMapper.toDTO(savedArticulo);
        return new ResponseEntity<>(responseDTO, HttpStatus.CREATED);
    }

    /**
     * Endpoint para obtener un ArticuloManufacturado por su ID.
     * GET /api/v1/articulos-manufacturados/{id}
     * @param id El ID del artículo manufacturado a buscar.
     * @return ResponseEntity con el DTO del artículo y el estado HTTP 200 OK.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ArticuloManufacturadoDTO> getById(@PathVariable Long id) {
        // Llama al servicio para obtener la entidad por ID
        ArticuloManufacturado articuloManufacturado = articuloManufacturadoService.findById(id);
        // Convierte la entidad a DTO para la respuesta
        ArticuloManufacturadoDTO responseDTO = articuloManufacturadoMapper.toDTO(articuloManufacturado);
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }

    /**
     * Endpoint para obtener todos los ArticulosManufacturados.
     * GET /api/v1/articulos-manufacturados
     * @return ResponseEntity con una lista de DTOs de artículos y el estado HTTP 200 OK.
     */
    @GetMapping
    public ResponseEntity<List<ArticuloManufacturadoDTO>> getAll() {
        // Llama al servicio para obtener todas las entidades
        List<ArticuloManufacturado> articulosManufacturados = articuloManufacturadoService.findAll();
        // Convierte la lista de entidades a una lista de DTOs para la respuesta
        List<ArticuloManufacturadoDTO> responseDTOs = articuloManufacturadoMapper.amToAmDto(articulosManufacturados);
        return new ResponseEntity<>(responseDTOs, HttpStatus.OK);
    }

    /**
     * Endpoint para actualizar un ArticuloManufacturado existente.
     * PUT /api/v1/articulos-manufacturados/{id}
     * @param id El ID del artículo manufacturado a actualizar.
     * @param articuloManufacturadoDTO El DTO con los datos actualizados.
     * @return ResponseEntity con el DTO del artículo actualizado y el estado HTTP 200 OK.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ArticuloManufacturadoDTO> update(@PathVariable Long id, @RequestBody ArticuloManufacturadoDTO articuloManufacturadoDTO) {
        // Asegura que el ID del DTO sea el mismo que el ID del path variable
        articuloManufacturadoDTO.setId(id);
        // Convierte el DTO de entrada a la entidad
        ArticuloManufacturado articuloManufacturado = articuloManufacturadoMapper.toEntity(articuloManufacturadoDTO);
        // Llama al servicio para actualizar la entidad
        ArticuloManufacturado updatedArticulo = articuloManufacturadoService.update(id, articuloManufacturado);
        // Convierte la entidad actualizada de nuevo a DTO para la respuesta
        ArticuloManufacturadoDTO responseDTO = articuloManufacturadoMapper.toDTO(updatedArticulo);
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }

    /**
     * Endpoint para "eliminar" (dar de baja lógicamente) un ArticuloManufacturado.
     * DELETE /api/v1/articulos-manufacturados/{id}
     * @param id El ID del artículo manufacturado a dar de baja.
     * @return ResponseEntity con el estado HTTP 204 NO CONTENT (sin contenido de respuesta).
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        // Llama al servicio para dar de baja la entidad
        articuloManufacturadoService.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}