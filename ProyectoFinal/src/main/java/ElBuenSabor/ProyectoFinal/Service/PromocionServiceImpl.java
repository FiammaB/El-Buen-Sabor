// Archivo: ElBuenSabor/ProyectoFinal/src/main/java/ElBuenSabor/ProyectoFinal/Service/PromocionServiceImpl.java
package ElBuenSabor.ProyectoFinal.Service;

import ElBuenSabor.ProyectoFinal.Entities.Promocion;
import ElBuenSabor.ProyectoFinal.Repositories.PromocionRepository;
import ElBuenSabor.ProyectoFinal.Repositories.SucursalRepository; // Importar SucursalRepository
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;

@Service
public class PromocionServiceImpl extends BaseServiceImpl<Promocion, Long> implements PromocionService {//

    private final PromocionRepository promocionRepository;
    private final SucursalRepository sucursalRepository; // Inyectar SucursalRepository

    public PromocionServiceImpl(PromocionRepository promocionRepository,
                                SucursalRepository sucursalRepository) { // Modificar constructor
        super(promocionRepository);
        this.promocionRepository = promocionRepository;
        this.sucursalRepository = sucursalRepository;
    }

    @Override
    public List<Promocion> getPromocionesActivas() {
        return List.of();
    }

    @Transactional(readOnly = true)
    @Override
    public List<Promocion> getPromocionesActivas(Long sucursalId) {
        // Usa el nuevo método del repositorio que filtra por sucursal y estado activo
        return promocionRepository.findActivePromotionsBySucursalId(sucursalId, LocalDate.now());
    }

    @Override
    @Transactional
    public Promocion save(Promocion newEntity) throws Exception {
        // Para Promocion, que tiene ManyToMany con Sucursal, necesitas manejar la lista de sucursales
        if (newEntity.getSucursales() == null || newEntity.getSucursales().isEmpty()) {
            throw new Exception("La Promoción debe estar asociada al menos a una sucursal.");
        }
        // Asegúrate de que las entidades Sucursal estén cargadas completamente si es necesario
        // (Aunque para ManyToMany, JPA suele manejarlo en la persistencia si los IDs son correctos)
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
            actual.setImagen(updatedPromocion.getImagen()); // La imagen se setea en el controlador

            // Sincronizar la colección de ArticulosManufacturados
            if (updatedPromocion.getArticulosManufacturados() != null) {
                actual.getArticulosManufacturados().clear();
                actual.getArticulosManufacturados().addAll(updatedPromocion.getArticulosManufacturados());
            } else {
                actual.getArticulosManufacturados().clear();
            }

            // Sincronizar la colección de Sucursales (ManyToMany)
            if (updatedPromocion.getSucursales() != null) {
                actual.getSucursales().clear();
                actual.getSucursales().addAll(updatedPromocion.getSucursales());
                // Importante: Para relaciones ManyToMany, si quieres la bidireccionalidad
                // y que las sucursales también "sepan" de la promoción, deberías asegurarte
                // de que se añada la promoción a la lista de promociones de cada sucursal.
                // Sin embargo, si la relación es gestionada por Promoción (owning side),
                // esto no es estrictamente necesario para la persistencia, pero es buena práctica
                // para la coherencia del modelo en memoria.
                // updatedPromocion.getSucursales().forEach(sucursal -> sucursal.getPromociones().add(actual));
            } else {
                actual.getSucursales().clear();
            }

            return baseRepository.save(actual);
        } catch (Exception e) {
            throw new Exception("Error al actualizar la promoción: " + e.getMessage());
        }
    }

    @Transactional(readOnly = true)
    @Override
    public List<Promocion> findAllBySucursalId(Long sucursalId) throws Exception {
        try {
            sucursalRepository.findById(sucursalId); // Verificar si la sucursal existe
            return promocionRepository.findBySucursalesId(sucursalId);
        } catch (Exception e) {
            throw new Exception("Error al obtener promociones por sucursal: " + e.getMessage());
        }
    }
}
