package ElBuenSabor.ProyectoFinal.Service;

import ElBuenSabor.ProyectoFinal.DTO.*;
import ElBuenSabor.ProyectoFinal.Entities.*;
import ElBuenSabor.ProyectoFinal.Repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class ClienteServiceImpl extends BaseServiceImpl<Cliente, Long> implements ClienteService {

    private final ClienteRepository clienteRepository;
    private final DomicilioRepository domicilioRepository;
    private final LocalidadRepository localidadRepository;
    private final ProvinciaRepository provinciaRepository;
    private final PaisRepository paisRepository;
    private final PasswordEncoder passwordEncoder;
    private final ImagenRepository imagenRepository;
    private final PedidoRepository pedidoRepository; // Para verificar pedidos activos

    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#$%^&+=_*\\-.])(?=\\S+$).{8,}$");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$");

    @Autowired
    public ClienteServiceImpl(ClienteRepository clienteRepository, DomicilioRepository domicilioRepository,
                              LocalidadRepository localidadRepository, ProvinciaRepository provinciaRepository,
                              PaisRepository paisRepository, PasswordEncoder passwordEncoder,
                              ImagenRepository imagenRepository, PedidoRepository pedidoRepository) {
        super(clienteRepository);
        this.clienteRepository = clienteRepository;
        this.domicilioRepository = domicilioRepository;
        this.localidadRepository = localidadRepository;
        this.provinciaRepository = provinciaRepository;
        this.paisRepository = paisRepository;
        this.passwordEncoder = passwordEncoder;
        this.imagenRepository = imagenRepository;
        this.pedidoRepository = pedidoRepository;
    }

    @Override
    @Transactional
    public ClienteResponseDTO registrarCliente(ClienteRegistroDTO dto) throws Exception {
        // HU#39: Verificar que no exista un usuario con su email (incluyendo los 'baja')
        if (clienteRepository.existsByEmailRaw(dto.getEmail())) {
            throw new Exception("El email ya está registrado.");
        }
        // HU#38: Validar formato de email
        if (!EMAIL_PATTERN.matcher(dto.getEmail()).matches()) {
            throw new Exception("Formato de email inválido.");
        }
        // HU#37: Verificar que las dos contraseñas ingresadas sean exactamente iguales.
        if (!dto.getPassword().equals(dto.getConfirmPassword())) {
            throw new Exception("Las contraseñas no coinciden.");
        }
        // HU#36: Validar fortaleza de la contraseña
        if (!PASSWORD_PATTERN.matcher(dto.getPassword()).matches()) {
            throw new Exception("La contraseña debe tener al menos 8 caracteres, una mayúscula, una minúscula, un número y un símbolo.");
        }

        // Crear Domicilio (HU#35)
        Pais pais = findOrCreatePais(dto.getNombrePais());
        Provincia provincia = findOrCreateProvincia(dto.getNombreProvincia(), pais);
        Localidad localidad = findOrCreateLocalidad(dto.getNombreLocalidad(), provincia);

        Domicilio domicilio = Domicilio.builder()
                .calle(dto.getCalle())
                .numero(dto.getNumero())
                .cp(dto.getCp())
                .localidad(localidad)
                .build();
        // No guardar domicilio aquí si se va a asociar al cliente y guardar por cascada.

        Cliente cliente = new Cliente();
        cliente.setNombre(dto.getNombre());
        cliente.setApellido(dto.getApellido());
        cliente.setTelefono(dto.getTelefono());
        cliente.setEmail(dto.getEmail());
        cliente.setUsername(dto.getEmail()); // HU#5: email como username
        cliente.setPassword(passwordEncoder.encode(dto.getPassword())); // HU#40: contraseña encriptada
        cliente.setFechaNacimiento(dto.getFechaNacimiento());
        cliente.setBaja(false); // HU#41: Por defecto activo
        // Rol es implícitamente CLIENTE (HU#41)

        if (dto.getImagenId() != null) {
            Imagen img = imagenRepository.findById(dto.getImagenId())
                    .orElseThrow(() -> new Exception("Imagen activa no encontrada con ID: " + dto.getImagenId()));
            cliente.setImagen(img);
        }

        // Asociar domicilio
        // Si Cliente es dueño de la relación con @JoinColumn en Cliente.domicilios,
        // la FK cliente_id estará en la tabla Domicilio.
        // Guardar cliente primero para tener su ID, luego setear el cliente_id en domicilio.
        // O, si la cascada está bien y Domicilio tiene ManyToOne a Cliente:
        // cliente.getDomicilios().add(domicilio);
        // domicilio.setCliente(cliente); // Si la relación es bidireccional y Domicilio tiene el campo.
        // Con @JoinColumn en Cliente.domicilios, el cliente_id se setea en la tabla domicilio.
        // Es más simple guardar el cliente primero, luego el domicilio con el cliente_id,
        // o guardar el domicilio y luego añadirlo al cliente y guardar el cliente.
        // Con CascadeType.ALL en Cliente.domicilios, al guardar cliente, se guarda el domicilio.
        cliente.getDomicilios().add(domicilio); // Añadir a la colección gestionada por Cliente

        Cliente savedCliente = clienteRepository.save(cliente); // Esto guardará el cliente y el domicilio por cascada
        return convertToDTO(savedCliente);
    }

    private Pais findOrCreatePais(String nombre) {
        Optional<Pais> opt = paisRepository.findByNombreRaw(nombre);
        return opt.orElseGet(() -> paisRepository.save(Pais.builder().nombre(nombre).build()));
    }
    private Provincia findOrCreateProvincia(String nombre, Pais pais) {
        // Asumiendo que ProvinciaRepository tiene findByNombreAndPaisRaw
        // Optional<Provincia> opt = provinciaRepository.findByNombreAndPaisRaw(nombre, pais);
        // Si no, filtrar:
        Optional<Provincia> opt = provinciaRepository.findAllRaw().stream()
                .filter(p -> p.getNombre().equalsIgnoreCase(nombre) && p.getPais().getId().equals(pais.getId()))
                .findFirst();
        return opt.orElseGet(() -> provinciaRepository.save(Provincia.builder().nombre(nombre).pais(pais).build()));
    }
    private Localidad findOrCreateLocalidad(String nombre, Provincia provincia) {
        // Asumiendo que LocalidadRepository tiene findByNombreAndProvinciaRaw
        Optional<Localidad> opt = localidadRepository.findByNombreAndProvinciaRaw(nombre, provincia);
        return opt.orElseGet(() -> localidadRepository.save(Localidad.builder().nombre(nombre).provincia(provincia).build()));
    }


    @Override
    @Transactional(readOnly = true)
    public ClienteResponseDTO loginCliente(LoginDTO loginDTO) throws Exception {
        // HU#44: Usar email como username
        Cliente cliente = clienteRepository.findByEmailRaw(loginDTO.getEmail())
                .orElseThrow(() -> new Exception("Credenciales inválidas. Email no encontrado."));

        // HU#46: Si el cliente está dado de baja
        if (cliente.isBaja()) {
            throw new Exception("El cliente está dado de baja y no puede realizar pedidos.");
        }
        // HU#44: Verificar contraseña
        if (!passwordEncoder.matches(loginDTO.getPassword(), cliente.getPassword())) {
            throw new Exception("Credenciales inválidas. Contraseña incorrecta.");
        }
        return convertToDTO(cliente);
    }

    @Override
    @Transactional
    public ClienteResponseDTO actualizarCliente(Long id, ClienteActualizacionDTO dto, Usuario actor) throws Exception {
        Cliente clienteAActualizar = clienteRepository.findByIdRaw(id)
                .orElseThrow(() -> new Exception("Cliente no encontrado con ID: " + id + " para actualizar."));

        boolean esAdmin = (actor instanceof Empleado && ((Empleado) actor).getRol() == Rol.ADMINISTRADOR);
        boolean esElMismoCliente = actor != null && actor.getId().equals(clienteAActualizar.getId()) && actor instanceof Cliente;

        if (!esAdmin && !esElMismoCliente) {
            throw new Exception("No tiene permisos para actualizar este perfil de cliente.");
        }

        // HU#50, HU#151 (Admin modifica cliente)
        clienteAActualizar.setNombre(dto.getNombre());
        clienteAActualizar.setApellido(dto.getApellido());
        clienteAActualizar.setTelefono(dto.getTelefono());
        clienteAActualizar.setFechaNacimiento(dto.getFechaNacimiento());

        // HU#51: Cambio de contraseña
        if (dto.getNewPassword() != null && !dto.getNewPassword().isEmpty()) {
            if (esElMismoCliente) { // Cliente cambiando su propia contraseña
                if (dto.getCurrentPassword() == null || !passwordEncoder.matches(dto.getCurrentPassword(), clienteAActualizar.getPassword())) {
                    throw new Exception("La contraseña actual ingresada es incorrecta.");
                }
            } // Admin puede cambiarla sin la actual (si esa es la regla de negocio)

            if (!dto.getNewPassword().equals(dto.getConfirmNewPassword())) {
                throw new Exception("Las nuevas contraseñas no coinciden.");
            }
            if (!PASSWORD_PATTERN.matcher(dto.getNewPassword()).matches()) {
                throw new Exception("La nueva contraseña debe cumplir los requisitos de fortaleza.");
            }
            clienteAActualizar.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        }

        // Actualizar imagen
        if (dto.getImagenId() != null) {//Operator '!=' cannot be applied to 'long', 'null'(ln 191)*//
            if (dto.getImagenId() == 0L) { clienteAActualizar.setImagen(null); }
            else {
                Imagen img = imagenRepository.findById(dto.getImagenId())
                        .orElseThrow(() -> new Exception("Imagen activa no encontrada con ID: " + dto.getImagenId()));
                clienteAActualizar.setImagen(img);
            }
        }

        // HU#50: Actualizar domicilio (asumiendo que se actualiza el primero o uno específico)
        // La lógica actual de ClienteActualizacionDTO es para un solo domicilio.
        if (dto.getCalle() != null && !dto.getCalle().isEmpty()) {
            Domicilio domPrincipal = null;
            if (clienteAActualizar.getDomicilios() != null && !clienteAActualizar.getDomicilios().isEmpty()) {
                // Actualizar el primer domicilio activo, o el primero si todos están de baja
                domPrincipal = clienteAActualizar.getDomicilios().stream().filter(d -> !d.isBaja()).findFirst()
                        .orElse(clienteAActualizar.getDomicilios().get(0));
            } else { // Si no tiene domicilios, crear uno nuevo
                domPrincipal = new Domicilio();
                domPrincipal.setBaja(false);
                if (clienteAActualizar.getDomicilios() == null) clienteAActualizar.setDomicilios(new ArrayList<>());
                clienteAActualizar.getDomicilios().add(domPrincipal);
                // Si la FK cliente_id está en Domicilio y no se maneja por @JoinColumn en Cliente,
                // se necesitaría setear domPrincipal.setCliente(clienteAActualizar) o similar.
                // Con @JoinColumn en Cliente.domicilios, la asociación se maneja al guardar Cliente.
            }

            Pais pais = findOrCreatePais(dto.getNombrePais());
            Provincia provincia = findOrCreateProvincia(dto.getNombreProvincia(), pais);
            Localidad localidad = findOrCreateLocalidad(dto.getNombreLocalidad(), provincia);

            domPrincipal.setCalle(dto.getCalle());
            domPrincipal.setNumero(dto.getNumero());
            domPrincipal.setCp(dto.getCp());
            domPrincipal.setLocalidad(localidad);
            // No es necesario guardar el domicilio por separado si CascadeType.ALL está en Cliente.domicilios
        }

        // HU#151: Admin puede dar de baja/alta
        if (esAdmin && dto.getBaja() != null && dto.getBaja() != clienteAActualizar.isBaja()) {//Cannot resolve method 'getBaja' in 'ClienteActualizacionDTO'(ln 230)*//
            if (dto.getBaja()) { // Dar de baja
                this.softDelete(id); // Llama al softDelete que verifica pedidos activos
            } else { // Dar de alta
                this.reactivate(id);
            }
        }
        // El save del cliente al final actualizará todo por cascada si está configurado.
        return convertToDTO(clienteRepository.save(clienteAActualizar));
    }

    @Override
    @Transactional(readOnly = true)
    public ClienteResponseDTO findClienteByIdDTO(Long id) throws Exception {
        return convertToDTO(super.findById(id).orElse(null));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClienteResponseDTO> findAllClientesDTO() throws Exception {
        return super.findAll().stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Cliente> findByEmailRaw(String email) throws Exception {
        return clienteRepository.findByEmailRaw(email);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByEmailRaw(String email) throws Exception {
        return clienteRepository.existsByEmailRaw(email);
    }

    // --- Implementación de métodos de BaseService ---
    @Override
    @Transactional(readOnly = true)
    public List<Cliente> findAllIncludingDeleted() throws Exception {
        return clienteRepository.findAllRaw();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Cliente> findByIdIncludingDeleted(Long id) throws Exception {
        return clienteRepository.findByIdRaw(id);
    }

    @Override
    @Transactional
    public Cliente softDelete(Long id) throws Exception {
        Cliente cliente = this.findByIdIncludingDeleted(id)
                .orElseThrow(() -> new Exception("Cliente no encontrado con ID: " + id + " para dar de baja."));
        if (cliente.isBaja()) {
            throw new Exception("El cliente ya está dado de baja.");
        }
        // Lógica de negocio: No dar de baja si tiene pedidos activos no finalizados.
        if (pedidoRepository.existsActivePedidoWithCliente(id)) { // Cannot resolve method 'existsActivePedidoWithCliente' in 'PedidoRepository' (ln287)*//
            throw new Exception("No se puede dar de baja al cliente porque tiene pedidos activos no finalizados.");
        }
        cliente.setBaja(true);
        return clienteRepository.save(cliente);
    }

    @Override
    @Transactional
    public Cliente reactivate(Long id) throws Exception {
        Cliente cliente = this.findByIdIncludingDeleted(id)
                .orElseThrow(() -> new Exception("Cliente no encontrado con ID: " + id + " para reactivar."));
        if (!cliente.isBaja()) {
            throw new Exception("El cliente no está dado de baja, no se puede reactivar.");
        }
        cliente.setBaja(false);
        return clienteRepository.save(cliente);
    }

    // --- Implementación de métodos de ClienteService que devuelven DTO ---
    @Override
    @Transactional(readOnly = true)
    public List<ClienteResponseDTO> findAllClientesIncludingDeleted() throws Exception {
        return this.findAllIncludingDeleted().stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ClienteResponseDTO findClienteByIdIncludingDeletedDTO(Long id) throws Exception {
        return convertToDTO(this.findByIdIncludingDeleted(id).orElse(null));
    }

    private ClienteResponseDTO convertToDTO(Cliente cliente) {
        if (cliente == null) return null;
        ClienteResponseDTO dto = new ClienteResponseDTO();
        dto.setId(cliente.getId());
        dto.setNombre(cliente.getNombre());
        dto.setApellido(cliente.getApellido());
        dto.setTelefono(cliente.getTelefono());
        dto.setEmail(cliente.getEmail());
        dto.setUsername(cliente.getUsername());
        dto.setAuth0Id(cliente.getAuth0Id()); // Heredado de Usuario
        dto.setFechaNacimiento(cliente.getFechaNacimiento());
        dto.setBaja(cliente.isBaja());//Cannot resolve method 'setBaja' in 'ClienteResponseDTO' (ln 330)*//

        if (cliente.getImagen() != null) {
            ImagenDTO imgDto = new ImagenDTO();
            imgDto.setId(cliente.getImagen().getId());
            imgDto.setDenominacion(cliente.getImagen().getDenominacion());
            imgDto.setBaja(cliente.getImagen().isBaja());
            dto.setImagen(imgDto);
        }

        if (cliente.getDomicilios() != null) {
            dto.setDomicilios(cliente.getDomicilios().stream()
                    .filter(dom -> !dom.isBaja()) // Mostrar solo domicilios activos
                    .map(this::convertToSimpleDomicilioDTO) // Usar un DTO simple para domicilio
                    .collect(Collectors.toList()));
        } else {
            dto.setDomicilios(new ArrayList<>());
        }
        return dto;
    }

    private DomicilioDTO convertToSimpleDomicilioDTO(Domicilio domicilio) {
        if (domicilio == null) return null;
        DomicilioDTO dto = new DomicilioDTO();
        dto.setId(domicilio.getId());
        dto.setCalle(domicilio.getCalle());
        dto.setNumero(domicilio.getNumero());
        dto.setCp(domicilio.getCp());
        dto.setBaja(domicilio.isBaja());
        if (domicilio.getLocalidad() != null) {
            LocalidadDTO locDto = new LocalidadDTO();
            locDto.setId(domicilio.getLocalidad().getId());
            locDto.setNombre(domicilio.getLocalidad().getNombre());
            locDto.setBaja(domicilio.getLocalidad().isBaja());
            // Para simplificar, no incluimos Provincia/Pais aquí, pero podría hacerse
            dto.setLocalidad(locDto);
        }
        return dto;
    }
}
