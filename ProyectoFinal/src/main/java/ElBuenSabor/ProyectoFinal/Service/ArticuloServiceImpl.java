package ElBuenSabor.ProyectoFinal.Service;

import ElBuenSabor.ProyectoFinal.DTO.ArticuloInsumoDTO;
import ElBuenSabor.ProyectoFinal.DTO.ArticuloManufacturadoDTO;
import ElBuenSabor.ProyectoFinal.DTO.ArticuloManufacturadoDetalleDTO;
import ElBuenSabor.ProyectoFinal.Entities.*;
import ElBuenSabor.ProyectoFinal.Repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ArticuloServiceImpl extends BaseServiceImpl<Articulo, Long> implements ArticuloService {

    private final ArticuloRepository articuloRepository;
    private final ArticuloInsumoRepository articuloInsumoRepository;
    private final ArticuloManufacturadoRepository articuloManufacturadoRepository;
    private final CategoriaRepository categoriaRepository;
    private final UnidadMedidaRepository unidadMedidaRepository;
    private final ImagenRepository imagenRepository;
    private final ArticuloManufacturadoDetalleRepository articuloManufacturadoDetalleRepository;


    @Autowired
    public ArticuloServiceImpl(ArticuloRepository articuloRepository,
                               ArticuloInsumoRepository articuloInsumoRepository,
                               ArticuloManufacturadoRepository articuloManufacturadoRepository,
                               CategoriaRepository categoriaRepository,
                               UnidadMedidaRepository unidadMedidaRepository,
                               ImagenRepository imagenRepository,
                               ArticuloManufacturadoDetalleRepository articuloManufacturadoDetalleRepository) {
        super(articuloRepository);
        this.articuloRepository = articuloRepository;
        this.articuloInsumoRepository = articuloInsumoRepository;
        this.articuloManufacturadoRepository = articuloManufacturadoRepository;
        this.categoriaRepository = categoriaRepository;
        this.unidadMedidaRepository = unidadMedidaRepository;
        this.imagenRepository = imagenRepository;
        this.articuloManufacturadoDetalleRepository = articuloManufacturadoDetalleRepository;
    }

    // --- Métodos de Borrado Lógico (Correctos) ---
    @Override
    @Transactional
    public void darBajaArticulo(Long id) throws Exception {
        Optional<Articulo> articuloOptional = articuloRepository.findById(id);
        if (!articuloOptional.isPresent()) {
            throw new Exception("Artículo no encontrado con ID: " + id);
        }
        Articulo articulo = articuloOptional.get();
        articulo.setEstaDadoDeBaja(true);
        articuloRepository.save(articulo);
    }

    @Override
    @Transactional
    public void darAltaArticulo(Long id) throws Exception {
        Optional<Articulo> articuloOptional = articuloRepository.findById(id);
        if (!articuloOptional.isPresent()) {
            throw new Exception("Artículo no encontrado con ID: " + id);
        }
        Articulo articulo = articuloOptional.get();
        articulo.setEstaDadoDeBaja(false);
        articuloRepository.save(articulo);
    }

    // --- Sobreescribir findAll y métodos de búsqueda para filtrar por activos ---
    @Override
    @Transactional(readOnly = true)
    public List<Articulo> findAll() throws Exception {
        return findAllActivos();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Articulo> findAllActivos() throws Exception {
        try {
            return articuloRepository.findByEstaDadoDeBajaFalse();
        } catch (Exception e) {
            throw new Exception("Error al obtener todos los artículos activos: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<ArticuloInsumo> findAllArticulosInsumo() throws Exception {
        return findAllArticulosInsumoActivos();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ArticuloInsumo> findAllArticulosInsumoActivos() throws Exception {
        try {
            return articuloInsumoRepository.findByEstaDadoDeBajaFalse();
        } catch (Exception e) {
            throw new Exception("Error al obtener todos los Artículos Insumo activos: " + e.getMessage(), e);
        }
    }


    @Override
    @Transactional(readOnly = true)
    public List<ArticuloManufacturado> findAllArticulosManufacturadosActivos() throws Exception {
        try {
            return articuloManufacturadoRepository.findByEstaDadoDeBajaFalse();
        } catch (Exception e) {
            throw new Exception("Error al obtener todos los Artículos Manufacturados activos: " + e.getMessage(), e);
        }
    }



    @Override
    @Transactional(readOnly = true)
    public List<Articulo> findByDenominacionContainingIgnoreCaseActivos(String denominacion) throws Exception {
        try {
            return articuloRepository.findByDenominacionContainingIgnoreCaseAndEstaDadoDeBajaFalse(denominacion);
        } catch (Exception e) {
            throw new Exception("Error al buscar artículos activos por denominación: " + e.getMessage(), e);
        }
    }

 
    @Override
    @Transactional(readOnly = true)
    public List<Articulo> findByCategoriaIdActivos(Long categoriaId) throws Exception {
        try {
            return articuloRepository.findByCategoriaIdAndEstaDadoDeBajaFalse(categoriaId);
        } catch (Exception e) {
            throw new Exception("Error al buscar artículos activos por ID de categoría: " + e.getMessage(), e);
        }
    }

    // --- Métodos para ArticuloInsumo (se mantienen) ---
    @Override
    @Transactional
    public ArticuloInsumo createArticuloInsumo(ArticuloInsumoDTO dto) throws Exception {
        try {
            ArticuloInsumo insumo = new ArticuloInsumo();
            insumo.setDenominacion(dto.getDenominacion());
            insumo.setPrecioVenta(dto.getPrecioVenta());
            insumo.setPrecioCompra(dto.getPrecioCompra());
            insumo.setStockActual(dto.getStockActual());
            insumo.setStockMinimo(dto.getStockMinimo());
            insumo.setEsParaElaborar(dto.getEsParaElaborar());
            insumo.setEstaDadoDeBaja(false); // Asignar explícitamente el estado al crear

            Categoria categoria = categoriaRepository.findById(dto.getCategoriaId())
                    .orElseThrow(() -> new Exception("Categoría no encontrada con ID: " + dto.getCategoriaId()));
            insumo.setCategoria(categoria);

            UnidadMedida unidadMedida = unidadMedidaRepository.findById(dto.getUnidadMedidaId())
                    .orElseThrow(() -> new Exception("Unidad de Medida no encontrada con ID: " + dto.getUnidadMedidaId()));
            insumo.setUnidadMedida(unidadMedida);

            Imagen imagen = null;
            if (dto.getImagen() != null && dto.getImagen().getDenominacion() != null && !dto.getImagen().getDenominacion().isEmpty()) {
                if (dto.getImagen().getId() != null) {
                    imagen = imagenRepository.findById(dto.getImagen().getId())
                            .orElseThrow(() -> new Exception("Imagen no encontrada con ID: " + dto.getImagen().getId()));
                    imagen.setDenominacion(dto.getImagen().getDenominacion());
                    imagen = imagenRepository.save(imagen);
                } else {
                    imagen = Imagen.builder().denominacion(dto.getImagen().getDenominacion()).build();
                    imagen = imagenRepository.save(imagen);
                }
            }
            insumo.setImagen(imagen);

            return articuloInsumoRepository.save(insumo);
        } catch (Exception e) {
            throw new Exception("Error al crear Artículo Insumo: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    public ArticuloInsumo updateArticuloInsumo(Long id, ArticuloInsumoDTO dto) throws Exception {
        try {
            ArticuloInsumo insumo = articuloInsumoRepository.findById(id)
                    .orElseThrow(() -> new Exception("Artículo Insumo no encontrado con ID: " + id));

            insumo.setDenominacion(dto.getDenominacion());
            insumo.setPrecioVenta(dto.getPrecioVenta());
            insumo.setPrecioCompra(dto.getPrecioCompra());
            insumo.setStockActual(dto.getStockActual());
            insumo.setStockMinimo(dto.getStockMinimo());
            insumo.setEsParaElaborar(dto.getEsParaElaborar());

            if (dto.getCategoriaId() != null) {
                Categoria categoria = categoriaRepository.findById(dto.getCategoriaId())
                        .orElseThrow(() -> new Exception("Categoría no encontrada con ID: " + dto.getCategoriaId()));
                insumo.setCategoria(categoria);
            }

            if (dto.getUnidadMedidaId() != null) {
                UnidadMedida unidadMedida = unidadMedidaRepository.findById(dto.getUnidadMedidaId())
                        .orElseThrow(() -> new Exception("Unidad de Medida no encontrada con ID: " + dto.getUnidadMedidaId()));
                insumo.setUnidadMedida(unidadMedida);
            }

            // Gestión de Imagen (simplificada, solo URL)
            Imagen imagen = null;
            if (dto.getImagen() != null && dto.getImagen().getDenominacion() != null && !dto.getImagen().getDenominacion().isEmpty()) {
                if (insumo.getImagen() != null && insumo.getImagen().getId() != null) {
                    imagen = insumo.getImagen();
                    imagen.setDenominacion(dto.getImagen().getDenominacion());
                    imagen = imagenRepository.save(imagen);
                } else {
                    imagen = Imagen.builder().denominacion(dto.getImagen().getDenominacion()).build();
                    imagen = imagenRepository.save(imagen);
                }
            } else {
                if (insumo.getImagen() != null) {
                    imagenRepository.delete(insumo.getImagen());
                }
                imagen = null;
            }
            insumo.setImagen(imagen);

            return articuloInsumoRepository.save(insumo);
        } catch (Exception e) {
            throw new Exception("Error al actualizar Artículo Insumo: " + e.getMessage(), e);
        }
    }



    @Override
    @Transactional(readOnly = true)
    public List<ArticuloInsumo> findArticulosInsumoByStockActualLessThanEqual(Double stockMinimo) throws Exception {
        try {
            return articuloInsumoRepository.findByStockActualLessThanEqual(stockMinimo);
        } catch (Exception e) {
            throw new Exception("Error al buscar artículos por stock mínimo: " + e.getMessage(), e);
        }
    }

    // --- Métodos para ArticuloManufacturado ---
    @Override
    @Transactional
    public ArticuloManufacturado createArticuloManufacturado(ArticuloManufacturadoDTO dto) throws Exception {
        try {
            if (dto.getDetalles() == null || dto.getDetalles().isEmpty()) {
                throw new Exception("Un artículo manufacturado debe tener al menos un ingrediente.");
            }

            ArticuloManufacturado manufacturado = new ArticuloManufacturado();
            manufacturado.setDenominacion(dto.getDenominacion());
            manufacturado.setPrecioVenta(dto.getPrecioVenta());
            manufacturado.setDescripcion(dto.getDescripcion());
            manufacturado.setTiempoEstimadoMinutos(dto.getTiempoEstimadoMinutos());
            manufacturado.setPreparacion(dto.getPreparacion());
            manufacturado.setEstaDadoDeBaja(false);

            Categoria categoria = categoriaRepository.findById(dto.getCategoriaId())
                    .orElseThrow(() -> new Exception("Categoría no encontrada con ID: " + dto.getCategoriaId()));
            manufacturado.setCategoria(categoria);

            // Gestión de Imagen (directamente desde la URL del DTO)
            Imagen imagen = null;
            if (dto.getImagen() != null && dto.getImagen().getDenominacion() != null && !dto.getImagen().getDenominacion().isEmpty()) {
                if (dto.getImagen().getId() != null) {
                    imagen = imagenRepository.findById(dto.getImagen().getId())
                            .orElseThrow(() -> new Exception("Imagen no encontrada con ID: " + dto.getImagen().getId()));
                    imagen.setDenominacion(dto.getImagen().getDenominacion());
                    imagen = imagenRepository.save(imagen);
                } else {
                    imagen = Imagen.builder().denominacion(dto.getImagen().getDenominacion()).build();
                    imagen = imagenRepository.save(imagen);
                }
            }
            manufacturado.setImagen(imagen);

            Set<ArticuloManufacturadoDetalle> detallesSet = new HashSet<>();
            if (dto.getDetalles() != null && !dto.getDetalles().isEmpty()) {
                for (ArticuloManufacturadoDetalleDTO detalleDTO : dto.getDetalles()) {
                    ArticuloInsumo insumo = articuloInsumoRepository.findById(detalleDTO.getArticuloInsumoId())
                            .orElseThrow(() -> new Exception("Artículo Insumo para detalle no encontrado con ID: " + detalleDTO.getArticuloInsumoId()));

                    ArticuloManufacturadoDetalle detalle = new ArticuloManufacturadoDetalle();
                    detalle.setCantidad(detalleDTO.getCantidad());
                    detalle.setArticuloInsumo(insumo);
                    detalle.setArticuloManufacturado(manufacturado);

                    detallesSet.add(detalle);
                }
            } else {
                throw new Exception("El artículo manufacturado debe tener al menos un ingrediente.");
            }
            manufacturado.setDetalles(detallesSet);

            return articuloManufacturadoRepository.save(manufacturado);
        } catch (Exception e) {
            throw new Exception("Error al crear Artículo Manufacturado: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    public ArticuloManufacturado updateArticuloManufacturado(Long id, ArticuloManufacturadoDTO dto) throws Exception {
        try {
            ArticuloManufacturado manufacturado = articuloManufacturadoRepository.findById(id)
                    .orElseThrow(() -> new Exception("Artículo Manufacturado no encontrado con ID: " + id));

            if (dto.getDetalles() == null || dto.getDetalles().isEmpty()) {
                throw new Exception("Un artículo manufacturado debe tener al menos un ingrediente para ser actualizado.");
            }

            manufacturado.setDenominacion(dto.getDenominacion());
            manufacturado.setPrecioVenta(dto.getPrecioVenta());
            manufacturado.setDescripcion(dto.getDescripcion());
            manufacturado.setTiempoEstimadoMinutos(dto.getTiempoEstimadoMinutos());
            manufacturado.setPreparacion(dto.getPreparacion());

            if (dto.getCategoriaId() != null) {
                Categoria categoria = categoriaRepository.findById(dto.getCategoriaId())
                        .orElseThrow(() -> new Exception("Categoría no encontrada con ID: " + dto.getCategoriaId()));
                manufacturado.setCategoria(categoria);
            }

            // Gestión de Imagen (directamente desde la URL del DTO)
            Imagen imagen = null;
            if (dto.getImagen() != null && dto.getImagen().getDenominacion() != null && !dto.getImagen().getDenominacion().isEmpty()) {
                if (manufacturado.getImagen() != null && manufacturado.getImagen().getId() != null) {
                    imagen = manufacturado.getImagen();
                    imagen.setDenominacion(dto.getImagen().getDenominacion());
                    imagen = imagenRepository.save(imagen);
                } else {
                    imagen = Imagen.builder().denominacion(dto.getImagen().getDenominacion()).build();
                    imagen = imagenRepository.save(imagen);
                }
            } else {
                if (manufacturado.getImagen() != null) {
                    imagenRepository.delete(manufacturado.getImagen());
                }
                imagen = null;
            }
            manufacturado.setImagen(imagen);

            // Manejo de Detalles (Ingredientes): Reemplazo completo de la colección
            if (dto.getDetalles() != null) {
                // CORRECCIÓN: Manipular la colección existente, no reemplazarla
                // Paso 1: Limpiar la colección existente. Hibernate sabrá que debe eliminar los huérfanos.
                if (manufacturado.getDetalles() != null) {
                    // Es seguro borrar aquí porque orphanRemoval=true y cascade=ALL están en la relación
                    manufacturado.getDetalles().clear();
                } else {
                    // Si la colección era null (ej. lazy-loaded y no accedida), inicialízala
                    manufacturado.setDetalles(new HashSet<>());
                }

                // Paso 2: Añadir los nuevos detalles a la colección ahora vacía.
                for (ArticuloManufacturadoDetalleDTO detalleDTO : dto.getDetalles()) {
                    ArticuloInsumo insumo = articuloInsumoRepository.findById(detalleDTO.getArticuloInsumoId())
                            .orElseThrow(() -> new Exception("Artículo Insumo para detalle no encontrado con ID: " + detalleDTO.getArticuloInsumoId()));

                    ArticuloManufacturadoDetalle detalle = new ArticuloManufacturadoDetalle();
                    detalle.setCantidad(detalleDTO.getCantidad());
                    detalle.setArticuloInsumo(insumo);
                    detalle.setArticuloManufacturado(manufacturado); // ¡CRUCIAL: Asigna la relación bidireccional aquí!

                    manufacturado.getDetalles().add(detalle); // Añadir a la colección gestionada
                }
            } else {
                // Si el DTO no trae detalles (o trae null), limpiar la colección existente para eliminar todos los detalles.
                if (manufacturado.getDetalles() != null) {
                    manufacturado.getDetalles().clear();
                }
            }

            return articuloManufacturadoRepository.save(manufacturado);
        } catch (Exception e) {
            throw new Exception("Error al actualizar Artículo Manufacturado: " + e.getMessage() + (e.getCause() != null ? " Causa: " + e.getCause().getMessage() : ""));
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<ArticuloManufacturado> findAllArticulosManufacturados() throws Exception {
        return findAllArticulosManufacturadosActivos();
    }

    // --- Métodos generales de búsqueda ---
    @Override
    @Transactional(readOnly = true)
    public List<Articulo> findByDenominacionContainingIgnoreCase(String denominacion) throws Exception {
        return findByDenominacionContainingIgnoreCaseActivos(denominacion);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Articulo> findByCategoriaId(Long categoriaId) throws Exception {
        return findByCategoriaIdActivos(categoriaId);
    }

    // **IMPORTANTE**: Modificar el método delete(ID id) en BaseService o ArticuloService
    // para que haga el borrado lógico en lugar del físico.
    @Override
    @Transactional
    public boolean delete(Long id) throws Exception {
        try {
            Optional<Articulo> articuloOptional = articuloRepository.findById(id);
            if (!articuloOptional.isPresent()) {
                throw new Exception("Artículo no encontrado con ID: " + id + " para eliminar.");
            }
            Articulo articulo = articuloOptional.get();
            articulo.setEstaDadoDeBaja(true);
            articuloRepository.save(articulo);
            return true;
        } catch (Exception e) {
            throw new Exception("Error al realizar borrado lógico del artículo: " + e.getMessage(), e);
        }
    }
}