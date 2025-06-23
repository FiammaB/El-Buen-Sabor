package ElBuenSabor.ProyectoFinal.Service;

import ElBuenSabor.ProyectoFinal.DTO.UnidadMedidaDTO;
import ElBuenSabor.ProyectoFinal.Entities.UnidadMedida;

public interface UnidadMedidaService extends BaseService<UnidadMedida, Long> {
    UnidadMedida createUnidadMedida(UnidadMedidaDTO dto) throws Exception;
    UnidadMedida updateUnidadMedida(Long id, UnidadMedidaDTO dto) throws Exception;
    UnidadMedida findByDenominacion(String denominacion) throws Exception;
}