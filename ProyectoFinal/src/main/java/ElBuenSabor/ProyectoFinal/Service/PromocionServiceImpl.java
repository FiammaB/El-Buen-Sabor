// Archivo: ElBuenSabor/ProyectoFinal/src/main/java/ElBuenSabor/ProyectoFinal/Service/PromocionServiceImpl.java
package ElBuenSabor.ProyectoFinal.Service;

import ElBuenSabor.ProyectoFinal.Entities.ArticuloInsumo;
import ElBuenSabor.ProyectoFinal.Entities.ArticuloManufacturado;
import ElBuenSabor.ProyectoFinal.Entities.Promocion;
import ElBuenSabor.ProyectoFinal.Entities.Sucursal;
import ElBuenSabor.ProyectoFinal.Repositories.ArticuloInsumoRepository;
import ElBuenSabor.ProyectoFinal.Repositories.ArticuloManufacturadoRepository;
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
    private final ArticuloManufacturadoRepository articuloManufacturadoRepository;
    private final ArticuloInsumoRepository articuloInsumoRepository;

    public PromocionServiceImpl(PromocionRepository promocionRepository,
                                SucursalRepository sucursalRepository,
                                ArticuloManufacturadoRepository articuloManufacturadoRepository,
                                ArticuloInsumoRepository articuloInsumoRepository) {
        super(promocionRepository);
        this.promocionRepository = promocionRepository;
        this.sucursalRepository = sucursalRepository;
        this.articuloManufacturadoRepository = articuloManufacturadoRepository;
        this.articuloInsumoRepository = articuloInsumoRepository;
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
    public Promocion update(Long id, Promocion existingPromocion) throws Exception {
        // Solo los campos simples si querés
        // O incluso directamente: return baseRepository.save(existingPromocion);
        return baseRepository.save(existingPromocion);
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