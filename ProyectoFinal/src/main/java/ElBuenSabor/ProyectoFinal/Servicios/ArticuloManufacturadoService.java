package ElBuenSabor.ProyectoFinal.Servicios;

import ElBuenSabor.ProyectoFinal.Entities.ArticuloManufacturado;

import java.util.List;

public interface ArticuloManufacturadoService {

    ArticuloManufacturado save(ArticuloManufacturado articuloManufacturado);
    ArticuloManufacturado findById(Long id);
    List<ArticuloManufacturado> findAll();
    ArticuloManufacturado update(Long id, ArticuloManufacturado articuloManufacturado);
    void delete(Long id);

}
