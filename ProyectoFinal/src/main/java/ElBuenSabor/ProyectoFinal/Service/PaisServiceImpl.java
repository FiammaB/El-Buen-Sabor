package ElBuenSabor.ProyectoFinal.Service;

import ElBuenSabor.ProyectoFinal.DTO.PaisDTO;
import ElBuenSabor.ProyectoFinal.Entities.Pais;
import ElBuenSabor.ProyectoFinal.Repositories.PaisRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PaisServiceImpl extends BaseServiceImpl<Pais, Long> implements PaisService {

    private final PaisRepository paisRepository;

    @Autowired
    public PaisServiceImpl(PaisRepository paisRepository) {
        super(paisRepository);
        this.paisRepository = paisRepository;
    }

    @Override
    @Transactional
    public Pais createPais(PaisDTO dto) throws Exception {
        try {
            if (dto.getNombre() == null || dto.getNombre().trim().isEmpty()) {
                throw new Exception("El nombre del país no puede estar vacío.");
            }
            if (paisRepository.findByNombre(dto.getNombre()) != null) {
                throw new Exception("Ya existe un país con el nombre: " + dto.getNombre());
            }
            Pais pais = Pais.builder().nombre(dto.getNombre()).build();
            return paisRepository.save(pais);
        } catch (Exception e) {
            throw new Exception("Error al crear el país: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    public Pais updatePais(Long id, PaisDTO dto) throws Exception {
        try {
            if (dto.getNombre() == null || dto.getNombre().trim().isEmpty()) {
                throw new Exception("El nombre del país no puede estar vacío.");
            }
            Pais pais = paisRepository.findById(id)
                    .orElseThrow(() -> new Exception("País no encontrado con ID: " + id));

            // Verificar si el nuevo nombre ya existe en otro país (opcional)
            Pais paisExistenteConNuevoNombre = paisRepository.findByNombre(dto.getNombre());
            if (paisExistenteConNuevoNombre != null && !paisExistenteConNuevoNombre.getId().equals(id)) {
                throw new Exception("Ya existe otro país con el nombre: " + dto.getNombre());
            }

            pais.setNombre(dto.getNombre());
            return paisRepository.save(pais);
        } catch (Exception e) {
            throw new Exception("Error al actualizar el país: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Pais findByNombre(String nombre) throws Exception {
        try {
            return paisRepository.findByNombre(nombre);
        } catch (Exception e) {
            throw new Exception("Error al buscar país por nombre: " + e.getMessage(), e);
        }
    }
}