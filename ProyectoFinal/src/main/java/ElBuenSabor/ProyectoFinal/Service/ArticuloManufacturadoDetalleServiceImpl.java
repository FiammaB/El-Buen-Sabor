package ElBuenSabor.ProyectoFinal.Service;

import ElBuenSabor.ProyectoFinal.Entities.ArticuloManufacturadoDetalle;

import ElBuenSabor.ProyectoFinal.Repositories.ArticuloManufacturadoDetalleRepository;

import org.springframework.stereotype.Service;

@Service
public class ArticuloManufacturadoDetalleServiceImpl extends BaseServiceImpl<ArticuloManufacturadoDetalle, Long> implements ArticuloManufacturadoDetalleService {


    public ArticuloManufacturadoDetalleServiceImpl(ArticuloManufacturadoDetalleRepository articuloManufacturadoDetalleRepository) {
        super(articuloManufacturadoDetalleRepository);
    }

}