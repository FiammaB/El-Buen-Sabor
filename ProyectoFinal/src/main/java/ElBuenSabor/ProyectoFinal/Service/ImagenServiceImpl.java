package ElBuenSabor.ProyectoFinal.Service;

import ElBuenSabor.ProyectoFinal.DTO.ImagenDTO;
import ElBuenSabor.ProyectoFinal.Entities.Imagen;
import ElBuenSabor.ProyectoFinal.Repositories.ArticuloRepository; // Para verificar uso en Articulos
import ElBuenSabor.ProyectoFinal.Repositories.ClienteRepository;  // Para verificar uso en Clientes
import ElBuenSabor.ProyectoFinal.Repositories.PromocionRepository; // Para verificar uso en Promociones
import ElBuenSabor.ProyectoFinal.Repositories.ImagenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ImagenServiceImpl extends BaseServiceImpl<Imagen, Long> implements ImagenService {

    private final ImagenRepository imagenRepository;
    // Repositorios necesarios para verificar si la imagen está en uso
    private final ArticuloRepository articuloRepository;
    private final ClienteRepository clienteRepository;
    private final PromocionRepository promocionRepository;


    @Autowired
    public ImagenServiceImpl(ImagenRepository imagenRepository,
                             ArticuloRepository articuloRepository,
                             ClienteRepository clienteRepository,
                             PromocionRepository promocionRepository) {
        super(imagenRepository);
        this.imagenRepository = imagenRepository;
        this.articuloRepository = articuloRepository;
        this.clienteRepository = clienteRepository;
        this.promocionRepository = promocionRepository;
    }

    @Override
    @Transactional
    public ImagenDTO createImagen(ImagenDTO dto) throws Exception {
        if (dto.getDenominacion() == null || dto.getDenominacion().trim().isEmpty()) {
            throw new Exception("La denominación (URL/path) de la imagen no puede estar vacía.");
        }
        // Opcional: Validar unicidad de denominacion si es un requisito
        // Optional<Imagen> existenteRaw = imagenRepository.findByDenominacionRaw(dto.getDenominacion().trim());
        // if (existenteRaw.isPresent()) {
        //     throw new Exception("Ya existe una imagen con la denominación: " + dto.getDenominacion().trim());
        // }
        Imagen imagen = new Imagen();
        imagen.setDenominacion(dto.getDenominacion().trim());
        return convertToDTO(imagenRepository.save(imagen));
    }

    @Override
    @Transactional
    public ImagenDTO updateImagen(Long id, ImagenDTO dto) throws Exception {
        if (dto.getDenominacion() == null || dto.getDenominacion().trim().isEmpty()) {
            throw new Exception("La denominación (URL/path) de la imagen no puede estar vacía.");
        }

        Imagen imagen = this.findByIdIncludingDeleted(id) // Permite actualizar incluso si está 'baja'
                .orElseThrow(() -> new Exception("Imagen no encontrada con ID: " + id + " para actualizar."));

        // Opcional: Validar unicidad de denominacion si cambia y es un requisito
        // if (!imagen.getDenominacion().equals(dto.getDenominacion().trim())) {
        //    Optional<Imagen> existenteRaw = imagenRepository.findByDenominacionRaw(dto.getDenominacion().trim());
        //    if (existenteRaw.isPresent() && !existenteRaw.get().getId().equals(id)) {
        //        throw new Exception("Ya existe otra imagen con la denominación: " + dto.getDenominacion().trim());
        //    }
        // }

        imagen.setDenominacion(dto.getDenominacion().trim());
        if (dto.isBaja() != imagen.isBaja()) { // Si el DTO permite cambiar el estado de baja
            imagen.setBaja(dto.isBaja());
        }
        return convertToDTO(imagenRepository.save(imagen));
    }

    @Override
    @Transactional(readOnly = true)
    public ImagenDTO findImagenById(Long id) throws Exception {
        return convertToDTO(super.findById(id).orElse(null));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ImagenDTO> findAllImagenes() throws Exception {
        return super.findAll().stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Imagen> findByDenominacionRaw(String denominacion) throws Exception {
        return imagenRepository.findByDenominacionRaw(denominacion);
    }

    // --- Implementación de métodos de BaseService ---
    @Override
    @Transactional(readOnly = true)
    public List<Imagen> findAllIncludingDeleted() throws Exception {
        return imagenRepository.findAllRaw();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Imagen> findByIdIncludingDeleted(Long id) throws Exception {
        return imagenRepository.findByIdRaw(id);
    }

    @Override
    @Transactional
    public Imagen softDelete(Long id) throws Exception {
        Imagen imagen = this.findByIdIncludingDeleted(id)
                .orElseThrow(() -> new Exception("Imagen no encontrada con ID: " + id + " para dar de baja."));
        if (imagen.isBaja()) {
            throw new Exception("La imagen ya está dada de baja.");
        }

        // Verificar si la imagen está en uso ANTES de marcarla como baja
        if (isImagenInUse(id)) {
            throw new Exception("No se puede dar de baja la imagen porque está actualmente en uso por uno o más artículos, clientes o promociones.");
        }

        imagen.setBaja(true);
        return imagenRepository.save(imagen);
    }

    @Override
    @Transactional
    public Imagen reactivate(Long id) throws Exception {
        Imagen imagen = this.findByIdIncludingDeleted(id)
                .orElseThrow(() -> new Exception("Imagen no encontrada con ID: " + id + " para reactivar."));
        if (!imagen.isBaja()) {
            throw new Exception("La imagen no está dada de baja, no se puede reactivar.");
        }
        imagen.setBaja(false);
        return imagenRepository.save(imagen);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isImagenInUse(Long imagenId) throws Exception {
        // Esta query puede ser costosa si no hay índices en las columnas de imagen_id de las otras tablas.
        // Considera añadir métodos existsByImagenId en los repositorios correspondientes.
        // Ejemplo simplificado:
        long countArticulos = articuloRepository.countByImagenIdAndBajaFalse(imagenId); //
        long countClientes = clienteRepository.countByImagenIdAndBajaFalse(imagenId);   //
        long countPromociones = promocionRepository.countByImagenIdAndBajaFalse(imagenId); //

        return countArticulos > 0 || countClientes > 0 || countPromociones > 0;
    }


    // --- Implementación de métodos de ImagenService que devuelven DTO ---
    @Override
    @Transactional(readOnly = true)
    public List<ImagenDTO> findAllImagenesIncludingDeleted() throws Exception {
        return this.findAllIncludingDeleted().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ImagenDTO findImagenByIdIncludingDeleted(Long id) throws Exception {
        return convertToDTO(this.findByIdIncludingDeleted(id).orElse(null));
    }

    private ImagenDTO convertToDTO(Imagen imagen) {
        if (imagen == null) return null;
        ImagenDTO dto = new ImagenDTO();
        dto.setId(imagen.getId());
        dto.setDenominacion(imagen.getDenominacion());
        dto.setBaja(imagen.isBaja());
        return dto;
    }
}
