package ElBuenSabor.ProyectoFinal.Controllers;

import ElBuenSabor.ProyectoFinal.DTO.*;
import ElBuenSabor.ProyectoFinal.Entities.Cliente;
import ElBuenSabor.ProyectoFinal.Security.JwtUtil;
import ElBuenSabor.ProyectoFinal.Service.ClienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private ClienteService clienteService;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/login")
    public JwtResponseDTO login(@RequestBody LoginDTO loginDTO) throws Exception {
        Cliente cliente = clienteService.loginCliente(loginDTO);

        if (cliente == null) {
            throw new Exception("Credenciales inválidas");
        }

        // Generar token JWT con el username y rol
        String token = jwtUtil.generateToken(cliente.getUsername(), cliente.getRol().name());

        // Mapear cliente a DTO
        ClienteResponseDTO clienteDTO = mapToDTO(cliente);

        return new JwtResponseDTO(token, clienteDTO);
    }

    @PostMapping("/register")
    public ClienteResponseDTO register(@RequestBody ClienteRegistroDTO registroDTO) throws Exception {
        Cliente cliente = clienteService.registrarCliente(registroDTO);
        return mapToDTO(cliente);
    }

    private ClienteResponseDTO mapToDTO(Cliente cliente) {
        ClienteResponseDTO dto = new ClienteResponseDTO();
        dto.setId(cliente.getId());
        dto.setNombre(cliente.getNombre());
        dto.setApellido(cliente.getApellido());
        dto.setTelefono(cliente.getTelefono());
        dto.setEmail(cliente.getEmail());
        dto.setFechaNacimiento(cliente.getFechaNacimiento());
        dto.setUsername(cliente.getUsername());
        dto.setAuth0Id(cliente.getAuth0Id());
        dto.setRol(cliente.getRol() != null ? cliente.getRol().name() : null);
        dto.setEstaDadoDeBaja(cliente.isEstaDadoDeBaja());

        // Imagen
        if (cliente.getImagen() != null) {
            ImagenDTO imagenDTO = new ImagenDTO();
            imagenDTO.setId(cliente.getImagen().getId());
            imagenDTO.setDenominacion(cliente.getImagen().getDenominacion());
            dto.setImagen(imagenDTO);
        }

        // Domicilios
        if (cliente.getDomicilios() != null) {
            List<DomicilioDTO> domicilioDTOs = cliente.getDomicilios().stream().map(d -> {
                DomicilioDTO ddto = new DomicilioDTO();
                ddto.setId(d.getId());
                ddto.setCalle(d.getCalle());
                ddto.setNumero(d.getNumero());
                ddto.setCp(d.getCp());
                if (d.getLocalidad() != null) {
                    ddto.setLocalidadNombre(d.getLocalidad().getNombre());
                }
                return ddto;
            }).collect(Collectors.toList());
            dto.setDomicilios(domicilioDTOs);
        }

        return dto;
    }
}
