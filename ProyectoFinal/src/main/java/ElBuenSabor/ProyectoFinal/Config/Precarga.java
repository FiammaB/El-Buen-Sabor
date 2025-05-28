package ElBuenSabor.ProyectoFinal.Config;

import ElBuenSabor.ProyectoFinal.Entities.ArticuloInsumo;
import ElBuenSabor.ProyectoFinal.Entities.ArticuloManufacturado;
import ElBuenSabor.ProyectoFinal.Entities.Categoria;
import ElBuenSabor.ProyectoFinal.Entities.UnidadMedida;
import ElBuenSabor.ProyectoFinal.Repositories.ArticuloInsumoRepository;
import ElBuenSabor.ProyectoFinal.Repositories.ArticuloManufacturadoRepository;
import ElBuenSabor.ProyectoFinal.Repositories.CategoriaRepository;
import ElBuenSabor.ProyectoFinal.Repositories.UnidadMedidaRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Precarga {

    @Autowired
    private CategoriaRepository categoriaRepository;

    @Autowired
    private UnidadMedidaRepository unidadMedidaRepository;

    @Autowired
    private ArticuloInsumoRepository articuloInsumoRepository;

    @Autowired
    private ArticuloManufacturadoRepository articuloManufacturadoRepository;

    @PostConstruct
    public void init() {
        // Crear categoría
        Categoria categoria = new Categoria("Comidas");
        categoriaRepository.save(categoria);

        // Crear unidad de medida
        UnidadMedida unidad = new UnidadMedida("Gramos");
        unidadMedidaRepository.save(unidad);

        // Crear ingredientes
        ArticuloInsumo pan = new ArticuloInsumo("Pan", 100.0, unidad);
        ArticuloInsumo carne = new ArticuloInsumo("Carne", 250.0, unidad);
        ArticuloInsumo queso = new ArticuloInsumo("Queso", 150.0, unidad);
        articuloInsumoRepository.save(pan);
        articuloInsumoRepository.save(carne);
        articuloInsumoRepository.save(queso);

        // Crear artículo manufacturado
        ArticuloManufacturado hamburguesa = new ArticuloManufacturado();
        hamburguesa.setDenominacion("Hamburguesa Clásica");
        hamburguesa.setPrecioVenta(1500.0);
        hamburguesa.setUnidadMedida(unidad);
        hamburguesa.setCategoria(categoria);
        hamburguesa.setDescripcion("Hamburguesa con pan, carne y queso");
        hamburguesa.setTiempoEstimadoMinutos(15);
        hamburguesa.setPreparacion("Cocinar la carne, tostar el pan y armar con queso.");
        articuloManufacturadoRepository.save(hamburguesa);

        System.out.println("✅ Precarga completada.");
    }
}