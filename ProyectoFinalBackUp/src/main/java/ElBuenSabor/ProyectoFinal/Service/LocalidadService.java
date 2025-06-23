package ElBuenSabor.ProyectoFinal.Service;

import ElBuenSabor.ProyectoFinal.DTO.LocalidadCreateUpdateDTO;
import ElBuenSabor.ProyectoFinal.DTO.LocalidadDTO; // Para respuestas
import ElBuenSabor.ProyectoFinal.Entities.Localidad;
import java.util.List;

public interface LocalidadService extends BaseService<Localidad, Long> {
    LocalidadDTO createLocalidad(LocalidadCreateUpdateDTO dto) throws Exception;
    LocalidadDTO updateLocalidad(Long id, LocalidadCreateUpdateDTO dto) throws Exception;
    LocalidadDTO findLocalidadById(Long id) throws Exception;
    List<LocalidadDTO> findAllLocalidades() throws Exception;
    List<LocalidadDTO> findByProvinciaId(Long provinciaId) throws Exception;
    LocalidadDTO findByNombre(String nombre) throws Exception;
}