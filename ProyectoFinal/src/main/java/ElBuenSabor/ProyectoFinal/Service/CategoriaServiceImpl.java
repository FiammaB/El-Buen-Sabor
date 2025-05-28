package ElBuenSabor.ProyectoFinal.Service;

import ElBuenSabor.ProyectoFinal.DTO.CategoriaCreateUpdateDTO;
import ElBuenSabor.ProyectoFinal.DTO.CategoriaDTO;
import ElBuenSabor.ProyectoFinal.DTO.SucursalSimpleDTO; // Para el DTO de Categoria
import ElBuenSabor.ProyectoFinal.Entities.Categoria;
import ElBuenSabor.ProyectoFinal.Entities.Sucursal;
import ElBuenSabor.ProyectoFinal.Entities.TipoRubro;
import ElBuenSabor.ProyectoFinal.Repositories.ArticuloRepository; // Para isCategoriaInUse
import ElBuenSabor.ProyectoFinal.Repositories.CategoriaRepository;
import ElBuenSabor.ProyectoFinal.Repositories.SucursalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CategoriaServiceImpl extends BaseServiceImpl<Categoria, Long> implements CategoriaService {

    private final CategoriaRepository categoriaRepository;
    private final SucursalRepository sucursalRepository;
    private final ArticuloRepository articuloRepository; // Para verificar si está en uso

    @Autowired
    public CategoriaServiceImpl(CategoriaRepository categoriaRepository,
                                SucursalRepository sucursalRepository,
                                ArticuloRepository articuloRepository) {
        super(categoriaRepository);
        this.categoriaRepository = categoriaRepository;
        this.sucursalRepository = sucursalRepository;
        this.articuloRepository = articuloRepository;
    }

    @Override
    @Transactional
    public CategoriaDTO createCategoria(CategoriaCreateUpdateDTO dto) throws Exception {
        if (dto.getDenominacion() == null || dto.getDenominacion().trim().isEmpty() || dto.getTipoRubro() == null) {
            throw new Exception("Denominación y Tipo de Rubro son obligatorios.");
        }
        Optional<Categoria> existenteRaw = categoriaRepository.findByDenominacionAndTipoRubroRaw(dto.getDenominacion().trim(), dto.getTipoRubro());
        if (existenteRaw.isPresent()) {
            throw new Exception("Ya existe una categoría con la denominación '" + dto.getDenominacion().trim() + "' para el tipo de rubro '" + dto.getTipoRubro() + "'.");
        }

        Categoria categoria = new Categoria();
        categoria.setDenominacion(dto.getDenominacion().trim());
        categoria.setTipoRubro(dto.getTipoRubro());
        categoria.setBaja(false);

        if (dto.getCategoriaPadreId() != null) {
            Categoria padre = categoriaRepository.findById(dto.getCategoriaPadreId()) // Solo padres activos
                    .orElseThrow(() -> new Exception("Categoría padre activa no encontrada con ID: " + dto.getCategoriaPadreId()));
            if (padre.getTipoRubro() != dto.getTipoRubro()) {
                throw new Exception("El tipo de rubro de la subcategoría debe coincidir con el de la categoría padre.");
            }
            categoria.setCategoriaPadre(padre);
        }

        // Manejo de Sucursales
        Set<Sucursal> sucursalesAsociadas = new HashSet<>();
        if (dto.getSucursalIds() != null && !dto.getSucursalIds().isEmpty()) {
            for (Long sucId : dto.getSucursalIds()) {
                Sucursal suc = sucursalRepository.findById(sucId) // Solo sucursales activas
                        .orElseThrow(() -> new Exception("Sucursal activa no encontrada con ID: " + sucId));
                sucursalesAsociadas.add(suc);
            }
        }
        categoria.setSucursales(sucursalesAsociadas); // La entidad Categoria es dueña de la relación con Sucursal via mappedBy

        Categoria savedCategoria = categoriaRepository.save(categoria);
        // Actualizar el lado inverso en Sucursal (si Sucursal es dueña de la tabla de unión)
        // Si Categoria es mappedBy, la actualización de categoria.getSucursales() no actualiza la tabla de unión.
        // La entidad Sucursal tiene @JoinTable, por lo que Sucursal es la dueña.
        // Debemos actualizar la colección en las sucursales y guardarlas.
        for(Sucursal suc : sucursalesAsociadas) {
            if (suc.getCategorias() == null) suc.setCategorias(new ArrayList<>()); // Corrección aquí
            if (!suc.getCategorias().contains(savedCategoria)) { // Evitar duplicados
                suc.getCategorias().add(savedCategoria);
                sucursalRepository.save(suc); // Guardar la sucursal para persistir el cambio en la relación
            }
        }
        return convertToDTO(savedCategoria);
    }

    @Override
    @Transactional
    public CategoriaDTO updateCategoria(Long id, CategoriaCreateUpdateDTO dto) throws Exception {
        if (dto.getDenominacion() == null || dto.getDenominacion().trim().isEmpty() || dto.getTipoRubro() == null) {
            throw new Exception("Denominación y Tipo de Rubro son obligatorios.");
        }
        Categoria categoria = this.findByIdIncludingDeleted(id)
                .orElseThrow(() -> new Exception("Categoría no encontrada con ID: " + id + " para actualizar."));

        Optional<Categoria> existenteRaw = categoriaRepository.findByDenominacionAndTipoRubroRaw(dto.getDenominacion().trim(), dto.getTipoRubro());
        if (existenteRaw.isPresent() && !existenteRaw.get().getId().equals(id)) {
            throw new Exception("Ya existe otra categoría con la denominación '" + dto.getDenominacion().trim() + "' para el tipo de rubro '" + dto.getTipoRubro() + "'.");
        }
        if (categoria.getTipoRubro() != dto.getTipoRubro() && !categoria.getArticulos().isEmpty()){
            throw new Exception("No se puede cambiar el tipo de rubro de una categoría que tiene artículos asociados.");
        }


        categoria.setDenominacion(dto.getDenominacion().trim());
        categoria.setTipoRubro(dto.getTipoRubro());
        // El estado 'baja' se maneja por softDelete/reactivate

        if (dto.getCategoriaPadreId() != null) {
            if (dto.getCategoriaPadreId().equals(id)) {
                throw new Exception("Una categoría no puede ser su propia categoría padre.");
            }
            Categoria padre = categoriaRepository.findById(dto.getCategoriaPadreId())
                    .orElseThrow(() -> new Exception("Categoría padre activa no encontrada con ID: " + dto.getCategoriaPadreId()));
            if (padre.getTipoRubro() != dto.getTipoRubro()) {
                throw new Exception("El tipo de rubro de la subcategoría debe coincidir con el de la categoría padre.");
            }
            categoria.setCategoriaPadre(padre);
        } else {
            categoria.setCategoriaPadre(null);
        }

        // Actualizar relación con Sucursales (asumiendo que Sucursal es la dueña de la tabla de unión)
        // 1. Quitar la categoría de las sucursales antiguas
        Set<Sucursal> sucursalesActuales = new HashSet<>(categoria.getSucursales()); // Copia para evitar ConcurrentModification
        for (Sucursal sucActual : sucursalesActuales) {
            if (dto.getSucursalIds() == null || !dto.getSucursalIds().contains(sucActual.getId())) {
                sucActual.getCategorias().remove(categoria);
                sucursalRepository.save(sucActual);
            }
        }
        // 2. Añadir la categoría a las nuevas sucursales
        Set<Sucursal> nuevasSucursalesAsociadas = new HashSet<>();
        if (dto.getSucursalIds() != null && !dto.getSucursalIds().isEmpty()) {
            for (Long sucIdNuevo : dto.getSucursalIds()) {
                Sucursal sucNueva = sucursalRepository.findById(sucIdNuevo)
                        .orElseThrow(() -> new Exception("Sucursal activa no encontrada con ID: " + sucIdNuevo));
                if (sucNueva.getCategorias() == null) sucNueva.setCategorias(new ArrayList<>());
                if (!sucNueva.getCategorias().contains(categoria)) {
                    sucNueva.getCategorias().add(categoria);
                    sucursalRepository.save(sucNueva);
                }
                nuevasSucursalesAsociadas.add(sucNueva);
            }
        }
        categoria.setSucursales(nuevasSucursalesAsociadas); // Actualizar el lado de Categoria (no propietario)

        return convertToDTO(categoriaRepository.save(categoria));
    }

    @Override
    @Transactional(readOnly = true)
    public CategoriaDTO findCategoriaById(Long id) throws Exception {
        return convertToDTO(super.findById(id).orElse(null));
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoriaDTO> findAllCategorias() throws Exception {
        return super.findAll().stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoriaDTO> findByTipoRubro(TipoRubro tipoRubro, boolean activas) throws Exception {
        List<Categoria> categorias;
        if (activas) {
            categorias = categoriaRepository.findByTipoRubro(tipoRubro); // Ya filtrado por @Where
        } else {
            categorias = categoriaRepository.findByTipoRubroRaw(tipoRubro);
        }
        return categorias.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoriaDTO> findBySucursalesId(Long sucursalId) throws Exception {
        if (!sucursalRepository.existsById(sucursalId)) { // Verifica sucursal activa
            throw new Exception("Sucursal activa no encontrada con ID: " + sucursalId);
        }
        return categoriaRepository.findBySucursalesId(sucursalId).stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoriaDTO> findByCategoriaPadreIsNull(TipoRubro tipoRubro) throws Exception {
        // Este método ahora filtra por tipo de rubro para las categorías raíz.
        return categoriaRepository.findByCategoriaPadreIsNullRaw().stream() // Trae todas las raíz
                .filter(cat -> cat.getTipoRubro() == tipoRubro && !cat.isBaja()) // Filtra por tipo y activas
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoriaDTO> findSubcategorias(Long categoriaPadreId, boolean activas) throws Exception {
        List<Categoria> subcategorias;
        if (activas) {
            subcategorias = categoriaRepository.findByCategoriaPadreId(categoriaPadreId); // Ya filtrado por @Where
        } else {
            subcategorias = categoriaRepository.findByCategoriaPadreIdRaw(categoriaPadreId);
        }
        return subcategorias.stream().map(this::convertToDTO).collect(Collectors.toList());
    }


    @Override
    @Transactional(readOnly = true)
    public Optional<Categoria> findByDenominacionAndTipoRubroRaw(String denominacion, TipoRubro tipoRubro) throws Exception {
        return categoriaRepository.findByDenominacionAndTipoRubroRaw(denominacion, tipoRubro);
    }

    // --- Implementación de métodos de BaseService ---
    @Override
    @Transactional(readOnly = true)
    public List<Categoria> findAllIncludingDeleted() throws Exception {
        return categoriaRepository.findAllRaw();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Categoria> findByIdIncludingDeleted(Long id) throws Exception {
        return categoriaRepository.findByIdRaw(id);
    }

    @Override
    @Transactional
    public Categoria softDelete(Long id) throws Exception {
        Categoria categoria = this.findByIdIncludingDeleted(id)
                .orElseThrow(() -> new Exception("Categoría no encontrada con ID: " + id + " para dar de baja."));
        if (categoria.isBaja()) {
            throw new Exception("La categoría ya está dada de baja.");
        }
        if (isCategoriaInUse(id)) {
            throw new Exception("No se puede dar de baja la categoría porque tiene artículos activos o subcategorías activas asociadas.");
        }
        // Desvincular de sucursales antes de dar de baja (si Sucursal es dueña de la tabla de unión)
        for (Sucursal suc : new HashSet<>(categoria.getSucursales())) {
            suc.getCategorias().remove(categoria);
            sucursalRepository.save(suc);
        }
        categoria.getSucursales().clear();

        categoria.setBaja(true);
        return categoriaRepository.save(categoria);
    }

    @Override
    @Transactional
    public Categoria reactivate(Long id) throws Exception {
        Categoria categoria = this.findByIdIncludingDeleted(id)
                .orElseThrow(() -> new Exception("Categoría no encontrada con ID: " + id + " para reactivar."));
        if (!categoria.isBaja()) {
            throw new Exception("La categoría no está dada de baja, no se puede reactivar.");
        }
        // Al reactivar, si tenía padre, asegurar que el padre esté activo.
        if (categoria.getCategoriaPadre() != null && categoria.getCategoriaPadre().isBaja()) {
            throw new Exception("No se puede reactivar la categoría porque su categoría padre ('" + categoria.getCategoriaPadre().getDenominacion() + "') está dada de baja.");
        }
        categoria.setBaja(false);
        return categoriaRepository.save(categoria);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isCategoriaInUse(Long categoriaId) throws Exception {
        if (articuloRepository.existsByCategoriaIdAndBajaFalse(categoriaId)) {
            return true;
        }
        if (categoriaRepository.existsActiveSubCategoria(categoriaId)) {
            return true;
        }
        return false;
    }

    // --- Implementación de métodos de CategoriaService que devuelven DTO ---
    @Override
    @Transactional(readOnly = true)
    public List<CategoriaDTO> findAllCategoriasIncludingDeleted(TipoRubro tipoRubro) throws Exception {
        List<Categoria> categorias;
        if (tipoRubro != null) {
            categorias = categoriaRepository.findByTipoRubroRaw(tipoRubro);
        } else {
            categorias = this.findAllIncludingDeleted(); // Llama al método local de BaseService
        }
        return categorias.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public CategoriaDTO findCategoriaByIdIncludingDeleted(Long id) throws Exception {
        return convertToDTO(this.findByIdIncludingDeleted(id).orElse(null));
    }

    private CategoriaDTO convertToDTO(Categoria categoria) {
        if (categoria == null) return null;
        CategoriaDTO dto = new CategoriaDTO();
        dto.setId(categoria.getId());
        dto.setDenominacion(categoria.getDenominacion());
        dto.setTipoRubro(categoria.getTipoRubro());
        dto.setBaja(categoria.isBaja());

        if (categoria.getCategoriaPadre() != null) {
            dto.setCategoriaPadreId(categoria.getCategoriaPadre().getId());
            dto.setCategoriaPadreDenominacion(categoria.getCategoriaPadre().getDenominacion());
        }

        if (categoria.getSubCategorias() != null && !categoria.getSubCategorias().isEmpty()) {
            dto.setSubCategorias(categoria.getSubCategorias().stream()
                    .filter(sub -> !sub.isBaja()) // Mostrar solo subcategorías activas en el DTO
                    .map(this::convertToSimpleDTO) // Usar un DTO más simple para subcategorías
                    .collect(Collectors.toSet()));
        }

        if (categoria.getSucursales() != null && !categoria.getSucursales().isEmpty()) {
            dto.setSucursalIds(categoria.getSucursales().stream()
                    .filter(s -> !s.isBaja()) // Opcional: solo IDs de sucursales activas
                    .map(Sucursal::getId)
                    .collect(Collectors.toList()));
        }
        return dto;
    }

    private CategoriaDTO convertToSimpleDTO(Categoria categoria) {
        if (categoria == null) return null;
        CategoriaDTO dto = new CategoriaDTO();
        dto.setId(categoria.getId());
        dto.setDenominacion(categoria.getDenominacion());
        dto.setTipoRubro(categoria.getTipoRubro());
        dto.setBaja(categoria.isBaja());
        if (categoria.getCategoriaPadre() != null) {
            dto.setCategoriaPadreId(categoria.getCategoriaPadre().getId());
            // No incluir padreDenominacion ni subCategorias aquí para evitar recursión y mantenerlo simple
        }
        // No incluir sucursales aquí
        return dto;
    }
}
