package ElBuenSabor.ProyectoFinal.Service;

import ElBuenSabor.ProyectoFinal.DTO.PaisDTO;
import ElBuenSabor.ProyectoFinal.DTO.ProvinciaCreateUpdateDTO;
import ElBuenSabor.ProyectoFinal.DTO.ProvinciaDTO;
import ElBuenSabor.ProyectoFinal.Entities.Pais;
import ElBuenSabor.ProyectoFinal.Entities.Provincia;
import ElBuenSabor.ProyectoFinal.Repositories.PaisRepository;
import ElBuenSabor.ProyectoFinal.Repositories.ProvinciaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProvinciaServiceImpl extends BaseServiceImpl<Provincia, Long> implements ProvinciaService {

    private final ProvinciaRepository provinciaRepository;
    private final PaisRepository paisRepository;

    @Autowired
    public ProvinciaServiceImpl(ProvinciaRepository provinciaRepository, PaisRepository paisRepository) {
        super(provinciaRepository);
        this.provinciaRepository = provinciaRepository;
        this.paisRepository = paisRepository;
    }

    // ... createProvincia, updateProvincia, findProvinciaById, findAllProvincias, findByPaisId, findByNombre, findByNombreRaw ...
    // (Estos métodos parecen estar bien en tu artefacto actual, asumiendo que devuelven DTOs de activas)

    @Override
    @Transactional
    public ProvinciaDTO createProvincia(ProvinciaCreateUpdateDTO dto) throws Exception {
        if (dto.getNombre() == null || dto.getNombre().trim().isEmpty()) {
            throw new Exception("El nombre de la provincia no puede estar vacío.");
        }
        if (dto.getPaisId() == null) {
            throw new Exception("El ID del país es obligatorio.");
        }

        Optional<Provincia> existenteRaw = provinciaRepository.findByNombreRaw(dto.getNombre().trim());
        if (existenteRaw.isPresent()) {
            throw new Exception("Ya existe una provincia con el nombre: " + dto.getNombre().trim());
        }

        Pais pais = paisRepository.findById(dto.getPaisId())
                .orElseThrow(() -> new Exception("País activo no encontrado con ID: " + dto.getPaisId()));

        Provincia provincia = new Provincia();
        provincia.setNombre(dto.getNombre().trim());
        provincia.setPais(pais);
        return convertToDTO(provinciaRepository.save(provincia));
    }

    @Override
    @Transactional
    public ProvinciaDTO updateProvincia(Long id, ProvinciaCreateUpdateDTO dto) throws Exception {
        if (dto.getNombre() == null || dto.getNombre().trim().isEmpty()) {
            throw new Exception("El nombre de la provincia no puede estar vacío.");
        }
        if (dto.getPaisId() == null) {
            throw new Exception("El ID del país es obligatorio.");
        }

        Provincia provincia = this.findByIdIncludingDeleted(id) // Actualizar incluso si está de baja
                .orElseThrow(() -> new Exception("Provincia no encontrada con ID: " + id + " para actualizar."));

        Optional<Provincia> existenteRaw = provinciaRepository.findByNombreRaw(dto.getNombre().trim());
        if (existenteRaw.isPresent() && !existenteRaw.get().getId().equals(id)) {
            throw new Exception("Ya existe otra provincia con el nombre: " + dto.getNombre().trim());
        }

        Pais pais = paisRepository.findById(dto.getPaisId())
                .orElseThrow(() -> new Exception("País activo no encontrado con ID: " + dto.getPaisId()));

        provincia.setNombre(dto.getNombre().trim());
        provincia.setPais(pais);
        // Si el DTO permite cambiar el estado de 'baja'
        // if (dto.getBaja() != null) { // Asumiendo que ProvinciaCreateUpdateDTO puede tener 'baja'
        //     provincia.setBaja(dto.isBaja());
        // }
        return convertToDTO(provinciaRepository.save(provincia));
    }

    @Override
    @Transactional(readOnly = true)
    public ProvinciaDTO findProvinciaById(Long id) throws Exception {
        return convertToDTO(super.findById(id).orElse(null)); // Llama al findById de BaseServiceImpl (activos)
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProvinciaDTO> findAllProvincias() throws Exception {
        return super.findAll().stream().map(this::convertToDTO).collect(Collectors.toList()); // Llama al findAll de BaseServiceImpl (activos)
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProvinciaDTO> findByPaisId(Long paisId) throws Exception {
        return provinciaRepository.findByPaisId(paisId).stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ProvinciaDTO findByNombre(String nombre) throws Exception {
        return convertToDTO(provinciaRepository.findByNombre(nombre));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Provincia> findByNombreRaw(String nombre) throws Exception {
        return provinciaRepository.findByNombreRaw(nombre);
    }

    // --- Implementación Correcta para métodos de BaseService que devuelven Entidad ---
    @Override
    @Transactional(readOnly = true)
    public List<Provincia> findAllIncludingDeleted() throws Exception { // Devuelve List<Provincia>
        return provinciaRepository.findAllRaw(); // Usa el método del repositorio
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Provincia> findByIdIncludingDeleted(Long id) throws Exception { // Devuelve Optional<Provincia>
        return provinciaRepository.findByIdRaw(id); // Usa el método del repositorio
    }

    @Override
    @Transactional
    public Provincia softDelete(Long id) throws Exception {
        Provincia provincia = this.findByIdIncludingDeleted(id)
                .orElseThrow(() -> new Exception("Provincia no encontrada con ID: " + id + " para dar de baja."));
        if (provincia.isBaja()) {
            throw new Exception("La provincia ya está dada de baja.");
        }
        provincia.setBaja(true);
        return provinciaRepository.save(provincia);
    }

    @Override
    @Transactional
    public Provincia reactivate(Long id) throws Exception {
        Provincia provincia = this.findByIdIncludingDeleted(id)
                .orElseThrow(() -> new Exception("Provincia no encontrada con ID: " + id + " para reactivar."));
        if (!provincia.isBaja()) {
            throw new Exception("La provincia no está dada de baja, no se puede reactivar.");
        }
        provincia.setBaja(false);
        return provinciaRepository.save(provincia);
    }

    // --- Implementación para métodos de ProvinciaService que devuelven DTO ---
    @Override
    @Transactional(readOnly = true)
    public List<ProvinciaDTO> findAllProvinciasIncludingDeleted() throws Exception { // Devuelve List<ProvinciaDTO>
        return this.findAllIncludingDeleted().stream() // Llama al método local que devuelve List<Provincia>
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ProvinciaDTO findProvinciaByIdIncludingDeleted(Long id) throws Exception { // Devuelve ProvinciaDTO
        return convertToDTO(this.findByIdIncludingDeleted(id).orElse(null)); // Llama al método local que devuelve Optional<Provincia>
    }


    private ProvinciaDTO convertToDTO(Provincia provincia) {
        if (provincia == null) return null;
        ProvinciaDTO dto = new ProvinciaDTO();
        dto.setId(provincia.getId());
        dto.setNombre(provincia.getNombre());
        dto.setBaja(provincia.isBaja());

        if (provincia.getPais() != null) {
            PaisDTO paisDTO = new PaisDTO();
            paisDTO.setId(provincia.getPais().getId());
            paisDTO.setNombre(provincia.getPais().getNombre());
            paisDTO.setBaja(provincia.getPais().isBaja());
            dto.setPais(paisDTO);
        }
        return dto;
    }
}