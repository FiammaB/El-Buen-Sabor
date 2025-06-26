package ElBuenSabor.ProyectoFinal.Service;

import ElBuenSabor.ProyectoFinal.DTO.ReporteMonetarioDTO;
import ElBuenSabor.ProyectoFinal.Repositories.ReporteMonetarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class ReporteMonetarioServiceImpl implements ReporteMonetarioService {

    @Autowired
    private ReporteMonetarioRepository reporteRepository;

    @Override
    public ReporteMonetarioDTO obtenerTotales(String desde, String hasta) {
        LocalDate fechaDesde = LocalDate.parse(desde);
        LocalDate fechaHasta = LocalDate.parse(hasta);

        return reporteRepository.obtenerTotales(fechaDesde, fechaHasta);
    }
}