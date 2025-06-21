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

            existente.setNombre(updatedCliente.getNombre());
            existente.setApellido(updatedCliente.getApellido());
            existente.setTelefono(updatedCliente.getTelefono());
            existente.setFechaNacimiento(updatedCliente.getFechaNacimiento());
            existente.setBaja(updatedCliente.getBaja());
            existente.setImagen(updatedCliente.getImagen());
            existente.setUsuario(updatedCliente.getUsuario());

            if (updatedCliente.getDomicilios() != null) {
                existente.getDomicilios().clear();
                existente.getDomicilios().addAll(updatedCliente.getDomicilios());
            }

            return baseRepository.save(existente);
        } catch (Exception e) {
            throw new Exception("Error al actualizar el cliente: " + e.getMessage());
        }
    }
}
