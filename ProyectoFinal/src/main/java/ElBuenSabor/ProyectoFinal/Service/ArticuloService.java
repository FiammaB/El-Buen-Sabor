package ElBuenSabor.ProyectoFinal.Service;

import ElBuenSabor.ProyectoFinal.DTO.ArticuloInsumoDTO;
import ElBuenSabor.ProyectoFinal.DTO.ArticuloManufacturadoDTO;
import ElBuenSabor.ProyectoFinal.Entities.Articulo;
import ElBuenSabor.ProyectoFinal.Entities.ArticuloInsumo;
import ElBuenSabor.ProyectoFinal.Entities.ArticuloManufacturado;

import java.util.List;
import java.util.Optional;

public interface ArticuloService extends BaseService<Articulo, Long> {

    // --- Métodos para ArticuloInsumo ---
    ArticuloInsumoDTO createArticuloInsumo(ArticuloInsumoDTO dto) throws Exception;
    ArticuloInsumoDTO updateArticuloInsumo(Long id, ArticuloInsumoDTO dto) throws Exception;
    ArticuloInsumoDTO findArticuloInsumoById(Long id) throws Exception;
    List<ArticuloInsumoDTO> findAllArticulosInsumo() throws Exception;
    List<ArticuloInsumoDTO> findArticulosInsumoByStockBajo() throws Exception;
    ArticuloInsumoDTO registrarCompraInsumo(Long insumoId, Double cantidadComprada, Double nuevoPrecioCosto) throws Exception;
    // Para admin:
    ArticuloInsumoDTO findArticuloInsumoByIdIncludingDeleted(Long id) throws Exception;
    List<ArticuloInsumoDTO> findAllArticulosInsumoIncludingDeleted() throws Exception;


    // --- Métodos para ArticuloManufacturado ---
    ArticuloManufacturadoDTO createArticuloManufacturado(ArticuloManufacturadoDTO dto) throws Exception;
    ArticuloManufacturadoDTO updateArticuloManufacturado(Long id, ArticuloManufacturadoDTO dto) throws Exception;
    ArticuloManufacturadoDTO findArticuloManufacturadoById(Long id) throws Exception;
    List<ArticuloManufacturadoDTO> findAllArticulosManufacturados() throws Exception;
    Integer calcularMaximoProducible(Long manufacturadoId) throws Exception;
    // Para admin:
    ArticuloManufacturadoDTO findArticuloManufacturadoByIdIncludingDeleted(Long id) throws Exception;
    List<ArticuloManufacturadoDTO> findAllArticulosManufacturadoIncludingDeleted() throws Exception;


    // --- Métodos Generales para Articulo (aplican a ambos tipos) ---
    List<Object> findArticulosByDenominacion(String denominacion) throws Exception;
    List<Object> findArticulosByCategoriaId(Long categoriaId) throws Exception;

    // Métodos de BaseService: softDelete, reactivate, findAllIncludingDeleted (entidad), findByIdIncludingDeleted (entidad)

    // Para vistas de admin que devuelven DTOs genéricos (o específicos)
    List<Object> findAllArticulosRawDTOs() throws Exception; // Renombrado desde findAllArticulosIncludingDeleted
    Object findArticuloByIdRawDTO(Long id) throws Exception; // Renombrado desde findArticuloByIdIncludingDeleted

    boolean isArticuloInActiveUse(Long articuloId) throws Exception;
}
