// Archivo: ElBuenSabor/ProyectoFinal/src/main/java/ElBuenSabor/ProyectoFinal/Service/PromocionServiceImpl.java
package ElBuenSabor.ProyectoFinal.Service;

import ElBuenSabor.ProyectoFinal.DTO.*;
import ElBuenSabor.ProyectoFinal.Entities.*;
import ElBuenSabor.ProyectoFinal.Exceptions.ResourceNotFoundException;
import ElBuenSabor.ProyectoFinal.Repositories.*;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ElBuenSabor.ProyectoFinal.DTO.PromocionDetalleCreateDTO; // <-- IMPORTANTE
import ElBuenSabor.ProyectoFinal.DTO.PromocionInsumoDetalleCreateDTO;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PromocionServiceImpl extends BaseServiceImpl<Promocion, Long> implements PromocionService {

    private final PromocionRepository promocionRepository;
    private final SucursalRepository sucursalRepository;
    private final ArticuloManufacturadoRepository articuloManufacturadoRepository;
    private final ArticuloInsumoRepository articuloInsumoRepository;
    private final ImagenRepository imagenRepository; // <-- 1. AÑADIR EL CAMPO


    public PromocionServiceImpl(PromocionRepository promocionRepository,
                                SucursalRepository sucursalRepository,
                                ArticuloManufacturadoRepository articuloManufacturadoRepository,
                                ArticuloInsumoRepository articuloInsumoRepository,
                                ImagenRepository imagenRepository) { // <-- 2. AÑADIR EL PARÁMETRO) {
        super(promocionRepository);
        this.promocionRepository = promocionRepository;
        this.sucursalRepository = sucursalRepository;
        this.articuloManufacturadoRepository = articuloManufacturadoRepository;
        this.articuloInsumoRepository = articuloInsumoRepository;
        this.imagenRepository = imagenRepository; // <-- 3. ASIGNARLO

    }

    @Override
    public List<Promocion> getPromocionesActivas() {
        // Implementa la lógica para obtener promociones activas globales si es necesario
        // Por ahora, devuelve una lista vacía o podrías llamar a promocionRepository.findByBajaFalse()
        return promocionRepository.findByBajaFalse(); // O el método correcto en tu repositorio
    }


    // ESTE ES EL NUEVO MÉTODO SAVE QUE RECIBE EL DTO
    // En PromocionServiceImpl.java

    @Override
    @Transactional
    public Promocion save(PromocionCreateDTO dto) throws Exception {
        Promocion nuevaPromocion = Promocion.builder()
                .denominacion(dto.getDenominacion())
                .fechaDesde(dto.getFechaDesde())
                .fechaHasta(dto.getFechaHasta())
                .horaDesde(dto.getHoraDesde())
                .horaHasta(dto.getHoraHasta())
                .descripcionDescuento(dto.getDescripcionDescuento())
                .precioPromocional(dto.getPrecioPromocional())
                .tipoPromocion(dto.getTipoPromocion())
                .build();
        nuevaPromocion.setBaja(false);

        if (dto.getImagenId() != null) {
            nuevaPromocion.setImagen(imagenRepository.findById(dto.getImagenId())
                    .orElseThrow(() -> new ResourceNotFoundException("Imagen no encontrada con ID: " + dto.getImagenId())));
        }

        if (dto.getPromocionInsumoDetalles() != null) {
            // 👇 TIPO CORREGIDO AQUÍ
            for (PromocionInsumoDetalleCreateDTO detalleDto : dto.getPromocionInsumoDetalles()) {
                ArticuloInsumo insumo = articuloInsumoRepository.findById(detalleDto.getArticuloInsumoId())
                        .orElseThrow(() -> new ResourceNotFoundException("Insumo no encontrado: " + detalleDto.getArticuloInsumoId()));
                PromocionInsumoDetalle detalle = PromocionInsumoDetalle.builder()
                        .articuloInsumo(insumo).cantidad(detalleDto.getCantidad()).promocion(nuevaPromocion).build();
                nuevaPromocion.getPromocionInsumoDetalles().add(detalle);
            }
        }

        if (dto.getPromocionDetalles() != null) {
            // 👇 TIPO CORREGIDO AQUÍ
            for (PromocionDetalleCreateDTO detalleDto : dto.getPromocionDetalles()) {
                ArticuloManufacturado articulo = articuloManufacturadoRepository.findById(detalleDto.getArticuloManufacturado().getId())
                        .orElseThrow(() -> new ResourceNotFoundException("Artículo no encontrado: " + detalleDto.getArticuloManufacturado().getId()));
                PromocionDetalle detalle = PromocionDetalle.builder()
                        .articuloManufacturado(articulo).cantidad(detalleDto.getCantidad()).promocion(nuevaPromocion).build();
                nuevaPromocion.getPromocionDetalles().add(detalle);
            }
        }

        return promocionRepository.save(nuevaPromocion);
    }
    // EL MÉTODO SAVE ANTIGUO QUE RECIBÍA LA ENTIDAD YA NO ES NECESARIO DIRECTAMENTE DESDE EL CONTROLLER,
// PERO BASE SERVICE LO USA, ASÍ QUE LO DEJAMOS.
    @Override
    @Transactional
    public Promocion save(Promocion newEntity) throws Exception {
        return super.save(newEntity);
    }

    @Override
    @Transactional
    public Promocion update(Long id, PromocionCreateDTO dto) throws Exception {
        Promocion existingPromocion = promocionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Promoción no encontrada: " + id));

        existingPromocion.setDenominacion(dto.getDenominacion());
        existingPromocion.setFechaDesde(dto.getFechaDesde());
        existingPromocion.setFechaHasta(dto.getFechaHasta());
        existingPromocion.setHoraDesde(dto.getHoraDesde());
        existingPromocion.setHoraHasta(dto.getHoraHasta());
        existingPromocion.setDescripcionDescuento(dto.getDescripcionDescuento());
        existingPromocion.setPrecioPromocional(dto.getPrecioPromocional());
        existingPromocion.setTipoPromocion(dto.getTipoPromocion());

        if (dto.getImagenId() != null) {
            existingPromocion.setImagen(imagenRepository.findById(dto.getImagenId())
                    .orElseThrow(() -> new ResourceNotFoundException("Imagen no encontrada: " + dto.getImagenId())));
        }

        existingPromocion.getPromocionDetalles().clear();
        if (dto.getPromocionDetalles() != null) {
            // 👇 TIPO CORREGIDO AQUÍ
            for (PromocionDetalleCreateDTO detalleDto : dto.getPromocionDetalles()) {
                ArticuloManufacturado articulo = articuloManufacturadoRepository.findById(detalleDto.getArticuloManufacturado().getId())
                        .orElseThrow(() -> new ResourceNotFoundException("Artículo no encontrado: " + detalleDto.getArticuloManufacturado().getId()));
                existingPromocion.getPromocionDetalles().add(PromocionDetalle.builder()
                        .articuloManufacturado(articulo).cantidad(detalleDto.getCantidad()).promocion(existingPromocion).build());
            }
        }

        existingPromocion.getPromocionInsumoDetalles().clear();
        if (dto.getPromocionInsumoDetalles() != null) {
            // 👇 TIPO CORREGIDO AQUÍ
            for (PromocionInsumoDetalleCreateDTO detalleDto : dto.getPromocionInsumoDetalles()) {
                ArticuloInsumo insumo = articuloInsumoRepository.findById(detalleDto.getArticuloInsumoId())
                        .orElseThrow(() -> new ResourceNotFoundException("Insumo no encontrado: " + detalleDto.getArticuloInsumoId()));
                existingPromocion.getPromocionInsumoDetalles().add(PromocionInsumoDetalle.builder()
                        .articuloInsumo(insumo).cantidad(detalleDto.getCantidad()).promocion(existingPromocion).build());
            }
        }

        return promocionRepository.save(existingPromocion);
    }

    /*
    @Override
    @Transactional
    public Promocion update(Long id, Promocion existingPromocion) throws Exception {
        // Solo los campos simples si querés
        // O incluso directamente: return baseRepository.save(existingPromocion);
        return baseRepository.save(existingPromocion);
    }
*/

    // <-- NUEVA IMPLEMENTACIÓN: toggleBaja
    @Override
    @Transactional
    public Promocion toggleBaja(Long id, boolean baja) throws Exception {
        try {
            Promocion promocion = findById(id); // Obtener la promoción por ID
            promocion.setBaja(baja); // Establecer el nuevo estado de baja
            return baseRepository.save(promocion); // Guardar la promoción actualizada
        } catch (Exception e) {
            throw new Exception("Error al cambiar el estado de baja de la promoción: " + e.getMessage(), e);
        }
    }
}