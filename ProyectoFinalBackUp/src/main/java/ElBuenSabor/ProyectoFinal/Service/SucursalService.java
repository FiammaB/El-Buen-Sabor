package ElBuenSabor.ProyectoFinal.Service;

import ElBuenSabor.ProyectoFinal.DTO.SucursalCreateUpdateDTO;
import ElBuenSabor.ProyectoFinal.DTO.SucursalDTO; // Para respuestas
import ElBuenSabor.ProyectoFinal.Entities.Sucursal;
import java.util.List;

public interface SucursalService extends BaseService<Sucursal, Long> {
    SucursalDTO createSucursal(SucursalCreateUpdateDTO dto) throws Exception;
    SucursalDTO updateSucursal(Long id, SucursalCreateUpdateDTO dto) throws Exception;
    // Métodos para obtener sucursales con sus detalles (ej. categorías, promociones)
    //List<SucursalDTO> findAllSucursalesConDetalles() throws Exception;
    //SucursalDTO findSucursalByIdConDetalles(Long id) throws Exception;
    //boolean existsById(Long id);
}