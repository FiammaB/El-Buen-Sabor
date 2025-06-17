package ElBuenSabor.ProyectoFinal.Utils;

import ElBuenSabor.ProyectoFinal.Entities.*;
import ElBuenSabor.ProyectoFinal.Service.*;
import jakarta.transaction.Transactional;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Component
public class DataLoader implements CommandLineRunner {

    private final PaisService paisService;
    private final ProvinciaService provinciaService;
    private final LocalidadService localidadService;
    private final CategoriaService categoriaService;
    private final UnidadMedidaService unidadMedidaService;
    private final ImagenService imagenService;
    private final UsuarioService usuarioService;
    private final ClienteService clienteService;
    private final ArticuloInsumoService articuloInsumoService;
    private final ArticuloManufacturadoService articuloManufacturadoService;
    private final ArticuloManufacturadoDetalleService articuloManufacturadoDetalleService;
    private final DomicilioService domicilioService;

    public DataLoader(PaisService paisService,
                      ProvinciaService provinciaService,
                      LocalidadService localidadService,
                      CategoriaService categoriaService,
                      UnidadMedidaService unidadMedidaService,
                      ImagenService imagenService,
                      UsuarioService usuarioService,
                      ClienteService clienteService,
                      ArticuloInsumoService articuloInsumoService,
                      ArticuloManufacturadoService articuloManufacturadoService,
                      ArticuloManufacturadoDetalleService articuloManufacturadoDetalleService,
                      DomicilioService domicilioService) {
        this.paisService = paisService;
        this.provinciaService = provinciaService;
        this.localidadService = localidadService;
        this.categoriaService = categoriaService;
        this.unidadMedidaService = unidadMedidaService;
        this.imagenService = imagenService;
        this.usuarioService = usuarioService;
        this.clienteService = clienteService;
        this.articuloInsumoService = articuloInsumoService;
        this.articuloManufacturadoService = articuloManufacturadoService;
        this.articuloManufacturadoDetalleService = articuloManufacturadoDetalleService;
        this.domicilioService = domicilioService;
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        System.out.println("Cargando datos de ejemplo...");

        try {
            // 1. Ubicación
            Pais pais = paisService.save(Pais.builder().nombre("Argentina").build());
            Provincia provincia = provinciaService.save(Provincia.builder().nombre("Mendoza").pais(pais).build());
            Localidad localidad = localidadService.save(Localidad.builder().nombre("Maipú").provincia(provincia).build());

            // 2. Imágenes
            Imagen imgCliente = imagenService.save(Imagen.builder().denominacion("https://example.com/cliente.jpg").build());
            Imagen imgHarina = imagenService.save(Imagen.builder().denominacion("https://example.com/harina.jpg").build());
            Imagen imgTomate = imagenService.save(Imagen.builder().denominacion("https://example.com/tomate.jpg").build());
            Imagen imgHamburguesa = imagenService.save(Imagen.builder().denominacion("https://example.com/hamburguesa.jpg").build());

            // 3. Domicilio
            Domicilio domicilioCliente = domicilioService.save(Domicilio.builder()
                    .calle("Calle Falsa")
                    .numero(123)
                    .cp(5515)
                    .localidad(localidad)
                    .build());

            // 4. Usuario CLIENTE
            Usuario usuarioCliente = usuarioService.save(Usuario.builder()
                    .auth0Id("auth0|123456789")
                    .username("cliente_test")
                    .rol(Rol.CLIENTE)
                    .build());

            // 5. Cliente asociado al Usuario
            Cliente cliente = Cliente.builder()
                    .nombre("Fiamma")
                    .apellido("Brizuela")
                    .telefono("2615551234")
                    .email("gastonsisterna30@gmail.com")
                    .password("cliente123")
                    .fechaNacimiento(LocalDate.of(1990, 5, 15))
                    .imagen(imgCliente)
                    .usuario(usuarioCliente)
                    .domicilios(Set.of(domicilioCliente))
                    .build();

            clienteService.save(cliente);

            // 6. Categoría y Unidades
            Categoria categoriaComida = categoriaService.save(Categoria.builder().denominacion("Comida").build());
            UnidadMedida unidadGramos = unidadMedidaService.save(UnidadMedida.builder().denominacion("gramos").build());
            UnidadMedida unidadPorcion = unidadMedidaService.save(UnidadMedida.builder().denominacion("unidad").build());

            // 7. Insumos
            ArticuloInsumo insumoHarina = articuloInsumoService.save(ArticuloInsumo.builder()
                    .denominacion("Harina")
                    .precioVenta(500.0)
                    .precioCompra(300.0)
                    .stockActual(1000.0)
                    .stockMinimo(200.0)
                    .esParaElaborar(true)
                    .categoria(categoriaComida)
                    .unidadMedida(unidadGramos)
                    .imagen(imgHarina)
                    .build());

            ArticuloInsumo insumoTomate = articuloInsumoService.save(ArticuloInsumo.builder()
                    .denominacion("Tomate")
                    .precioVenta(100.0)
                    .precioCompra(50.0)
                    .stockActual(500.0)
                    .stockMinimo(100.0)
                    .esParaElaborar(true)
                    .categoria(categoriaComida)
                    .unidadMedida(unidadGramos)
                    .imagen(imgTomate)
                    .build());

            // 8. Artículo Manufacturado
            ArticuloManufacturado hamburguesa = ArticuloManufacturado.builder()
                    .denominacion("Hamburguesa Clásica")
                    .precioVenta(1250.0)
                    .descripcion("Deliciosa hamburguesa con queso y lechuga")
                    .tiempoEstimadoMinutos(20)
                    .preparacion("Preparar la carne, cocinar, armar.")
                    .categoria(categoriaComida)
                    .unidadMedida(unidadPorcion)
                    .imagen(imgHamburguesa)
                    .build();

            Set<ArticuloManufacturadoDetalle> detalles = new HashSet<>();
            detalles.add(ArticuloManufacturadoDetalle.builder().cantidad(200.0).articuloInsumo(insumoHarina).articuloManufacturado(hamburguesa).build());
            detalles.add(ArticuloManufacturadoDetalle.builder().cantidad(50.0).articuloInsumo(insumoTomate).articuloManufacturado(hamburguesa).build());

            hamburguesa.setDetalles(detalles);
            articuloManufacturadoService.save(hamburguesa);

            // 9. Usuario ADMIN
            Usuario adminUsuario = Usuario.builder()
                    .username("admin@buen.com")
                    .rol(Rol.ADMINISTRADOR)
                    .build();
            usuarioService.save(adminUsuario);

            System.out.println("Datos de ejemplo cargados exitosamente.");
        } catch (Exception e) {
            System.err.println("Error al cargar datos de ejemplo: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
