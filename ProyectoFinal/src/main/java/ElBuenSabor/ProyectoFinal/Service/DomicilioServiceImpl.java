package ElBuenSabor.ProyectoFinal.Service;

import ElBuenSabor.ProyectoFinal.Entities.Domicilio;
import ElBuenSabor.ProyectoFinal.Exceptions.ResourceNotFoundException; // Posiblemente ya no sea necesaria
import ElBuenSabor.ProyectoFinal.Repositories.DomicilioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // Importar Transactional

import java.util.List; // Importar List si no se usara el findAll del padre

@Service

public class DomicilioServiceImpl extends BaseServiceImpl<Domicilio, Long> implements DomicilioService {


    public DomicilioServiceImpl(DomicilioRepository domicilioRepository) {
        super(domicilioRepository); // Llama al constructor de la clase base
    }



    @Override
    @Transactional
    public Domicilio update(Long id, Domicilio updatedDomicilio) throws Exception {
        try {
            Domicilio actual = findById(id); //

            actual.setCalle(updatedDomicilio.getCalle()); //
            actual.setNumero(updatedDomicilio.getNumero()); //
            actual.setCp(updatedDomicilio.getCp()); //
            actual.setLocalidad(updatedDomicilio.getLocalidad()); //


            return baseRepository.save(actual);
        } catch (Exception e) {

            throw new Exception("Error al actualizar el domicilio: " + e.getMessage());
        }
    }
}