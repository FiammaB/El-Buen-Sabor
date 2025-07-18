// Archivo: ElBuenSabor/ProyectoFinal/src/main/java/ElBuenSabor/ProyectoFinal/Service/PromocionServiceImpl.java
package ElBuenSabor.ProyectoFinal.Service;

import ElBuenSabor.ProyectoFinal.DTO.PromocionCreateDTO;
import ElBuenSabor.ProyectoFinal.DTO.PromocionDetalleDTO;
import ElBuenSabor.ProyectoFinal.Entities.*;
import ElBuenSabor.ProyectoFinal.Exceptions.ResourceNotFoundException;
import ElBuenSabor.ProyectoFinal.Repositories.*;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Transactional
    public Promocion save(PromocionCreateDTO dto) throws Exception {
        // 1. Crear la entidad Promocion principal con los datos simples
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

        // 2. Asignar la imagen
        if (dto.getImagenId() != null) {
            nuevaPromocion.setImagen(imagenRepository.findById(dto.getImagenId())
                    .orElseThrow(() -> new ResourceNotFoundException("Imagen no encontrada con ID: " + dto.getImagenId())));
        }

        // 3. Asignar los insumos (sin cantidad)
        if (dto.getArticuloInsumoIds() != null && !dto.getArticuloInsumoIds().isEmpty()) {
            List<ArticuloInsumo> insumos = articuloInsumoRepository.findAllById(dto.getArticuloInsumoIds());
            nuevaPromocion.getArticulosInsumos().addAll(insumos);
        }

        // 4. ¡CAMBIO CLAVE! Guardamos la promoción ANTES de procesar los detalles
        Promocion promocionGuardada = promocionRepository.save(nuevaPromocion);

        // 5. Ahora, con la promoción ya guardada y con un ID, creamos y asignamos los detalles
        if (dto.getPromocionDetalles() != null && !dto.getPromocionDetalles().isEmpty()) {
            for (PromocionDetalleDTO detalleDTO : dto.getPromocionDetalles()) {
                ArticuloManufacturado articulo = articuloManufacturadoRepository.findById(detalleDTO.getArticuloManufacturado().getId())
                        .orElseThrow(() -> new ResourceNotFoundException("Artículo no encontrado con ID: " + detalleDTO.getArticuloManufacturado().getId()));

                PromocionDetalle nuevoDetalle = PromocionDetalle.builder()
                        .promocion(promocionGuardada) // Usamos la promoción que ya tiene ID
                        .articuloManufacturado(articulo)
                        .cantidad(detalleDTO.getCantidad())
                        .build();

                promocionGuardada.getPromocionDetalles().add(nuevoDetalle);
            }
        }

        // 6. Volvemos a guardar la promoción, ahora con sus detalles asociados.
        return promocionRepository.save(promocionGuardada);
    }


    // EL MÉTODO SAVE ANTIGUO QUE RECIBÍA LA ENTIDAD YA NO ES NECESARIO DIRECTAMENTE DESDE EL CONTROLLER,
// PERO BASE SERVICE LO USA, ASÍ QUE LO DEJAMOS.
    @Override
    @Transactional
    public Promocion save(Promocion newEntity) throws Exception {
        return super.save(newEntity);
    }

    @Transactional
    public Promocion update(Long id, PromocionCreateDTO dto) throws Exception {
        // 1. Buscar la promoción existente en la base de datos.
        // Esta entidad 'existingPromocion' está "gestionada" por la transacción.
        Promocion existingPromocion = promocionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Promoción no encontrada con ID: " + id));

        // 2. Actualizar los campos simples
        existingPromocion.setDenominacion(dto.getDenominacion());
        existingPromocion.setFechaDesde(dto.getFechaDesde());
        existingPromocion.setFechaHasta(dto.getFechaHasta());
        existingPromocion.setHoraDesde(dto.getHoraDesde());
        existingPromocion.setHoraHasta(dto.getHoraHasta());
        existingPromocion.setDescripcionDescuento(dto.getDescripcionDescuento());
        existingPromocion.setPrecioPromocional(dto.getPrecioPromocional());
        existingPromocion.setTipoPromocion(dto.getTipoPromocion());

        // 3. Actualizar la imagen (si se proporciona una nueva)
        if (dto.getImagenId() != null) {
            existingPromocion.setImagen(imagenRepository.findById(dto.getImagenId())
                    .orElseThrow(() -> new ResourceNotFoundException("Imagen no encontrada con ID: " + dto.getImagenId())));
        }

        // 4. Sincronizar las listas de artículos (la parte clave)

        // --- LÓGICA PARA ARTÍCULOS MANUFACTURADOS CON CANTIDAD ---
        existingPromocion.getPromocionDetalles().clear(); // Limpiar detalles existentes
        if (dto.getPromocionDetalles() != null && !dto.getPromocionDetalles().isEmpty()) {
            for (PromocionDetalleDTO detalleDTO : dto.getPromocionDetalles()) {
                // Buscar el artículo manufacturado
                ArticuloManufacturado articulo = articuloManufacturadoRepository.findById(detalleDTO.getArticuloManufacturado().getId())
                        .orElseThrow(() -> new ResourceNotFoundException("Artículo no encontrado con ID: " + detalleDTO.getArticuloManufacturado().getId()));

                // Crear la nueva entidad de detalle
                PromocionDetalle nuevoDetalle = PromocionDetalle.builder()
                        .promocion(existingPromocion)
                        .articuloManufacturado(articulo)
                        .cantidad(detalleDTO.getCantidad())
                        .build();

                existingPromocion.getPromocionDetalles().add(nuevoDetalle);
            }
        }
        // --- LÓGICA PARA ARTÍCULOS INSUMO (SIN CANTIDAD) ---
        existingPromocion.getArticulosInsumos().clear(); // Limpiar la lista existente
        if (dto.getArticuloInsumoIds() != null && !dto.getArticuloInsumoIds().isEmpty()) {
            List<ArticuloInsumo> insumos = articuloInsumoRepository.findAllById(dto.getArticuloInsumoIds());
            existingPromocion.getArticulosInsumos().addAll(insumos); // Añadir los nuevos
        }


        // 5. Guardar la entidad. Como está dentro de una transacción,
        // JPA/Hibernate detectará todos los cambios (campos simples y colecciones) y los persistirá.
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