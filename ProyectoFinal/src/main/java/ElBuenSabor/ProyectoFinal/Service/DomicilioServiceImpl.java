package ElBuenSabor.ProyectoFinal.Service;

import ElBuenSabor.ProyectoFinal.DTO.*;
import ElBuenSabor.ProyectoFinal.Entities.Domicilio;
import ElBuenSabor.ProyectoFinal.Entities.Localidad;
import ElBuenSabor.ProyectoFinal.Entities.Pais;
import ElBuenSabor.ProyectoFinal.Entities.Provincia;
import ElBuenSabor.ProyectoFinal.Repositories.DomicilioRepository;
import ElBuenSabor.ProyectoFinal.Repositories.LocalidadRepository;
import ElBuenSabor.ProyectoFinal.Repositories.PaisRepository;
import ElBuenSabor.ProyectoFinal.Repositories.ProvinciaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DomicilioServiceImpl extends BaseServiceImpl<Domicilio, Long> implements DomicilioService {

    private final DomicilioRepository domicilioRepository;
    private final LocalidadRepository localidadRepository;
    private final ProvinciaRepository provinciaRepository;
    private final PaisRepository paisRepository;

    @Autowired
    public DomicilioServiceImpl(DomicilioRepository domicilioRepository,
                                LocalidadRepository localidadRepository,
                                ProvinciaRepository provinciaRepository,
                                PaisRepository paisRepository) {
        super(domicilioRepository);
        this.domicilioRepository = domicilioRepository;
        this.localidadRepository = localidadRepository;
        this.provinciaRepository = provinciaRepository;
        this.paisRepository = paisRepository;
    }

    @Override
    @Transactional
    public DomicilioDTO createDomicilioIndependiente(DomicilioCreateUpdateDTO dto) throws Exception {
        try {
            if (dto.getCalle() == null || dto.getCalle().trim().isEmpty() ||
                    dto.getNumero() == null || dto.getCp() == null ||
                    dto.getLocalidadId() == null) { // En DomicilioCreateUpdateDTO ahora usamos localidadId
                throw new Exception("Todos los campos del domicilio son requeridos (calle, número, CP, localidadId).");
            }

            Localidad localidad = localidadRepository.findById(dto.getLocalidadId())
                    .orElseThrow(() -> new Exception("Localidad no encontrada con ID: " + dto.getLocalidadId()));

            // Opcional: Si DomicilioCreateUpdateDTO también incluyera nombres de Pais/Provincia/Localidad
            // para crearlos si no existen (similar a ClienteServiceImpl)
            // Esta implementación asume que la localidad ya existe.

            Domicilio domicilio = Domicilio.builder()
                    .calle(dto.getCalle())
                    .numero(dto.getNumero())
                    .cp(dto.getCp())
                    .localidad(localidad)
                    .build();

            return convertToDTO(domicilioRepository.save(domicilio));
        } catch (Exception e) {
            throw new Exception("Error al crear el domicilio: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    public DomicilioDTO updateDomicilioIndependiente(Long id, DomicilioCreateUpdateDTO dto) throws Exception {
        try {
            if (dto.getCalle() == null || dto.getCalle().trim().isEmpty() ||
                    dto.getNumero() == null || dto.getCp() == null ||
                    dto.getLocalidadId() == null) {
                throw new Exception("Todos los campos del domicilio son requeridos (calle, número, CP, localidadId).");
            }

            Domicilio domicilio = domicilioRepository.findById(id)
                    .orElseThrow(() -> new Exception("Domicilio no encontrado con ID: " + id));

            Localidad localidad = localidadRepository.findById(dto.getLocalidadId())
                    .orElseThrow(() -> new Exception("Localidad no encontrada con ID: " + dto.getLocalidadId()));

            domicilio.setCalle(dto.getCalle());
            domicilio.setNumero(dto.getNumero());
            domicilio.setCp(dto.getCp());
            domicilio.setLocalidad(localidad);

            return convertToDTO(domicilioRepository.save(domicilio));
        } catch (Exception e) {
            throw new Exception("Error al actualizar el domicilio: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public DomicilioDTO findDomicilioById(Long id) throws Exception {
        Domicilio domicilio = domicilioRepository.findById(id)
                .orElseThrow(() -> new Exception("Domicilio no encontrado con ID: " + id));
        return convertToDTO(domicilio);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DomicilioDTO> findAllDomicilios() throws Exception {
        return domicilioRepository.findAll().stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    private DomicilioDTO convertToDTO(Domicilio domicilio) {
        if (domicilio == null) return null;
        DomicilioDTO dto = new DomicilioDTO();
        dto.setId(domicilio.getId());
        dto.setCalle(domicilio.getCalle());
        dto.setNumero(domicilio.getNumero());
        dto.setCp(domicilio.getCp());
        // dto.setBaja(domicilio.isBaja()); // Si Domicilio tuviera campo 'baja'

        if (domicilio.getLocalidad() != null) {
            Localidad locEnt = domicilio.getLocalidad();
            LocalidadDTO locDto = new LocalidadDTO();
            locDto.setId(locEnt.getId());
            locDto.setNombre(locEnt.getNombre());
            locDto.setProvinciaId(locEnt.getProvincia() != null ? locEnt.getProvincia().getId() : null);

            if (locEnt.getProvincia() != null) {
                Provincia provEnt = locEnt.getProvincia();
                ProvinciaDTO provDto = new ProvinciaDTO();
                provDto.setId(provEnt.getId());
                provDto.setNombre(provEnt.getNombre());
                provDto.setPaisId(provEnt.getPais() != null ? provEnt.getPais().getId() : null);

                if (provEnt.getPais() != null) {
                    PaisDTO paisDto = new PaisDTO();
                    paisDto.setId(provEnt.getPais().getId());
                    paisDto.setNombre(provEnt.getPais().getNombre());
                    provDto.setPais(paisDto);
                }
                locDto.setProvincia(provDto);
            }
            dto.setLocalidad(locDto);
            dto.setLocalidadId(locEnt.getId());
        }
        return dto;
    }
}