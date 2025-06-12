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

    @Override
    @Transactional
    public ProvinciaDTO createProvincia(ProvinciaCreateUpdateDTO dto) throws Exception {
        try {
            if (dto.getNombre() == null || dto.getNombre().trim().isEmpty()) {
                throw new Exception("El nombre de la provincia no puede estar vacío.");
            }
            if (provinciaRepository.findByNombre(dto.getNombre()) != null) {
                throw new Exception("Ya existe una provincia con el nombre: " + dto.getNombre());
            }
            Pais pais = paisRepository.findById(dto.getPaisId())
                    .orElseThrow(() -> new Exception("País no encontrado con ID: " + dto.getPaisId()));

            Provincia provincia = Provincia.builder()
                    .nombre(dto.getNombre())
                    .pais(pais)
                    .build();
            return convertToDTO(provinciaRepository.save(provincia));
        } catch (Exception e) {
            throw new Exception("Error al crear la provincia: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    public ProvinciaDTO updateProvincia(Long id, ProvinciaCreateUpdateDTO dto) throws Exception {
        try {
            if (dto.getNombre() == null || dto.getNombre().trim().isEmpty()) {
                throw new Exception("El nombre de la provincia no puede estar vacío.");
            }
            Provincia provincia = provinciaRepository.findById(id)
                    .orElseThrow(() -> new Exception("Provincia no encontrada con ID: " + id));

            Provincia provinciaExistenteConNuevoNombre = provinciaRepository.findByNombre(dto.getNombre());
            if (provinciaExistenteConNuevoNombre != null && !provinciaExistenteConNuevoNombre.getId().equals(id)) {
                throw new Exception("Ya existe otra provincia con el nombre: " + dto.getNombre());
            }

            Pais pais = paisRepository.findById(dto.getPaisId())
                    .orElseThrow(() -> new Exception("País no encontrado con ID: " + dto.getPaisId()));

            provincia.setNombre(dto.getNombre());
            provincia.setPais(pais);
            return convertToDTO(provinciaRepository.save(provincia));
        } catch (Exception e) {
            throw new Exception("Error al actualizar la provincia: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public ProvinciaDTO findProvinciaById(Long id) throws Exception {
        Provincia provincia = provinciaRepository.findById(id)
                .orElseThrow(() -> new Exception("Provincia no encontrada con ID: " + id));
        return convertToDTO(provincia);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProvinciaDTO> findAllProvincias() throws Exception {
        return provinciaRepository.findAll().stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProvinciaDTO> findByPaisId(Long paisId) throws Exception {
        // Necesitarías añadir `List<Provincia> findByPaisId(Long paisId);` a ProvinciaRepository
        // Por ahora, lo simularé filtrando todas. No es eficiente para DB grandes.
        // Mejor solución: paisRepository.findById(paisId).orElse(null)?.getProvincias() si la relación es bidireccional
        // o un método específico en ProvinciaRepository.
        // Asumiendo que ProvinciaRepository tiene findByPais_Id(Long paisId) o similar
        List<Provincia> provincias = provinciaRepository.findAll().stream()
                .filter(p -> p.getPais() != null && p.getPais().getId().equals(paisId))
                .collect(Collectors.toList());
        if (provincias.isEmpty() && !paisRepository.existsById(paisId)){
            throw new Exception("País no encontrado con ID: " + paisId);
        }
        return provincias.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ProvinciaDTO findByNombre(String nombre) throws Exception {
        Provincia provincia = provinciaRepository.findByNombre(nombre); //
        if (provincia == null) {
            // Opcional: throw new Exception("Provincia no encontrada con nombre: " + nombre);
            return null;
        }
        return convertToDTO(provincia);
    }

    private ProvinciaDTO convertToDTO(Provincia provincia) {
        if (provincia == null) return null;
        ProvinciaDTO dto = new ProvinciaDTO();
        dto.setId(provincia.getId());
        dto.setNombre(provincia.getNombre());
        if (provincia.getPais() != null) {
            PaisDTO paisDTO = new PaisDTO();
            paisDTO.setId(provincia.getPais().getId());
            paisDTO.setNombre(provincia.getPais().getNombre());
            dto.setPais(paisDTO);
            dto.setPaisId(provincia.getPais().getId()); // Para consistencia en el DTO
        }
        return dto;
    }
}