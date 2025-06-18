package ElBuenSabor.ProyectoFinal.Service;

import ElBuenSabor.ProyectoFinal.DTO.ClientePerfilUpdateDTO;
import ElBuenSabor.ProyectoFinal.Entities.Categoria;
// ResourceNotFoundException ya se maneja en BaseServiceImpl
// import ElBuenSabor.ProyectoFinal.Exceptions.ResourceNotFoundException;
import ElBuenSabor.ProyectoFinal.Entities.Cliente;
import ElBuenSabor.ProyectoFinal.Entities.Domicilio;
import ElBuenSabor.ProyectoFinal.Entities.Imagen;
import ElBuenSabor.ProyectoFinal.Repositories.CategoriaRepository;
// Ya no es necesario si se inyecta por constructor explícito al padre
// import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // Importar Transactional

import java.util.HashSet;
import java.util.List; // Importar List si no se usara el findAll del padre
import java.util.Set;

@Service
public class CategoriaServiceImpl extends BaseServiceImpl<Categoria, Long> implements CategoriaService {


    public CategoriaServiceImpl(CategoriaRepository categoriaRepository) {
        super(categoriaRepository); // Llama al constructor de la clase base
    }

    @Override
    @Transactional
    public Categoria update(Long id, Categoria updated) throws Exception {
        try {
            Categoria actual = findById(id);

            actual.setDenominacion(updated.getDenominacion());
            actual.setCategoriaPadre(updated.getCategoriaPadre());

            return baseRepository.save(actual);
        } catch (Exception e) {
            throw new Exception("Error al actualizar la categoría: " + e.getMessage());
        }
    }

    @Transactional
    public void actualizarPerfil(Long id, ClientePerfilUpdateDTO dto) throws Exception {
        Cliente cliente = findById(id);

        // Actualizar datos básicos
        cliente.setNombre(dto.getNombre());
        cliente.setApellido(dto.getApellido());
        cliente.setTelefono(dto.getTelefono());
        cliente.setEmail(dto.getEmail());
        cliente.setFechaNacimiento(dto.getFechaNacimiento());

        // Actualizar domicilio
        if (dto.getDomicilioIds() != null) {
            Set<Domicilio> domicilios = new HashSet<>();
            for (Long domId : dto.getDomicilioIds()) {
                Domicilio dom = domicilioService.findById(domId);
                domicilios.add(dom);
            }
            cliente.setDomicilios(domicilios);
        }

        // Actualizar imagen
        if (dto.getImagenId() != null) {
            Imagen img = imagenService.findById(dto.getImagenId());
            cliente.setImagen(img);
        }

        // --- Validar y actualizar contraseña ---
        if (dto.getNuevaPassword() != null && !dto.getNuevaPassword().isBlank()) {
            // 1. Verificar que la actual es correcta (si usás un PasswordEncoder)
            if (!passwordEncoder.matches(dto.getPasswordActual(), cliente.getPassword())) {
                throw new Exception("La contraseña actual es incorrecta.");
            }
            // 2. Verificar que las contraseñas coincidan
            if (!dto.getNuevaPassword().equals(dto.getRepetirPassword())) {
                throw new Exception("Las nuevas contraseñas no coinciden.");
            }
            // 3. Validar fuerza de contraseña (puede ser regex)
            String nuevaPassword = dto.getNuevaPassword();
            if (nuevaPassword.length() < 8 ||
                    !nuevaPassword.matches(".*[a-z].*") ||
                    !nuevaPassword.matches(".*[A-Z].*") ||
                    !nuevaPassword.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?].*")) {
                throw new Exception("La contraseña debe tener al menos 8 caracteres, una mayúscula, una minúscula y un símbolo.");
            }
            // 4. Encriptar y guardar
            cliente.setPassword(passwordEncoder.encode(nuevaPassword));
        }

        // Guardar cambios
        baseRepository.save(cliente);
    }
}