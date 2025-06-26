package ElBuenSabor.ProyectoFinal.Controllers;

import ElBuenSabor.ProyectoFinal.DTO.ReporteMonetarioDTO;
import ElBuenSabor.ProyectoFinal.Service.ReporteMonetarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/reportes")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class ReporteMonetarioController {

    @Autowired
    private ReporteMonetarioService reporteMonetarioService;

    @GetMapping("/monetario")
    public ReporteMonetarioDTO obtenerReporteMonetario(
            @RequestParam("desde") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam("hasta") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta
    ) {
        return reporteMonetarioService.obtenerTotales(desde.toString(), hasta.toString());
    }
}