package ElBuenSabor.ProyectoFinal.Service;

import ElBuenSabor.ProyectoFinal.DTO.PersonaPerfilUpdateDTO;
import ElBuenSabor.ProyectoFinal.DTO.UsuarioDTO;
import ElBuenSabor.ProyectoFinal.Entities.Usuario;

public interface UsuarioService extends BaseService<Usuario, Long> {

    Usuario findByEmail(String email);

    Usuario save(Usuario usuario);

    // ✅ Ahora es genérico: sirve para Cliente, Cocinero, Cajero y Delivery
    void actualizarPerfil(String email, PersonaPerfilUpdateDTO dto) throws Exception;

    Usuario registrarCocinero(UsuarioDTO usuarioDTO);

    Usuario registrarCajero(UsuarioDTO usuarioDTO);

    // ✅ Nuevo: Registrar Delivery con primerInicio = true
    Usuario registrarDelivery(UsuarioDTO usuarioDTO);

    void actualizarNombre(Long id, String nuevoNombre);
}
