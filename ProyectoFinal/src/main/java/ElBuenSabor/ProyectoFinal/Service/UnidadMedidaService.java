package ElBuenSabor.ProyectoFinal.Service;

import ElBuenSabor.ProyectoFinal.DTO.UnidadMedidaDTO;
import ElBuenSabor.ProyectoFinal.Entities.UnidadMedida; // Para Optional<UnidadMedida>
import java.util.List;
import java.util.Optional;

public interface UnidadMedidaService extends BaseService<UnidadMedida, Long> {
    UnidadMedidaDTO createUnidadMedida(UnidadMedidaDTO dto) throws Exception;
    UnidadMedidaDTO updateUnidadMedida(Long id, UnidadMedidaDTO dto) throws Exception;

    UnidadMedidaDTO findUnidadMedidaById(Long id) throws Exception; // Devuelve DTO de activa
    List<UnidadMedidaDTO> findAllUnidadesMedida() throws Exception; // Devuelve DTOs de activas

    UnidadMedidaDTO findByDenominacion(String denominacion) throws Exception; // Devuelve DTO de activa
    Optional<UnidadMedida> findByDenominacionRaw(String denominacion) throws Exception; // Para validación

    // Heredados de BaseService y a implementar en UnidadMedidaServiceImpl para devolver DTOs
    List<UnidadMedidaDTO> findAllUnidadesMedidaIncludingDeleted() throws Exception;
    UnidadMedidaDTO findUnidadMedidaByIdIncludingDeleted(Long id) throws Exception;
}
