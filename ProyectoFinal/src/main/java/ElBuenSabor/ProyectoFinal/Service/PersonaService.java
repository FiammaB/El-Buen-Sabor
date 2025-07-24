package ElBuenSabor.ProyectoFinal.Service;

import ElBuenSabor.ProyectoFinal.Entities.Persona;
import ElBuenSabor.ProyectoFinal.Entities.Usuario;
import ElBuenSabor.ProyectoFinal.DTO.ClienteAdminUpdateDTO;

import java.util.Optional;

public interface PersonaService extends BaseService<Persona, Long> {

    void actualizarClienteDesdeAdmin(Long personaId, ClienteAdminUpdateDTO dto) throws Exception;

    // ✅ Nuevo método: buscar Persona asociada a un Usuario
    Optional<Persona> findByUsuario(Usuario usuario);
}
