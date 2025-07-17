// Archivo: ElBuenSabor/ProyectoFinal/src/main/java/ElBuenSabor/ProyectoFinal/Service/PromocionServiceImpl.java
package ElBuenSabor.ProyectoFinal.Service;

import ElBuenSabor.ProyectoFinal.Entities.Promocion;
import ElBuenSabor.ProyectoFinal.Repositories.PromocionRepository;
import ElBuenSabor.ProyectoFinal.Repositories.SucursalRepository;
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
        System.out.println("--- INICIO PromocionServiceImpl.save ---");
        System.out.println("Entidad newEntity recibida en save: " + newEntity.getId());
        System.out.println("  - Artículos Manufacturados en newEntity (antes de persistir): " + newEntity.getArticulosManufacturados().size());
        System.out.println("  - Artículos Insumos en newEntity (antes de persistir): " + newEntity.getArticulosInsumos().size());

        if (newEntity.getSucursales() == null || newEntity.getSucursales().isEmpty()) {
            throw new Exception("La Promoción debe estar asociada al menos a una sucursal.");
        }
        Promocion saved = super.save(newEntity);
        // Forzar la carga de las colecciones Lazy usando Hibernate.initialize
        Hibernate.initialize(saved.getArticulosManufacturados());
        Hibernate.initialize(saved.getArticulosInsumos());
        Hibernate.initialize(saved.getSucursales());

        System.out.println("Entidad 'saved' DESPUÉS de baseRepository.save y Hibernate.initialize:");
        System.out.println("  - Artículos Manufacturados en 'saved': " + saved.getArticulosManufacturados().size() + " items. Contenido: " + saved.getArticulosManufacturados().stream().map(a -> a.getDenominacion() + "(ID:" + a.getId() + ")").collect(Collectors.joining(", ")));
        System.out.println("  - Artículos Insumos en 'saved': " + saved.getArticulosInsumos().size() + " items. Contenido: " + saved.getArticulosInsumos().stream().map(i -> i.getDenominacion() + "(ID:" + i.getId() + ")").collect(Collectors.joining(", ")));
        System.out.println("--- FIN PromocionServiceImpl.save ---");
        return saved;
    }

    @Override
    @Transactional
    public Promocion update(Long id, Promocion updatedPromocion) throws Exception {
        System.out.println("--- INICIO PromocionServiceImpl.update ---");
        System.out.println("Entidad updatedPromocion recibida del controlador (ID: " + updatedPromocion.getId() + "):");
        System.out.println("  - Artículos Manufacturados en updatedPromocion: " + updatedPromocion.getArticulosManufacturados().size() + " items. Contenido: " + updatedPromocion.getArticulosManufacturados().stream().map(a -> a.getDenominacion() + "(ID:" + a.getId() + ")").collect(Collectors.joining(", ")));
        System.out.println("  - Artículos Insumos en updatedPromocion: " + updatedPromocion.getArticulosInsumos().size() + " items. Contenido: " + updatedPromocion.getArticulosInsumos().stream().map(i -> i.getDenominacion() + "(ID:" + i.getId() + ")").collect(Collectors.joining(", ")));

        try {
            Promocion actual = findById(id);

            // <-- ¡NUEVO! Forzar la inicialización de las colecciones de 'actual' antes de manipularlas
            Hibernate.initialize(actual.getArticulosManufacturados());
            Hibernate.initialize(actual.getArticulosInsumos());
            Hibernate.initialize(actual.getSucursales());

            System.out.println("Entidad 'actual' obtenida del repo para update (ID: " + actual.getId() + "):");
            System.out.println("  - Artículos Manufacturados en 'actual' (DESPUÉS de fetch y initialize): " + actual.getArticulosManufacturados().size() + " items. Contenido: " + actual.getArticulosManufacturados().stream().map(a -> a.getDenominacion() + "(ID:" + a.getId() + ")").collect(Collectors.joining(", ")));
            System.out.println("  - Artículos Insumos en 'actual' (DESPUÉS de fetch y initialize): " + actual.getArticulosInsumos().size() + " items. Contenido: " + actual.getArticulosInsumos().stream().map(i -> i.getDenominacion() + "(ID:" + i.getId() + ")").collect(Collectors.joining(", ")));


            actual.setDenominacion(updatedPromocion.getDenominacion());
            actual.setFechaDesde(updatedPromocion.getFechaDesde());
            actual.setFechaHasta(updatedPromocion.getFechaHasta());
            actual.setHoraDesde(updatedPromocion.getHoraDesde());
            actual.setHoraHasta(updatedPromocion.getHoraHasta());
            actual.setDescripcionDescuento(updatedPromocion.getDescripcionDescuento());
            actual.setPrecioPromocional(updatedPromocion.getPrecioPromocional());
            actual.setTipoPromocion(updatedPromocion.getTipoPromocion());
            actual.setImagen(updatedPromocion.getImagen());

            // Procesamiento de Artículos Manufacturados
            if (updatedPromocion.getArticulosManufacturados() != null) {
                System.out.println("Actualizando Artículos Manufacturados en 'actual'. Source list size: " + updatedPromocion.getArticulosManufacturados().size());
                System.out.println("Source Articulos Manufacturados (IDs): " + updatedPromocion.getArticulosManufacturados().stream().map(a -> a.getId()).collect(Collectors.toList()));
                actual.getArticulosManufacturados().clear();
                System.out.println("actual.ArticulosManufacturados DESPUÉS de clear: " + actual.getArticulosManufacturados().size());
                actual.getArticulosManufacturados().addAll(updatedPromocion.getArticulosManufacturados());
                System.out.println("actual.ArticulosManufacturados DESPUÉS de addAll: " + actual.getArticulosManufacturados().size() + " items. Contenido: " + actual.getArticulosManufacturados().stream().map(a -> a.getDenominacion() + "(ID:" + a.getId() + ")").collect(Collectors.joining(", ")));
            } else {
                actual.getArticulosManufacturados().clear();
                System.out.println("updatedPromocion.getArticulosManufacturados() era null, lista 'actual' vaciada.");
            }

            // Procesamiento de Artículos Insumos
            if (updatedPromocion.getArticulosInsumos() != null) {
                System.out.println("Actualizando Artículos Insumos en 'actual'. Source list size: " + updatedPromocion.getArticulosInsumos().size());
                System.out.println("Source Articulos Insumos (IDs): " + updatedPromocion.getArticulosInsumos().stream().map(i -> i.getId()).collect(Collectors.toList()));
                actual.getArticulosInsumos().clear();
                System.out.println("actual.ArticulosInsumos DESPUÉS de clear: " + actual.getArticulosInsumos().size());
                actual.getArticulosInsumos().addAll(updatedPromocion.getArticulosInsumos());
                System.out.println("actual.ArticulosInsumos DESPUÉS de addAll: " + actual.getArticulosInsumos().size() + " items. Contenido: " + actual.getArticulosInsumos().stream().map(i -> i.getDenominacion() + "(ID:" + i.getId() + ")").collect(Collectors.joining(", ")));
            } else {
                actual.getArticulosInsumos().clear();
                System.out.println("updatedPromocion.getArticulosInsumos() era null, lista 'actual' vaciada.");
            }

            if (updatedPromocion.getSucursales() != null) {
                actual.getSucursales().clear();
                actual.getSucursales().addAll(updatedPromocion.getSucursales());
            } else {
                actual.getSucursales().clear();
            }

            System.out.println("Entidad 'actual' ANTES de baseRepository.save (final check):");
            System.out.println("  - Artículos Manufacturados: " + actual.getArticulosManufacturados().size() + " items. Contenido: " + actual.getArticulosManufacturados().stream().map(a -> a.getDenominacion() + "(ID:" + a.getId() + ")").collect(Collectors.joining(", ")));
            System.out.println("  - Artículos Insumos: " + actual.getArticulosInsumos().size() + " items. Contenido: " + actual.getArticulosInsumos().stream().map(i -> i.getDenominacion() + "(ID:" + i.getId() + ")").collect(Collectors.joining(", ")));


            Promocion saved = baseRepository.save(actual);
            // No es necesario inicializar aquí de nuevo, ya que se hizo al inicio del método
            // y la entidad 'saved' debería reflejar el estado de 'actual'.
            System.out.println("Entidad 'saved' DESPUÉS de baseRepository.save y Hibernate.initialize (final result):");
            System.out.println("  - Artículos Manufacturados en 'saved': " + saved.getArticulosManufacturados().size() + " items. Contenido: " + saved.getArticulosManufacturados().stream().map(a -> a.getDenominacion() + "(ID:" + a.getId() + ")").collect(Collectors.joining(", ")));
            System.out.println("  - Artículos Insumos en 'saved': " + saved.getArticulosInsumos().size() + " items. Contenido: " + saved.getArticulosInsumos().stream().map(i -> i.getDenominacion() + "(ID:" + i.getId() + ")").collect(Collectors.joining(", ")));
            System.out.println("--- FIN PromocionServiceImpl.update ---");
            return saved;
        } catch (Exception e) {
            System.err.println("Error en PromocionServiceImpl.update: " + e.getMessage());
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