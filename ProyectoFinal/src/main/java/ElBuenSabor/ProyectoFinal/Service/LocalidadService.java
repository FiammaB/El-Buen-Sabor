package ElBuenSabor.ProyectoFinal.Service;

import ElBuenSabor.ProyectoFinal.DTO.LocalidadCreateUpdateDTO;
import ElBuenSabor.ProyectoFinal.DTO.LocalidadDTO;
import ElBuenSabor.ProyectoFinal.Entities.Localidad; // Para Optional<Localidad>
import ElBuenSabor.ProyectoFinal.Entities.Provincia;
import java.util.List;
import java.util.Optional;

public interface LocalidadService extends BaseService<Localidad, Long> {
    LocalidadDTO createLocalidad(LocalidadCreateUpdateDTO dto) throws Exception;
    LocalidadDTO updateLocalidad(Long id, LocalidadCreateUpdateDTO dto) throws Exception;

    LocalidadDTO findLocalidadById(Long id) throws Exception;
    List<LocalidadDTO> findAllLocalidades() throws Exception;
    List<LocalidadDTO> findByProvinciaId(Long provinciaId) throws Exception;

    LocalidadDTO findByNombre(String nombre) throws Exception;
    Optional<Localidad> findByNombreRaw(String nombre) throws Exception;
    Optional<Localidad> findByNombreAndProvinciaRaw(String nombre, Provincia provincia) throws Exception;


    // Heredados de BaseService y a implementar en LocalidadServiceImpl para devolver DTOs
    List<LocalidadDTO> findAllLocalidadesIncludingDeleted() throws Exception;
    LocalidadDTO findLocalidadByIdIncludingDeleted(Long id) throws Exception;
}