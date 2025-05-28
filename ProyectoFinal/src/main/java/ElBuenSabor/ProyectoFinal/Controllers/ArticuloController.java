package ElBuenSabor.ProyectoFinal.Controllers;

import ElBuenSabor.ProyectoFinal.DTO.ArticuloInsumoDTO;
import ElBuenSabor.ProyectoFinal.DTO.ArticuloManufacturadoDTO;
import ElBuenSabor.ProyectoFinal.DTO.RegistrarCompraDTO;
import ElBuenSabor.ProyectoFinal.Entities.Articulo;
import ElBuenSabor.ProyectoFinal.Service.ArticuloService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// Imports para el helper convertToGenericArticuloDTO si se mantiene en el controller
// Aunque idealmente el servicio ya devuelve los DTOs correctos para estos endpoints.
import ElBuenSabor.ProyectoFinal.Entities.ArticuloInsumo;
import ElBuenSabor.ProyectoFinal.Entities.ArticuloManufacturado;
import ElBuenSabor.ProyectoFinal.Entities.ArticuloManufacturadoDetalle;
import ElBuenSabor.ProyectoFinal.Entities.Categoria;
import ElBuenSabor.ProyectoFinal.Entities.Imagen;
import ElBuenSabor.ProyectoFinal.Entities.UnidadMedida;
import ElBuenSabor.ProyectoFinal.DTO.ArticuloSimpleBaseDTO;
import ElBuenSabor.ProyectoFinal.DTO.CategoriaDTO;
import ElBuenSabor.ProyectoFinal.DTO.UnidadMedidaDTO;
import ElBuenSabor.ProyectoFinal.DTO.ImagenDTO;
import ElBuenSabor.ProyectoFinal.DTO.ArticuloManufacturadoDetalleDTO;
import java.util.HashSet;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/api/v1/articulos")
@CrossOrigin(origins = "*") // Ajustar según necesidades de seguridad en producción
public class ArticuloController {

    @Autowired
    private ArticuloService articuloService;

    // --- Endpoints para ArticuloInsumo ---

    @PostMapping("/insumos")
    public ResponseEntity<?> crearArticuloInsumo(@Valid @RequestBody ArticuloInsumoDTO insumoDTO) {
        try {
            ArticuloInsumoDTO nuevoInsumo = articuloService.createArticuloInsumo(insumoDTO);
            return new ResponseEntity<>(nuevoInsumo, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/insumos/{id}")
    public ResponseEntity<?> obtenerArticuloInsumoPorId(@PathVariable Long id) {
        try {
            ArticuloInsumoDTO dto = articuloService.findArticuloInsumoById(id);
            if (dto != null) {
                return ResponseEntity.ok(dto);
            } else {
                return new ResponseEntity<>("Artículo Insumo activo no encontrado con ID: " + id, HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>("Error al obtener el Artículo Insumo: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/insumos/{id}")
    public ResponseEntity<?> actualizarArticuloInsumo(@PathVariable Long id, @Valid @RequestBody ArticuloInsumoDTO insumoDTO) {
        try {
            ArticuloInsumoDTO insumoActualizado = articuloService.updateArticuloInsumo(id, insumoDTO);
            return ResponseEntity.ok(insumoActualizado);
        } catch (Exception e) {
            if (e.getMessage().contains("no encontrado")) {
                return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/insumos")
    public ResponseEntity<?> listarArticulosInsumoActivos() {
        try {
            List<ArticuloInsumoDTO> dtos = articuloService.findAllArticulosInsumo();
            return ResponseEntity.ok(dtos);
        } catch (Exception e) {
            return new ResponseEntity<>("Error al listar Artículos Insumo activos: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/insumos/stock-bajo")
    public ResponseEntity<?> listarArticulosInsumoConStockBajo() {
        try {
            List<ArticuloInsumoDTO> dtos = articuloService.findArticulosInsumoByStockBajo();
            return ResponseEntity.ok(dtos);
        } catch (Exception e) {
            return new ResponseEntity<>("Error al listar Artículos Insumo con stock bajo: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/insumos/{id}/registrar-compra")
    public ResponseEntity<?> registrarCompraInsumo(@PathVariable Long id, @Valid @RequestBody RegistrarCompraDTO compraDTO) {
        try {
            ArticuloInsumoDTO insumoActualizado = articuloService.registrarCompraInsumo(id, compraDTO.getCantidadComprada(), compraDTO.getNuevoPrecioCosto());
            return ResponseEntity.ok(insumoActualizado);
        } catch (Exception e) {
            if (e.getMessage().contains("no encontrado") || e.getMessage().contains("dado de baja")) {
                return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }


    // --- Endpoints para ArticuloManufacturado ---

    @PostMapping("/manufacturados")
    public ResponseEntity<?> crearArticuloManufacturado(@Valid @RequestBody ArticuloManufacturadoDTO manufacturadoDTO) {
        try {
            ArticuloManufacturadoDTO nuevoManufacturado = articuloService.createArticuloManufacturado(manufacturadoDTO);
            return new ResponseEntity<>(nuevoManufacturado, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/manufacturados/{id}")
    public ResponseEntity<?> obtenerArticuloManufacturadoPorId(@PathVariable Long id) {
        try {
            ArticuloManufacturadoDTO dto = articuloService.findArticuloManufacturadoById(id);
            if (dto != null) {
                return ResponseEntity.ok(dto);
            } else {
                return new ResponseEntity<>("Artículo Manufacturado activo no encontrado con ID: " + id, HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>("Error al obtener el Artículo Manufacturado: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/manufacturados/{id}")
    public ResponseEntity<?> actualizarArticuloManufacturado(@PathVariable Long id, @Valid @RequestBody ArticuloManufacturadoDTO manufacturadoDTO) {
        try {
            ArticuloManufacturadoDTO manufacturadoActualizado = articuloService.updateArticuloManufacturado(id, manufacturadoDTO);
            return ResponseEntity.ok(manufacturadoActualizado);
        } catch (Exception e) {
            if (e.getMessage().contains("no encontrado")) {
                return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/manufacturados")
    public ResponseEntity<?> listarArticulosManufacturadoActivos() {
        try {
            List<ArticuloManufacturadoDTO> dtos = articuloService.findAllArticulosManufacturados();
            return ResponseEntity.ok(dtos);
        } catch (Exception e) {
            return new ResponseEntity<>("Error al listar Artículos Manufacturados activos: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/manufacturados/{id}/maximo-producible")
    public ResponseEntity<?> obtenerMaximoProducible(@PathVariable Long id) {
        try {
            Integer maximo = articuloService.calcularMaximoProducible(id);
            return ResponseEntity.ok(maximo);
        } catch (Exception e) {
            if (e.getMessage().contains("no encontrado") || e.getMessage().contains("dado de baja")) {
                return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // --- Endpoints Generales para Articulos (Insumos y Manufacturados) ---

    @GetMapping("/buscar")
    public ResponseEntity<?> buscarArticulosPorDenominacion(@RequestParam String denominacion) {
        try {
            List<Object> dtos = articuloService.findArticulosByDenominacion(denominacion);
            return ResponseEntity.ok(dtos);
        } catch (Exception e) {
            return new ResponseEntity<>("Error al buscar artículos por denominación: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/categoria/{categoriaId}")
    public ResponseEntity<?> buscarArticulosPorCategoria(@PathVariable Long categoriaId) {
        try {
            List<Object> dtos = articuloService.findArticulosByCategoriaId(categoriaId);
            return ResponseEntity.ok(dtos);
        } catch (Exception e) {
            if (e.getMessage().contains("no encontrada")) {
                return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>("Error al buscar artículos por categoría: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // --- Endpoints de Administración (Soft Delete, Reactivate, Listar Todos) ---

    @DeleteMapping("/{id}")
    public ResponseEntity<?> darBajaArticulo(@PathVariable Long id) {
        try {
            articuloService.softDelete(id); // El servicio maneja la lógica de isArticuloInActiveUse
            return ResponseEntity.ok("Artículo ID: " + id + " dado de baja correctamente (borrado lógico).");
        } catch (Exception e) {
            if (e.getMessage().contains("no encontrado") || e.getMessage().contains("ya está dado de baja")) {
                return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
            }
            if (e.getMessage().contains("está en uso activo")) {
                return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT); // 409 Conflict
            }
            return new ResponseEntity<>("Error al dar de baja el artículo: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PatchMapping("/{id}/reactivar")
    public ResponseEntity<?> reactivarArticulo(@PathVariable Long id) {
        try {
            Articulo articuloReactivado = articuloService.reactivate(id);
            // El servicio devuelve la entidad, el controlador la convierte al DTO apropiado.
            Object dto = convertToGenericArticuloDTO(articuloReactivado);
            return ResponseEntity.ok(dto);
        } catch (Exception e) {
            if (e.getMessage().contains("no encontrado") || e.getMessage().contains("no está dado de baja") || e.getMessage().contains("no se puede reactivar")) {
                return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
            }
            return new ResponseEntity<>("Error al reactivar el artículo: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/admin/todos")
    public ResponseEntity<?> getAllArticulosIncludingDeletedForAdmin() {
        try {
            List<Object> dtos = articuloService.findAllArticulosRawDTOs();
            return ResponseEntity.ok(dtos);
        } catch (Exception e) {
            return new ResponseEntity<>("Error al obtener todos los artículos (incluyendo bajas): " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/admin/{id}")
    public ResponseEntity<?> getArticuloByIdIncludingDeletedForAdmin(@PathVariable Long id) {
        try {
            Object dto = articuloService.findArticuloByIdRawDTO(id);
            if (dto != null) {
                return ResponseEntity.ok(dto);
            } else {
                return new ResponseEntity<>("Artículo (activo o inactivo) no encontrado con ID: " + id, HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>("Error al obtener el artículo (incluyendo bajas): " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // --- Helpers de Conversión DTO (Idealmente en una clase Mapper o en el Servicio) ---
    // Estos se mantienen aquí para el caso de que el servicio devuelva entidades (ej. reactivate)
    // y el controlador necesite convertirlas. Si el servicio siempre devuelve DTOs, estos helpers aquí son menos necesarios.

    private Object convertToGenericArticuloDTO(Articulo articulo) {
        if (articulo == null) return null;
        if (articulo instanceof ArticuloInsumo) {
            return convertToArticuloInsumoDTO((ArticuloInsumo) articulo);
        } else if (articulo instanceof ArticuloManufacturado) {
            return convertToArticuloManufacturadoDTO((ArticuloManufacturado) articulo);
        }
        // Fallback para un Articulo que no sea ni Insumo ni Manufacturado
        ArticuloSimpleBaseDTO baseDto = new ArticuloSimpleBaseDTO(); // Asegúrate que ArticuloSimpleBaseDTO exista y esté bien definido
        baseDto.setId(articulo.getId());
        baseDto.setDenominacion(articulo.getDenominacion());
        baseDto.setPrecioVenta(articulo.getPrecioVenta());
        baseDto.setBaja(articulo.isBaja());
        if (articulo.getCategoria() != null) baseDto.setCategoria(convertToCategoriaSimpleDTO(articulo.getCategoria()));
        if (articulo.getUnidadMedida() != null) baseDto.setUnidadMedida(convertToUnidadMedidaDTO(articulo.getUnidadMedida()));
        if (articulo.getImagen() != null) baseDto.setImagen(convertToImagenDTO(articulo.getImagen()));
        baseDto.setTipo(articulo.getClass().getSimpleName());
        return baseDto;
    }

    private ArticuloInsumoDTO convertToArticuloInsumoDTO(ArticuloInsumo insumo) {
        if (insumo == null) return null;
        ArticuloInsumoDTO dto = new ArticuloInsumoDTO();
        dto.setId(insumo.getId());
        dto.setDenominacion(insumo.getDenominacion());
        dto.setPrecioVenta(insumo.getPrecioVenta());
        dto.setBaja(insumo.isBaja());
        if (insumo.getCategoria() != null) {
            dto.setCategoriaId(insumo.getCategoria().getId());
            dto.setCategoria(convertToCategoriaSimpleDTO(insumo.getCategoria()));
        }
        if (insumo.getUnidadMedida() != null) {
            dto.setUnidadMedidaId(insumo.getUnidadMedida().getId());
            dto.setUnidadMedida(convertToUnidadMedidaDTO(insumo.getUnidadMedida()));
        }
        if (insumo.getImagen() != null) {
            dto.setImagenId(insumo.getImagen().getId());
            dto.setImagen(convertToImagenDTO(insumo.getImagen()));
        }
        dto.setPrecioCompra(insumo.getPrecioCompra());
        dto.setStockActual(insumo.getStockActual());
        dto.setStockMinimo(insumo.getStockMinimo());
        dto.setEsParaElaborar(insumo.getEsParaElaborar());
        return dto;
    }

    private ArticuloManufacturadoDTO convertToArticuloManufacturadoDTO(ArticuloManufacturado manufacturado) {
        if (manufacturado == null) return null;
        ArticuloManufacturadoDTO dto = new ArticuloManufacturadoDTO();
        dto.setId(manufacturado.getId());
        dto.setDenominacion(manufacturado.getDenominacion());
        dto.setPrecioVenta(manufacturado.getPrecioVenta());
        dto.setBaja(manufacturado.isBaja());
        if (manufacturado.getCategoria() != null) {
            dto.setCategoriaId(manufacturado.getCategoria().getId());
            dto.setCategoria(convertToCategoriaSimpleDTO(manufacturado.getCategoria()));
        }
        if (manufacturado.getUnidadMedida() != null) {
            dto.setUnidadMedidaId(manufacturado.getUnidadMedida().getId());
            dto.setUnidadMedida(convertToUnidadMedidaDTO(manufacturado.getUnidadMedida()));
        }
        if (manufacturado.getImagen() != null) {
            dto.setImagenId(manufacturado.getImagen().getId());
            dto.setImagen(convertToImagenDTO(manufacturado.getImagen()));
        }
        dto.setDescripcion(manufacturado.getDescripcion());
        dto.setTiempoEstimadoMinutos(manufacturado.getTiempoEstimadoMinutos());
        dto.setPreparacion(manufacturado.getPreparacion());
        if (manufacturado.getDetalles() != null) {
            dto.setDetalles(manufacturado.getDetalles().stream()
                    .map(this::convertToArticuloManufacturadoDetalleDTO)
                    .collect(Collectors.toSet()));
        } else {
            dto.setDetalles(new HashSet<>());
        }
        return dto;
    }

    private ArticuloManufacturadoDetalleDTO convertToArticuloManufacturadoDetalleDTO(ArticuloManufacturadoDetalle detalle) {
        if (detalle == null) return null;
        ArticuloManufacturadoDetalleDTO dto = new ArticuloManufacturadoDetalleDTO();
        dto.setId(detalle.getId());
        dto.setCantidad(detalle.getCantidad());
        if (detalle.getArticuloInsumo() != null) {
            dto.setArticuloInsumoId(detalle.getArticuloInsumo().getId());
            ArticuloInsumoDTO insumoSimpleDto = new ArticuloInsumoDTO();
            insumoSimpleDto.setId(detalle.getArticuloInsumo().getId());
            insumoSimpleDto.setDenominacion(detalle.getArticuloInsumo().getDenominacion());
            insumoSimpleDto.setBaja(detalle.getArticuloInsumo().isBaja());
            if (detalle.getArticuloInsumo().getUnidadMedida() != null){
                insumoSimpleDto.setUnidadMedida(convertToUnidadMedidaDTO(detalle.getArticuloInsumo().getUnidadMedida()));
            }
            dto.setArticuloInsumo(insumoSimpleDto);
        }
        return dto;
    }

    private CategoriaDTO convertToCategoriaSimpleDTO(Categoria categoria) {
        if (categoria == null) return null;
        CategoriaDTO dto = new CategoriaDTO();
        dto.setId(categoria.getId());
        dto.setDenominacion(categoria.getDenominacion());
        dto.setBaja(categoria.isBaja());
        return dto;
    }

    private UnidadMedidaDTO convertToUnidadMedidaDTO(UnidadMedida unidadMedida) {
        if (unidadMedida == null) return null;
        UnidadMedidaDTO dto = new UnidadMedidaDTO();
        dto.setId(unidadMedida.getId());
        dto.setDenominacion(unidadMedida.getDenominacion());
        dto.setBaja(unidadMedida.isBaja());
        return dto;
    }

    private ImagenDTO convertToImagenDTO(Imagen imagen) {
        if (imagen == null) return null;
        ImagenDTO dto = new ImagenDTO();
        dto.setId(imagen.getId());
        dto.setDenominacion(imagen.getDenominacion());
        dto.setBaja(imagen.isBaja());
        return dto;
    }
}
