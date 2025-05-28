package ElBuenSabor.ProyectoFinal.Service;

import ElBuenSabor.ProyectoFinal.DTO.*;
import ElBuenSabor.ProyectoFinal.Entities.*;
import ElBuenSabor.ProyectoFinal.Repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ArticuloServiceImpl extends BaseServiceImpl<Articulo, Long> implements ArticuloService {

    private final ArticuloRepository articuloRepository;
    private final ArticuloInsumoRepository articuloInsumoRepository;
    private final ArticuloManufacturadoRepository articuloManufacturadoRepository; // Inyectado
    private final CategoriaRepository categoriaRepository;
    private final UnidadMedidaRepository unidadMedidaRepository;
    private final ImagenRepository imagenRepository;
    private final ArticuloManufacturadoDetalleRepository articuloManufacturadoDetalleRepository;
    private final DetallePedidoRepository detallePedidoRepository;
    private final PromocionRepository promocionRepository;


    @Autowired
    public ArticuloServiceImpl(ArticuloRepository articuloRepository,
                               ArticuloInsumoRepository articuloInsumoRepository,
                               ArticuloManufacturadoRepository articuloManufacturadoRepository,
                               CategoriaRepository categoriaRepository,
                               UnidadMedidaRepository unidadMedidaRepository,
                               ImagenRepository imagenRepository,
                               ArticuloManufacturadoDetalleRepository articuloManufacturadoDetalleRepository,
                               DetallePedidoRepository detallePedidoRepository,
                               PromocionRepository promocionRepository) {
        super(articuloRepository);
        this.articuloRepository = articuloRepository;
        this.articuloInsumoRepository = articuloInsumoRepository;
        this.articuloManufacturadoRepository = articuloManufacturadoRepository;
        this.categoriaRepository = categoriaRepository;
        this.unidadMedidaRepository = unidadMedidaRepository;
        this.imagenRepository = imagenRepository;
        this.articuloManufacturadoDetalleRepository = articuloManufacturadoDetalleRepository;
        this.detallePedidoRepository = detallePedidoRepository;
        this.promocionRepository = promocionRepository;
    }

    // ... (mapDtoToArticuloBase y todos los métodos CRUD para ArticuloInsumo y ArticuloManufacturado) ...
    // (createArticuloInsumo, updateArticuloInsumo, etc.)
    // (createArticuloManufacturado, updateArticuloManufacturado, etc.)
    // (calcularMaximoProducible, etc.)
    // (findArticulosByDenominacion, findArticulosByCategoriaId)
    // (findAllIncludingDeleted (entidad), findByIdIncludingDeleted (entidad))
    // (findAllArticulosRawDTOs, findArticuloByIdRawDTO)
    // (todos los helpers de conversión DTO)

    @Override
    @Transactional
    public ArticuloInsumoDTO createArticuloInsumo(ArticuloInsumoDTO dto) throws Exception {
        Optional<Articulo> existenteDenominacion = articuloRepository.findByDenominacionContainingIgnoreCaseRaw(dto.getDenominacion().trim()).stream()
                .filter(a -> a.getDenominacion().equalsIgnoreCase(dto.getDenominacion().trim()))
                .findFirst();
        if (existenteDenominacion.isPresent()) {
            throw new Exception("Ya existe un artículo con la denominación exacta: " + dto.getDenominacion().trim());
        }
        ArticuloInsumo insumo = new ArticuloInsumo();
        mapDtoToArticuloBase(dto, insumo, dto.getCategoriaId(), dto.getUnidadMedidaId(), dto.getImagenId(), dto.getImagen() != null ? dto.getImagen().getDenominacion() : null);
        insumo.setPrecioCompra(dto.getPrecioCompra());
        insumo.setStockActual(dto.getStockActual());
        insumo.setStockMinimo(dto.getStockMinimo());
        insumo.setEsParaElaborar(dto.getEsParaElaborar());
        insumo.setBaja(false);
        return convertToArticuloInsumoDTO(articuloInsumoRepository.save(insumo));
    }

    private void mapDtoToArticuloBase(ArticuloBaseDTO dto, Articulo articulo, Long categoriaId, Long unidadMedidaId, Long imagenId, String imagenDenominacion) throws Exception {
        if (dto.getDenominacion() == null || dto.getDenominacion().trim().isEmpty()) {
            throw new Exception("La denominación del artículo no puede estar vacía.");
        }
        if (dto.getPrecioVenta() == null || dto.getPrecioVenta() < 0) {
            throw new Exception("El precio de venta del artículo no puede ser nulo o negativo.");
        }
        if (categoriaId == null) {
            throw new Exception("El ID de la categoría es obligatorio para el artículo.");
        }

        articulo.setDenominacion(dto.getDenominacion().trim());
        articulo.setPrecioVenta(dto.getPrecioVenta());

        Categoria categoria = categoriaRepository.findById(categoriaId)
                .orElseThrow(() -> new Exception("Categoría activa no encontrada con ID: " + categoriaId));
        if (categoria.isBaja()) {
            throw new Exception("La categoría '" + categoria.getDenominacion() + "' está dada de baja y no se puede asignar al artículo.");
        }
        articulo.setCategoria(categoria);

        if (unidadMedidaId != null) {
            UnidadMedida unidadMedida = unidadMedidaRepository.findById(unidadMedidaId)
                    .orElseThrow(() -> new Exception("Unidad de Medida activa no encontrada con ID: " + unidadMedidaId));
            if (unidadMedida.isBaja()) {
                throw new Exception("La unidad de medida '" + unidadMedida.getDenominacion() + "' está dada de baja y no se puede asignar al artículo.");
            }
            articulo.setUnidadMedida(unidadMedida);
        } else {
            // Si es un ArticuloInsumo, la unidad de medida es obligatoria.
            // Para ArticuloManufacturado, puede tener una por defecto si no se especifica.
            if (articulo instanceof ArticuloInsumo) {
                throw new Exception("El ID de la unidad de medida es obligatorio para el artículo insumo.");
            } else if (articulo instanceof ArticuloManufacturado) {
                // Asignar "Unidad" por defecto para manufacturados si no se provee unidadMedidaId
                UnidadMedida unidadPorDefecto = unidadMedidaRepository.findByDenominacion("Unidad");
                if (unidadPorDefecto == null) {
                    // Crear "Unidad" si no existe en la base de datos
                    unidadPorDefecto = unidadMedidaRepository.save(UnidadMedida.builder().denominacion("Unidad").build());
                } else if (unidadPorDefecto.isBaja()){
                    throw new Exception("La unidad de medida por defecto 'Unidad' está dada de baja y no puede ser usada.");
                }
                articulo.setUnidadMedida(unidadPorDefecto);
            } else {
                // Tipo de artículo desconocido o no manejado para la unidad de medida por defecto
                throw new Exception("No se pudo determinar la unidad de medida para el tipo de artículo.");
            }
        }

        Imagen imagenParaAsignar = null;
        if (imagenId != null) {
            if (imagenId == 0L) { // Convención para indicar que no se quiere/se quita la imagen
                imagenParaAsignar = null;
            } else {
                imagenParaAsignar = imagenRepository.findById(imagenId)
                        .orElseThrow(() -> new Exception("Imagen activa no encontrada con ID: " + imagenId));
                if (imagenParaAsignar.isBaja()) {
                    throw new Exception("La imagen seleccionada con ID: " + imagenId + " está dada de baja.");
                }
            }
        } else if (imagenDenominacion != null && !imagenDenominacion.trim().isEmpty()) {
            // Crear nueva imagen si se proporciona la denominación (URL/path) y no hay ID
            // Opcional: buscar si ya existe una imagen con esa denominación para reutilizarla
            // Optional<Imagen> imgExistenteDenom = imagenRepository.findByDenominacionRaw(imagenDenominacion.trim());
            // if(imgExistenteDenom.isPresent()){
            //     imagenParaAsignar = imgExistenteDenom.get();
            //     if(imagenParaAsignar.isBaja()) throw new Exception("Se encontró una imagen con la misma denominación pero está dada de baja.");
            // } else {
            //     Imagen nuevaImagen = Imagen.builder().denominacion(imagenDenominacion.trim()).baja(false).build();
            //     imagenParaAsignar = imagenRepository.save(nuevaImagen);
            // }
            Imagen nuevaImagen = Imagen.builder().denominacion(imagenDenominacion.trim()).build();
            imagenParaAsignar = imagenRepository.save(nuevaImagen);

        }
        articulo.setImagen(imagenParaAsignar);

        // El campo 'baja' se maneja en los métodos create/update específicos,
        // usualmente inicializándose a false en la creación y
        // permitiendo su modificación en la actualización a través del DTO si corresponde.
        // articulo.setBaja(dto.isBaja()); // Esto se haría en el método de update del servicio concreto.
    }

    @Override
    @Transactional
    public ArticuloInsumoDTO updateArticuloInsumo(Long id, ArticuloInsumoDTO dto) throws Exception {
        ArticuloInsumo insumo = articuloInsumoRepository.findByIdRaw(id)
                .orElseThrow(() -> new Exception("Artículo Insumo no encontrado con ID: " + id + " para actualizar."));
        Optional<Articulo> existenteDenominacion = articuloRepository.findByDenominacionContainingIgnoreCaseRaw(dto.getDenominacion().trim()).stream()
                .filter(a -> a.getDenominacion().equalsIgnoreCase(dto.getDenominacion().trim()) && !a.getId().equals(id))
                .findFirst();
        if (existenteDenominacion.isPresent()) {
            throw new Exception("Ya existe otro artículo con la denominación exacta: " + dto.getDenominacion().trim());
        }
        mapDtoToArticuloBase(dto, insumo, dto.getCategoriaId(), dto.getUnidadMedidaId(), dto.getImagenId(), dto.getImagen() != null ? dto.getImagen().getDenominacion() : null);
        insumo.setPrecioCompra(dto.getPrecioCompra());
        insumo.setStockActual(dto.getStockActual());
        insumo.setStockMinimo(dto.getStockMinimo());
        insumo.setEsParaElaborar(dto.getEsParaElaborar());
        insumo.setBaja(dto.isBaja());
        return convertToArticuloInsumoDTO(articuloInsumoRepository.save(insumo));
    }

    @Override
    @Transactional(readOnly = true)
    public ArticuloInsumoDTO findArticuloInsumoById(Long id) throws Exception {
        return convertToArticuloInsumoDTO(articuloInsumoRepository.findById(id).orElse(null));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ArticuloInsumoDTO> findAllArticulosInsumo() throws Exception {
        return articuloInsumoRepository.findAll().stream().map(this::convertToArticuloInsumoDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ArticuloInsumoDTO> findArticulosInsumoByStockBajo() throws Exception {
        return articuloInsumoRepository.findByStockActualLessThanEqualStockMinimoAndBajaFalse()
                .stream().map(this::convertToArticuloInsumoDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ArticuloInsumoDTO registrarCompraInsumo(Long insumoId, Double cantidadComprada, Double nuevoPrecioCosto) throws Exception {
        if (cantidadComprada == null || cantidadComprada <= 0) {
            throw new Exception("La cantidad comprada debe ser mayor a cero.");
        }
        ArticuloInsumo insumo = articuloInsumoRepository.findByIdRaw(insumoId)
                .orElseThrow(() -> new Exception("Artículo Insumo no encontrado con ID: " + insumoId));
        if (insumo.isBaja()) {
            throw new Exception("No se puede registrar compra para un Artículo Insumo que está dado de baja.");
        }
        insumo.setStockActual(insumo.getStockActual() + cantidadComprada);
        if (nuevoPrecioCosto != null && nuevoPrecioCosto >= 0) {
            insumo.setPrecioCompra(nuevoPrecioCosto);
        }
        return convertToArticuloInsumoDTO(articuloInsumoRepository.save(insumo));
    }

    @Override
    @Transactional(readOnly = true)
    public ArticuloInsumoDTO findArticuloInsumoByIdIncludingDeleted(Long id) throws Exception {
        return convertToArticuloInsumoDTO(articuloInsumoRepository.findByIdRaw(id).orElse(null));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ArticuloInsumoDTO> findAllArticulosInsumoIncludingDeleted() throws Exception {
        return articuloInsumoRepository.findAllRaw().stream().map(this::convertToArticuloInsumoDTO).collect(Collectors.toList());
    }

    // --- Métodos para ArticuloManufacturado ---
    @Override
    @Transactional
    public ArticuloManufacturadoDTO createArticuloManufacturado(ArticuloManufacturadoDTO dto) throws Exception {
        Optional<Articulo> existenteDenominacion = articuloRepository.findByDenominacionContainingIgnoreCaseRaw(dto.getDenominacion().trim()).stream()
                .filter(a -> a.getDenominacion().equalsIgnoreCase(dto.getDenominacion().trim()))
                .findFirst();
        if (existenteDenominacion.isPresent()) {
            throw new Exception("Ya existe un artículo con la denominación exacta: " + dto.getDenominacion().trim());
        }
        if (dto.getDetalles() == null || dto.getDetalles().isEmpty()) {
            throw new Exception("Un artículo manufacturado debe tener al menos un ingrediente (detalle).");
        }

        ArticuloManufacturado manufacturado = new ArticuloManufacturado();
        mapDtoToArticuloBase(dto, manufacturado, dto.getCategoriaId(), dto.getUnidadMedidaId(), dto.getImagenId(), dto.getImagen() != null ? dto.getImagen().getDenominacion() : null);

        manufacturado.setDescripcion(dto.getDescripcion());
        manufacturado.setTiempoEstimadoMinutos(dto.getTiempoEstimadoMinutos());
        manufacturado.setPreparacion(dto.getPreparacion());
        manufacturado.setBaja(false);

        Set<ArticuloManufacturadoDetalle> detallesEntidad = new HashSet<>();
        for (ArticuloManufacturadoDetalleDTO detalleDTO : dto.getDetalles()) {
            if (detalleDTO.getArticuloInsumoId() == null || detalleDTO.getCantidad() == null || detalleDTO.getCantidad() <= 0) {
                throw new Exception("Cada detalle debe tener un ID de insumo y una cantidad positiva.");
            }
            ArticuloInsumo insumoComponente = articuloInsumoRepository.findById(detalleDTO.getArticuloInsumoId())
                    .orElseThrow(() -> new Exception("Ingrediente (ArticuloInsumo) activo no encontrado con ID: " + detalleDTO.getArticuloInsumoId() + " para el detalle."));
            if (insumoComponente.isBaja()){
                throw new Exception("El ingrediente '" + insumoComponente.getDenominacion() + "' está dado de baja y no puede ser usado en una receta.");
            }
            ArticuloManufacturadoDetalle detalle = new ArticuloManufacturadoDetalle();
            detalle.setCantidad(detalleDTO.getCantidad());
            detalle.setArticuloInsumo(insumoComponente);
            detallesEntidad.add(detalle);
        }
        manufacturado.setDetalles(detallesEntidad);

        ArticuloManufacturado savedManufacturado = articuloManufacturadoRepository.save(manufacturado);
        return convertToArticuloManufacturadoDTO(savedManufacturado);
    }

    @Override
    @Transactional
    public ArticuloManufacturadoDTO updateArticuloManufacturado(Long id, ArticuloManufacturadoDTO dto) throws Exception {
        ArticuloManufacturado manufacturado = articuloManufacturadoRepository.findByIdRaw(id)
                .orElseThrow(() -> new Exception("Artículo Manufacturado no encontrado con ID: " + id + " para actualizar."));
        Optional<Articulo> existenteDenominacion = articuloRepository.findByDenominacionContainingIgnoreCaseRaw(dto.getDenominacion().trim()).stream()
                .filter(a -> a.getDenominacion().equalsIgnoreCase(dto.getDenominacion().trim()) && !a.getId().equals(id))
                .findFirst();
        if (existenteDenominacion.isPresent()) {
            throw new Exception("Ya existe otro artículo con la denominación exacta: " + dto.getDenominacion().trim());
        }
        if (dto.getDetalles() == null || dto.getDetalles().isEmpty()) {
            throw new Exception("Un artículo manufacturado debe tener al menos un ingrediente (detalle).");
        }

        mapDtoToArticuloBase(dto, manufacturado, dto.getCategoriaId(), dto.getUnidadMedidaId(), dto.getImagenId(), dto.getImagen() != null ? dto.getImagen().getDenominacion() : null);

        manufacturado.setDescripcion(dto.getDescripcion());
        manufacturado.setTiempoEstimadoMinutos(dto.getTiempoEstimadoMinutos());
        manufacturado.setPreparacion(dto.getPreparacion());
        manufacturado.setBaja(dto.isBaja());

        manufacturado.getDetalles().clear();
        // articuloManufacturadoRepository.saveAndFlush(manufacturado); // Opcional

        Set<ArticuloManufacturadoDetalle> nuevosDetallesEntidad = new HashSet<>();
        for (ArticuloManufacturadoDetalleDTO detalleDTO : dto.getDetalles()) {
            if (detalleDTO.getArticuloInsumoId() == null || detalleDTO.getCantidad() == null || detalleDTO.getCantidad() <= 0) {
                throw new Exception("Cada detalle debe tener un ID de insumo y una cantidad positiva.");
            }
            ArticuloInsumo insumoComponente = articuloInsumoRepository.findById(detalleDTO.getArticuloInsumoId())
                    .orElseThrow(() -> new Exception("Ingrediente (ArticuloInsumo) activo no encontrado con ID: " + detalleDTO.getArticuloInsumoId() + " para el detalle."));
            if (insumoComponente.isBaja()){
                throw new Exception("El ingrediente '" + insumoComponente.getDenominacion() + "' está dado de baja y no puede ser usado en una receta.");
            }
            ArticuloManufacturadoDetalle detalle = new ArticuloManufacturadoDetalle();
            detalle.setCantidad(detalleDTO.getCantidad());
            detalle.setArticuloInsumo(insumoComponente);
            nuevosDetallesEntidad.add(detalle);
        }
        manufacturado.setDetalles(nuevosDetallesEntidad);

        ArticuloManufacturado updatedManufacturado = articuloManufacturadoRepository.save(manufacturado);
        return convertToArticuloManufacturadoDTO(updatedManufacturado);
    }

    @Override
    @Transactional(readOnly = true)
    public ArticuloManufacturadoDTO findArticuloManufacturadoById(Long id) throws Exception {
        return convertToArticuloManufacturadoDTO(articuloManufacturadoRepository.findById(id).orElse(null));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ArticuloManufacturadoDTO> findAllArticulosManufacturados() throws Exception {
        return articuloManufacturadoRepository.findAll().stream()
                .map(this::convertToArticuloManufacturadoDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Integer calcularMaximoProducible(Long manufacturadoId) throws Exception {
        ArticuloManufacturado manufacturado = articuloManufacturadoRepository.findByIdRaw(manufacturadoId)
                .orElseThrow(() -> new Exception("Artículo Manufacturado no encontrado con ID: " + manufacturadoId));
        if (manufacturado.isBaja()){
            throw new Exception("No se puede calcular el máximo producible de un artículo manufacturado dado de baja.");
        }
        if (manufacturado.getDetalles() == null || manufacturado.getDetalles().isEmpty()) {
            return 0;
        }
        int maximoProducible = Integer.MAX_VALUE;
        boolean ingredientesValidos = false;

        for (ArticuloManufacturadoDetalle detalle : manufacturado.getDetalles()) {
            ArticuloInsumo insumo = detalle.getArticuloInsumo();
            if (insumo == null || insumo.isBaja() || detalle.getCantidad() == null || detalle.getCantidad() <= 0) {
                return 0;
            }
            if (insumo.getStockActual() == null || insumo.getStockActual() < 0) {
                return 0;
            }
            if (insumo.getStockActual() < detalle.getCantidad()) {
                return 0;
            }
            int produciblesConEsteInsumo = (int) Math.floor(insumo.getStockActual() / detalle.getCantidad());
            if (produciblesConEsteInsumo < maximoProducible) {
                maximoProducible = produciblesConEsteInsumo;
            }
            ingredientesValidos = true;
        }
        return !ingredientesValidos ? 0 : (maximoProducible == Integer.MAX_VALUE ? 0 : maximoProducible);
    }

    @Override
    @Transactional(readOnly = true)
    public ArticuloManufacturadoDTO findArticuloManufacturadoByIdIncludingDeleted(Long id) throws Exception {
        return convertToArticuloManufacturadoDTO(articuloManufacturadoRepository.findByIdRaw(id).orElse(null));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ArticuloManufacturadoDTO> findAllArticulosManufacturadoIncludingDeleted() throws Exception {
        return articuloManufacturadoRepository.findAllRaw().stream()
                .map(this::convertToArticuloManufacturadoDTO)
                .collect(Collectors.toList());
    }

    // --- Métodos Generales para Articulo ---
    @Override
    @Transactional(readOnly = true)
    public List<Object> findArticulosByDenominacion(String denominacion) throws Exception {
        return articuloRepository.findByDenominacionContainingIgnoreCase(denominacion).stream()
                .map(this::convertToGenericArticuloDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Object> findArticulosByCategoriaId(Long categoriaId) throws Exception {
        if (!categoriaRepository.existsById(categoriaId)) {
            throw new Exception("Categoría activa no encontrada con ID: " + categoriaId);
        }
        return articuloRepository.findByCategoriaId(categoriaId).stream()
                .map(this::convertToGenericArticuloDTO)
                .collect(Collectors.toList());
    }

    // --- Implementación de métodos de BaseService ---
    @Override
    @Transactional(readOnly = true)
    public List<Articulo> findAllIncludingDeleted() throws Exception {
        return articuloRepository.findAllRaw();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Articulo> findByIdIncludingDeleted(Long id) throws Exception {
        return articuloRepository.findByIdRaw(id);
    }

    @Override
    @Transactional
    public Articulo softDelete(Long id) throws Exception {
        Articulo articulo = this.findByIdIncludingDeleted(id)
                .orElseThrow(() -> new Exception("Artículo no encontrado con ID: " + id + " para dar de baja."));
        if (articulo.isBaja()) {
            throw new Exception("El artículo ya está dado de baja.");
        }
        if (isArticuloInActiveUse(id)) {
            String tipo = (articulo instanceof ArticuloInsumo) ? "insumo" : "manufacturado";
            throw new Exception("No se puede dar de baja el artículo " + tipo + " '" + articulo.getDenominacion() + "' porque está en uso activo.");
        }
        articulo.setBaja(true);
        return articuloRepository.save(articulo);
    }

    @Override
    @Transactional
    public Articulo reactivate(Long id) throws Exception {
        Articulo articulo = this.findByIdIncludingDeleted(id)
                .orElseThrow(() -> new Exception("Artículo no encontrado con ID: " + id + " para reactivar."));
        if (!articulo.isBaja()) {
            throw new Exception("El artículo no está dado de baja, no se puede reactivar.");
        }
        if (articulo.getCategoria() == null || articulo.getCategoria().isBaja()) {
            throw new Exception("No se puede reactivar el artículo, su categoría '" + (articulo.getCategoria() != null ? articulo.getCategoria().getDenominacion() : "N/A") + "' no está activa o no existe.");
        }
        if (articulo.getUnidadMedida() == null || articulo.getUnidadMedida().isBaja()) {
            throw new Exception("No se puede reactivar el artículo, su unidad de medida '" + (articulo.getUnidadMedida() != null ? articulo.getUnidadMedida().getDenominacion() : "N/A") + "' no está activa o no existe.");
        }
        if (articulo.getImagen() != null && articulo.getImagen().isBaja()){
            throw new Exception("No se puede reactivar el artículo, su imagen asignada está dada de baja.");
        }
        if (articulo instanceof ArticuloManufacturado) {
            ArticuloManufacturado am = (ArticuloManufacturado) articulo;
            if (am.getDetalles() != null) {
                for (ArticuloManufacturadoDetalle detalle : am.getDetalles()) {
                    if (detalle.getArticuloInsumo() == null || detalle.getArticuloInsumo().isBaja()) {
                        throw new Exception("No se puede reactivar el artículo manufacturado, uno de sus ingredientes (" + (detalle.getArticuloInsumo() != null ? detalle.getArticuloInsumo().getDenominacion() : "ID desconocido") + ") no está activo.");
                    }
                }
            }
        }
        articulo.setBaja(false);
        return articuloRepository.save(articulo);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isArticuloInActiveUse(Long articuloId) throws Exception {
        // Verificar en Detalles de Pedido activos
        if (detallePedidoRepository.existsByArticuloInsumoIdAndPedidoActivo(articuloId) ||
                detallePedidoRepository.existsByArticuloManufacturadoIdAndPedidoActivo(articuloId)) {
            return true;
        }
        // Verificar si está en Promociones activas (solo aplica a manufacturados)
        if (promocionRepository.existsByArticulosManufacturadosIdAndActiva(articuloId)) {
            return true;
        }
        // Verificar si un insumo es parte de un ArticuloManufacturado ACTIVO
        // CORRECCIÓN: Usar el método del ArticuloManufacturadoRepository
        if (articuloManufacturadoRepository.existsActiveWithInsumoId(articuloId)) {
            return true;
        }
        return false;
    }

    // --- Implementación de métodos de ArticuloService que devuelven DTOs genéricos/Object ---
    @Override
    @Transactional(readOnly = true)
    public List<Object> findAllArticulosRawDTOs() throws Exception {
        return this.findAllIncludingDeleted().stream()
                .map(this::convertToGenericArticuloDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Object findArticuloByIdRawDTO(Long id) throws Exception {
        return convertToGenericArticuloDTO(this.findByIdIncludingDeleted(id).orElse(null));
    }

    // --- Helpers de Conversión ---
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

    private Object convertToGenericArticuloDTO(Articulo articulo) {
        if (articulo == null) return null;
        if (articulo instanceof ArticuloInsumo) {
            return convertToArticuloInsumoDTO((ArticuloInsumo) articulo);
        } else if (articulo instanceof ArticuloManufacturado) {
            return convertToArticuloManufacturadoDTO((ArticuloManufacturado) articulo);
        }
        ArticuloSimpleBaseDTO baseDto = new ArticuloSimpleBaseDTO();
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

    private CategoriaDTO convertToCategoriaSimpleDTO(Categoria categoria) {
        if (categoria == null) return null;
        CategoriaDTO dto = new CategoriaDTO();
        dto.setId(categoria.getId());
        dto.setDenominacion(categoria.getDenominacion());
        dto.setBaja(categoria.isBaja());
        if (categoria.getTipoRubro() != null) { // Asegurar que se mapee el tipo de rubro
            dto.setTipoRubro(categoria.getTipoRubro());
        }
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
