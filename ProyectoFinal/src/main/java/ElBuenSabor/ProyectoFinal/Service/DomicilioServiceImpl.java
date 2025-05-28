package ElBuenSabor.ProyectoFinal.Service;

import ElBuenSabor.ProyectoFinal.DTO.*; // Importar todos los DTOs necesarios
import ElBuenSabor.ProyectoFinal.Entities.Domicilio;
import ElBuenSabor.ProyectoFinal.Entities.Localidad;
import ElBuenSabor.ProyectoFinal.Entities.Pais;
import ElBuenSabor.ProyectoFinal.Entities.Provincia;
import ElBuenSabor.ProyectoFinal.Repositories.*; // Importar todos los repositorios necesarios
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class DomicilioServiceImpl extends BaseServiceImpl<Domicilio, Long> implements DomicilioService {

    private final DomicilioRepository domicilioRepository;
    private final LocalidadRepository localidadRepository;
    private final ProvinciaRepository provinciaRepository; // Para lógica de creación de localidad si es necesario
    private final PaisRepository paisRepository;         // Para lógica de creación de localidad si es necesario
    private final PedidoRepository pedidoRepository;       // Para isDomicilioInActiveUse
    private final ClienteRepository clienteRepository;     // Para isDomicilioInActiveUse (si un cliente tiene este domicilio como principal y está activo)
    private final SucursalRepository sucursalRepository;   // Para isDomicilioInActiveUse (si una sucursal usa este domicilio y está activa)


    @Autowired
    public DomicilioServiceImpl(DomicilioRepository domicilioRepository,
                                LocalidadRepository localidadRepository,
                                ProvinciaRepository provinciaRepository,
                                PaisRepository paisRepository,
                                PedidoRepository pedidoRepository,
                                ClienteRepository clienteRepository,
                                SucursalRepository sucursalRepository) {
        super(domicilioRepository);
        this.domicilioRepository = domicilioRepository;
        this.localidadRepository = localidadRepository;
        this.provinciaRepository = provinciaRepository;
        this.paisRepository = paisRepository;
        this.pedidoRepository = pedidoRepository;
        this.clienteRepository = clienteRepository;
        this.sucursalRepository = sucursalRepository;
    }

    @Override
    @Transactional
    public DomicilioDTO createDomicilio(DomicilioCreateUpdateDTO dto) throws Exception {
        // Para creación independiente, se asume que la localidad (y su jerarquía) ya existe
        // y se referencia por localidadId.
        if (dto.getLocalidadId() == null) {
            throw new Exception("El ID de la localidad es requerido para crear un domicilio.");
        }
        Localidad localidad = localidadRepository.findById(dto.getLocalidadId()) // findById ya filtra por activas
                .orElseThrow(() -> new Exception("Localidad activa no encontrada con ID: " + dto.getLocalidadId()));

        // Si se quiere permitir la creación de Localidad/Provincia/Pais por nombre desde aquí:
        // (Esta lógica es más compleja y similar a la de ClienteServiceImpl/SucursalServiceImpl)
        // if (dto.getNombreLocalidad() != null && !dto.getNombreLocalidad().isEmpty()) {
        //     Pais pais = paisRepository.findByNombre(dto.getNombrePais());
        //     if (pais == null) { pais = paisRepository.save(Pais.builder().nombre(dto.getNombrePais()).build()); }
        //     Provincia provincia = provinciaRepository.findByNombre(dto.getNombreProvincia());
        //     if (provincia == null) { provincia = provinciaRepository.save(Provincia.builder().nombre(dto.getNombreProvincia()).pais(pais).build()); }
        //     Localidad locExistente = localidadRepository.findByNombreAndProvincia(dto.getNombreLocalidad(), provincia);
        //     if (locExistente == null) { localidad = localidadRepository.save(Localidad.builder().nombre(dto.getNombreLocalidad()).provincia(provincia).build());}
        //     else {localidad = locExistente;}
        // }


        Domicilio domicilio = new Domicilio();
        domicilio.setCalle(dto.getCalle());
        domicilio.setNumero(dto.getNumero());
        domicilio.setCp(dto.getCp());
        domicilio.setLocalidad(localidad);
        return convertToDTO(domicilioRepository.save(domicilio));
    }

    @Override
    @Transactional
    public DomicilioDTO updateDomicilio(Long id, DomicilioCreateUpdateDTO dto) throws Exception {
        Domicilio domicilio = this.findByIdIncludingDeleted(id) // Permite actualizar incluso si está 'baja'
                .orElseThrow(() -> new Exception("Domicilio no encontrado con ID: " + id + " para actualizar."));

        if (dto.getLocalidadId() == null) {
            throw new Exception("El ID de la localidad es requerido para actualizar un domicilio.");
        }
        Localidad localidad = localidadRepository.findById(dto.getLocalidadId())
                .orElseThrow(() -> new Exception("Localidad activa no encontrada con ID: " + dto.getLocalidadId()));

        domicilio.setCalle(dto.getCalle());
        domicilio.setNumero(dto.getNumero());
        domicilio.setCp(dto.getCp());
        domicilio.setLocalidad(localidad);
        // Si el DTO permitiera cambiar 'baja':
        // if (dto.getBaja() != null) domicilio.setBaja(dto.isBaja());
        return convertToDTO(domicilioRepository.save(domicilio));
    }

    @Override
    @Transactional(readOnly = true)
    public DomicilioDTO findDomicilioById(Long id) throws Exception {
        return convertToDTO(super.findById(id).orElse(null));
    }

    @Override
    @Transactional(readOnly = true)
    public List<DomicilioDTO> findAllDomicilios() throws Exception {
        return super.findAll().stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<DomicilioDTO> findByLocalidadId(Long localidadId) throws Exception {
        if (!localidadRepository.existsById(localidadId)) { // Verifica localidad activa
            throw new Exception("Localidad activa no encontrada con ID: " + localidadId);
        }
        // domicilioRepository.findByLocalidadIdAndBajaFalse(localidadId) ya filtra por Domicilio.baja=false
        return domicilioRepository.findByLocalidadIdAndBajaFalse(localidadId).stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    // --- Implementación de métodos de BaseService ---
    @Override
    @Transactional(readOnly = true)
    public List<Domicilio> findAllIncludingDeleted() throws Exception {
        return domicilioRepository.findAllRaw();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Domicilio> findByIdIncludingDeleted(Long id) throws Exception {
        return domicilioRepository.findByIdRaw(id);
    }

    @Override
    @Transactional
    public Domicilio softDelete(Long id) throws Exception {
        Domicilio domicilio = this.findByIdIncludingDeleted(id)
                .orElseThrow(() -> new Exception("Domicilio no encontrado con ID: " + id + " para dar de baja."));
        if (domicilio.isBaja()) {
            throw new Exception("El domicilio ya está dado de baja.");
        }
        if (isDomicilioInActiveUse(id)) {
            throw new Exception("No se puede dar de baja el domicilio porque está en uso activo por pedidos, clientes o sucursales.");
        }
        domicilio.setBaja(true);
        return domicilioRepository.save(domicilio);
    }

    @Override
    @Transactional
    public Domicilio reactivate(Long id) throws Exception {
        Domicilio domicilio = this.findByIdIncludingDeleted(id)
                .orElseThrow(() -> new Exception("Domicilio no encontrado con ID: " + id + " para reactivar."));
        if (!domicilio.isBaja()) {
            throw new Exception("El domicilio no está dado de baja, no se puede reactivar.");
        }
        // Asegurar que la localidad del domicilio esté activa
        if (domicilio.getLocalidad() == null || domicilio.getLocalidad().isBaja()) {
            throw new Exception("No se puede reactivar el domicilio porque su localidad asociada no está activa o no existe.");
        }
        domicilio.setBaja(false);
        return domicilioRepository.save(domicilio);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isDomicilioInActiveUse(Long domicilioId) throws Exception {
        // Verificar si hay pedidos activos con este domicilio de entrega
        if (pedidoRepository.existsActivePedidoWithDomicilio(domicilioId)) { // Necesitas este método en PedidoRepository
            return true;
        }
        // Verificar si hay clientes activos que tengan este domicilio
        // Esto es más complejo si un cliente tiene una lista de domicilios.
        // Necesitarías una query en ClienteRepository: existsByDomiciliosIdAndBajaFalse(domicilioId)
        // if (clienteRepository.existsByAnyDomicilioIdAndBajaFalse(domicilioId)) { return true; }

        // Verificar si hay sucursales activas que usen este domicilio
        // Necesitarías una query en SucursalRepository: existsByDomicilioIdAndBajaFalse(domicilioId)
        // if (sucursalRepository.existsByDomicilioIdAndBajaFalse(domicilioId)) { return true; }

        return false; // Simplificado por ahora, expandir con queries en otros repositorios
    }

    // --- Implementación de métodos de DomicilioService que devuelven DTO ---
    @Override
    @Transactional(readOnly = true)
    public List<DomicilioDTO> findAllDomiciliosIncludingDeleted() throws Exception {
        return this.findAllIncludingDeleted().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public DomicilioDTO findDomicilioByIdIncludingDeleted(Long id) throws Exception {
        return convertToDTO(this.findByIdIncludingDeleted(id).orElse(null));
    }

    private DomicilioDTO convertToDTO(Domicilio domicilio) {
        if (domicilio == null) return null;
        DomicilioDTO dto = new DomicilioDTO();
        dto.setId(domicilio.getId());
        dto.setCalle(domicilio.getCalle());
        dto.setNumero(domicilio.getNumero());
        dto.setCp(domicilio.getCp());
        dto.setBaja(domicilio.isBaja());

        if (domicilio.getLocalidad() != null) {
            Localidad locEnt = domicilio.getLocalidad();
            LocalidadDTO locDto = new LocalidadDTO();
            locDto.setId(locEnt.getId());
            locDto.setNombre(locEnt.getNombre());
            locDto.setBaja(locEnt.isBaja());

            if (locEnt.getProvincia() != null) {
                Provincia provEnt = locEnt.getProvincia();
                ProvinciaDTO provDto = new ProvinciaDTO();
                provDto.setId(provEnt.getId());
                provDto.setNombre(provEnt.getNombre());
                provDto.setBaja(provEnt.isBaja());

                if (provEnt.getPais() != null) {
                    Pais paisEnt = provEnt.getPais();
                    PaisDTO paisDto = new PaisDTO();
                    paisDto.setId(paisEnt.getId());
                    paisDto.setNombre(paisEnt.getNombre());
                    paisDto.setBaja(paisEnt.isBaja());
                    provDto.setPais(paisDto);
                }
                locDto.setProvincia(provDto);
            }
            dto.setLocalidad(locDto);
        }
        return dto;
    }
}
