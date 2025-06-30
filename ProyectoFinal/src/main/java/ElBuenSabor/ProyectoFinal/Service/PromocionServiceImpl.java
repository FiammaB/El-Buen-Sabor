// Archivo: ElBuenSabor/ProyectoFinal/src/main/java/ElBuenSabor/ProyectoFinal/Service/PromocionServiceImpl.java
package ElBuenSabor.ProyectoFinal.Service;

import ElBuenSabor.ProyectoFinal.Entities.Promocion;
import ElBuenSabor.ProyectoFinal.Repositories.PromocionRepository;
import ElBuenSabor.ProyectoFinal.Repositories.SucursalRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;

@Service
public class PromocionServiceImpl extends BaseServiceImpl<Promocion, Long> implements PromocionService {

    private final PromocionRepository promocionRepository;
    private final SucursalRepository sucursalRepository;

    public PromocionServiceImpl(PromocionRepository promocionRepository,
                                SucursalRepository sucursalRepository) {
        super(promocionRepository);
        this.promocionRepository = promocionRepository;
        this.sucursalRepository = sucursalRepository;
    }

    @Override
    public List<Promocion> getPromocionesActivas() {
        // Implementa la lógica para obtener promociones activas globales si es necesario
        // Por ahora, devuelve una lista vacía o podrías llamar a promocionRepository.findByBajaFalse()
        return promocionRepository.findByBajaFalse(); // O el método correcto en tu repositorio
    }



    @Override
    @Transactional
    public Promocion save(Promocion newEntity) throws Exception {
        if (newEntity.getSucursales() == null || newEntity.getSucursales().isEmpty()) {
            throw new Exception("La Promoción debe estar asociada al menos a una sucursal.");
        }
        return super.save(newEntity);
    }

    @Override
    @Transactional
    public Promocion update(Long id, Promocion updatedPromocion) throws Exception {
        try {
            Promocion actual = findById(id);

            actual.setDenominacion(updatedPromocion.getDenominacion());
            actual.setFechaDesde(updatedPromocion.getFechaDesde());
            actual.setFechaHasta(updatedPromocion.getFechaHasta());
            actual.setHoraDesde(updatedPromocion.getHoraDesde());
            actual.setHoraHasta(updatedPromocion.getHoraHasta());
            actual.setDescripcionDescuento(updatedPromocion.getDescripcionDescuento());
            actual.setPrecioPromocional(updatedPromocion.getPrecioPromocional());
            actual.setTipoPromocion(updatedPromocion.getTipoPromocion());
            actual.setImagen(updatedPromocion.getImagen());

            if (updatedPromocion.getArticulosManufacturados() != null) {
                actual.getArticulosManufacturados().clear();
                actual.getArticulosManufacturados().addAll(updatedPromocion.getArticulosManufacturados());
            } else {
                actual.getArticulosManufacturados().clear();
            }

            if (updatedPromocion.getSucursales() != null) {
                actual.getSucursales().clear();
                actual.getSucursales().addAll(updatedPromocion.getSucursales());
            } else {
                actual.getSucursales().clear();
            }

            return baseRepository.save(actual);
        } catch (Exception e) {
            throw new Exception("Error al actualizar la promoción: " + e.getMessage());
        }
    }



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