package ElBuenSabor.ProyectoFinal.Service;

import ElBuenSabor.ProyectoFinal.Entities.Persona;
import ElBuenSabor.ProyectoFinal.Entities.Usuario;
import ElBuenSabor.ProyectoFinal.Exceptions.ResourceNotFoundException;
import ElBuenSabor.ProyectoFinal.Repositories.PersonaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ElBuenSabor.ProyectoFinal.DTO.ClienteAdminUpdateDTO; // <-- AÑADIR IMPORT
import org.springframework.beans.factory.annotation.Autowired; // <-- AÑADIR IMPORT


@Service
public class PersonaServiceImpl extends BaseServiceImpl<Persona, Long> implements PersonaService {

    private final PersonaRepository personaRepository;
    private final UsuarioService usuarioService;

    @Autowired // <-- Se puede poner en el constructor
    public PersonaServiceImpl(PersonaRepository personaRepository, UsuarioService usuarioService) {
        super(personaRepository);
        this.personaRepository = personaRepository; // Guardar la referencia si se usa directamente
        this.usuarioService = usuarioService;      // Guardar la referencia del nuevo servicio
    }

    // <-- 2. AÑADIR LA IMPLEMENTACIÓN DEL NUEVO MÉTODO
    @Override
    @Transactional
    public void actualizarClienteDesdeAdmin(Long personaId, ClienteAdminUpdateDTO dto) throws Exception {
        // Buscar la persona o lanzar un error
        Persona persona = personaRepository.findById(personaId)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró la persona con id: " + personaId));

        // Actualizar el teléfono en la entidad Persona
        persona.setTelefono(dto.getTelefono());

        // Actualizar el estado en la entidad Usuario asociada
        Usuario usuario = persona.getUsuario();
        if (usuario != null) {
            boolean nuevaBaja = !dto.isActivo(); // Convertir 'activo' a 'baja'
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
