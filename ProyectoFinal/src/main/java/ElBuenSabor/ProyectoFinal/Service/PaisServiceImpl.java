package ElBuenSabor.ProyectoFinal.Service;

// ... imports ...
import ElBuenSabor.ProyectoFinal.DTO.PaisDTO;
import ElBuenSabor.ProyectoFinal.Entities.Pais;
import ElBuenSabor.ProyectoFinal.Repositories.PaisRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List; // Asegurar import
import java.util.Optional; // Asegurar import
import java.util.stream.Collectors; // Asegurar import


@Service
public class PaisServiceImpl extends BaseServiceImpl<Pais, Long> implements PaisService {

    private final PaisRepository paisRepository;

    @Autowired
    public PaisServiceImpl(PaisRepository paisRepository) {
        super(paisRepository); // paisRepository es JpaRepository<Pais, Long>
        this.paisRepository = paisRepository;
    }

    @Override
    @Transactional
    public PaisDTO createPais(PaisDTO dto) throws Exception {
        if (dto.getNombre() == null || dto.getNombre().trim().isEmpty()) { // Validación básica, complementa @Valid
            throw new Exception("El nombre del país no puede estar vacío.");
        }
        // Validar unicidad usando el método raw que ignora el filtro @Where
        Optional<Pais> existenteRaw = paisRepository.findByNombreRaw(dto.getNombre().trim());
        if (existenteRaw.isPresent()) {
            throw new Exception("Ya existe un país con el nombre: " + dto.getNombre().trim());
        }
        Pais pais = new Pais();
        pais.setNombre(dto.getNombre().trim());
        // 'baja' es false por defecto desde BaseEntity
        return convertToDTO(paisRepository.save(pais));
    }

    @Override
    @Transactional
    public PaisDTO updatePais(Long id, PaisDTO dto) throws Exception {
        if (dto.getNombre() == null || dto.getNombre().trim().isEmpty()) {
            throw new Exception("El nombre del país no puede estar vacío.");
        }
        // findById de BaseServiceImpl solo traerá activos debido a @Where
        Pais pais = this.findById(id)
                .orElseThrow(() -> new Exception("País activo no encontrado con ID: " + id + " para actualizar."));

        Optional<Pais> existenteRaw = paisRepository.findByNombreRaw(dto.getNombre().trim());
        if (existenteRaw.isPresent() && !existenteRaw.get().getId().equals(id)) {
            throw new Exception("Ya existe otro país con el nombre: " + dto.getNombre().trim());
        }
        pais.setNombre(dto.getNombre().trim());
        // Si el DTO tuviera un campo 'baja' y se quisiera modificar desde aquí:
        // pais.setBaja(dto.isBaja());
        return convertToDTO(paisRepository.save(pais));
    }

    @Override
    @Transactional(readOnly = true)
    public PaisDTO findPaisById(Long id) throws Exception {
        // findById de BaseServiceImpl solo trae activos
        return convertToDTO(this.findById(id).orElse(null));
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaisDTO> findAllPaises() throws Exception {
        // findAll de BaseServiceImpl solo trae activos
        return this.findAll().stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public PaisDTO findByNombre(String nombre) throws Exception {
        // paisRepository.findByNombre() solo trae activos
        return convertToDTO(paisRepository.findByNombre(nombre));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Pais> findByNombreRaw(String nombre) throws Exception {
        return paisRepository.findByNombreRaw(nombre);
    }

    // Los métodos softDelete y reactivate de BaseServiceImpl ya están disponibles.
    // Si se necesitan findAllIncludingDeleted y findByIdIncludingDeleted, y BaseServiceImpl
    // no puede implementarlos genéricamente, se añadirían aquí usando los métodos Raw del repositorio.

    @Override
    @Transactional(readOnly = true)
    public List<Pais> findAllIncludingDeleted() throws Exception {
        // Asumiendo que PaisRepository tiene findAllRaw() o se implementa aquí
        // return paisRepository.findAllRaw();
        // O si no, y queremos evitar el filtro @Where temporalmente (NO RECOMENDADO PARA PRODUCCIÓN)
        // se podría usar EntityManager para una query nativa, o aceptar que por ahora devuelve activos.
        // Por ahora, para cumplir la interfaz, si no hay findAllRaw:
        System.err.println("ADVERTENCIA: PaisServiceImpl.findAllIncludingDeleted() actualmente solo devuelve activos si no hay un método findAllRaw() en el repositorio.");
        return paisRepository.findAll(); // Esto devolverá solo activos si @Where está en Pais
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Pais> findByIdIncludingDeleted(Long id) throws Exception {
        return paisRepository.findByIdRaw(id); // Usar el método del repositorio que ignora @Where
    }

    @Override
    @Transactional
    public Pais softDelete(Long id) throws Exception {
        // Primero, obtenemos la entidad SIN el filtro @Where, usando el método 'Raw' del repositorio.
        Pais pais = paisRepository.findByIdRaw(id)
                .orElseThrow(() -> new Exception("País no encontrado con ID: " + id + " para dar de baja."));

        if (pais.isBaja()) {
            throw new Exception("El país ya está dado de baja.");
        }

        pais.setBaja(true);
        return paisRepository.save(pais);
    }

    @Override
    @Transactional
    public Pais reactivate(Long id) throws Exception {
        // Obtenemos la entidad SIN el filtro @Where.
        Pais pais = paisRepository.findByIdRaw(id)
                .orElseThrow(() -> new Exception("País no encontrado con ID: " + id + " para reactivar."));

        if (!pais.isBaja()) {
            throw new Exception("El país no está dado de baja, no se puede reactivar.");
        }

        pais.setBaja(false);
        return paisRepository.save(pais);
    }

    private PaisDTO convertToDTO(Pais pais) {
        if (pais == null) return null;
        PaisDTO dto = new PaisDTO();
        dto.setId(pais.getId());
        dto.setNombre(pais.getNombre());
        dto.setBaja(pais.isBaja()); // Incluir el estado de baja
        return dto;
    }
}