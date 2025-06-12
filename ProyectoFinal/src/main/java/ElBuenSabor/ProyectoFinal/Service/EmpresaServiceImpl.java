package ElBuenSabor.ProyectoFinal.Service;

import ElBuenSabor.ProyectoFinal.DTO.EmpresaDTO;
import ElBuenSabor.ProyectoFinal.Entities.Empresa;
import ElBuenSabor.ProyectoFinal.Repositories.EmpresaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EmpresaServiceImpl extends BaseServiceImpl<Empresa, Long> implements EmpresaService {

    private final EmpresaRepository empresaRepository;

    @Autowired
    public EmpresaServiceImpl(EmpresaRepository empresaRepository) {
        super(empresaRepository);
        this.empresaRepository = empresaRepository;
    }

    @Override
    @Transactional
    public Empresa createEmpresa(EmpresaDTO dto) throws Exception {
        try {
            Empresa empresa = new Empresa();
            empresa.setNombre(dto.getNombre());
            empresa.setRazonSocial(dto.getRazonSocial());
            empresa.setCuil(dto.getCuil());
            // Las sucursales se añadirían a través del SucursalService,
            // no directamente al crear la empresa generalmente.
            return empresaRepository.save(empresa);
        } catch (Exception e) {
            throw new Exception("Error al crear la empresa: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    public Empresa updateEmpresa(Long id, EmpresaDTO dto) throws Exception {
        try {
            Empresa empresa = empresaRepository.findById(id)
                    .orElseThrow(() -> new Exception("Empresa no encontrada con ID: " + id));
            empresa.setNombre(dto.getNombre());
            empresa.setRazonSocial(dto.getRazonSocial());
            empresa.setCuil(dto.getCuil());
            return empresaRepository.save(empresa);
        } catch (Exception e) {
            throw new Exception("Error al actualizar la empresa: " + e.getMessage(), e);
        }
    }
}