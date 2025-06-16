package ElBuenSabor.ProyectoFinal.Service;

import ElBuenSabor.ProyectoFinal.Entities.Cliente;
// ResourceNotFoundException ya se maneja en BaseServiceImpl
// import ElBuenSabor.ProyectoFinal.Exceptions.ResourceNotFoundException;
import ElBuenSabor.ProyectoFinal.Repositories.ClienteRepository;
// Ya no es necesario si se inyecta por constructor explícito al padre
// import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // Importar Transactional

import java.util.List; // Importar List si no se usara el findAll del padre

@Service
// ClienteServiceImpl ahora extiende BaseServiceImpl
// y la interfaz ClienteService (que debe extender BaseService)
public class ClienteServiceImpl extends BaseServiceImpl<Cliente, Long> implements ClienteService {


    public ClienteServiceImpl(ClienteRepository clienteRepository) {
        super(clienteRepository); // Llama al constructor de la clase base
    }

    @Override
    @Transactional
    public Cliente update(Long id, Cliente updatedCliente) throws Exception {
        try {
            Cliente existente = findById(id);

            existente.setNombre(updatedCliente.getNombre());
            existente.setApellido(updatedCliente.getApellido());
            existente.setTelefono(updatedCliente.getTelefono());
            existente.setEmail(updatedCliente.getEmail());

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