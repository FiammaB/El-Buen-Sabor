package ElBuenSabor.ProyectoFinal.Service;

import ElBuenSabor.ProyectoFinal.Entities.Persona;
import ElBuenSabor.ProyectoFinal.DTO.ClienteAdminUpdateDTO; // <-- AÑADIR IMPORTACIÓN

public interface PersonaService extends BaseService<Persona, Long>{
    void actualizarClienteDesdeAdmin(Long personaId, ClienteAdminUpdateDTO dto) throws Exception;

}
