package ElBuenSabor.ProyectoFinal.Data;

import ElBuenSabor.ProyectoFinal.Entities.Cliente;
import ElBuenSabor.ProyectoFinal.Entities.Rol;
import ElBuenSabor.ProyectoFinal.Repositories.ClienteRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class DataLoader {

    private final ClienteRepository clienteRepository;
    private final PasswordEncoder passwordEncoder;

    public DataLoader(ClienteRepository clienteRepository, PasswordEncoder passwordEncoder) {
        this.clienteRepository = clienteRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostConstruct
    public void init() {
        System.out.println("ℹ️ Precargando usuarios de prueba...");

        if (clienteRepository.findAll().isEmpty()) {

            Cliente admin = new Cliente();
            admin.setNombre("Admin");
            admin.setApellido("Sistema");
            admin.setEmail("admin@elbuen.com");
            admin.setUsername("admin@elbuen.com");
            admin.setPassword(passwordEncoder.encode("123456"));
            admin.setFechaNacimiento(LocalDate.of(1980, 1, 1));
            admin.setRol(Rol.ADMINISTRADOR); // ✅ corregido


            Cliente cliente = new Cliente();
            cliente.setNombre("Cliente");
            cliente.setApellido("Prueba");
            cliente.setEmail("cliente@elbuen.com");
            cliente.setUsername("cliente@elbuen.com");
            cliente.setPassword(passwordEncoder.encode("123456"));
            cliente.setFechaNacimiento(LocalDate.of(1995, 5, 5));
            cliente.setRol(Rol.CLIENTE);

            Cliente empleado = new Cliente();
            empleado.setNombre("Empleado");
            empleado.setApellido("Soporte");
            empleado.setEmail("empleado@elbuen.com");
            empleado.setUsername("empleado@elbuen.com");
            empleado.setPassword(passwordEncoder.encode("123456"));
            empleado.setFechaNacimiento(LocalDate.of(1992, 3, 15));
            empleado.setRol(Rol.EMPLEADO);

            clienteRepository.save(admin);
            clienteRepository.save(cliente);
            clienteRepository.save(empleado);

            System.out.println("✅ Usuarios precargados correctamente");
        }
    }
}
