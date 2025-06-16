package ElBuenSabor.ProyectoFinal.Service;

import ElBuenSabor.ProyectoFinal.DTO.ArticuloManufacturadoDTO;
import ElBuenSabor.ProyectoFinal.DTO.ImagenDTO;
import ElBuenSabor.ProyectoFinal.DTO.PromocionDTO;
import ElBuenSabor.ProyectoFinal.DTO.SucursalDTO;
import ElBuenSabor.ProyectoFinal.Entities.*;
import ElBuenSabor.ProyectoFinal.Exceptions.ResourceNotFoundException; // Posiblemente ya no sea necesaria
import ElBuenSabor.ProyectoFinal.Repositories.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
// PromocionServiceImpl ahora extiende BaseServiceImpl
// y la interfaz PromocionService (que debe extender BaseService)
public class PromocionServiceImpl extends BaseServiceImpl<Promocion, Long> implements PromocionService {

    public PromocionServiceImpl(PromocionRepository promocionRepository) {
        super(promocionRepository);
    }


    @Override
    @Transactional
    public Promocion update(Long id, Promocion updatedPromocion) throws Exception { // <<-- Añadir throws Exception
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

            }

            // Sincronizar la colección de Sucursales
            if (updatedPromocion.getSucursales() != null) {
                actual.getSucursales().clear();
                actual.getSucursales().addAll(updatedPromocion.getSucursales());
                // Si la relación es bidireccional, asegura que las Sucursales apunten a esta Promocion
                actual.getSucursales().forEach(sucursal -> sucursal.getPromociones().add(actual));
            }


            // Llamamos a save del baseRepository (heredado del padre) para persistir los cambios
            return baseRepository.save(actual);
        } catch (Exception e) {
            // Re-lanzamos cualquier excepción, manteniendo la consistencia con BaseService.
            throw new Exception("Error al actualizar la promoción: " + e.getMessage());
        }
    }
}