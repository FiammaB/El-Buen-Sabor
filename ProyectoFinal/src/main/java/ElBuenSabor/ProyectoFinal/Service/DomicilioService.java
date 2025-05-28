package ElBuenSabor.ProyectoFinal.Service;

import ElBuenSabor.ProyectoFinal.DTO.DomicilioCreateUpdateDTO;
import ElBuenSabor.ProyectoFinal.DTO.DomicilioDTO;
import ElBuenSabor.ProyectoFinal.Entities.Domicilio; // Para Optional
import java.util.List;
import java.util.Optional;

public interface DomicilioService extends BaseService<Domicilio, Long> {
    // Para creación/actualización independiente (ej. por un admin)
    DomicilioDTO createDomicilio(DomicilioCreateUpdateDTO dto) throws Exception;
    DomicilioDTO updateDomicilio(Long id, DomicilioCreateUpdateDTO dto) throws Exception;

    DomicilioDTO findDomicilioById(Long id) throws Exception;
    List<DomicilioDTO> findAllDomicilios() throws Exception;
    List<DomicilioDTO> findByLocalidadId(Long localidadId) throws Exception; // Activos de una localidad

    // Heredados de BaseService y a implementar en DomicilioServiceImpl para devolver DTOs
    List<DomicilioDTO> findAllDomiciliosIncludingDeleted() throws Exception;
    DomicilioDTO findDomicilioByIdIncludingDeleted(Long id) throws Exception;

    // Método para verificar si un domicilio está en uso activo
    boolean isDomicilioInActiveUse(Long domicilioId) throws Exception;
}
