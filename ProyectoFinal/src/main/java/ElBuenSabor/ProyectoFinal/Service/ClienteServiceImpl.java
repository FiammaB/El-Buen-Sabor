package ElBuenSabor.ProyectoFinal.Service;

import ElBuenSabor.ProyectoFinal.DTO.ClienteActualizacionDTO;
import ElBuenSabor.ProyectoFinal.DTO.ClienteRegistroDTO;
import ElBuenSabor.ProyectoFinal.DTO.LoginDTO;
import ElBuenSabor.ProyectoFinal.Entities.*;
import ElBuenSabor.ProyectoFinal.Repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.regex.Pattern;

@Service
public class ClienteServiceImpl extends BaseServiceImpl<Cliente, Long> implements ClienteService {

    private final ClienteRepository clienteRepository;
    private final DomicilioRepository domicilioRepository;
    private final LocalidadRepository localidadRepository;
    private final ProvinciaRepository provinciaRepository;
    private final PaisRepository paisRepository;
    private final PasswordEncoder passwordEncoder;
    private final ImagenRepository imagenRepository;

    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#$%^&+=_*\\-.])(?=\\S+$).{8,}$");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$");

    @Autowired
    public ClienteServiceImpl(ClienteRepository clienteRepository, DomicilioRepository domicilioRepository,
                              LocalidadRepository localidadRepository, ProvinciaRepository provinciaRepository,
                              PaisRepository paisRepository, PasswordEncoder passwordEncoder,
                              ImagenRepository imagenRepository) {
        super(clienteRepository);
        this.clienteRepository = clienteRepository;
        this.domicilioRepository = domicilioRepository;
        this.localidadRepository = localidadRepository;
        this.provinciaRepository = provinciaRepository;
        this.paisRepository = paisRepository;
        this.passwordEncoder = passwordEncoder;
        this.imagenRepository = imagenRepository;
    }

    @Override
    @Transactional
    public Cliente registrarCliente(ClienteRegistroDTO registroDTO) throws Exception {
        if (clienteRepository.existsByEmail(registroDTO.getEmail())) {
            throw new Exception("El email ya está registrado.");
        }

        if (!registroDTO.getPassword().equals(registroDTO.getConfirmPassword())) {
            throw new Exception("Las contraseñas no coinciden.");
        }

        if (!isValidPassword(registroDTO.getPassword())) {
            throw new Exception("La contraseña debe tener al menos 8 caracteres, una mayúscula, una minúscula, un número y un símbolo.");
        }

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
                    .password(passwordEncoder.encode(registroDTO.getPassword()))
                    .estaDadoDeBaja(false)
                    .domicilios(new ArrayList<>())
                    .fechaNacimiento(registroDTO.getFechaNacimiento())
                    .build();

            if (registroDTO.getImagenId() != null) {
                Imagen imagenCliente = imagenRepository.findById(registroDTO.getImagenId())
                        .orElseThrow(() -> new Exception("Imagen de cliente no encontrada con ID: " + registroDTO.getImagenId()));
                cliente.setImagen(imagenCliente);
            }

            cliente.getDomicilios().add(domicilio);
            cliente.setUsername(registroDTO.getUsername());
            cliente.setRol(Rol.CLIENTE);

            return clienteRepository.save(cliente);
        } catch (Exception e) {
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

        if (cliente.isEstaDadoDeBaja()) {
            throw new Exception("El cliente está dado de baja y no puede acceder al sistema.");
        }

        if (!passwordEncoder.matches(loginDTO.getPassword(), cliente.getPassword())) {
            throw new Exception("Credenciales inválidas. Contraseña incorrecta.");
        }
        return cliente;
    }

    @Override
    public Cliente actualizarCliente(Long id, ClienteActualizacionDTO actualizacionDTO) throws Exception {
        // Implementación futura si hace falta
        return null;
    }

    @Override
    public void darBajaCliente(Long id) throws Exception {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new Exception("Cliente no encontrado con ID: " + id));
        cliente.setEstaDadoDeBaja(true);
        clienteRepository.save(cliente);
    }

    @Override
    public void darAltaCliente(Long id) throws Exception {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new Exception("Cliente no encontrado con ID: " + id));
        cliente.setEstaDadoDeBaja(false);
        clienteRepository.save(cliente);
    }

    private boolean isValidPassword(String password) {
        return password != null && PASSWORD_PATTERN.matcher(password).matches();
    }

    private boolean isValidEmail(String email) {
        return email != null && EMAIL_PATTERN.matcher(email).matches();
    }
}
