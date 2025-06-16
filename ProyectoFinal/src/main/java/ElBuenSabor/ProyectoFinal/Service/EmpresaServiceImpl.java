package ElBuenSabor.ProyectoFinal.Service;

import ElBuenSabor.ProyectoFinal.Entities.Empresa;
import ElBuenSabor.ProyectoFinal.Exceptions.ResourceNotFoundException; // Posiblemente ya no sea necesaria
import ElBuenSabor.ProyectoFinal.Repositories.EmpresaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // Importar Transactional

import java.util.List; // Importar List si no se usara el findAll del padre

@Service

public class EmpresaServiceImpl extends BaseServiceImpl<Empresa, Long> implements EmpresaService {


    public EmpresaServiceImpl(EmpresaRepository empresaRepository) {
        super(empresaRepository);
    }

    @Override
    @Transactional
    public Empresa update(Long id, Empresa updatedEmpresa) throws Exception {
        try {
            Empresa actual = findById(id);

            actual.setNombre(updatedEmpresa.getNombre());
            actual.setRazonSocial(updatedEmpresa.getRazonSocial());
            actual.setCuil(updatedEmpresa.getCuil());

            return baseRepository.save(actual);
        } catch (Exception e) {

            throw new Exception("Error al actualizar la empresa: " + e.getMessage());
        }
    }
}