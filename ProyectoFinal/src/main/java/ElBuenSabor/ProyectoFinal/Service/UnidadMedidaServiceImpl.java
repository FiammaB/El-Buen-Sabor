package ElBuenSabor.ProyectoFinal.Service;

import ElBuenSabor.ProyectoFinal.DTO.UnidadMedidaDTO;
import ElBuenSabor.ProyectoFinal.Entities.UnidadMedida;
import ElBuenSabor.ProyectoFinal.Repositories.UnidadMedidaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UnidadMedidaServiceImpl extends BaseServiceImpl<UnidadMedida, Long> implements UnidadMedidaService {

    private final UnidadMedidaRepository unidadMedidaRepository;

    @Autowired
    public UnidadMedidaServiceImpl(UnidadMedidaRepository unidadMedidaRepository) {
        super(unidadMedidaRepository);
        this.unidadMedidaRepository = unidadMedidaRepository;
    }

    @Override
    @Transactional
    public UnidadMedidaDTO createUnidadMedida(UnidadMedidaDTO dto) throws Exception {
        if (dto.getDenominacion() == null || dto.getDenominacion().trim().isEmpty()) {
            throw new Exception("La denominación de la unidad de medida no puede estar vacía.");
        }
        Optional<UnidadMedida> existenteRaw = unidadMedidaRepository.findByDenominacionRaw(dto.getDenominacion().trim());
        if (existenteRaw.isPresent()) {
            throw new Exception("Ya existe una unidad de medida con la denominación: " + dto.getDenominacion().trim());
        }
        UnidadMedida unidad = new UnidadMedida();
        unidad.setDenominacion(dto.getDenominacion().trim());
        // 'baja' es false por defecto
        return convertToDTO(unidadMedidaRepository.save(unidad));
    }

    @Override
    @Transactional
    public UnidadMedidaDTO updateUnidadMedida(Long id, UnidadMedidaDTO dto) throws Exception {
        if (dto.getDenominacion() == null || dto.getDenominacion().trim().isEmpty()) {
            throw new Exception("La denominación de la unidad de medida no puede estar vacía.");
        }

        UnidadMedida unidad = this.findByIdIncludingDeleted(id) // Permite actualizar incluso si está 'baja'
                .orElseThrow(() -> new Exception("Unidad de Medida no encontrada con ID: " + id + " para actualizar."));

        Optional<UnidadMedida> existenteRaw = unidadMedidaRepository.findByDenominacionRaw(dto.getDenominacion().trim());
        if (existenteRaw.isPresent() && !existenteRaw.get().getId().equals(id)) {
            throw new Exception("Ya existe otra unidad de medida con la denominación: " + dto.getDenominacion().trim());
        }

        unidad.setDenominacion(dto.getDenominacion().trim());
        if (dto.isBaja() != unidad.isBaja()) { // Si el DTO permite cambiar el estado de baja
            unidad.setBaja(dto.isBaja());
        }
        return convertToDTO(unidadMedidaRepository.save(unidad));
    }

    @Override
    @Transactional(readOnly = true)
    public UnidadMedidaDTO findUnidadMedidaById(Long id) throws Exception {
        return convertToDTO(super.findById(id).orElse(null)); // Llama a BaseServiceImpl (activos)
    }

    @Override
    @Transactional(readOnly = true)
    public List<UnidadMedidaDTO> findAllUnidadesMedida() throws Exception {
        return super.findAll().stream().map(this::convertToDTO).collect(Collectors.toList()); // Llama a BaseServiceImpl (activos)
    }

    @Override
    @Transactional(readOnly = true)
    public UnidadMedidaDTO findByDenominacion(String denominacion) throws Exception {
        return convertToDTO(unidadMedidaRepository.findByDenominacion(denominacion)); // Afectado por @Where
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UnidadMedida> findByDenominacionRaw(String denominacion) throws Exception {
        return unidadMedidaRepository.findByDenominacionRaw(denominacion);
    }

    // --- Implementación de métodos de BaseService ---
    @Override
    @Transactional(readOnly = true)
    public List<UnidadMedida> findAllIncludingDeleted() throws Exception {
        return unidadMedidaRepository.findAllRaw();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UnidadMedida> findByIdIncludingDeleted(Long id) throws Exception {
        return unidadMedidaRepository.findByIdRaw(id);
    }

    @Override
    @Transactional
    public UnidadMedida softDelete(Long id) throws Exception {
        UnidadMedida unidad = this.findByIdIncludingDeleted(id)
                .orElseThrow(() -> new Exception("Unidad de Medida no encontrada con ID: " + id + " para dar de baja."));
        if (unidad.isBaja()) {
            throw new Exception("La unidad de medida ya está dada de baja.");
        }
        // Aquí se podría añadir lógica para verificar si la unidad de medida está en uso
        // por algún ArtículoInsumo activo antes de permitir la baja.
        // Ejemplo: if (articuloInsumoRepository.existsByUnidadMedidaAndBajaFalse(unidad)) { throw new Exception(...); }
        unidad.setBaja(true);
        return unidadMedidaRepository.save(unidad);
    }

    @Override
    @Transactional
    public UnidadMedida reactivate(Long id) throws Exception {
        UnidadMedida unidad = this.findByIdIncludingDeleted(id)
                .orElseThrow(() -> new Exception("Unidad de Medida no encontrada con ID: " + id + " para reactivar."));
        if (!unidad.isBaja()) {
            throw new Exception("La unidad de medida no está dada de baja, no se puede reactivar.");
        }
        unidad.setBaja(false);
        return unidadMedidaRepository.save(unidad);
    }

    // --- Implementación de métodos de UnidadMedidaService que devuelven DTO ---
    @Override
    @Transactional(readOnly = true)
    public List<UnidadMedidaDTO> findAllUnidadesMedidaIncludingDeleted() throws Exception {
        return this.findAllIncludingDeleted().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public UnidadMedidaDTO findUnidadMedidaByIdIncludingDeleted(Long id) throws Exception {
        return convertToDTO(this.findByIdIncludingDeleted(id).orElse(null));
    }

    private UnidadMedidaDTO convertToDTO(UnidadMedida unidadMedida) {
        if (unidadMedida == null) return null;
        UnidadMedidaDTO dto = new UnidadMedidaDTO();
        dto.setId(unidadMedida.getId());
        dto.setDenominacion(unidadMedida.getDenominacion());
        dto.setBaja(unidadMedida.isBaja());
        return dto;
    }
}
