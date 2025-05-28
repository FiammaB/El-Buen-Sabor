package ElBuenSabor.ProyectoFinal.Service;

import ElBuenSabor.ProyectoFinal.DTO.ProvinciaCreateUpdateDTO;
import ElBuenSabor.ProyectoFinal.DTO.ProvinciaDTO;
import ElBuenSabor.ProyectoFinal.Entities.Provincia; // Para el tipo de retorno de Optional
import java.util.List;
import java.util.Optional;

public interface ProvinciaService extends BaseService<Provincia, Long> {
    ProvinciaDTO createProvincia(ProvinciaCreateUpdateDTO dto) throws Exception;
    ProvinciaDTO updateProvincia(Long id, ProvinciaCreateUpdateDTO dto) throws Exception;

    ProvinciaDTO findProvinciaById(Long id) throws Exception; // Devuelve DTO de provincia activa
    List<ProvinciaDTO> findAllProvincias() throws Exception; // Devuelve DTOs de provincias activas
    List<ProvinciaDTO> findByPaisId(Long paisId) throws Exception; // Devuelve DTOs de provincias activas de un país

    ProvinciaDTO findByNombre(String nombre) throws Exception; // Devuelve DTO de provincia activa
    Optional<Provincia> findByNombreRaw(String nombre) throws Exception; // Para validación de unicidad

    // Hereda: softDelete, reactivate, findAllIncludingDeleted, findByIdIncludingDeleted
    // Si se necesitan específicamente para Provincia y devuelven DTO:
    List<ProvinciaDTO> findAllProvinciasIncludingDeleted() throws Exception;
    ProvinciaDTO findProvinciaByIdIncludingDeleted(Long id) throws Exception;
}