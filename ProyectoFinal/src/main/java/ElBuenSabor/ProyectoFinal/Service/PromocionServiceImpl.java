package ElBuenSabor.ProyectoFinal.Service;

import ElBuenSabor.ProyectoFinal.DTO.*; // Importar todos los DTOs
import ElBuenSabor.ProyectoFinal.Entities.*; // Importar todas las Entidades
import ElBuenSabor.ProyectoFinal.Repositories.*; // Importar todos los Repositorios
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.ArrayList; // Para inicializar listas

@Service
public class PromocionServiceImpl extends BaseServiceImpl<Promocion, Long> implements PromocionService {

    private final PromocionRepository promocionRepository;
    private final ArticuloManufacturadoRepository articuloManufacturadoRepository;
    private final SucursalRepository sucursalRepository;
    private final ImagenRepository imagenRepository;
    private final PedidoRepository pedidoRepository; // Para verificar si una promoción fue usada

    @Autowired
    public PromocionServiceImpl(PromocionRepository promocionRepository,
                                ArticuloManufacturadoRepository articuloManufacturadoRepository,
                                SucursalRepository sucursalRepository,
                                ImagenRepository imagenRepository,
                                PedidoRepository pedidoRepository) {
        super(promocionRepository);
        this.promocionRepository = promocionRepository;
        this.articuloManufacturadoRepository = articuloManufacturadoRepository;
        this.sucursalRepository = sucursalRepository;
        this.imagenRepository = imagenRepository;
        this.pedidoRepository = pedidoRepository;
    }

    @Override
    @Transactional
    public PromocionDTO createPromocion(PromocionCreateUpdateDTO dto) throws Exception {
        // Validar unicidad de denominación (opcional, pero buena práctica)
        Optional<Promocion> existenteDenom = promocionRepository.findByDenominacionRaw(dto.getDenominacion().trim());
        if (existenteDenom.isPresent()) {
            throw new Exception("Ya existe una promoción con la denominación: " + dto.getDenominacion().trim());
        }

        Promocion promocion = new Promocion();
        mapDtoToEntity(dto, promocion); // Método helper para mapear
        promocion.setBaja(false); // Nueva promoción nunca está de baja

        Promocion savedPromocion = promocionRepository.save(promocion);
        // Sincronizar el lado inverso de las relaciones ManyToMany si es necesario
        // (Promocion es dueña de ambas JoinTable, así que guardar Promocion es suficiente para las tablas de unión)
        return convertToDTO(savedPromocion);
    }

    @Override
    @Transactional
    public PromocionDTO updatePromocion(Long id, PromocionCreateUpdateDTO dto) throws Exception {
        Promocion promocion = this.findByIdIncludingDeleted(id)
                .orElseThrow(() -> new Exception("Promoción no encontrada con ID: " + id + " para actualizar."));

        Optional<Promocion> existenteDenom = promocionRepository.findByDenominacionRaw(dto.getDenominacion().trim());
        if (existenteDenom.isPresent() && !existenteDenom.get().getId().equals(id)) {
            throw new Exception("Ya existe otra promoción con la denominacion: " + dto.getDenominacion().trim());
        }

        mapDtoToEntity(dto, promocion);
        // El estado 'baja' se maneja con softDelete/reactivate, o si el DTO lo permite explícitamente
        // if (dto.getBaja() != null) promocion.setBaja(dto.isBaja());

        Promocion updatedPromocion = promocionRepository.save(promocion);
        return convertToDTO(updatedPromocion);
    }

    private void mapDtoToEntity(PromocionCreateUpdateDTO dto, Promocion promocion) throws Exception {
        promocion.setDenominacion(dto.getDenominacion().trim());
        promocion.setFechaDesde(dto.getFechaDesde());
        promocion.setFechaHasta(dto.getFechaHasta());
        promocion.setHoraDesde(dto.getHoraDesde());
        promocion.setHoraHasta(dto.getHoraHasta());
        promocion.setDescripcionDescuento(dto.getDescripcionDescuento());
        promocion.setPrecioPromocional(dto.getPrecioPromocional());
        promocion.setTipoPromocion(dto.getTipoPromocion());

        // Manejar Imagen
        if (dto.getImagenId() != null) {
            if (dto.getImagenId() == 0L) { // Convención para quitar imagen
                promocion.setImagen(null);
            } else {
                Imagen imagen = imagenRepository.findById(dto.getImagenId()) // Solo activas
                        .orElseThrow(() -> new Exception("Imagen activa no encontrada con ID: " + dto.getImagenId()));
                promocion.setImagen(imagen);
            }
        } // else if (dto.getImagenDenominacion() != null ... ) para crear nueva imagen

        // Manejar ArticulosManufacturados
        Set<ArticuloManufacturado> articulos = new HashSet<>();
        if (dto.getArticuloManufacturadoIds() != null && !dto.getArticuloManufacturadoIds().isEmpty()) {
            for (Long amId : dto.getArticuloManufacturadoIds()) {
                ArticuloManufacturado am = articuloManufacturadoRepository.findById(amId) // Solo activos
                        .orElseThrow(() -> new Exception("Artículo Manufacturado activo no encontrado con ID: " + amId));
                articulos.add(am);
            }
        }
        promocion.setArticulosManufacturados(articulos);

        // Manejar Sucursales
        Set<Sucursal> sucursales = new HashSet<>();
        if (dto.getSucursalIds() != null && !dto.getSucursalIds().isEmpty()) {
            for (Long sucId : dto.getSucursalIds()) {
                Sucursal suc = sucursalRepository.findById(sucId) // Solo activas
                        .orElseThrow(() -> new Exception("Sucursal activa no encontrada con ID: " + sucId));
                sucursales.add(suc);
            }
        }
        promocion.setSucursales(sucursales);
    }


    @Override
    @Transactional(readOnly = true)
    public PromocionDTO findPromocionById(Long id) throws Exception {
        return convertToDTO(super.findById(id).orElse(null));
    }

    @Override
    @Transactional(readOnly = true)
    public List<PromocionDTO> findAllPromociones() throws Exception {
        return super.findAll().stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PromocionDTO> findActivePromocionesForDisplay(LocalDate fechaActual, LocalTime horaActual, Long sucursalId) throws Exception {
        Sucursal sucursal = null;
        if (sucursalId != null) {
            sucursal = sucursalRepository.findById(sucursalId) // Solo activas
                    .orElseThrow(() -> new Exception("Sucursal activa no encontrada con ID: " + sucursalId));
        }

        List<Promocion> promociones;
        if (sucursal != null) {
            // Obtener promociones de la sucursal (ya filtradas por baja=false en Promocion)
            promociones = promocionRepository.findBySucursalesIdAndBajaFalse(sucursalId);
        } else {
            // Obtener todas las promociones activas (baja=false)
            promociones = promocionRepository.findAll(); // @Where en Promocion ya filtra
        }

        final Sucursal finalSucursal = sucursal; // Para la lambda
        return promociones.stream()
                .filter(p -> !p.getFechaDesde().isAfter(fechaActual) && !p.getFechaHasta().isBefore(fechaActual)) // Dentro del rango de fechas
                .filter(p -> { // Dentro del rango de horas (si aplica)
                    if (p.getHoraDesde() == null && p.getHoraHasta() == null) return true; // Aplica todo el día
                    if (p.getHoraDesde() != null && p.getHoraHasta() == null) return !horaActual.isBefore(p.getHoraDesde());
                    if (p.getHoraDesde() == null && p.getHoraHasta() != null) return !horaActual.isAfter(p.getHoraHasta());
                    return !horaActual.isBefore(p.getHoraDesde()) && !horaActual.isAfter(p.getHoraHasta());
                })
                .filter(p -> finalSucursal == null || p.getSucursales().contains(finalSucursal)) // Si se especificó sucursal, asegurar que la promo aplique
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PromocionDTO> findPromocionesBySucursalId(Long sucursalId, boolean soloActivas) throws Exception {
        List<Promocion> promociones;
        if (soloActivas) {
            promociones = promocionRepository.findBySucursalesIdAndBajaFalse(sucursalId);
        } else {
            promociones = promocionRepository.findBySucursalIdRaw(sucursalId);
        }
        return promociones.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Promocion> findByDenominacionRaw(String denominacion) throws Exception {
        return promocionRepository.findByDenominacionRaw(denominacion);
    }

    // --- Implementación de métodos de BaseService ---
    @Override
    @Transactional(readOnly = true)
    public List<Promocion> findAllIncludingDeleted() throws Exception {
        return promocionRepository.findAllRaw();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Promocion> findByIdIncludingDeleted(Long id) throws Exception {
        return promocionRepository.findByIdRaw(id);
    }

    @Override
    @Transactional
    public Promocion softDelete(Long id) throws Exception {
        Promocion promocion = this.findByIdIncludingDeleted(id)
                .orElseThrow(() -> new Exception("Promoción no encontrada con ID: " + id + " para dar de baja."));
        if (promocion.isBaja()) {
            throw new Exception("La promoción ya está dada de baja.");
        }
        // Lógica de negocio: ¿Se puede dar de baja una promoción si está "activa" y fue usada en pedidos?
        // Las consignas no lo especifican, pero podría ser una restricción.
        // Por ahora, permitimos la baja.
        if (isPromocionInActiveUse(id)){
            throw new Exception("La promoción no puede ser dada de baja porque está referenciada en pedidos no finalizados.");
        }

        promocion.setBaja(true);
        return promocionRepository.save(promocion);
    }

    @Override
    @Transactional
    public Promocion reactivate(Long id) throws Exception {
        Promocion promocion = this.findByIdIncludingDeleted(id)
                .orElseThrow(() -> new Exception("Promoción no encontrada con ID: " + id + " para reactivar."));
        if (!promocion.isBaja()) {
            throw new Exception("La promoción no está dada de baja, no se puede reactivar.");
        }
        // Validar que los artículos y sucursales asociados estén activos
        for(ArticuloManufacturado am : promocion.getArticulosManufacturados()){
            if(am.isBaja()){
                throw new Exception("No se puede reactivar la promoción, el artículo manufacturado '" + am.getDenominacion() + "' está dado de baja.");
            }
        }
        for(Sucursal suc : promocion.getSucursales()){
            if(suc.isBaja()){
                throw new Exception("No se puede reactivar la promoción, la sucursal '" + suc.getNombre() + "' está dada de baja.");
            }
        }
        if(promocion.getImagen() != null && promocion.getImagen().isBaja()){
            throw new Exception("No se puede reactivar la promoción, su imagen asignada está dada de baja.");
        }

        promocion.setBaja(false);
        return promocionRepository.save(promocion);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isPromocionInActiveUse(Long promocionId) throws Exception {
        // Una promoción se considera "en uso" si está asociada a algún DetallePedido
        // de un pedido que no esté en un estado final (CANCELADO, ENTREGADO, FACTURADO).
        // Esto es complejo porque Promocion no está directamente en DetallePedido.
        // Se aplicaría a través de los artículos que están en la promoción.
        // Por ahora, una verificación simple: si hay pedidos que usaron artículos de esta promoción
        // y no están finalizados. Esto requeriría una query más elaborada.
        // Simplificación: Por ahora, asumimos que una promoción no impide su baja a menos que
        // las consignas lo especifiquen más claramente para pedidos históricos.
        // La restricción más fuerte es sobre los artículos que la componen.
        System.err.println("ADVERTENCIA: isPromocionInActiveUse no está completamente implementado para verificar pedidos.");
        return false;
    }

    // --- Implementación de métodos de PromocionService que devuelven DTO ---
    @Override
    @Transactional(readOnly = true)
    public List<PromocionDTO> findAllPromocionesIncludingDeleted() throws Exception {
        return this.findAllIncludingDeleted().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public PromocionDTO findPromocionByIdIncludingDeleted(Long id) throws Exception {
        return convertToDTO(this.findByIdIncludingDeleted(id).orElse(null));
    }

    private PromocionDTO convertToDTO(Promocion promocion) {
        if (promocion == null) return null;
        PromocionDTO dto = new PromocionDTO();
        dto.setId(promocion.getId());
        dto.setDenominacion(promocion.getDenominacion());
        dto.setFechaDesde(promocion.getFechaDesde());
        dto.setFechaHasta(promocion.getFechaHasta());
        dto.setHoraDesde(promocion.getHoraDesde());
        dto.setHoraHasta(promocion.getHoraHasta());
        dto.setDescripcionDescuento(promocion.getDescripcionDescuento());
        dto.setPrecioPromocional(promocion.getPrecioPromocional());
        dto.setTipoPromocion(promocion.getTipoPromocion());
        dto.setBaja(promocion.isBaja());

        if (promocion.getImagen() != null) {
            dto.setImagen(convertToImagenSimpleDTO(promocion.getImagen()));
        }

        if (promocion.getArticulosManufacturados() != null) {
            dto.setArticulosManufacturados(promocion.getArticulosManufacturados().stream()
                    .filter(am -> !am.isBaja()) // Mostrar solo artículos activos en la promo
                    .map(this::convertToArticuloManufacturadoSimpleDTO)
                    .collect(Collectors.toSet()));
        }

        if (promocion.getSucursales() != null) {
            dto.setSucursales(promocion.getSucursales().stream()
                    .filter(s -> !s.isBaja()) // Mostrar solo sucursales activas
                    .map(this::convertToSucursalSimpleDTO)
                    .collect(Collectors.toSet()));
        }
        return dto;
    }

    // Helpers para DTOs simples de entidades relacionadas
    private ImagenDTO convertToImagenSimpleDTO(Imagen imagen) {
        if (imagen == null) return null;
        ImagenDTO dto = new ImagenDTO();
        dto.setId(imagen.getId());
        dto.setDenominacion(imagen.getDenominacion());
        dto.setBaja(imagen.isBaja());
        return dto;
    }

    private ArticuloManufacturadoSimpleDTO convertToArticuloManufacturadoSimpleDTO(ArticuloManufacturado am) {
        if (am == null) return null;
        ArticuloManufacturadoSimpleDTO dto = new ArticuloManufacturadoSimpleDTO();
        dto.setId(am.getId());
        dto.setDenominacion(am.getDenominacion());
        dto.setPrecioVenta(am.getPrecioVenta());
        dto.setBaja(am.isBaja());
        return dto;
    }

    private SucursalSimpleDTO convertToSucursalSimpleDTO(Sucursal sucursal) {
        if (sucursal == null) return null;
        SucursalSimpleDTO dto = new SucursalSimpleDTO();
        dto.setId(sucursal.getId());
        dto.setNombre(sucursal.getNombre());
        dto.setBaja(sucursal.isBaja());
        // Podrías añadir más campos si es necesario para la visualización de la promoción
        return dto;
    }
}
