package ElBuenSabor.ProyectoFinal.Service;

import ElBuenSabor.ProyectoFinal.DTO.PersonaPerfilUpdateDTO;
import ElBuenSabor.ProyectoFinal.DTO.UsuarioDTO;
import ElBuenSabor.ProyectoFinal.Entities.Persona;
import ElBuenSabor.ProyectoFinal.Entities.Usuario;

public interface UsuarioService extends BaseService<Usuario, Long> {

    Usuario findByEmail(String email);

    Usuario save(Usuario usuario);

    // ✅ Ahora devuelve la Persona actualizada (Cliente, Cocinero, Cajero o Delivery)
    Persona actualizarPerfil(String email, PersonaPerfilUpdateDTO dto) throws Exception;

    Usuario registrarCocinero(UsuarioDTO usuarioDTO);

    Usuario registrarCajero(UsuarioDTO usuarioDTO);

    // ✅ Registrar Delivery con primerInicio = true
    Usuario registrarDelivery(UsuarioDTO usuarioDTO);

    void actualizarNombre(Long id, String nuevoNombre);
}
