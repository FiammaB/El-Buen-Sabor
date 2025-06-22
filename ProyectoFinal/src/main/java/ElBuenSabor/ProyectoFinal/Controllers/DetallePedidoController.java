package ElBuenSabor.ProyectoFinal.Controllers;

import ElBuenSabor.ProyectoFinal.DTO.ProductoRankingDTO;
import ElBuenSabor.ProyectoFinal.Service.DetallePedidoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/detallepedido")
public class DetallePedidoController {

    private final DetallePedidoService detallePedidoService;

    @Autowired
    public DetallePedidoController(DetallePedidoService detallePedidoService) {
        this.detallePedidoService = detallePedidoService;
    }

    @GetMapping("/ranking")
    public ResponseEntity<List<ProductoRankingDTO>> obtenerRankingProductos(
            @RequestParam("desde") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam("hasta") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta
    ) {
        List<ProductoRankingDTO> ranking = detallePedidoService.getRankingProductos(desde, hasta);
        return ResponseEntity.ok(ranking);
    }
}
