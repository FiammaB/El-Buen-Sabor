package ElBuenSabor.ProyectoFinal.Service;

import ElBuenSabor.ProyectoFinal.DTO.CategoriaCreateUpdateDTO;
import ElBuenSabor.ProyectoFinal.Entities.Categoria;
import ElBuenSabor.ProyectoFinal.Entities.Sucursal;
import ElBuenSabor.ProyectoFinal.Repositories.CategoriaRepository;
import ElBuenSabor.ProyectoFinal.Repositories.SucursalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CategoriaServiceImpl extends BaseServiceImpl<Categoria, Long> implements CategoriaService {

    private final CategoriaRepository categoriaRepository;
    private final SucursalRepository sucursalRepository;

    @Autowired
    public CategoriaServiceImpl(CategoriaRepository categoriaRepository, SucursalRepository sucursalRepository) {
        super(categoriaRepository);
        this.categoriaRepository = categoriaRepository;
        this.sucursalRepository = sucursalRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Categoria> findBySucursalesId(Long sucursalId) throws Exception {
        try {
            return categoriaRepository.findBySucursalesId(sucursalId); //
        } catch (Exception e) {
            throw new Exception("Error al buscar categorías por ID de sucursal: " + e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<Categoria> findByCategoriaPadreIsNull() throws Exception {
        try {
            return categoriaRepository.findByCategoriaPadreIsNull(); //
        } catch (Exception e) {
            throw new Exception("Error al buscar categorías de nivel superior: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public Categoria createCategoria(CategoriaCreateUpdateDTO dto) throws Exception {
        try {
            Categoria categoria = new Categoria();
            categoria.setDenominacion(dto.getDenominacion());

            if (dto.getCategoriaPadreId() != null) {
                Categoria padre = categoriaRepository.findById(dto.getCategoriaPadreId())
                        .orElseThrow(() -> new Exception("Categoría padre no encontrada con ID: " + dto.getCategoriaPadreId()));
                categoria.setCategoriaPadre(padre);
            }

            if (dto.getSucursalIds() != null && !dto.getSucursalIds().isEmpty()) {
                Set<Sucursal> sucursales = new HashSet<>(sucursalRepository.findAllById(dto.getSucursalIds()));
                if(sucursales.size() != dto.getSucursalIds().size()){
                    // Algunos IDs de sucursal no fueron encontrados
                    throw new Exception("Algunas sucursales especificadas para la categoría no fueron encontradas.");
                }
                categoria.setSucursales(sucursales);
                // También es necesario actualizar el lado inverso de la relación si es bidireccional y no está manejado por JPA automáticamente
                for(Sucursal s : sucursales) {
                    s.getCategorias().add(categoria); // Asegúrate que getCategorias() no sea nulo
                }
            }

            return categoriaRepository.save(categoria);
        } catch (Exception e) {
            throw new Exception("Error al crear la categoría: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public Categoria updateCategoria(Long id, CategoriaCreateUpdateDTO dto) throws Exception {
        try {
            Categoria categoria = categoriaRepository.findById(id)
                    .orElseThrow(() -> new Exception("Categoría no encontrada con ID: " + id));

            categoria.setDenominacion(dto.getDenominacion());

            if (dto.getCategoriaPadreId() != null) {
                if (dto.getCategoriaPadreId().equals(id)) { // Evitar que una categoría sea padre de sí misma
                    throw new Exception("Una categoría no puede ser su propia categoría padre.");
                }
                Categoria padre = categoriaRepository.findById(dto.getCategoriaPadreId())
                        .orElseThrow(() -> new Exception("Categoría padre no encontrada con ID: " + dto.getCategoriaPadreId()));
                categoria.setCategoriaPadre(padre);
            } else {
                categoria.setCategoriaPadre(null); // Si se envía null, se quita la categoría padre
            }

            // Actualizar sucursales
            // Remover la categoría de las sucursales antiguas que ya no están en la lista
            if (categoria.getSucursales() != null) {
                List<Sucursal> sucursalesActuales = categoria.getSucursales().stream().collect(Collectors.toList());
                for (Sucursal s : sucursalesActuales) {
                    if (dto.getSucursalIds() == null || !dto.getSucursalIds().contains(s.getId())) {
                        s.getCategorias().remove(categoria); // Quitar de la sucursal
                    }
                }
            }

            if (dto.getSucursalIds() != null && !dto.getSucursalIds().isEmpty()) {
                Set<Sucursal> nuevasSucursales = new HashSet<>(sucursalRepository.findAllById(dto.getSucursalIds()));
                if(nuevasSucursales.size() != dto.getSucursalIds().size()){
                    throw new Exception("Algunas sucursales especificadas para la categoría no fueron encontradas.");
                }
                categoria.setSucursales(nuevasSucursales);
                for(Sucursal s : nuevasSucursales) {
                    if(s.getCategorias() == null) s.setCategorias(new ArrayList<>()); // Asegurar inicialización
                    s.getCategorias().add(categoria); // Añadir a la nueva sucursal
                }
            } else {
                // Si no se envían IDs de sucursal, se quitan todas las asociaciones
                if (categoria.getSucursales() != null) {
                    categoria.getSucursales().clear();
                }
            }

            return categoriaRepository.save(categoria);
        } catch (Exception e) {
            throw new Exception("Error al actualizar la categoría: " + e.getMessage());
        }
    }
}