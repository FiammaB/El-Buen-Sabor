// ProyectoFinal/src/main/java/ElBuenSabor/ProyectoFinal/Service/RegistroAnulacionServiceImpl.java
package ElBuenSabor.ProyectoFinal.Service;

import ElBuenSabor.ProyectoFinal.Entities.RegistroAnulacion;
import ElBuenSabor.ProyectoFinal.Repositories.RegistroAnulacionRepository;
import org.springframework.stereotype.Service;

@Service
public class RegistroAnulacionServiceImpl extends BaseServiceImpl<RegistroAnulacion, Long> implements RegistroAnulacionService {

    public RegistroAnulacionServiceImpl(RegistroAnulacionRepository registroAnulacionRepository) {
        super(registroAnulacionRepository);
    }
}