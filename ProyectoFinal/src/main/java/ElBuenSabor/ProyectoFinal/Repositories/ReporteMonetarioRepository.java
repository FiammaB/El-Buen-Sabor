package ElBuenSabor.ProyectoFinal.Repositories;

import ElBuenSabor.ProyectoFinal.DTO.ReporteMonetarioDTO;

import java.time.LocalDate;

public interface ReporteMonetarioRepository {
    ReporteMonetarioDTO obtenerTotales(LocalDate desde, LocalDate hasta);
}
