package ElBuenSabor.ProyectoFinal.Service;

import ElBuenSabor.ProyectoFinal.DTO.SucursalCreateUpdateDTO;
import ElBuenSabor.ProyectoFinal.DTO.SucursalDTO;
import ElBuenSabor.ProyectoFinal.Entities.Sucursal; // Para Optional
import java.util.List;
import java.util.Optional;

public interface SucursalService extends BaseService<Sucursal, Long> {
    SucursalDTO createSucursal(SucursalCreateUpdateDTO dto) throws Exception;
    SucursalDTO updateSucursal(Long id, SucursalCreateUpdateDTO dto) throws Exception;

    SucursalDTO findSucursalByIdDTO(Long id) throws Exception; // Devuelve DTO de activa
    List<SucursalDTO> findAllSucursalesDTO() throws Exception; // Devuelve DTOs de activas
    List<SucursalDTO> findByEmpresaId(Long empresaId, boolean soloActivas) throws Exception;

    Optional<Sucursal> findByNombreAndEmpresaIdRaw(String nombre, Long empresaId) throws Exception;

    // Heredados de BaseService y a implementar para devolver DTOs
    List<SucursalDTO> findAllSucursalesIncludingDeleted() throws Exception;
    SucursalDTO findSucursalByIdIncludingDeletedDTO(Long id) throws Exception;

    boolean isSucursalInActiveUse(Long sucursalId) throws Exception; // Ej. tiene pedidos activos no finalizados
}
