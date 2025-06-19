package ElBuenSabor.ProyectoFinal.Service;

import ElBuenSabor.ProyectoFinal.Entities.Categoria;
// ResourceNotFoundException ya se maneja en BaseServiceImpl
// import ElBuenSabor.ProyectoFinal.Exceptions.ResourceNotFoundException;
import ElBuenSabor.ProyectoFinal.Repositories.CategoriaRepository;
// Ya no es necesario si se inyecta por constructor explícito al padre
// import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // Importar Transactional

import java.util.List; // Importar List si no se usara el findAll del padre

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
}