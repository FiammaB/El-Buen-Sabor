package ElBuenSabor.ProyectoFinal.Mapper;


import ElBuenSabor.ProyectoFinal.DTO.ImagenDTO;
import ElBuenSabor.ProyectoFinal.Entities.Imagen;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ImagenMapper extends BaseMapper<Imagen, ImagenDTO> {

    ImagenDTO imagenToImagenDto(Imagen imagen);
    Imagen imagenDtoToImagen(ImagenDTO imagenDTO);

    List<ImagenDTO> imagenListToImagenDtoList(List<Imagen> imagenes);
    List<Imagen> imagenDtoListToImagenList(List<ImagenDTO> imagenesDTO);

}
