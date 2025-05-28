package ElBuenSabor.ProyectoFinal.Service;

import ElBuenSabor.ProyectoFinal.DTO.PaisDTO;
import ElBuenSabor.ProyectoFinal.Entities.Pais;
import java.util.List; // Añadir si falta
import java.util.Optional; // Añadir si falta

public interface PaisService extends BaseService<Pais, Long> {
    PaisDTO createPais(PaisDTO dto) throws Exception; // Cambiado para devolver DTO
    PaisDTO updatePais(Long id, PaisDTO dto) throws Exception; // Cambiado para devolver DTO

    PaisDTO findPaisById(Long id) throws Exception; // Para devolver DTO
    List<PaisDTO> findAllPaises() throws Exception; // Para devolver DTOs

    PaisDTO findByNombre(String nombre) throws Exception; // Devuelve DTO de país activo
    Optional<Pais> findByNombreRaw(String nombre) throws Exception; // Para validación de unicidad

    // Los métodos softDelete y reactivate se heredan de BaseService
    // findAllIncludingDeleted y findByIdIncludingDeleted también se heredan
}