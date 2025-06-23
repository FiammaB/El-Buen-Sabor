package ElBuenSabor.ProyectoFinal.Service;

import ElBuenSabor.ProyectoFinal.DTO.EmpresaDTO; // Asumiendo que lo usarás para crear/actualizar
import ElBuenSabor.ProyectoFinal.Entities.Empresa;

public interface EmpresaService extends BaseService<Empresa, Long> {
    // Si necesitas métodos específicos, añádelos aquí.
    // Por ejemplo, para usar DTOs en la creación/actualización:
    Empresa createEmpresa(EmpresaDTO dto) throws Exception;
    Empresa updateEmpresa(Long id, EmpresaDTO dto) throws Exception;
}