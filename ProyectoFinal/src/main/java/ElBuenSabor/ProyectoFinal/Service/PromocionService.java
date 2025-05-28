package ElBuenSabor.ProyectoFinal.Service;

import ElBuenSabor.ProyectoFinal.DTO.PromocionCreateUpdateDTO;
import ElBuenSabor.ProyectoFinal.DTO.PromocionDTO;
import ElBuenSabor.ProyectoFinal.Entities.Promocion; // Para Optional
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public interface PromocionService extends BaseService<Promocion, Long> {
    PromocionDTO createPromocion(PromocionCreateUpdateDTO dto) throws Exception;
    PromocionDTO updatePromocion(Long id, PromocionCreateUpdateDTO dto) throws Exception;

    PromocionDTO findPromocionById(Long id) throws Exception;
    List<PromocionDTO> findAllPromociones() throws Exception;
    List<PromocionDTO> findActivePromocionesForDisplay(LocalDate fechaActual, LocalTime horaActual, Long sucursalId) throws Exception; // Para frontend
    List<PromocionDTO> findPromocionesBySucursalId(Long sucursalId, boolean soloActivas) throws Exception;

    Optional<Promocion> findByDenominacionRaw(String denominacion) throws Exception;

    // Heredados de BaseService y a implementar para devolver DTOs
    List<PromocionDTO> findAllPromocionesIncludingDeleted() throws Exception;
    PromocionDTO findPromocionByIdIncludingDeleted(Long id) throws Exception;

    boolean isPromocionInActiveUse(Long promocionId) throws Exception; // ¿Se usa en pedidos? Generalmente no directo.
}
