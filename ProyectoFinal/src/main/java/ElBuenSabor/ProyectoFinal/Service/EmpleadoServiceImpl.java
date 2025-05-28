package ElBuenSabor.ProyectoFinal.Service;

import ElBuenSabor.ProyectoFinal.DTO.*; // Asegúrate que DomicilioDTO, ImagenDTO, etc., estén aquí
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
public class EmpleadoServiceImpl extends BaseServiceImpl<Empleado, Long> implements EmpleadoService {

    private final EmpleadoRepository empleadoRepository;
    private final PasswordEncoder passwordEncoder;
    private final ImagenRepository imagenRepository;
    private final DomicilioRepository domicilioRepository;
    // Repositorios para la creación de domicilios si se incluye en el DTO (actualmente no)
    // private final LocalidadRepository localidadRepository;
    // private final ProvinciaRepository provinciaRepository;
    // private final PaisRepository paisRepository;

    // Mismas regex de ClienteServiceImpl, podrían ir a una clase utilitaria
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#$%^&+=_*\\-.])(?=\\S+$).{8,}$");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$");

    @Autowired
    public EmpleadoServiceImpl(EmpleadoRepository empleadoRepository,
                               PasswordEncoder passwordEncoder,
                               ImagenRepository imagenRepository,
                               DomicilioRepository domicilioRepository
                               /*, LocalidadRepository localidadRepository,
                               ProvinciaRepository provinciaRepository,
                               PaisRepository paisRepository */) {
        super(empleadoRepository);
        this.empleadoRepository = empleadoRepository;
        this.passwordEncoder = passwordEncoder;
        this.imagenRepository = imagenRepository;
        this.domicilioRepository = domicilioRepository; // Inyectar si se gestionan domicilios aquí
        // this.localidadRepository = localidadRepository;
        // this.provinciaRepository = provinciaRepository;
        // this.paisRepository = paisRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Empleado> findAllIncludingDeleted() throws Exception { // Implementa el de BaseService
        return empleadoRepository.findAllRaw();
    }

    @Override
    @Transactional
    public EmpleadoResponseDTO registrarEmpleado(EmpleadoCreateUpdateDTO dto) throws Exception {
        // HU#130: Validar que no exista empleado con el mismo email
        if (empleadoRepository.existsByEmailRaw(dto.getEmail())) {
            throw new Exception("El email '" + dto.getEmail() + "' ya está registrado para otro empleado.");
        }
        // HU#128: Validar formato de email
        if (!EMAIL_PATTERN.matcher(dto.getEmail()).matches()) {
            throw new Exception("Formato de email inválido.");
        }
        // HU#129: Verificar que las contraseñas coincidan
        if (dto.getPassword() == null || dto.getConfirmPassword() == null || !dto.getPassword().equals(dto.getConfirmPassword())) {
            throw new Exception("Las contraseñas provisorias no coinciden o están vacías.");
        }
        // HU#127: Validar fortaleza de la contraseña
        if (!PASSWORD_PATTERN.matcher(dto.getPassword()).matches()) {
            throw new Exception("La contraseña provisoria debe tener al menos 8 caracteres, una mayúscula, una minúscula, un número y un símbolo.");
        }
        if (dto.getRol() == null) { // Validación extra
            throw new Exception("El rol es obligatorio para registrar un empleado.");
        }


        Empleado empleado = new Empleado();
        empleado.setNombre(dto.getNombre());
        empleado.setApellido(dto.getApellido());
        empleado.setTelefono(dto.getTelefono());
        empleado.setEmail(dto.getEmail());
        empleado.setUsername(dto.getEmail()); // Usar email como username
        empleado.setPassword(passwordEncoder.encode(dto.getPassword())); // Contraseña provisoria
        empleado.setRol(dto.getRol());
        empleado.setFechaAlta(LocalDate.now());
        empleado.setFechaNacimiento(dto.getFechaNacimiento());
        empleado.setBaja(false); // Nuevo empleado está activo

        if (dto.getImagenId() != null) {
            Imagen img = imagenRepository.findById(dto.getImagenId()) // Busca imagen activa
                    .orElseThrow(() -> new Exception("Imagen activa no encontrada con ID: " + dto.getImagenId()));
            empleado.setImagen(img);
        }

        // Domicilios: Si EmpleadoCreateUpdateDTO tuviera un campo para domicilio, se manejaría aquí.
        // Por ahora, la entidad Empleado tiene una lista, pero el DTO no la gestiona en creación.

        return convertToDTO(empleadoRepository.save(empleado));
    }

    @Override
    @Transactional(readOnly = true)
    public EmpleadoResponseDTO loginEmpleado(LoginDTO loginDTO) throws Exception {
        Empleado empleado = empleadoRepository.findByEmailRaw(loginDTO.getEmail())
                .orElseThrow(() -> new Exception("Credenciales inválidas. Email de empleado no encontrado."));

        if (empleado.isBaja()) { // HU#142
            throw new Exception("El empleado está dado de baja y no puede acceder al sistema.");
        }

        if (!passwordEncoder.matches(loginDTO.getPassword(), empleado.getPassword())) {
            throw new Exception("Credenciales inválidas. Contraseña incorrecta.");
        }
        // Lógica para HU#137 (primer login, cambio de contraseña obligatorio)
        // podría manejarse devolviendo un flag en EmpleadoResponseDTO o un estado específico
        // que el frontend interprete para redirigir a la pantalla de cambio de contraseña.
        // Por ahora, el login es exitoso.
        return convertToDTO(empleado);
    }

    @Override
    @Transactional
    public EmpleadoResponseDTO actualizarPerfilEmpleado(Long empleadoId, EmpleadoCreateUpdateDTO dto, Usuario actor) throws Exception {
        Empleado empleadoAActualizar = empleadoRepository.findByIdRaw(empleadoId)
                .orElseThrow(() -> new Exception("Empleado no encontrado con ID: " + empleadoId + " para actualizar."));

        // Determinar si el actor es admin o el mismo empleado
        boolean esAdmin = (actor instanceof Empleado && ((Empleado) actor).getRol() == Rol.ADMINISTRADOR);
        boolean esElMismoEmpleado = actor != null && actor.getId().equals(empleadoAActualizar.getId());

        if (!esAdmin && !esElMismoEmpleado) {
            throw new Exception("No tiene permisos para actualizar este perfil de empleado.");
        }

        // Actualizar campos básicos (HU#145 - Empleado, HU#155 - Admin)
        empleadoAActualizar.setNombre(dto.getNombre());
        empleadoAActualizar.setApellido(dto.getApellido());
        empleadoAActualizar.setTelefono(dto.getTelefono());
        empleadoAActualizar.setFechaNacimiento(dto.getFechaNacimiento());

        // El email (username) generalmente no se actualiza. Si se permitiera, se necesitaría:
        // if (dto.getEmail() != null && !dto.getEmail().equals(empleadoAActualizar.getEmail())) {
        //     if (!EMAIL_PATTERN.matcher(dto.getEmail()).matches()) throw new Exception("Formato de email inválido.");
        //     if (empleadoRepository.existsByEmailRaw(dto.getEmail())) throw new Exception("El nuevo email ya está en uso.");
        //     empleadoAActualizar.setEmail(dto.getEmail());
        //     empleadoAActualizar.setUsername(dto.getEmail());
        // }


        // Cambio de contraseña (HU#147 - Empleado, HU#04 - Admin podría setear una nueva provisoria)
        // El campo 'password' en el DTO se usa para la nueva contraseña en este contexto.
        if (dto.getPassword() != null && !dto.getPassword().isEmpty()) {
            if (esElMismoEmpleado && !esAdmin) { // Empleado cambiando su propia contraseña
                if (dto.getCurrentPassword() == null || !passwordEncoder.matches(dto.getCurrentPassword(), empleadoAActualizar.getPassword())) {
                    throw new Exception("La contraseña actual ingresada es incorrecta.");
                }
            } // Si es admin, puede cambiar la contraseña sin la actual.

            if (dto.getConfirmPassword() == null || !dto.getPassword().equals(dto.getConfirmPassword())) {
                throw new Exception("Las nuevas contraseñas no coinciden o la confirmación está vacía.");
            }
            if (!PASSWORD_PATTERN.matcher(dto.getPassword()).matches()) {
                throw new Exception("La nueva contraseña debe tener al menos 8 caracteres, una mayúscula, una minúscula, un número y un símbolo.");
            }
            empleadoAActualizar.setPassword(passwordEncoder.encode(dto.getPassword()));
        }

        // Actualizar imagen
        if (dto.getImagenId() != null) {
            if (dto.getImagenId() == 0L) { // Convención para quitar imagen
                empleadoAActualizar.setImagen(null);
            } else {
                Imagen img = imagenRepository.findById(dto.getImagenId())
                        .orElseThrow(() -> new Exception("Imagen activa no encontrada con ID: " + dto.getImagenId()));
                empleadoAActualizar.setImagen(img);
            }
        }

        // Cambio de Rol y estado de Baja (solo admin - HU#156)
        if (esAdmin) {
            if (dto.getRol() != null) { // HU#156: Admin puede cambiar rol
                empleadoAActualizar.setRol(dto.getRol());
            }
            if (dto.getBaja() != null && dto.getBaja() != empleadoAActualizar.isBaja()) { // HU#156: Admin puede dar de alta/baja
                if (dto.getBaja()) {
                    if (empleadoAActualizar.getId().equals(actor.getId())) {
                        throw new Exception("Un administrador no puede darse de baja a sí mismo.");
                    }
                    empleadoAActualizar.setBaja(true);
                } else {
                    empleadoAActualizar.setBaja(false);
                }
            }
        } else { // Si no es admin, no puede cambiar rol ni estado de baja
            if (dto.getRol() != null && dto.getRol() != empleadoAActualizar.getRol()) { // HU#149
                throw new Exception("No tiene permisos para cambiar el rol del empleado.");
            }
            if (dto.getBaja() != null && dto.getBaja() != empleadoAActualizar.isBaja()) {
                throw new Exception("No tiene permisos para cambiar el estado de baja del empleado.");
            }
        }
        return convertToDTO(empleadoRepository.save(empleadoAActualizar));
    }

    @Override
    @Transactional(readOnly = true)
    public EmpleadoResponseDTO findEmpleadoByIdDTO(Long id) throws Exception {
        return convertToDTO(super.findById(id).orElse(null));
    }

    @Override
    @Transactional(readOnly = true)
    public List<EmpleadoResponseDTO> findAllEmpleados() throws Exception {
        return super.findAll().stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<EmpleadoResponseDTO> findByRol(Rol rol, boolean soloActivos) throws Exception {
        List<Empleado> empleados;
        if (soloActivos) {
            empleados = empleadoRepository.findByRol(rol); // @Where ya filtra por baja=false
        } else {
            empleados = empleadoRepository.findByRolRaw(rol);
        }
        return empleados.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void darBajaEmpleado(Long id) throws Exception {
        // El actor que llama a esto debe ser validado como ADMIN en el controlador
        Empleado empleado = this.findByIdIncludingDeleted(id)
                .orElseThrow(() -> new Exception("Empleado no encontrado con ID: " + id + " para dar de baja."));

        // Aquí se podría añadir lógica adicional si es necesario antes de llamar a softDelete
        // Por ejemplo, verificar si es el último administrador, etc.
        // La validación de no auto-baja de admin se puede hacer aquí o en el controlador.

        this.softDelete(id); // Llama al softDelete de BaseServiceImpl
    }

    @Override
    @Transactional
    public void darAltaEmpleado(Long id) throws Exception {
        // El actor que llama a esto debe ser validado como ADMIN en el controlador
        this.reactivate(id); // Llama al reactivate de BaseServiceImpl
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Empleado> findByEmailRaw(String email) throws Exception {
        return empleadoRepository.findByEmailRaw(email);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EmpleadoResponseDTO> findAllEmpleadosIncludingDeleted(Rol rol) throws Exception {
        List<Empleado> empleados;
        if (rol != null) {
            empleados = empleadoRepository.findByRolRaw(rol);
        } else {
            empleados = empleadoRepository.findAllRaw();
        }
        return empleados.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public EmpleadoResponseDTO findEmpleadoByIdIncludingDeletedDTO(Long id) throws Exception {
        return convertToDTO(empleadoRepository.findByIdRaw(id).orElse(null));
    }

    // --- Implementación de métodos de BaseService (softDelete, reactivate, etc.) ---
    @Override
    @Transactional(readOnly = true)
    public Optional<Empleado> findByIdIncludingDeleted(Long id) throws Exception {
        return empleadoRepository.findByIdRaw(id);
    }

    @Override
    @Transactional
    public Empleado softDelete(Long id) throws Exception { // Este es el que llama BaseServiceImpl.delete()
        Empleado empleado = this.findByIdIncludingDeleted(id)
                .orElseThrow(() -> new Exception("Empleado no encontrado con ID: " + id + " para dar de baja."));
        if (empleado.isBaja()) {
            throw new Exception("El empleado ya está dado de baja.");
        }
        // Aquí se podrían añadir validaciones específicas antes de la baja,
        // por ejemplo, si el empleado tiene tareas pendientes críticas.
        // La validación de no auto-baja de admin se maneja mejor en el método que es llamado por el admin.
        empleado.setBaja(true);
        return empleadoRepository.save(empleado);
    }

    @Override
    @Transactional
    public Empleado reactivate(Long id) throws Exception {
        Empleado empleado = this.findByIdIncludingDeleted(id)
                .orElseThrow(() -> new Exception("Empleado no encontrado con ID: " + id + " para reactivar."));
        if (!empleado.isBaja()) {
            throw new Exception("El empleado no está dado de baja, no se puede reactivar.");
        }
        empleado.setBaja(false);
        return empleadoRepository.save(empleado);
    }

    // --- Helper de Conversión ---
    private EmpleadoResponseDTO convertToDTO(Empleado empleado) {
        if (empleado == null) return null;
        EmpleadoResponseDTO dto = new EmpleadoResponseDTO();
        dto.setId(empleado.getId());
        dto.setNombre(empleado.getNombre());
        dto.setApellido(empleado.getApellido());
        dto.setTelefono(empleado.getTelefono());
        dto.setEmail(empleado.getEmail());
        dto.setUsername(empleado.getUsername());
        dto.setRol(empleado.getRol());
        dto.setFechaNacimiento(empleado.getFechaNacimiento());
        dto.setFechaAlta(empleado.getFechaAlta());
        dto.setBaja(empleado.isBaja());

        if (empleado.getImagen() != null) {
            ImagenDTO imgDto = new ImagenDTO();
            imgDto.setId(empleado.getImagen().getId());
            imgDto.setDenominacion(empleado.getImagen().getDenominacion());
            imgDto.setBaja(empleado.getImagen().isBaja()); // Mostrar estado de la imagen
            dto.setImagen(imgDto);
        }

        if (empleado.getDomicilios() != null) {
            dto.setDomicilios(empleado.getDomicilios().stream()
                    .filter(dom -> !dom.isBaja())
                    .map(this::convertToSimpleDomicilioDTO)
                    .collect(Collectors.toList()));
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
            dto.setLocalidad(locDto);
        }
        return dto;
    }
}
