package ElBuenSabor.ProyectoFinal.Service;

import ElBuenSabor.ProyectoFinal.DTO.ProductoRankingDTO;
import ElBuenSabor.ProyectoFinal.Entities.DetallePedido;
import ElBuenSabor.ProyectoFinal.Repositories.DetallePedidoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class DetallePedidoServiceImpl implements DetallePedidoService {

    private final DetallePedidoRepository detallePedidoRepository;

    @Autowired
    public DetallePedidoServiceImpl(DetallePedidoRepository detallePedidoRepository) {
        this.detallePedidoRepository = detallePedidoRepository;
    }

    @Override
    public List<DetallePedido> findAll() {
        return (List<DetallePedido>) detallePedidoRepository.findAll();
    }

    @Override
    public Optional<DetallePedido> findById(Long id) {
        return detallePedidoRepository.findById(id);
    }

    @Override
    public DetallePedido save(DetallePedido entity) {
        return detallePedidoRepository.save(entity);
    }

    @Override
    public void deleteById(Long id) {
        detallePedidoRepository.deleteById(id);
    }

    @Override
    public List<ProductoRankingDTO> getRankingProductos(LocalDate desde, LocalDate hasta) {
        return detallePedidoRepository.obtenerRankingProductos(desde, hasta);
    }
}
