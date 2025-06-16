package ElBuenSabor.ProyectoFinal.Service;


import ElBuenSabor.ProyectoFinal.Entities.Imagen;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ImagenService extends BaseService<Imagen, Long >{
    Imagen upload(MultipartFile file) throws Exception;
}
