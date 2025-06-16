package ElBuenSabor.ProyectoFinal.Service;

import ElBuenSabor.ProyectoFinal.Entities.Localidad;
import ElBuenSabor.ProyectoFinal.Exceptions.ResourceNotFoundException; // Posiblemente ya no sea necesaria
import ElBuenSabor.ProyectoFinal.Repositories.LocalidadRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // Importar Transactional

import java.util.List; // Importar List si no se usara el findAll del padre

@Service

public class LocalidadServiceImpl extends BaseServiceImpl<Localidad, Long> implements LocalidadService {

    public LocalidadServiceImpl(LocalidadRepository localidadRepository) {
        super(localidadRepository);
    }

    @Override
    @Transactional
    public Localidad update(Long id, Localidad updatedLocalidad) throws Exception {
        try {
            Localidad actual = findById(id);

            actual.setNombre(updatedLocalidad.getNombre());
            actual.setProvincia(updatedLocalidad.getProvincia());
            actual.setBaja(updatedLocalidad.getBaja());
            return baseRepository.save(actual);
        } catch (Exception e) {
            throw new Exception("Error al actualizar la localidad: " + e.getMessage());
        }
    }
}