package ElBuenSabor.ProyectoFinal.Service;

import ElBuenSabor.ProyectoFinal.Entities.UnidadMedida;
import ElBuenSabor.ProyectoFinal.Exceptions.ResourceNotFoundException; // Posiblemente ya no sea necesaria
import ElBuenSabor.ProyectoFinal.Repositories.UnidadMedidaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // Importar Transactional

import java.util.List; // Importar List si no se usara el findAll del padre

@Service
public class UnidadMedidaServiceImpl extends BaseServiceImpl<UnidadMedida, Long> implements UnidadMedidaService {


    public UnidadMedidaServiceImpl(UnidadMedidaRepository unidadMedidaRepository) {
        super(unidadMedidaRepository); // Llama al constructor de la clase base
    }

    @Override
    @Transactional
    public UnidadMedida update(Long id, UnidadMedida updatedUnidad) throws Exception { // <<-- Añadir throws Exception
        try {

            UnidadMedida actual = findById(id);

            actual.setDenominacion(updatedUnidad.getDenominacion());

            return baseRepository.save(actual);
        } catch (Exception e) {

            throw new Exception("Error al actualizar la unidad de medida: " + e.getMessage());
        }
    }
}