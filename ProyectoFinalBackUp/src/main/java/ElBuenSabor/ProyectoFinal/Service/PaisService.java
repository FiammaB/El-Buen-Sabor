package ElBuenSabor.ProyectoFinal.Service;

import ElBuenSabor.ProyectoFinal.DTO.PaisDTO;
import ElBuenSabor.ProyectoFinal.Entities.Pais;

public interface PaisService extends BaseService<Pais, Long> {
    Pais createPais(PaisDTO dto) throws Exception;
    Pais updatePais(Long id, PaisDTO dto) throws Exception;
    Pais findByNombre(String nombre) throws Exception;
}