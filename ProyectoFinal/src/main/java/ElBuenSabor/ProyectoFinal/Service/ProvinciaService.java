package ElBuenSabor.ProyectoFinal.Service;

import ElBuenSabor.ProyectoFinal.DTO.ProvinciaCreateUpdateDTO;
import ElBuenSabor.ProyectoFinal.DTO.ProvinciaDTO; // Para respuestas
import ElBuenSabor.ProyectoFinal.Entities.Provincia;
import java.util.List;

public interface ProvinciaService extends BaseService<Provincia, Long> {
    ProvinciaDTO createProvincia(ProvinciaCreateUpdateDTO dto) throws Exception;
    ProvinciaDTO updateProvincia(Long id, ProvinciaCreateUpdateDTO dto) throws Exception;
    ProvinciaDTO findProvinciaById(Long id) throws Exception; // Para devolver DTO
    List<ProvinciaDTO> findAllProvincias() throws Exception; // Para devolver DTOs
    List<ProvinciaDTO> findByPaisId(Long paisId) throws Exception;
    ProvinciaDTO findByNombre(String nombre) throws Exception;
}