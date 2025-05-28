package ElBuenSabor.ProyectoFinal.Service;

import ElBuenSabor.ProyectoFinal.DTO.EmpresaCreateUpdateDTO;
import ElBuenSabor.ProyectoFinal.DTO.EmpresaDTO;
import ElBuenSabor.ProyectoFinal.Entities.Empresa; // Para Optional<Empresa>
import java.util.List;
import java.util.Optional;

public interface EmpresaService extends BaseService<Empresa, Long> {
    EmpresaDTO createEmpresa(EmpresaCreateUpdateDTO dto) throws Exception;
    EmpresaDTO updateEmpresa(Long id, EmpresaCreateUpdateDTO dto) throws Exception;

    EmpresaDTO findEmpresaById(Long id) throws Exception;
    List<EmpresaDTO> findAllEmpresas() throws Exception;

    Optional<Empresa> findByNombreRaw(String nombre) throws Exception;
    Optional<Empresa> findByCuilRaw(Integer cuil) throws Exception;

    // Heredados de BaseService y a implementar en EmpresaServiceImpl para devolver DTOs
    List<EmpresaDTO> findAllEmpresasIncludingDeleted() throws Exception;
    EmpresaDTO findEmpresaByIdIncludingDeleted(Long id) throws Exception;

    // Método específico para verificar si la empresa tiene sucursales activas
    boolean hasActiveSucursales(Long empresaId) throws Exception;
}
