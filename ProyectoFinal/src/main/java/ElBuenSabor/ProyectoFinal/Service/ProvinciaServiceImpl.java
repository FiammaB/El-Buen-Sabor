package ElBuenSabor.ProyectoFinal.Service;

import ElBuenSabor.ProyectoFinal.Entities.Provincia;
import ElBuenSabor.ProyectoFinal.Exceptions.ResourceNotFoundException; // Posiblemente ya no sea necesaria
import ElBuenSabor.ProyectoFinal.Repositories.ProvinciaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // Importar Transactional

import java.util.List; // Importar List si no se usara el findAll del padre

@Service

public class ProvinciaServiceImpl extends BaseServiceImpl<Provincia, Long> implements ProvinciaService {

    public ProvinciaServiceImpl(ProvinciaRepository provinciaRepository) {
        super(provinciaRepository);
    }


    @Override
    @Transactional
    public Provincia update(Long id, Provincia updatedProvincia) throws Exception { // <<-- Añadir throws Exception
        try {

            Provincia actual = findById(id);

            actual.setNombre(updatedProvincia.getNombre());
            actual.setPais(updatedProvincia.getPais());

            return baseRepository.save(actual);
        } catch (Exception e) {

            throw new Exception("Error al actualizar la provincia: " + e.getMessage());
        }
    }
}