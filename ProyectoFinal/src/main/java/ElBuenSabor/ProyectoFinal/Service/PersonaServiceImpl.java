package ElBuenSabor.ProyectoFinal.Service;

import ElBuenSabor.ProyectoFinal.Entities.Persona;
import ElBuenSabor.ProyectoFinal.Repositories.PersonaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PersonaServiceImpl extends BaseServiceImpl<Persona, Long> implements PersonaService {

    public PersonaServiceImpl(PersonaRepository personaRepository) {
        super(personaRepository);
    }

    @Override
    @Transactional
    public Persona update(Long id, Persona updatedPersona) throws Exception {
        try {
            Persona existente = findById(id);

            // ✅ Actualizar datos simples
            existente.setNombre(updatedPersona.getNombre());
            existente.setApellido(updatedPersona.getApellido());
            existente.setTelefono(updatedPersona.getTelefono());
            existente.setFechaNacimiento(updatedPersona.getFechaNacimiento());
            existente.setBaja(updatedPersona.getBaja());
            existente.setImagen(updatedPersona.getImagen());

            // ✅ Actualizar datos de Usuario embebido (solo username)
            if (existente.getUsuario() != null && updatedPersona.getUsuario() != null
                    && updatedPersona.getUsuario().getUsername() != null) {
                existente.getUsuario().setUsername(updatedPersona.getUsuario().getUsername());
            }

            // ✅ Actualizar domicilio principal (si existe)
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
            throw new Exception("Error al actualizar el persona: " + e.getMessage());
        }
    }
}
