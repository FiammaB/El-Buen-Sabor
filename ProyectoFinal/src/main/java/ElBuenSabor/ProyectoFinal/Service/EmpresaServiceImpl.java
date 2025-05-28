package ElBuenSabor.ProyectoFinal.Service;

import ElBuenSabor.ProyectoFinal.DTO.EmpresaCreateUpdateDTO;
import ElBuenSabor.ProyectoFinal.DTO.EmpresaDTO;
import ElBuenSabor.ProyectoFinal.DTO.SucursalSimpleDTO; // Importar
import ElBuenSabor.ProyectoFinal.Entities.Empresa;
import ElBuenSabor.ProyectoFinal.Entities.Sucursal; // Importar
import ElBuenSabor.ProyectoFinal.Repositories.EmpresaRepository;
import ElBuenSabor.ProyectoFinal.Repositories.SucursalRepository; // Para verificar sucursales activas
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class EmpresaServiceImpl extends BaseServiceImpl<Empresa, Long> implements EmpresaService {

    private final EmpresaRepository empresaRepository;
    private final SucursalRepository sucursalRepository; // Inyectar para hasActiveSucursales

    @Autowired
    public EmpresaServiceImpl(EmpresaRepository empresaRepository, SucursalRepository sucursalRepository) {
        super(empresaRepository);
        this.empresaRepository = empresaRepository;
        this.sucursalRepository = sucursalRepository;
    }

    @Override
    @Transactional
    public EmpresaDTO createEmpresa(EmpresaCreateUpdateDTO dto) throws Exception {
        // Validaciones de unicidad
        if (empresaRepository.findByNombreRaw(dto.getNombre().trim()).isPresent()) {
            throw new Exception("Ya existe una empresa con el nombre: " + dto.getNombre().trim());
        }
        if (dto.getCuil() != null && empresaRepository.findByCuilRaw(dto.getCuil()).isPresent()) {
            throw new Exception("Ya existe una empresa con el CUIL: " + dto.getCuil());
        }

        Empresa empresa = new Empresa();
        empresa.setNombre(dto.getNombre().trim());
        empresa.setRazonSocial(dto.getRazonSocial().trim());
        empresa.setCuil(dto.getCuil());
        // 'baja' es false por defecto
        // Las sucursales se gestionan por separado, no al crear la empresa directamente aquí.
        return convertToDTO(empresaRepository.save(empresa));
    }

    @Override
    @Transactional
    public EmpresaDTO updateEmpresa(Long id, EmpresaCreateUpdateDTO dto) throws Exception {
        Empresa empresa = this.findByIdIncludingDeleted(id) // Permite actualizar incluso si está 'baja'
                .orElseThrow(() -> new Exception("Empresa no encontrada con ID: " + id + " para actualizar."));

        Optional<Empresa> existenteNombreRaw = empresaRepository.findByNombreRaw(dto.getNombre().trim());
        if (existenteNombreRaw.isPresent() && !existenteNombreRaw.get().getId().equals(id)) {
            throw new Exception("Ya existe otra empresa con el nombre: " + dto.getNombre().trim());
        }
        if (dto.getCuil() != null) {
            Optional<Empresa> existenteCuilRaw = empresaRepository.findByCuilRaw(dto.getCuil());
            if (existenteCuilRaw.isPresent() && !existenteCuilRaw.get().getId().equals(id)) {
                throw new Exception("Ya existe otra empresa con el CUIL: " + dto.getCuil());
            }
        }

        empresa.setNombre(dto.getNombre().trim());
        empresa.setRazonSocial(dto.getRazonSocial().trim());
        empresa.setCuil(dto.getCuil());
        // Si el DTO permitiera cambiar 'baja':
        // if (dto.getBaja() != null) empresa.setBaja(dto.isBaja());
        return convertToDTO(empresaRepository.save(empresa));
    }

    @Override
    @Transactional(readOnly = true)
    public EmpresaDTO findEmpresaById(Long id) throws Exception {
        return convertToDTO(super.findById(id).orElse(null));
    }

    @Override
    @Transactional(readOnly = true)
    public List<EmpresaDTO> findAllEmpresas() throws Exception {
        return super.findAll().stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Empresa> findByNombreRaw(String nombre) throws Exception {
        return empresaRepository.findByNombreRaw(nombre);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Empresa> findByCuilRaw(Integer cuil) throws Exception {
        return empresaRepository.findByCuilRaw(cuil);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasActiveSucursales(Long empresaId) throws Exception {
        // Necesitas un método en SucursalRepository:
        // boolean existsByEmpresaIdAndBajaFalse(Long empresaId);
        return sucursalRepository.existsByEmpresaIdAndBajaFalse(empresaId);
    }


    // --- Implementación de métodos de BaseService ---
    @Override
    @Transactional(readOnly = true)
    public List<Empresa> findAllIncludingDeleted() throws Exception {
        return empresaRepository.findAllRaw();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Empresa> findByIdIncludingDeleted(Long id) throws Exception {
        return empresaRepository.findByIdRaw(id);
    }

    @Override
    @Transactional
    public Empresa softDelete(Long id) throws Exception {
        Empresa empresa = this.findByIdIncludingDeleted(id)
                .orElseThrow(() -> new Exception("Empresa no encontrada con ID: " + id + " para dar de baja."));
        if (empresa.isBaja()) {
            throw new Exception("La empresa ya está dada de baja.");
        }
        // Lógica de negocio: No dar de baja si tiene sucursales activas.
        if (hasActiveSucursales(id)) {
            throw new Exception("No se puede dar de baja la empresa porque tiene sucursales activas asociadas.");
        }
        empresa.setBaja(true);
        return empresaRepository.save(empresa);
    }

    @Override
    @Transactional
    public Empresa reactivate(Long id) throws Exception {
        Empresa empresa = this.findByIdIncludingDeleted(id)
                .orElseThrow(() -> new Exception("Empresa no encontrada con ID: " + id + " para reactivar."));
        if (!empresa.isBaja()) {
            throw new Exception("La empresa no está dada de baja, no se puede reactivar.");
        }
        empresa.setBaja(false);
        return empresaRepository.save(empresa);
    }

    // --- Implementación de métodos de EmpresaService que devuelven DTO ---
    @Override
    @Transactional(readOnly = true)
    public List<EmpresaDTO> findAllEmpresasIncludingDeleted() throws Exception {
        return this.findAllIncludingDeleted().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public EmpresaDTO findEmpresaByIdIncludingDeleted(Long id) throws Exception {
        return convertToDTO(this.findByIdIncludingDeleted(id).orElse(null));
    }

    private EmpresaDTO convertToDTO(Empresa empresa) {
        if (empresa == null) return null;
        EmpresaDTO dto = new EmpresaDTO();
        dto.setId(empresa.getId());
        dto.setNombre(empresa.getNombre());
        dto.setRazonSocial(empresa.getRazonSocial());
        dto.setCuil(empresa.getCuil());
        dto.setBaja(empresa.isBaja());

        if (empresa.getSucursales() != null) {
            dto.setSucursales(empresa.getSucursales().stream()
                    .filter(suc -> !suc.isBaja()) // Opcional: mostrar solo sucursales activas en el DTO de empresa
                    .map(sucursal -> {
                        SucursalSimpleDTO sucDto = new SucursalSimpleDTO();
                        sucDto.setId(sucursal.getId());
                        sucDto.setNombre(sucursal.getNombre());
                        sucDto.setHorarioApertura(sucursal.getHorarioApertura());
                        sucDto.setHorarioCierre(sucursal.getHorarioCierre());
                        sucDto.setBaja(sucursal.isBaja());
                        if(sucursal.getDomicilio() != null){
                            sucDto.setDomicilioCalle(sucursal.getDomicilio().getCalle());
                            if(sucursal.getDomicilio().getLocalidad() != null){
                                sucDto.setDomicilioLocalidadNombre(sucursal.getDomicilio().getLocalidad().getNombre());
                            }
                        }
                        return sucDto;
                    }).collect(Collectors.toList()));
        } else {
            dto.setSucursales(new ArrayList<>());
        }
        return dto;
    }
}
