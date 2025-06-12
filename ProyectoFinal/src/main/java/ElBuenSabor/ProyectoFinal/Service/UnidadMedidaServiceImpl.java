package ElBuenSabor.ProyectoFinal.Service;

import ElBuenSabor.ProyectoFinal.DTO.UnidadMedidaDTO;
import ElBuenSabor.ProyectoFinal.Entities.UnidadMedida;
import ElBuenSabor.ProyectoFinal.Repositories.UnidadMedidaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    public UnidadMedida createUnidadMedida(UnidadMedidaDTO dto) throws Exception {
        try {
            if (dto.getDenominacion() == null || dto.getDenominacion().trim().isEmpty()) {
                throw new Exception("La denominación de la unidad de medida no puede estar vacía.");
            }
            if (unidadMedidaRepository.findByDenominacion(dto.getDenominacion()) != null) {
                throw new Exception("Ya existe una unidad de medida con la denominación: " + dto.getDenominacion());
            }
            UnidadMedida unidad = UnidadMedida.builder().denominacion(dto.getDenominacion()).build();
            return unidadMedidaRepository.save(unidad);
        } catch (Exception e) {
            throw new Exception("Error al crear la unidad de medida: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    public UnidadMedida updateUnidadMedida(Long id, UnidadMedidaDTO dto) throws Exception {
        try {
            if (dto.getDenominacion() == null || dto.getDenominacion().trim().isEmpty()) {
                throw new Exception("La denominación de la unidad de medida no puede estar vacía.");
            }
            UnidadMedida unidad = unidadMedidaRepository.findById(id)
                    .orElseThrow(() -> new Exception("Unidad de medida no encontrada con ID: " + id));

            UnidadMedida unidadExistenteConNuevaDenom = unidadMedidaRepository.findByDenominacion(dto.getDenominacion());
            if (unidadExistenteConNuevaDenom != null && !unidadExistenteConNuevaDenom.getId().equals(id)) {
                throw new Exception("Ya existe otra unidad de medida con la denominación: " + dto.getDenominacion());
            }

            unidad.setDenominacion(dto.getDenominacion());
            return unidadMedidaRepository.save(unidad);
        } catch (Exception e) {
            throw new Exception("Error al actualizar la unidad de medida: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public UnidadMedida findByDenominacion(String denominacion) throws Exception {
        try {
            return unidadMedidaRepository.findByDenominacion(denominacion); //
        } catch (Exception e) {
            throw new Exception("Error al buscar unidad de medida por denominación: " + e.getMessage(), e);
        }
    }
}