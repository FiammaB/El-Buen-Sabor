package ElBuenSabor.ProyectoFinal.Service;

import ElBuenSabor.ProyectoFinal.DTO.ProductoRankingDTO;
import ElBuenSabor.ProyectoFinal.Entities.DetallePedido;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface DetallePedidoService {

    List<DetallePedido> findAll();

    Optional<DetallePedido> findById(Long id);

    DetallePedido save(DetallePedido entity);

    void deleteById(Long id);

    List<ProductoRankingDTO> getRankingProductos(LocalDate desde, LocalDate hasta);
}
