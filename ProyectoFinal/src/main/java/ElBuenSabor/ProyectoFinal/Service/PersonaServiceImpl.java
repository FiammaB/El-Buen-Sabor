package ElBuenSabor.ProyectoFinal.Service;

import ElBuenSabor.ProyectoFinal.Entities.Persona;
import ElBuenSabor.ProyectoFinal.Entities.Usuario;
import ElBuenSabor.ProyectoFinal.Exceptions.ResourceNotFoundException;
import ElBuenSabor.ProyectoFinal.Repositories.PersonaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ElBuenSabor.ProyectoFinal.DTO.ClienteAdminUpdateDTO;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

@Service
public class PersonaServiceImpl extends BaseServiceImpl<Persona, Long> implements PersonaService {

    private final PersonaRepository personaRepository;
    private final UsuarioService usuarioService;

    @Autowired
    public PersonaServiceImpl(PersonaRepository personaRepository, UsuarioService usuarioService) {
        super(personaRepository);
        this.personaRepository = personaRepository;
        this.usuarioService = usuarioService;
    }

    @Override
    @Transactional
    public void actualizarClienteDesdeAdmin(Long personaId, ClienteAdminUpdateDTO dto) throws Exception {
        Persona persona = personaRepository.findById(personaId)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró la persona con id: " + personaId));

        persona.setTelefono(dto.getTelefono());

        Usuario usuario = persona.getUsuario();
        if (usuario != null) {
            boolean nuevaBaja = !dto.isActivo();
            if (usuario.isBaja() != nuevaBaja) {
                usuarioService.toggleBaja(usuario.getId(), nuevaBaja);
            }
        } else {
            throw new ResourceNotFoundException("La persona con ID " + personaId + " no tiene un usuario asociado.");
        }
    }

    @Override
    @Transactional
    public Persona update(Long id, Persona updatedPersona) throws Exception {
        try {
            Persona existente = findById(id);

            existente.setNombre(updatedPersona.getNombre());
            existente.setApellido(updatedPersona.getApellido());
            existente.setTelefono(updatedPersona.getTelefono());
            existente.setFechaNacimiento(updatedPersona.getFechaNacimiento());
            existente.setBaja(updatedPersona.getBaja());
            existente.setImagen(updatedPersona.getImagen());

            if (existente.getUsuario() != null && updatedPersona.getUsuario() != null
                    && updatedPersona.getUsuario().getUsername() != null) {
                existente.getUsuario().setUsername(updatedPersona.getUsuario().getUsername());
            }

            if (!updatedPersona.getDomicilios().isEmpty() && !existente.getDomicilios().isEmpty()) {
                var domicilioNuevo = updatedPersona.getDomicilios().get(0);
                var domicilioExistente = existente.getDomicilios().get(0);

                domicilioExistente.setCalle(domicilioNuevo.getCalle());
                domicilioExistente.setNumero(domicilioNuevo.getNumero());
                domicilioExistente.setCp(domicilioNuevo.getCp());

                if (domicilioNuevo.getLocalidad() != null) {
                    domicilioExistente.setLocalidad(domicilioNuevo.getLocalidad());
                }
            }

            return baseRepository.save(existente);
        } catch (Exception e) {
            throw new Exception("Error al actualizar la persona: " + e.getMessage());
        }
    }

    // ✅ Nuevo método para buscar Persona asociada a un Usuario
    @Override
    public Optional<Persona> findByUsuario(Usuario usuario) {
        return personaRepository.findByUsuario(usuario);
    }
}
