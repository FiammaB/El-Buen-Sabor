package ElBuenSabor.ProyectoFinal.Service;

import ElBuenSabor.ProyectoFinal.Entities.Cliente;
import ElBuenSabor.ProyectoFinal.Repositories.ClienteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ClienteServiceImpl extends BaseServiceImpl<Cliente, Long> implements ClienteService {

    public ClienteServiceImpl(ClienteRepository clienteRepository) {
        super(clienteRepository);
    }

    @Override
    @Transactional
    public Cliente update(Long id, Cliente updatedCliente) throws Exception {
        try {
            Cliente existente = findById(id);

            // Actualizar datos simples
            existente.setApellido(updatedCliente.getApellido());
            existente.setTelefono(updatedCliente.getTelefono());
            existente.setFechaNacimiento(updatedCliente.getFechaNacimiento());
            existente.setBaja(updatedCliente.getBaja());
            existente.setImagen(updatedCliente.getImagen());

            // Actualizar nombre de usuario si corresponde (usuario embebido)
            if (existente.getUsuario() != null && updatedCliente.getUsuario() != null) {
                if (updatedCliente.getUsuario().getUsername() != null) {
                    existente.getUsuario().setUsername(updatedCliente.getUsuario().getUsername());
                }
            }

            // Actualizar domicilio (asumimos solo uno por simplicidad, podés iterar si son varios)
            if (
                    updatedCliente.getDomicilios() != null &&
                            !updatedCliente.getDomicilios().isEmpty() &&
                            existente.getDomicilios() != null &&
                            !existente.getDomicilios().isEmpty()
            ) {
                // Actualiza SOLO los campos del primer domicilio
                var domicilioNuevo = updatedCliente.getDomicilios().get(0);
                var domicilioExistente = existente.getDomicilios().get(0);

                domicilioExistente.setCalle(domicilioNuevo.getCalle());
                domicilioExistente.setNumero(domicilioNuevo.getNumero());
                domicilioExistente.setCp(domicilioNuevo.getCp());
                // Si querés actualizar localidad también:
                if (domicilioNuevo.getLocalidad() != null) {
                    domicilioExistente.setLocalidad(domicilioNuevo.getLocalidad());
                }
            }

            return baseRepository.save(existente);
        } catch (Exception e) {
            throw new Exception("Error al actualizar el cliente: " + e.getMessage());
        }
    }
}
