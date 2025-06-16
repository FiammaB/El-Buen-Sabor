package ElBuenSabor.ProyectoFinal.Service;

import ElBuenSabor.ProyectoFinal.Entities.Pais;
import ElBuenSabor.ProyectoFinal.Exceptions.ResourceNotFoundException; // Posiblemente ya no sea necesaria
import ElBuenSabor.ProyectoFinal.Repositories.PaisRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // Importar Transactional

import java.util.List; // Importar List si no se usara el findAll del padre

@Service

public class PaisServiceImpl extends BaseServiceImpl<Pais, Long> implements PaisService {

    public PaisServiceImpl(PaisRepository paisRepository) {
        super(paisRepository);
    }

    @Override
    @Transactional
    public Pais update(Long id, Pais updatedPais) throws Exception {
        try {
            Pais actual = findById(id);

            actual.setNombre(updatedPais.getNombre());


            return baseRepository.save(actual);
        } catch (Exception e) {

            throw new Exception("Error al actualizar el país: " + e.getMessage());
        }
    }
}