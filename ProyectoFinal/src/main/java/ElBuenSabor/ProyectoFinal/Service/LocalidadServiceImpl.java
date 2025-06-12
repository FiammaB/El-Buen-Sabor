package ElBuenSabor.ProyectoFinal.Service;

import ElBuenSabor.ProyectoFinal.DTO.*;
import ElBuenSabor.ProyectoFinal.Entities.Localidad;
import ElBuenSabor.ProyectoFinal.Entities.Pais;
import ElBuenSabor.ProyectoFinal.Entities.Provincia;
import ElBuenSabor.ProyectoFinal.Repositories.LocalidadRepository;
import ElBuenSabor.ProyectoFinal.Repositories.ProvinciaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class LocalidadServiceImpl extends BaseServiceImpl<Localidad, Long> implements LocalidadService {

    private final LocalidadRepository localidadRepository;
    private final ProvinciaRepository provinciaRepository;

    @Autowired
    public LocalidadServiceImpl(LocalidadRepository localidadRepository, ProvinciaRepository provinciaRepository) {
        super(localidadRepository);
        this.localidadRepository = localidadRepository;
        this.provinciaRepository = provinciaRepository;
    }

    @Override
    @Transactional
    public LocalidadDTO createLocalidad(LocalidadCreateUpdateDTO dto) throws Exception {
        try {
            if (dto.getNombre() == null || dto.getNombre().trim().isEmpty()) {
                throw new Exception("El nombre de la localidad no puede estar vacío.");
            }
            if (localidadRepository.findByNombre(dto.getNombre()) != null) {
                throw new Exception("Ya existe una localidad con el nombre: " + dto.getNombre());
            }
            Provincia provincia = provinciaRepository.findById(dto.getProvinciaId())
                    .orElseThrow(() -> new Exception("Provincia no encontrada con ID: " + dto.getProvinciaId()));

            Localidad localidad = Localidad.builder()
                    .nombre(dto.getNombre())
                    .provincia(provincia)
                    .build();
            return convertToDTO(localidadRepository.save(localidad));
        } catch (Exception e) {
            throw new Exception("Error al crear la localidad: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    public LocalidadDTO updateLocalidad(Long id, LocalidadCreateUpdateDTO dto) throws Exception {
        try {
            if (dto.getNombre() == null || dto.getNombre().trim().isEmpty()) {
                throw new Exception("El nombre de la localidad no puede estar vacío.");
            }
            Localidad localidad = localidadRepository.findById(id)
                    .orElseThrow(() -> new Exception("Localidad no encontrada con ID: " + id));

            Localidad localidadExistenteConNuevoNombre = localidadRepository.findByNombre(dto.getNombre());
            if (localidadExistenteConNuevoNombre != null && !localidadExistenteConNuevoNombre.getId().equals(id)) {
                throw new Exception("Ya existe otra localidad con el nombre: " + dto.getNombre());
            }

            Provincia provincia = provinciaRepository.findById(dto.getProvinciaId())
                    .orElseThrow(() -> new Exception("Provincia no encontrada con ID: " + dto.getProvinciaId()));

            localidad.setNombre(dto.getNombre());
            localidad.setProvincia(provincia);
            return convertToDTO(localidadRepository.save(localidad));
        } catch (Exception e) {
            throw new Exception("Error al actualizar la localidad: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public LocalidadDTO findLocalidadById(Long id) throws Exception {
        Localidad localidad = localidadRepository.findById(id)
                .orElseThrow(() -> new Exception("Localidad no encontrada con ID: " + id));
        return convertToDTO(localidad);
    }

    @Override
    @Transactional(readOnly = true)
    public List<LocalidadDTO> findAllLocalidades() throws Exception {
        return localidadRepository.findAll().stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<LocalidadDTO> findByProvinciaId(Long provinciaId) throws Exception {
        // Asumiendo que LocalidadRepository tiene findByProvincia_Id(Long provinciaId) o similar
        // o provinciaRepository.findById(provinciaId).orElse(null)?.getLocalidades() si la relación es bidireccional
        List<Localidad> localidades = localidadRepository.findAll().stream()
                .filter(loc -> loc.getProvincia() != null && loc.getProvincia().getId().equals(provinciaId))
                .collect(Collectors.toList());
        if (localidades.isEmpty() && !provinciaRepository.existsById(provinciaId)){
            throw new Exception("Provincia no encontrada con ID: " + provinciaId);
        }
        return localidades.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public LocalidadDTO findByNombre(String nombre) throws Exception {
        Localidad localidad = localidadRepository.findByNombre(nombre); //
        if (localidad == null) {
            // Opcional: throw new Exception("Localidad no encontrada con nombre: " + nombre);
            return null;
        }
        return convertToDTO(localidad);
    }

    private LocalidadDTO convertToDTO(Localidad localidad) {
        if (localidad == null) return null;
        LocalidadDTO dto = new LocalidadDTO();
        dto.setId(localidad.getId());
        dto.setNombre(localidad.getNombre());
        if (localidad.getProvincia() != null) {
            ProvinciaDTO provinciaDTO = new ProvinciaDTO();
            provinciaDTO.setId(localidad.getProvincia().getId());
            provinciaDTO.setNombre(localidad.getProvincia().getNombre());
            if (localidad.getProvincia().getPais() != null) {
                PaisDTO paisDTO = new PaisDTO();
                paisDTO.setId(localidad.getProvincia().getPais().getId());
                paisDTO.setNombre(localidad.getProvincia().getPais().getNombre());
                provinciaDTO.setPais(paisDTO);
                provinciaDTO.setPaisId(paisDTO.getId());
            }
            dto.setProvincia(provinciaDTO);
            dto.setProvinciaId(provinciaDTO.getId());
        }
        return dto;
    }
}