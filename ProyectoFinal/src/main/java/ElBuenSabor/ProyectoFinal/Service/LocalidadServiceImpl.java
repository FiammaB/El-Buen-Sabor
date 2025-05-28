package ElBuenSabor.ProyectoFinal.Service;

import ElBuenSabor.ProyectoFinal.DTO.*; // Importar todos los DTOs necesarios
import ElBuenSabor.ProyectoFinal.Entities.Localidad;
import ElBuenSabor.ProyectoFinal.Entities.Pais;
import ElBuenSabor.ProyectoFinal.Entities.Provincia;
import ElBuenSabor.ProyectoFinal.Repositories.LocalidadRepository;
import ElBuenSabor.ProyectoFinal.Repositories.ProvinciaRepository;
// No necesitas PaisRepository aquí directamente si ProvinciaDTO ya lo maneja
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class LocalidadServiceImpl extends BaseServiceImpl<Localidad, Long> implements LocalidadService {

    private final LocalidadRepository localidadRepository;
    private final ProvinciaRepository provinciaRepository; // Para asociar la provincia

    @Autowired
    public LocalidadServiceImpl(LocalidadRepository localidadRepository, ProvinciaRepository provinciaRepository) {
        super(localidadRepository);
        this.localidadRepository = localidadRepository;
        this.provinciaRepository = provinciaRepository;
    }

    @Override
    @Transactional
    public LocalidadDTO createLocalidad(LocalidadCreateUpdateDTO dto) throws Exception {
        if (dto.getNombre() == null || dto.getNombre().trim().isEmpty()) {
            throw new Exception("El nombre de la localidad no puede estar vacío.");
        }
        if (dto.getProvinciaId() == null) {
            throw new Exception("El ID de la provincia es obligatorio.");
        }

        Provincia provincia = provinciaRepository.findById(dto.getProvinciaId()) // findById ya filtra por activas
                .orElseThrow(() -> new Exception("Provincia activa no encontrada con ID: " + dto.getProvinciaId()));

        // Validar unicidad de nombre de localidad DENTRO de la misma provincia
        Optional<Localidad> existenteRaw = localidadRepository.findByNombreAndProvinciaRaw(dto.getNombre().trim(), provincia);
        if (existenteRaw.isPresent()) {
            throw new Exception("Ya existe una localidad con el nombre '" + dto.getNombre().trim() + "' en la provincia '" + provincia.getNombre() + "'.");
        }

        Localidad localidad = new Localidad();
        localidad.setNombre(dto.getNombre().trim());
        localidad.setProvincia(provincia);
        return convertToDTO(localidadRepository.save(localidad));
    }

    @Override
    @Transactional
    public LocalidadDTO updateLocalidad(Long id, LocalidadCreateUpdateDTO dto) throws Exception {
        if (dto.getNombre() == null || dto.getNombre().trim().isEmpty()) {
            throw new Exception("El nombre de la localidad no puede estar vacío.");
        }
        if (dto.getProvinciaId() == null) {
            throw new Exception("El ID de la provincia es obligatorio.");
        }

        Localidad localidad = this.findByIdIncludingDeleted(id) // Permite actualizar incluso si está 'baja'
                .orElseThrow(() -> new Exception("Localidad no encontrada con ID: " + id + " para actualizar."));

        Provincia provincia = provinciaRepository.findById(dto.getProvinciaId())
                .orElseThrow(() -> new Exception("Provincia activa no encontrada con ID: " + dto.getProvinciaId()));

        Optional<Localidad> existenteRaw = localidadRepository.findByNombreAndProvinciaRaw(dto.getNombre().trim(), provincia);
        if (existenteRaw.isPresent() && !existenteRaw.get().getId().equals(id)) {
            throw new Exception("Ya existe otra localidad con el nombre '" + dto.getNombre().trim() + "' en la provincia '" + provincia.getNombre() + "'.");
        }

        localidad.setNombre(dto.getNombre().trim());
        localidad.setProvincia(provincia);
        // Si DTO permitiera cambiar 'baja':
        // if (dto.getBaja() != null) localidad.setBaja(dto.isBaja());
        return convertToDTO(localidadRepository.save(localidad));
    }

    @Override
    @Transactional(readOnly = true)
    public LocalidadDTO findLocalidadById(Long id) throws Exception {
        return convertToDTO(super.findById(id).orElse(null));
    }

    @Override
    @Transactional(readOnly = true)
    public List<LocalidadDTO> findAllLocalidades() throws Exception {
        return super.findAll().stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<LocalidadDTO> findByProvinciaId(Long provinciaId) throws Exception {
        // provinciaRepository.findById para asegurar que la provincia existe y está activa
        if (!provinciaRepository.existsById(provinciaId)) { // existsById respeta @Where
            throw new Exception("Provincia activa no encontrada con ID: " + provinciaId);
        }
        return localidadRepository.findByProvinciaId(provinciaId).stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public LocalidadDTO findByNombre(String nombre) throws Exception {
        return convertToDTO(localidadRepository.findByNombre(nombre));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Localidad> findByNombreRaw(String nombre) throws Exception {
        return localidadRepository.findByNombreRaw(nombre);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Localidad> findByNombreAndProvinciaRaw(String nombre, Provincia provincia) throws Exception {
        return localidadRepository.findByNombreAndProvinciaRaw(nombre, provincia);
    }

    // --- Implementación de métodos de BaseService ---
    @Override
    @Transactional(readOnly = true)
    public List<Localidad> findAllIncludingDeleted() throws Exception {
        return localidadRepository.findAllRaw();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Localidad> findByIdIncludingDeleted(Long id) throws Exception {
        return localidadRepository.findByIdRaw(id);
    }

    @Override
    @Transactional
    public Localidad softDelete(Long id) throws Exception {
        Localidad localidad = this.findByIdIncludingDeleted(id)
                .orElseThrow(() -> new Exception("Localidad no encontrada con ID: " + id + " para dar de baja."));
        if (localidad.isBaja()) {
            throw new Exception("La localidad ya está dada de baja.");
        }
        // Aquí podrías añadir lógica: no dar de baja si tiene domicilios activos asociados.
        // if (domicilioRepository.existsByLocalidadIdAndBajaFalse(id)) {
        //    throw new Exception("No se puede dar de baja la localidad, tiene domicilios activos asociados.");
        // }
        localidad.setBaja(true);
        return localidadRepository.save(localidad);
    }

    @Override
    @Transactional
    public Localidad reactivate(Long id) throws Exception {
        Localidad localidad = this.findByIdIncludingDeleted(id)
                .orElseThrow(() -> new Exception("Localidad no encontrada con ID: " + id + " para reactivar."));
        if (!localidad.isBaja()) {
            throw new Exception("La localidad no está dada de baja, no se puede reactivar.");
        }
        // Al reactivar una localidad, ¿deberíamos reactivar su provincia si estaba de baja?
        // Generalmente no, la provincia debe manejarse independientemente.
        // Pero sí debemos asegurar que su provincia asociada esté activa.
        if (localidad.getProvincia() == null || localidad.getProvincia().isBaja()) {
            throw new Exception("No se puede reactivar la localidad porque su provincia asociada no está activa o no existe.");
        }
        localidad.setBaja(false);
        return localidadRepository.save(localidad);
    }

    // --- Implementación de métodos de LocalidadService que devuelven DTO ---
    @Override
    @Transactional(readOnly = true)
    public List<LocalidadDTO> findAllLocalidadesIncludingDeleted() throws Exception {
        return this.findAllIncludingDeleted().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public LocalidadDTO findLocalidadByIdIncludingDeleted(Long id) throws Exception {
        return convertToDTO(this.findByIdIncludingDeleted(id).orElse(null));
    }

    private LocalidadDTO convertToDTO(Localidad localidad) {
        if (localidad == null) return null;
        LocalidadDTO dto = new LocalidadDTO();
        dto.setId(localidad.getId());
        dto.setNombre(localidad.getNombre());
        dto.setBaja(localidad.isBaja());

        if (localidad.getProvincia() != null) {
            Provincia provincia = localidad.getProvincia();
            ProvinciaDTO provinciaDTO = new ProvinciaDTO();
            provinciaDTO.setId(provincia.getId());
            provinciaDTO.setNombre(provincia.getNombre());
            provinciaDTO.setBaja(provincia.isBaja());

            if (provincia.getPais() != null) {
                Pais pais = provincia.getPais();
                PaisDTO paisDTO = new PaisDTO();
                paisDTO.setId(pais.getId());
                paisDTO.setNombre(pais.getNombre());
                paisDTO.setBaja(pais.isBaja());
                provinciaDTO.setPais(paisDTO);
            }
            dto.setProvincia(provinciaDTO);
        }
        return dto;
    }
}