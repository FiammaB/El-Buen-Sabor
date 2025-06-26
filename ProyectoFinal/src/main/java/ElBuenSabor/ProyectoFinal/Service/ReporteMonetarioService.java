package ElBuenSabor.ProyectoFinal.Service;

import ElBuenSabor.ProyectoFinal.DTO.ReporteMonetarioDTO;

public interface ReporteMonetarioService {
    ReporteMonetarioDTO obtenerTotales(String desde, String hasta);
}