package ElBuenSabor.ProyectoFinal.Service;

import ElBuenSabor.ProyectoFinal.DTO.ImagenDTO;
import ElBuenSabor.ProyectoFinal.Entities.Imagen; // Para Optional<Imagen>
import java.util.List;
import java.util.Optional;

public interface ImagenService extends BaseService<Imagen, Long> {
    ImagenDTO createImagen(ImagenDTO dto) throws Exception;
    ImagenDTO updateImagen(Long id, ImagenDTO dto) throws Exception;

    ImagenDTO findImagenById(Long id) throws Exception; // Devuelve DTO de activa
    List<ImagenDTO> findAllImagenes() throws Exception; // Devuelve DTOs de activas

    Optional<Imagen> findByDenominacionRaw(String denominacion) throws Exception; // Para validación si es necesario

    // Heredados de BaseService y a implementar en ImagenServiceImpl para devolver DTOs
    List<ImagenDTO> findAllImagenesIncludingDeleted() throws Exception;
    ImagenDTO findImagenByIdIncludingDeleted(Long id) throws Exception;

    // Método específico para verificar si una imagen está en uso antes del borrado lógico
    boolean isImagenInUse(Long imagenId) throws Exception;
}
