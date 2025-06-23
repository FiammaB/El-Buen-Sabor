package ElBuenSabor.ProyectoFinal.Service;

import ElBuenSabor.ProyectoFinal.DTO.ClienteActualizacionDTO;
import ElBuenSabor.ProyectoFinal.DTO.ClienteRegistroDTO;
import ElBuenSabor.ProyectoFinal.DTO.LoginDTO;
import ElBuenSabor.ProyectoFinal.Entities.*; // Importa todas las entidades
import ElBuenSabor.ProyectoFinal.Repositories.*; // Importa todos los repositorios
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern; // Para validación de regex

@Service
public class ClienteServiceImpl extends BaseServiceImpl<Cliente, Long> implements ClienteService {

    private final ClienteRepository clienteRepository;
    private final DomicilioRepository domicilioRepository;
    private final LocalidadRepository localidadRepository;
    private final ProvinciaRepository provinciaRepository;
    private final PaisRepository paisRepository;
    private final PasswordEncoder passwordEncoder;
    private final ImagenRepository imagenRepository; // Añadido por si se gestionan imágenes de cliente

    // Regex para validación de contraseña y email (ajustar según sea necesario)
    // La contraseña deberá tener un mínimo de 8 (ocho) caracteres,
    // y tendrá que tener por lo menos una letra mayúscula, una letra minúscula y un símbolo.
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#$%^&+=_*\\-.])(?=\\S+$).{8,}$");
    // El sistema tendrá que verificar que la dirección de mail haya sido escrita de forma correcta.
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$");


    @Autowired
    public ClienteServiceImpl(ClienteRepository clienteRepository, DomicilioRepository domicilioRepository,
                              LocalidadRepository localidadRepository, ProvinciaRepository provinciaRepository,
                              PaisRepository paisRepository, PasswordEncoder passwordEncoder,//Error passwordEncoder: Could not autowire. No beans of 'PasswordEncoder' type found.(Habia que terminar de configurar SpringSecurity, por ahora hice un Bean en el main de PasswordEncoder
                              ImagenRepository imagenRepository) { // ImagenRepository inyectado
        super(clienteRepository);
        this.clienteRepository = clienteRepository;
        this.domicilioRepository = domicilioRepository;
        this.localidadRepository = localidadRepository;
        this.provinciaRepository = provinciaRepository;
        this.paisRepository = paisRepository;
        this.passwordEncoder = passwordEncoder;
        this.imagenRepository = imagenRepository; // Asignar
    }

    @Override
    @Transactional
    public Cliente registrarCliente(ClienteRegistroDTO registroDTO) throws Exception {
        // 1. Verificar que el email no exista
        if (clienteRepository.existsByEmail(registroDTO.getEmail())) {
            throw new Exception("El email ya está registrado.");
        }

        // 2. Verificar que las contraseñas coincidan
        if (!registroDTO.getPassword().equals(registroDTO.getConfirmPassword())) {
            throw new Exception("Las contraseñas no coinciden.");
        }

        // 3. Validar la fuerza de la contraseña
        if (!isValidPassword(registroDTO.getPassword())) {
            throw new Exception("La contraseña debe tener al menos 8 caracteres, una mayúscula, una minúscula, un número y un símbolo.");
        }

        // 4. Validar formato de email
        if (!isValidEmail(registroDTO.getEmail())) {
            throw new Exception("Formato de email inválido.");
        }

        try {
            Pais pais = paisRepository.findByNombre(registroDTO.getNombrePais());
            if (pais == null) {
                pais = Pais.builder().nombre(registroDTO.getNombrePais()).build();
                pais = paisRepository.save(pais);
            }

            Provincia provincia = provinciaRepository.findByNombre(registroDTO.getNombreProvincia());
            if (provincia == null) {
                provincia = Provincia.builder().nombre(registroDTO.getNombreProvincia()).pais(pais).build();
                provincia = provinciaRepository.save(provincia);
            }

            Localidad localidad = localidadRepository.findByNombre(registroDTO.getNombreLocalidad());
            if (localidad == null) {
                localidad = Localidad.builder().nombre(registroDTO.getNombreLocalidad()).provincia(provincia).build();
                localidad = localidadRepository.save(localidad);
            }

            Domicilio domicilio = Domicilio.builder()
                    .calle(registroDTO.getCalle())
                    .numero(registroDTO.getNumero())
                    .cp(registroDTO.getCp())
                    .localidad(localidad)
                    .build();
            domicilio = domicilioRepository.save(domicilio);

            Cliente cliente = Cliente.builder()
                    .nombre(registroDTO.getNombre())
                    .apellido(registroDTO.getApellido())
                    .telefono(registroDTO.getTelefono())
                    .email(registroDTO.getEmail())
                    .password(passwordEncoder.encode(registroDTO.getPassword())) // Encriptar contraseña
                    .estaDadoDeBaja(false) // Por defecto no dado de baja
                    .domicilios(new ArrayList<>()) // Inicializar la lista de domicilios
                    .fechaNacimiento(registroDTO.getFechaNacimiento()) // Asumiendo que lo añadiste al DTO
                    .build();

            // Imagen de cliente (opcional, si se maneja en el registro)
            if (registroDTO.getImagenId() != null) {
                Imagen imagenCliente = imagenRepository.findById(registroDTO.getImagenId())
                        .orElseThrow(() -> new Exception("Imagen de cliente no encontrada con ID: " + registroDTO.getImagenId()));
                cliente.setImagen(imagenCliente);
            }


            cliente.getDomicilios().add(domicilio);
            cliente.setUsername(registroDTO.getEmail()); // Usar email como username
            // cliente.setAuth0Id(registroDTO.getEmail()); // Opcional, si usas Auth0


            return clienteRepository.save(cliente);
        } catch (Exception e) {
            // Es buena práctica loggear el error aquí también
            throw new Exception("Error al registrar el cliente: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Cliente loginCliente(LoginDTO loginDTO) throws Exception {
        Cliente cliente = clienteRepository.findByEmail(loginDTO.getEmail());
        if (cliente == null) {
            throw new Exception("Credenciales inválidas. Email no encontrado.");
        }

        if (cliente.isEstaDadoDeBaja()) { //
            throw new Exception("El cliente está dado de baja y no puede acceder al sistema.");
        }

        if (!passwordEncoder.matches(loginDTO.getPassword(), cliente.getPassword())) { //
            throw new Exception("Credenciales inválidas. Contraseña incorrecta.");
        }
        return cliente;
    }

    @Override
    @Transactional
    public Cliente actualizarCliente(Long id, ClienteActualizacionDTO actualizacionDTO) throws Exception {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new Exception("Cliente no encontrado con ID: " + id));

        // Actualizar datos personales
        cliente.setNombre(actualizacionDTO.getNombre());
        cliente.setApellido(actualizacionDTO.getApellido());
        cliente.setTelefono(actualizacionDTO.getTelefono());
        if (actualizacionDTO.getFechaNacimiento() != null) {
            cliente.setFechaNacimiento(actualizacionDTO.getFechaNacimiento());
        }

        // Actualizar imagen (opcional)
        //if (actualizacionDTO.getImagenId() != null) {
        if (actualizacionDTO.getImagenId() != 0) {
            if(actualizacionDTO.getImagenId() == 0L) { // Convención para quitar la imagen
                cliente.setImagen(null);
            } else {
                Imagen imagenCliente = imagenRepository.findById(actualizacionDTO.getImagenId())
                        .orElseThrow(() -> new Exception("Imagen de cliente no encontrada con ID: " + actualizacionDTO.getImagenId()));
                cliente.setImagen(imagenCliente);
            }
        }


        // Actualizar domicilio (asumiendo un solo domicilio principal por simplicidad o actualizando el primero)
        // Una lógica más compleja podría permitir elegir cuál domicilio actualizar o añadir nuevos.
        if (actualizacionDTO.getCalle() != null && !actualizacionDTO.getCalle().isEmpty()) {
            Domicilio domicilio;
            if (cliente.getDomicilios() == null || cliente.getDomicilios().isEmpty()) {
                domicilio = new Domicilio();
                if(cliente.getDomicilios() == null) cliente.setDomicilios(new ArrayList<>());
                cliente.getDomicilios().add(domicilio);
            } else {
                domicilio = cliente.getDomicilios().get(0); // Actualiza el primer domicilio
            }

            Pais pais = paisRepository.findByNombre(actualizacionDTO.getNombrePais());
            if (pais == null) {
                pais = Pais.builder().nombre(actualizacionDTO.getNombrePais()).build();
                pais = paisRepository.save(pais);
            }

            Provincia provincia = provinciaRepository.findByNombre(actualizacionDTO.getNombreProvincia());
            if (provincia == null) {
                provincia = Provincia.builder().nombre(actualizacionDTO.getNombreProvincia()).pais(pais).build();
                provincia = provinciaRepository.save(provincia);
            }

            Localidad localidad = localidadRepository.findByNombre(actualizacionDTO.getNombreLocalidad());
            if (localidad == null) {
                localidad = Localidad.builder().nombre(actualizacionDTO.getNombreLocalidad()).provincia(provincia).build();
                localidad = localidadRepository.save(localidad);
            }

            domicilio.setCalle(actualizacionDTO.getCalle());
            domicilio.setNumero(actualizacionDTO.getNumero());
            domicilio.setCp(actualizacionDTO.getCp());
            domicilio.setLocalidad(localidad);
            domicilioRepository.save(domicilio); // Guardar o actualizar el domicilio
        }

        // Actualizar contraseña
        if (actualizacionDTO.getNewPassword() != null && !actualizacionDTO.getNewPassword().isEmpty()) {
            if (actualizacionDTO.getCurrentPassword() == null || !passwordEncoder.matches(actualizacionDTO.getCurrentPassword(), cliente.getPassword())) {
                throw new Exception("La contraseña actual ingresada es incorrecta.");
            }
            if (!actualizacionDTO.getNewPassword().equals(actualizacionDTO.getConfirmNewPassword())) { //
                throw new Exception("Las nuevas contraseñas no coinciden.");
            }
            if (!isValidPassword(actualizacionDTO.getNewPassword())) { //
                throw new Exception("La nueva contraseña debe tener al menos 8 caracteres, una mayúscula, una minúscula, un número y un símbolo.");
            }
            cliente.setPassword(passwordEncoder.encode(actualizacionDTO.getNewPassword())); //
        }

        return clienteRepository.save(cliente);
    }

    @Override
    @Transactional
    public void darBajaCliente(Long id) throws Exception { //
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new Exception("Cliente no encontrado con ID: " + id));
        cliente.setEstaDadoDeBaja(true);
        clienteRepository.save(cliente);
    }

    @Override
    @Transactional
    public void darAltaCliente(Long id) throws Exception {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new Exception("Cliente no encontrado con ID: " + id));
        cliente.setEstaDadoDeBaja(false);
        clienteRepository.save(cliente);
    }

    // Métodos de validación privados
    private boolean isValidPassword(String password) { //
        if (password == null) return false;
        // Ajustado para incluir un número como comúnmente se requiere.
        // La regex original: ".*[!@#<span class="math-inline">%^&\*\(\)\_\+\\\\\-\=\\\\\[\\\\\]\{\};'\:\\",\.<\>/?\]\.\*"\); // Al menos un símbolo
        // La he simplificado a un conjunto común de símbolos.
        return PASSWORD_PATTERN.matcher(password).matches();
    }

    private boolean isValidEmail(String email) { //
        if (email == null) return false;
        return EMAIL_PATTERN.matcher(email).matches();
    }

    @Override
    public List<Cliente> findAll() throws Exception {
        return List.of();
    }

    @Override
    public Optional<Cliente> findById(Long aLong) throws Exception {
        return Optional.empty();
    }
}