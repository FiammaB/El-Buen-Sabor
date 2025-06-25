// Archivo: ElBuenSabor/ProyectoFinal/src/main/java/ElBuenSabor/ProyectoFinal/Utils/DataLoader.java
package ElBuenSabor.ProyectoFinal.Utils;
import ElBuenSabor.ProyectoFinal.Entities.*;
import ElBuenSabor.ProyectoFinal.Repositories.ArticuloManufacturadoRepository;
import ElBuenSabor.ProyectoFinal.Service.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
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
    private final PasswordEncoder passwordEncoder; // ✅ agregado
    private final PedidoService pedidoService;
    private final ArticuloManufacturadoRepository articuloManufacturadoRepository;
    private final PasswordEncoder passwordEncoder;
    private final SucursalService sucursalService;
    private final PedidoService pedidoService;
    private final FacturaService facturaService;
    private final EmpresaService empresaService;
    private final PromocionService promocionService;

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
                      ArticuloManufacturadoRepository articuloManufacturadoRepository,
                      DomicilioService domicilioService,
                      PasswordEncoder passwordEncoder, PedidoService pedidoService) { // ✅ agregado acá también
                      PasswordEncoder passwordEncoder,
                      SucursalService sucursalService,
                      PedidoService pedidoService,
                      FacturaService facturaService,
                      EmpresaService empresaService,
                      PromocionService promocionService) {
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
        this.articuloManufacturadoRepository = articuloManufacturadoRepository;
        this.domicilioService = domicilioService;
        this.passwordEncoder = passwordEncoder; // ✅ asignación
        this.pedidoService = pedidoService;
    }

        this.pedidoService = pedidoService;
        this.facturaService = facturaService;
        this.passwordEncoder = passwordEncoder;
        this.empresaService = empresaService;
        this.sucursalService = sucursalService;
        this.promocionService = promocionService;
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

            Localidad localidadCapital = localidadService.save(Localidad.builder().nombre("Ciudad de Mendoza").provincia(provincia).build());

            // 2. Imágenes (Crea instancias únicas si se van a reutilizar para diferentes artículos)

            Imagen imgCliente = imagenService.save(Imagen.builder().denominacion("https://example.com/cliente.jpg").build());
            Imagen imgHarina = imagenService.save(Imagen.builder().denominacion("https://example.com/harina.jpg").build());
            Imagen imgHamburguesaSimple = imagenService.save(Imagen.builder().denominacion("https://res.cloudinary.com/deagcdoak/image/upload/v1750351235/ijf0zghvaie0iagbgupo.jpg").build());
            Imagen imgHamburguesaTriple = imagenService.save(Imagen.builder().denominacion("https://res.cloudinary.com/deagcdoak/image/upload/v1750351426/xhxg6jpqpzalncutfdm3.jpg").build());
            Imagen imgHamburguesaPolloSimple = imagenService.save(Imagen.builder().denominacion("https://res.cloudinary.com/deagcdoak/image/upload/v1750351415/k4yrwvflb3uq3k8erz8g.jpg").build());
            Imagen imgHamburguesaPolloDoble = imagenService.save(Imagen.builder().denominacion("https://res.cloudinary.com/deagcdoak/image/upload/v1750351405/frdsc5iwdiwmczezsqny.jpg").build());
            Imagen imgLomito = imagenService.save(Imagen.builder().denominacion("https://res.cloudinary.com/deagcdoak/image/upload/v1750355139/gf9a7usbtgba3zm7dcmi.jpg").build());
            Imagen imgBarroluco = imagenService.save(Imagen.builder().denominacion("https://res.cloudinary.com/deagcdoak/image/upload/v1750355104/mqcnv1mmaw7e2y2vz3kj.jpg").build());
            Imagen imgEmpanadasCarne = imagenService.save(Imagen.builder().denominacion("https://res.cloudinary.com/deagcdoak/image/upload/v1750355129/ldfzerbk1sqxc3vkoejv.jpg").build());
            Imagen imgEmpanadasJyQ = imagenService.save(Imagen.builder().denominacion("https://res.cloudinary.com/deagcdoak/image/upload/v1750355113/jzfmt28xjmbxhqdvupuo.jpg").build());
            Imagen imgPizzaMuzzarella = imagenService.save(Imagen.builder().denominacion("https://res.cloudinary.com/deagcdoak/image/upload/v1750350963/hojeke2jdhagjeslrw8p.jpg").build());
            Imagen imgPizzaMargarita = imagenService.save(Imagen.builder().denominacion("https://res.cloudinary.com/deagcdoak/image/upload/v1750350978/vllegspkwpjahkqekqra.jpg").build());
            Imagen imgPizza4Quesos = imagenService.save(Imagen.builder().denominacion("https://res.cloudinary.com/deagcdoak/image/upload/v1750351103/xo2p7oynj8sjvyfqumre.jpg").build());
            Imagen imgPizzaEspecial = imagenService.save(Imagen.builder().denominacion("https://res.cloudinary.com/deagcdoak/image/upload/v1750351116/wd9zotrgzzg49y9ia24z.jpg").build());
            Imagen imgPizzaFugazzetta = imagenService.save(Imagen.builder().denominacion("https://res.cloudinary.com/deagcdoak/image/upload/v1750351127/hd0aqrgp2xx2upmhidrf.jpg").build());
            Imagen imgQuesoMuzarella = imagenService.save(Imagen.builder().denominacion("https://res.cloudinary.com/deagcdoak/image/upload/v1750352601/mmzhnakarizqlyxxyye4.jpg").build());
            Imagen imgQuesoBrie = imagenService.save(Imagen.builder().denominacion("https://res.cloudinary.com/deagcdoak/image/upload/v1750352592/tj3vrnlvkomuy4xzx3g5.jpg").build());
            Imagen imgQuesoRoquefort = imagenService.save(Imagen.builder().denominacion("https://res.cloudinary.com/deagcdoak/image/upload/v1750352629/mxyhkaedfghhffzuwizk.jpg").build());
            Imagen imgQuesoParmesano = imagenService.save(Imagen.builder().denominacion("https://res.com.cloudinary.com/deagcdoak/image/upload/v1750352611/ihli8hclpii1mquajqnc.jpg").build());
            Imagen imgPrepizza = imagenService.save(Imagen.builder().denominacion("https://res.cloudinary.com/deagcdoak/image/upload/v1750352582/sqtkgy0hyysceoy4afx2.jpg").build());
            Imagen imgAlbahaca = imagenService.save(Imagen.builder().denominacion("https://res.cloudinary.com/deagcdoak/image/upload/v1750352569/pyrpu1eqjwjypthadjeb.jpg").build());
            Imagen imgSalsaTomate = imagenService.save(Imagen.builder().denominacion("https://res.cloudinary.com/deagcdoak/image/upload/v1750352620/t2x6acnt3kksru1tviug.jpg").build());
            Imagen imgCebolla = imagenService.save(Imagen.builder().denominacion("https://res.cloudinary.com/deagcdoak/image/upload/v1750352555/uqm7pgv9b5otdrunu9c7.jpg").build());
            Imagen imgJamonCocido = imagenService.save(Imagen.builder().denominacion("https://res.cloudinary.com/deagcdoak/image/upload/v1750353159/rqivytetofp8sqkt11eb.jpg").build());
            Imagen imgPanHamburguesa = imagenService.save(Imagen.builder().denominacion("https://res.cloudinary.com/deagcdoak/image/upload/v1750353202/p7u7bzpiycxmx3kmlpld.jpg").build());
            Imagen imgLechuga = imagenService.save(Imagen.builder().denominacion("https://res.cloudinary.com/deagcdoak/image/upload/v1750353172/mkcyh0627xevlatn2brg.jpg").build());
            Imagen imgMedallonCarne = imagenService.save(Imagen.builder().denominacion("https://res.cloudinary.com/deagcdoak/image/upload/v1750353181/fnzvdhfpxvaehqargkr9.jpg").build());
            Imagen imgMedallonPollo = imagenService.save(Imagen.builder().denominacion("https://res.cloudinary.com/deagcdoak/image/upload/v1750353191/dlfjnhjxd0n4jujow6if.jpg").build());
            Imagen imgCheddar = imagenService.save(Imagen.builder().denominacion("https://res.cloudinary.com/deagcdoak/image/upload/v1750353211/fw4usszbbxg6ebm7u9m9.jpg").build());
            Imagen imgTomate = imagenService.save(Imagen.builder().denominacion("https://res.cloudinary.com/deagcdoak/image/upload/v1750353221/obwxfie9rwjnjfyoge4m.jpg").build());
            Imagen imgTapasEmpanada = imagenService.save(Imagen.builder().denominacion("https://res.cloudinary.com/deagcdoak/image/upload/v1750355168/qzkitscxs0ecwu17nw05.jpg").build());
            Imagen imgPanLomito = imagenService.save(Imagen.builder().denominacion("https://res.cloudinary.com/deagcdoak/image/upload/v1750355158/ce8qpkvkyvef0bnuxk66.jpg").build());
            Imagen imgPanBarroluco = imagenService.save(Imagen.builder().denominacion("https://res.cloudinary.com/deagcdoak/image/upload/v1750355148/gwina3myqo2gk4rkssew.jpg").build());
            Imagen imgCarneMolida = imagenService.save(Imagen.builder().denominacion("https://res.cloudinary.com/deagcdoak/image/upload/v1750355095/lhrwno1xb3qpfub5wt2t.jpg").build());
            Imagen imgBifeCarne = imagenService.save(Imagen.builder().denominacion("https://cmorres.com.ar/wp-content/uploads/2021/08/30072020-fotografia-sin-titulo-4883.jpg").build());

            // 3. Domicilio


            // 3. Empresa
            Empresa empresa = empresaService.save(Empresa.builder()
                    .nombre("El Buen Sabor S.A.")
                    .razonSocial("El Buen Sabor")
                    .cuil(301037568)
                    .build());

            // 4. Domicilios para Sucursales
            Domicilio domicilioSucursalCentro = domicilioService.save(Domicilio.builder()
                    .calle("Av. San Martín")
                    .numero(1000)
                    .cp(5500)
                    .localidad(localidadCapital)
                    .build());

            Domicilio domicilioSucursalMaipu = domicilioService.save(Domicilio.builder()
                    .calle("Ozamis")
                    .numero(500)
                    .cp(5515)
                    .localidad(localidad)
                    .build());

            // 5. Sucursales
            Sucursal sucursalCentro = sucursalService.save(Sucursal.builder()
                    .nombre("El Buen Sabor - Centro")
                    .horarioApertura(LocalTime.of(11, 0))
                    .horarioCierre(LocalTime.of(23, 0))
                    .domicilio(domicilioSucursalCentro)
                    .empresa(empresa)
                    .build());

            Sucursal sucursalMaipu = sucursalService.save(Sucursal.builder()
                    .nombre("El Buen Sabor - Maipú")
                    .horarioApertura(LocalTime.of(10, 30))
                    .horarioCierre(LocalTime.of(22, 30))
                    .domicilio(domicilioSucursalMaipu)
                    .empresa(empresa)
                    .build());

            // 6. Domicilio Cliente
            Domicilio domicilioCliente = domicilioService.save(Domicilio.builder()
                    .calle("Calle Falsa")
                    .numero(123)
                    .cp(5515)
                    .localidad(localidad)
                    .build());

// 4. Usuario CLIENTE


            // 7. Usuario CLIENTE

            Usuario usuarioCliente = usuarioService.save(Usuario.builder()
                    .email("faustinovinolo@gmail.com")
                    .password(passwordEncoder.encode("cliente123"))
                    .rol(Rol.CLIENTE)
                    .nombre("Fiamma")
                    .build());
// 5. Cliente asociado al Usuario

            // 8. Cliente asociado al Usuario
            Cliente cliente = Cliente.builder()
                    .apellido("Brizuela")
                    .telefono("2615551234")
                    .fechaNacimiento(LocalDate.of(1990, 5, 15))
                    .imagen(imgCliente)
                    .usuario(usuarioCliente)
                    .domicilios(Set.of(domicilioCliente))
                    .build();
            clienteService.save(cliente);
            // 6. Categoría y Unidades
            Categoria categoriaPizza = categoriaService.save(Categoria.builder().denominacion("Pizza").build());
            Categoria categoriaSanguche = categoriaService.save(Categoria.builder().denominacion("Sanguche").build());
            Categoria categoriaEmpanada = categoriaService.save(Categoria.builder().denominacion("Empanada").build());
            Categoria categoriaPan = categoriaService.save(Categoria.builder().denominacion("Pan").build());
            Categoria categoriaHamburguesa = categoriaService.save(Categoria.builder().denominacion("Hamburguesa").build());
            Categoria categoriaQueso = categoriaService.save(Categoria.builder().denominacion("Queso").build());
            Categoria categoriaComida = categoriaService.save(Categoria.builder().denominacion("Comida").build());
            Categoria categoriaVerdura = categoriaService.save(Categoria.builder().denominacion("Verdura").build());
            UnidadMedida unidadGramos = unidadMedidaService.save(UnidadMedida.builder().denominacion("gramos").build());
            UnidadMedida unidadPorcion = unidadMedidaService.save(UnidadMedida.builder().denominacion("unidad").build());
            UnidadMedida unidadMililitros = unidadMedidaService.save(UnidadMedida.builder().denominacion("mililitros").build());
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
                    .categoria(categoriaVerdura)
                    .unidadMedida(unidadGramos)
                    .imagen(imgTomate)
                    .build());
            ArticuloInsumo masaPrepizza = articuloInsumoService.save(ArticuloInsumo.builder()
                    .denominacion("Masa Prepizza")
                    .precioVenta(200.0)
                    .precioCompra(120.0)
                    .stockActual(300.0)
                    .stockMinimo(50.0)
                    .esParaElaborar(true)
                    .categoria(categoriaPizza)
                    .unidadMedida(unidadPorcion)
                    .imagen(imgPrepizza)
                    .build());
            ArticuloInsumo quesoMuzzarella = articuloInsumoService.save(ArticuloInsumo.builder()
                    .denominacion("Queso Muzzarella")
                    .precioVenta(800.0)
                    .precioCompra(600.0)
                    .stockActual(1000.0)
                    .stockMinimo(200.0)
                    .esParaElaborar(true)
                    .categoria(categoriaQueso)
                    .unidadMedida(unidadGramos)
                    .imagen(imgQuesoMuzarella)
                    .build());
            ArticuloInsumo quesoRoquefort = articuloInsumoService.save(ArticuloInsumo.builder()
                    .denominacion("Queso Roquefort")
                    .precioVenta(1200.0)
                    .precioCompra(900.0)
                    .stockActual(500.0)
                    .stockMinimo(100.0)
                    .esParaElaborar(true)
                    .categoria(categoriaQueso)
                    .unidadMedida(unidadGramos)
                    .imagen(imgQuesoRoquefort)
                    .build());
            ArticuloInsumo quesoBrie = articuloInsumoService.save(ArticuloInsumo.builder()
                    .denominacion("Queso Brie")
                    .precioVenta(1500.0)
                    .precioCompra(1000.0)
                    .stockActual(400.0)
                    .stockMinimo(100.0)
                    .esParaElaborar(true)
                    .categoria(categoriaQueso)
                    .unidadMedida(unidadGramos)
                    .imagen(imgQuesoBrie)
                    .build());
            ArticuloInsumo quesoParmesano = articuloInsumoService.save(ArticuloInsumo.builder()
                    .denominacion("Queso Parmesano")
                    .precioVenta(1100.0)
                    .precioCompra(850.0)
                    .stockActual(500.0)
                    .stockMinimo(100.0)
                    .esParaElaborar(true)
                    .categoria(categoriaQueso)
                    .unidadMedida(unidadGramos)
                    .imagen(imgQuesoParmesano)
                    .build());
            ArticuloInsumo salsaTomate = articuloInsumoService.save(ArticuloInsumo.builder()
                    .denominacion("Salsa de Tomate")
                    .precioVenta(150.0)
                    .precioCompra(100.0)
                    .stockActual(600.0)
                    .stockMinimo(100.0)
                    .esParaElaborar(true)
                    .categoria(categoriaComida)
                    .unidadMedida(unidadMililitros)
                    .imagen(imgSalsaTomate)
                    .build());
            ArticuloInsumo cebolla = articuloInsumoService.save(ArticuloInsumo.builder()
                    .denominacion("Cebolla")
                    .precioVenta(80.0)
                    .precioCompra(50.0)
                    .stockActual(300.0)
                    .stockMinimo(50.0)
                    .esParaElaborar(true)
                    .categoria(categoriaVerdura)
                    .unidadMedida(unidadGramos)
                    .imagen(imgCebolla)
                    .build());
            ArticuloInsumo albahaca = articuloInsumoService.save(ArticuloInsumo.builder()
                    .denominacion("Albahaca")
                    .precioVenta(20.0)
                    .precioCompra(10.0)
                    .stockActual(100.0)
                    .stockMinimo(20.0)
                    .esParaElaborar(true)
                    .categoria(categoriaComida)
                    .unidadMedida(unidadPorcion)
                    .imagen(imgAlbahaca)
                    .build());
            ArticuloInsumo jamonCocido = articuloInsumoService.save(ArticuloInsumo.builder()
                    .denominacion("Jamón Cocido")
                    .precioVenta(700.0)
                    .precioCompra(500.0)
                    .stockActual(800.0)
                    .stockMinimo(150.0)
                    .esParaElaborar(true)
                    .categoria(categoriaComida)
                    .unidadMedida(unidadGramos)
                    .imagen(imgJamonCocido)
                    .build());
            ArticuloInsumo panHamburguesa = articuloInsumoService.save(ArticuloInsumo.builder()
                    .denominacion("Pan de Hamburguesa")
                    .precioVenta(150.0)
                    .precioCompra(90.0)
                    .stockActual(300.0)
                    .stockMinimo(50.0)
                    .esParaElaborar(true)
                    .categoria(categoriaPan)
                    .unidadMedida(unidadPorcion)
                    .imagen(imgPanHamburguesa)
                    .build());
            ArticuloInsumo lechuga = articuloInsumoService.save(ArticuloInsumo.builder()
                    .denominacion("Lechuga")
                    .precioVenta(60.0)
                    .precioCompra(30.0)
                    .stockActual(400.0)
                    .stockMinimo(100.0)
                    .esParaElaborar(true)
                    .categoria(categoriaVerdura)
                    .unidadMedida(unidadGramos)
                    .imagen(imgLechuga)
                    .build());
            ArticuloInsumo medallonCarne = articuloInsumoService.save(ArticuloInsumo.builder()
                    .denominacion("Medallón de Carne")
                    .precioVenta(600.0)
                    .precioCompra(400.0)
                    .stockActual(250.0)
                    .stockMinimo(50.0)
                    .esParaElaborar(true)
                    .categoria(categoriaHamburguesa)
                    .unidadMedida(unidadPorcion)
                    .imagen(imgMedallonCarne)
                    .build());
            ArticuloInsumo medallonPollo = articuloInsumoService.save(ArticuloInsumo.builder()
                    .denominacion("Medallón de Pollo")
                    .precioVenta(500.0)
                    .precioCompra(350.0)
                    .stockActual(200.0)
                    .stockMinimo(50.0)
                    .esParaElaborar(true)
                    .categoria(categoriaHamburguesa)
                    .unidadMedida(unidadPorcion)
                    .imagen(imgMedallonPollo)
                    .build());
            ArticuloInsumo cheddar = articuloInsumoService.save(ArticuloInsumo.builder()
                    .denominacion("Queso Cheddar")
                    .precioVenta(400.0)
                    .precioCompra(250.0)
                    .stockActual(600.0)
                    .stockMinimo(100.0)
                    .esParaElaborar(true)
                    .categoria(categoriaQueso)
                    .unidadMedida(unidadGramos)
                    .imagen(imgCheddar)
                    .build());
            ArticuloInsumo tapasEmpanada = articuloInsumoService.save(ArticuloInsumo.builder()
                    .denominacion("Tapas para Empanadas")
                    .precioVenta(150.0)
                    .precioCompra(100.0)
                    .stockActual(500.0)
                    .stockMinimo(100.0)
                    .esParaElaborar(true)
                    .categoria(categoriaEmpanada)
                    .unidadMedida(unidadPorcion) // cada tapa se considera una unidad
                    .imagen(imgTapasEmpanada)
                    .build());
            ArticuloInsumo panLomito = articuloInsumoService.save(ArticuloInsumo.builder()
                    .denominacion("Pan para Lomito")
                    .precioVenta(200.0)
                    .precioCompra(120.0)
                    .stockActual(300.0)
                    .stockMinimo(50.0)
                    .esParaElaborar(true)
                    .categoria(categoriaPan)
                    .unidadMedida(unidadPorcion)
                    .imagen(imgPanLomito)
                    .build());
            ArticuloInsumo panBarroluco = articuloInsumoService.save(ArticuloInsumo.builder()
                    .denominacion("Pan de Miga Cuadrado")
                    .precioVenta(180.0)
                    .precioCompra(100.0)
                    .stockActual(300.0)
                    .stockMinimo(60.0)
                    .esParaElaborar(true)
                    .categoria(categoriaPan)
                    .unidadMedida(unidadPorcion)
                    .imagen(imgPanBarroluco)
                    .build());
            ArticuloInsumo carneMolida = articuloInsumoService.save(ArticuloInsumo.builder()
                    .denominacion("Carne Molida")
                    .precioVenta(1000.0)
                    .precioCompra(800.0)
                    .stockActual(400.0)
                    .stockMinimo(100.0)
                    .esParaElaborar(true)
                    .categoria(categoriaComida)
                    .unidadMedida(unidadGramos)
                    .imagen(imgCarneMolida)
                    .build());
            ArticuloInsumo bifeCarne = articuloInsumoService.save(ArticuloInsumo.builder()
                    .denominacion("Bife de Carne")
                    .precioVenta(1200.0)
                    .precioCompra(950.0)
                    .stockActual(200.0)
                    .stockMinimo(50.0)
                    .esParaElaborar(true)
                    .categoria(categoriaComida)
                    .unidadMedida(unidadPorcion)
                    .imagen(imgBifeCarne)
                    .build());
//
            // 8. Artículo Manufacturado
            ArticuloManufacturado hamburguesa = ArticuloManufacturado.builder()
                    .denominacion("Hamburguesa Clásica")

            // 9. Categoría y Unidades
            Categoria categoriaPizza = categoriaService.save(Categoria.builder().denominacion("Pizza").sucursales(new HashSet<>(Arrays.asList(sucursalCentro, sucursalMaipu))).build());
            Categoria categoriaSanguche = categoriaService.save(Categoria.builder().denominacion("Sanguche").sucursales(new HashSet<>(Arrays.asList(sucursalCentro, sucursalMaipu))).build());
            Categoria categoriaEmpanada = categoriaService.save(Categoria.builder().denominacion("Empanada").sucursales(new HashSet<>(Arrays.asList(sucursalCentro, sucursalMaipu))).build());
            Categoria categoriaPan = categoriaService.save(Categoria.builder().denominacion("Pan").sucursales(new HashSet<>(Arrays.asList(sucursalCentro, sucursalMaipu))).build());
            Categoria categoriaHamburguesa = categoriaService.save(Categoria.builder().denominacion("Hamburguesa").sucursales(new HashSet<>(Arrays.asList(sucursalCentro, sucursalMaipu))).build());
            Categoria categoriaQueso = categoriaService.save(Categoria.builder().denominacion("Queso").sucursales(new HashSet<>(Arrays.asList(sucursalCentro, sucursalMaipu))).build());
            Categoria categoriaComida = categoriaService.save(Categoria.builder().denominacion("Comida").sucursales(new HashSet<>(Arrays.asList(sucursalCentro, sucursalMaipu))).build());
            Categoria categoriaVerdura = categoriaService.save(Categoria.builder().denominacion("Verdura").sucursales(new HashSet<>(Arrays.asList(sucursalCentro, sucursalMaipu))).build());

            // Actualizar sucursales con categorías
            sucursalCentro.getCategorias().add(categoriaPizza);
            sucursalCentro.getCategorias().add(categoriaSanguche);
            sucursalCentro.getCategorias().add(categoriaEmpanada);
            sucursalMaipu.getCategorias().add(categoriaPizza);
            sucursalMaipu.getCategorias().add(categoriaHamburguesa);
            sucursalMaipu.getCategorias().add(categoriaQueso);
            sucursalService.update(sucursalCentro.getId(), sucursalCentro);
            sucursalService.update(sucursalMaipu.getId(), sucursalMaipu);

            UnidadMedida unidadGramos = unidadMedidaService.save(UnidadMedida.builder().denominacion("gramos").build());
            UnidadMedida unidadPorcion = unidadMedidaService.save(UnidadMedida.builder().denominacion("unidad").build());
            UnidadMedida unidadMililitros = unidadMedidaService.save(UnidadMedida.builder().denominacion("mililitros").build());

            // 10. Insumos (Asociar a sucursales de manera consistente y con nombres únicos)
            // Insumos para Sucursal Centro
            ArticuloInsumo insumoHarinaCentro = articuloInsumoService.save(ArticuloInsumo.builder()
                    .denominacion("Harina_Centro") // ✅ Denominación única
                    .precioVenta(500.0) .precioCompra(300.0) .stockActual(1000.0) .stockMinimo(200.0)
                    .esParaElaborar(true) .categoria(categoriaComida) .unidadMedida(unidadGramos)
                    .imagen(imagenService.save(Imagen.builder().denominacion("https://example.com/harina_centro.jpg").build())) // ✅ Imagen única
                    .sucursal(sucursalCentro) .build());

            ArticuloInsumo insumoTomateCentro = articuloInsumoService.save(ArticuloInsumo.builder()
                    .denominacion("Tomate_Centro") // ✅ Denominación única
                    .precioVenta(100.0) .precioCompra(50.0) .stockActual(500.0) .stockMinimo(100.0)
                    .esParaElaborar(true) .categoria(categoriaVerdura) .unidadMedida(unidadGramos)
                    .imagen(imagenService.save(Imagen.builder().denominacion("https://example.com/tomate_centro.jpg").build())) // ✅ Imagen única
                    .sucursal(sucursalCentro) .build());

            ArticuloInsumo masaPrepizzaCentro = articuloInsumoService.save(ArticuloInsumo.builder()
                    .denominacion("Masa Prepizza_Centro") // ✅ Denominación única
                    .precioVenta(200.0) .precioCompra(120.0) .stockActual(300.0) .stockMinimo(50.0)
                    .esParaElaborar(true) .categoria(categoriaPizza) .unidadMedida(unidadPorcion)
                    .imagen(imagenService.save(Imagen.builder().denominacion("https://example.com/masaprepizza_centro.jpg").build())) // ✅ Imagen única
                    .sucursal(sucursalCentro) .build());

            ArticuloInsumo quesoMuzzarellaCentro = articuloInsumoService.save(ArticuloInsumo.builder()
                    .denominacion("Queso Muzzarella_Centro") // ✅ Denominación única
                    .precioVenta(800.0) .precioCompra(600.0) .stockActual(1000.0) .stockMinimo(200.0)
                    .esParaElaborar(true) .categoria(categoriaQueso) .unidadMedida(unidadGramos)
                    .imagen(imagenService.save(Imagen.builder().denominacion("https://example.com/muzzarella_centro.jpg").build())) // ✅ Imagen única
                    .sucursal(sucursalCentro) .build());

            ArticuloInsumo salsaTomateCentro = articuloInsumoService.save(ArticuloInsumo.builder()
                    .denominacion("Salsa de Tomate_Centro") // ✅ Denominación única
                    .precioVenta(150.0) .precioCompra(100.0) .stockActual(600.0) .stockMinimo(100.0)
                    .esParaElaborar(true) .categoria(categoriaComida) .unidadMedida(unidadMililitros)
                    .imagen(imagenService.save(Imagen.builder().denominacion("https://example.com/salsatomate_centro.jpg").build())) // ✅ Imagen única
                    .sucursal(sucursalCentro) .build());

            ArticuloInsumo cebollaCentro = articuloInsumoService.save(ArticuloInsumo.builder()
                    .denominacion("Cebolla_Centro") // ✅ Denominación única
                    .precioVenta(80.0) .precioCompra(50.0) .stockActual(300.0) .stockMinimo(50.0)
                    .esParaElaborar(true) .categoria(categoriaVerdura) .unidadMedida(unidadGramos)
                    .imagen(imagenService.save(Imagen.builder().denominacion("https://example.com/cebolla_centro.jpg").build())) // ✅ Imagen única
                    .sucursal(sucursalCentro) .build());

            ArticuloInsumo jamonCocidoCentro = articuloInsumoService.save(ArticuloInsumo.builder()
                    .denominacion("Jamon Cocido_Centro") // ✅ Denominación única
                    .precioVenta(700.0) .precioCompra(500.0) .stockActual(800.0) .stockMinimo(150.0)
                    .esParaElaborar(true) .categoria(categoriaComida) .unidadMedida(unidadGramos)
                    .imagen(imagenService.save(Imagen.builder().denominacion("https://example.com/jamoncocido_centro.jpg").build())) // ✅ Imagen única
                    .sucursal(sucursalCentro) .build());

            ArticuloInsumo tapasEmpanadaCentro = articuloInsumoService.save(ArticuloInsumo.builder()
                    .denominacion("Tapas para Empanadas_Centro") // ✅ Denominación única
                    .precioVenta(150.0) .precioCompra(100.0) .stockActual(500.0) .stockMinimo(100.0)
                    .esParaElaborar(true) .categoria(categoriaEmpanada) .unidadMedida(unidadPorcion)
                    .imagen(imagenService.save(Imagen.builder().denominacion("https://example.com/tapasempanada_centro.jpg").build())) // ✅ Imagen única
                    .sucursal(sucursalCentro) .build());

            ArticuloInsumo carneMolidaCentro = articuloInsumoService.save(ArticuloInsumo.builder()
                    .denominacion("Carne Molida_Centro") // ✅ Denominación única
                    .precioVenta(1000.0) .precioCompra(800.0) .stockActual(400.0) .stockMinimo(100.0)
                    .esParaElaborar(true) .categoria(categoriaComida) .unidadMedida(unidadGramos)
                    .imagen(imagenService.save(Imagen.builder().denominacion("https://example.com/carnemolida_centro.jpg").build())) // ✅ Imagen única
                    .sucursal(sucursalCentro) .build());

            ArticuloInsumo bifeCarneCentro = articuloInsumoService.save(ArticuloInsumo.builder()
                    .denominacion("Bife de Carne_Centro") // ✅ Denominación única
                    .precioVenta(1200.0) .precioCompra(950.0) .stockActual(200.0) .stockMinimo(50.0)
                    .esParaElaborar(true) .categoria(categoriaComida) .unidadMedida(unidadPorcion)
                    .imagen(imagenService.save(Imagen.builder().denominacion("https://example.com/bifecarne_centro.jpg").build())) // ✅ Imagen única
                    .sucursal(sucursalCentro) .build());

            ArticuloInsumo panLomitoCentro = articuloInsumoService.save(ArticuloInsumo.builder()
                    .denominacion("Pan para Lomito_Centro") // ✅ Denominación única
                    .precioVenta(200.0) .precioCompra(120.0) .stockActual(300.0) .stockMinimo(50.0)
                    .esParaElaborar(true) .categoria(categoriaPan) .unidadMedida(unidadPorcion)
                    .imagen(imagenService.save(Imagen.builder().denominacion("https://example.com/panlomito_centro.jpg").build())) // ✅ Imagen única
                    .sucursal(sucursalCentro) .build());

            ArticuloInsumo panBarrolucoCentro = articuloInsumoService.save(ArticuloInsumo.builder()
                    .denominacion("Pan de Miga Cuadrado_Centro") // ✅ Denominación única
                    .precioVenta(180.0) .precioCompra(100.0) .stockActual(300.0) .stockMinimo(60.0)
                    .esParaElaborar(true) .categoria(categoriaPan) .unidadMedida(unidadPorcion)
                    .imagen(imagenService.save(Imagen.builder().denominacion("https://example.com/panbarroluco_centro.jpg").build())) // ✅ Imagen única
                    .sucursal(sucursalCentro) .build());

            // Insumos para Sucursal Maipú
            ArticuloInsumo insumoHarinaMaipu = articuloInsumoService.save(ArticuloInsumo.builder()
                    .denominacion("Harina_Maipu") // ✅ Denominación única
                    .precioVenta(520.0) .precioCompra(320.0) .stockActual(900.0) .stockMinimo(180.0)
                    .esParaElaborar(true) .categoria(categoriaComida) .unidadMedida(unidadGramos)
                    .imagen(imagenService.save(Imagen.builder().denominacion("https://example.com/harina_maipu.jpg").build())) // ✅ Imagen única
                    .sucursal(sucursalMaipu) .build());

            ArticuloInsumo masaPrepizzaMaipu = articuloInsumoService.save(ArticuloInsumo.builder()
                    .denominacion("Masa Prepizza_Maipu") // ✅ Denominación única
                    .precioVenta(210.0) .precioCompra(130.0) .stockActual(280.0) .stockMinimo(60.0)
                    .esParaElaborar(true) .categoria(categoriaPizza) .unidadMedida(unidadPorcion)
                    .imagen(imagenService.save(Imagen.builder().denominacion("https://example.com/masaprepizza_maipu.jpg").build())) // ✅ Imagen única
                    .sucursal(sucursalMaipu) .build());

            ArticuloInsumo quesoMuzzarellaMaipu = articuloInsumoService.save(ArticuloInsumo.builder()
                    .denominacion("Queso Muzzarella_Maipu") // ✅ Denominación única
                    .precioVenta(820.0) .precioCompra(620.0) .stockActual(950.0) .stockMinimo(210.0)
                    .esParaElaborar(true) .categoria(categoriaQueso) .unidadMedida(unidadGramos)
                    .imagen(imagenService.save(Imagen.builder().denominacion("https://example.com/muzzarella_maipu.jpg").build())) // ✅ Imagen única
                    .sucursal(sucursalMaipu) .build());

            ArticuloInsumo salsaTomateMaipu = articuloInsumoService.save(ArticuloInsumo.builder()
                    .denominacion("Salsa de Tomate_Maipu") // ✅ Denominación única
                    .precioVenta(160.0) .precioCompra(110.0) .stockActual(580.0) .stockMinimo(110.0)
                    .esParaElaborar(true) .categoria(categoriaComida) .unidadMedida(unidadMililitros)
                    .imagen(imagenService.save(Imagen.builder().denominacion("https://example.com/salsatomate_maipu.jpg").build())) // ✅ Imagen única
                    .sucursal(sucursalMaipu) .build());

            ArticuloInsumo quesoRoquefortMaipu = articuloInsumoService.save(ArticuloInsumo.builder()
                    .denominacion("Queso Roquefort_Maipu") // ✅ Denominación única
                    .precioVenta(1250.0) .precioCompra(950.0) .stockActual(480.0) .stockMinimo(110.0)
                    .esParaElaborar(true) .categoria(categoriaQueso) .unidadMedida(unidadGramos)
                    .imagen(imagenService.save(Imagen.builder().denominacion("https://example.com/roquefort_maipu.jpg").build())) // ✅ Imagen única
                    .sucursal(sucursalMaipu) .build());

            ArticuloInsumo quesoBrieMaipu = articuloInsumoService.save(ArticuloInsumo.builder()
                    .denominacion("Queso Brie_Maipu") // ✅ Denominación única
                    .precioVenta(1550.0) .precioCompra(1050.0) .stockActual(380.0) .stockMinimo(110.0)
                    .esParaElaborar(true) .categoria(categoriaQueso) .unidadMedida(unidadGramos)
                    .imagen(imagenService.save(Imagen.builder().denominacion("https://example.com/brie_maipu.jpg").build())) // ✅ Imagen única
                    .sucursal(sucursalMaipu) .build());

            ArticuloInsumo quesoParmesanoMaipu = articuloInsumoService.save(ArticuloInsumo.builder()
                    .denominacion("Queso Parmesano_Maipu") // ✅ Denominación única
                    .precioVenta(1150.0) .precioCompra(900.0) .stockActual(480.0) .stockMinimo(110.0)
                    .esParaElaborar(true) .categoria(categoriaQueso) .unidadMedida(unidadGramos)
                    .imagen(imagenService.save(Imagen.builder().denominacion("https://example.com/parmesano_maipu.jpg").build())) // ✅ Imagen única
                    .sucursal(sucursalMaipu) .build());

            ArticuloInsumo medallonCarneMaipu = articuloInsumoService.save(ArticuloInsumo.builder()
                    .denominacion("Medallón de Carne_Maipu") // ✅ Denominación única
                    .precioVenta(600.0) .precioCompra(400.0) .stockActual(250.0) .stockMinimo(50.0)
                    .esParaElaborar(true) .categoria(categoriaHamburguesa) .unidadMedida(unidadPorcion)
                    .imagen(imagenService.save(Imagen.builder().denominacion("https://example.com/medalloncarne_maipu.jpg").build())) // ✅ Imagen única
                    .sucursal(sucursalMaipu) .build());

            ArticuloInsumo panHamburguesaMaipu = articuloInsumoService.save(ArticuloInsumo.builder()
                    .denominacion("Pan de Hamburguesa_Maipu") // ✅ Denominación única
                    .precioVenta(150.0) .precioCompra(90.0) .stockActual(300.0) .stockMinimo(50.0)
                    .esParaElaborar(true) .categoria(categoriaPan) .unidadMedida(unidadPorcion)
                    .imagen(imagenService.save(Imagen.builder().denominacion("https://example.com/panhamburguesa_maipu.jpg").build())) // ✅ Imagen única
                    .sucursal(sucursalMaipu) .build());

            ArticuloInsumo lechugaMaipu = articuloInsumoService.save(ArticuloInsumo.builder()
                    .denominacion("Lechuga_Maipu") // ✅ Denominación única
                    .precioVenta(60.0) .precioCompra(30.0) .stockActual(400.0) .stockMinimo(100.0)
                    .esParaElaborar(true) .categoria(categoriaVerdura) .unidadMedida(unidadGramos)
                    .imagen(imagenService.save(Imagen.builder().denominacion("https://example.com/lechuga_maipu.jpg").build())) // ✅ Imagen única
                    .sucursal(sucursalMaipu) .build());

            ArticuloInsumo cheddarMaipu = articuloInsumoService.save(ArticuloInsumo.builder()
                    .denominacion("Queso Cheddar_Maipu") // ✅ Denominación única
                    .precioVenta(400.0) .precioCompra(250.0) .stockActual(600.0) .stockMinimo(100.0)
                    .esParaElaborar(true) .categoria(categoriaQueso) .unidadMedida(unidadGramos)
                    .imagen(imagenService.save(Imagen.builder().denominacion("https://example.com/cheddar_maipu.jpg").build())) // ✅ Imagen única
                    .sucursal(sucursalMaipu) .build());


            // 11. Artículo Manufacturado (Asociar a sucursales y usar insumos correctos y únicos)
            // Artículos Manufacturados para Sucursal Centro
            ArticuloManufacturado hamburguesaCentro = ArticuloManufacturado.builder()
                    .denominacion("Hamburguesa Clásica_Centro") // ✅ Denominación única
                    .precioVenta(1250.0)
                    .descripcion("Deliciosa hamburguesa con queso y tomate.")
                    .tiempoEstimadoMinutos(20)
                    .preparacion("Preparar la carne, cocinar, armar.")
                    .categoria(categoriaComida)
                    .unidadMedida(unidadPorcion)
                    .imagen(imagenService.save(Imagen.builder().denominacion("https://example.com/hamburguesasimple_centro.jpg").build())) // ✅ Imagen única
                    .sucursal(sucursalCentro)
                    .build();
            ArticuloManufacturado hamburguesaTriple = ArticuloManufacturado.builder()
                    .denominacion("Hamburguesa Triple")
                    .precioVenta(13500.0)
                    .descripcion("Tres jugosos medallones de carne, queso cheddar y lechuga en pan artesanal.")
                    .tiempoEstimadoMinutos(25)
                    .preparacion("Cocinar los tres medallones, apilar con queso cheddar, armar la hamburguesa.")
                    .categoria(categoriaComida)
                    .unidadMedida(unidadPorcion)
                    .imagen(imgHamburguesaTriple)
                    .build();
            ArticuloManufacturado hamburguesaPolloSimple = ArticuloManufacturado.builder()
                    .denominacion("Hamburguesa de Pollo")
                    .precioVenta(8500.0)
                    .descripcion("Medallón de pollo crujiente con queso cheddar y lechuga fresca.")
                    .tiempoEstimadoMinutos(20)
                    .preparacion("Freír el medallón de pollo, montar con cheddar y lechuga en el pan.")
                    .categoria(categoriaComida)
                    .unidadMedida(unidadPorcion)
                    .imagen(imgHamburguesaPolloSimple)
                    .build();
            ArticuloManufacturado hamburguesaPolloDoble = ArticuloManufacturado.builder()
                    .denominacion("Hamburguesa de Pollo Doble")
                    .precioVenta(10500.0)
                    .descripcion("Doble medallón de pollo, doble cheddar, lechuga fresca y pan suave.")
                    .tiempoEstimadoMinutos(23)
                    .preparacion("Freír ambos medallones, montar con cheddar y lechuga en el pan.")
                    .categoria(categoriaComida)
                    .unidadMedida(unidadPorcion)
                    .imagen(imgHamburguesaPolloDoble)
                    .build();
            ArticuloManufacturado pizzaMuzzarella = ArticuloManufacturado.builder()
                    .denominacion("Pizza Muzzarella")
            List<ArticuloManufacturadoDetalle> detallesHamburguesaCentro = new ArrayList<>();
            detallesHamburguesaCentro.add(ArticuloManufacturadoDetalle.builder().cantidad(200.0).articuloInsumo(insumoHarinaCentro).articuloManufacturado(hamburguesaCentro).build());
            detallesHamburguesaCentro.add(ArticuloManufacturadoDetalle.builder().cantidad(50.0).articuloInsumo(insumoTomateCentro).articuloManufacturado(hamburguesaCentro).build());
            hamburguesaCentro.setDetalles(detallesHamburguesaCentro);
            articuloManufacturadoService.save(hamburguesaCentro);

            ArticuloManufacturado pizzaMuzzarellaCentro = ArticuloManufacturado.builder()
                    .denominacion("Pizza Muzzarella_Centro") // ✅ Denominación única
                    .precioVenta(7500.0)
                    .descripcion("Clásica pizza con abundante queso muzzarella y salsa de tomate.")
                    .tiempoEstimadoMinutos(30)
                    .preparacion("Agregar salsa y muzzarella a la masa y hornear.")
                    .categoria(categoriaPizza)
                    .unidadMedida(unidadPorcion)
                    .imagen(imagenService.save(Imagen.builder().denominacion("https://example.com/pizzamuzzarella_centro.jpg").build())) // ✅ Imagen única
                    .sucursal(sucursalCentro)
                    .build();
            ArticuloManufacturado pizzaMargarita = ArticuloManufacturado.builder()
                    .denominacion("Pizza Margarita")
                    .precioVenta(8000.0)
                    .descripcion("Pizza napolitana con muzzarella, tomate en rodajas y albahaca fresca.")
                    .tiempoEstimadoMinutos(30)
                    .preparacion("Montar con muzzarella, tomate y albahaca; hornear.")
                    .categoria(categoriaPizza)
                    .unidadMedida(unidadPorcion)
                    .imagen(imgPizzaMargarita)
                    .build();
            ArticuloManufacturado pizza4Quesos = ArticuloManufacturado.builder()
                    .denominacion("Pizza Cuatro Quesos")
                    .precioVenta(9500.0)
                    .descripcion("Muzzarella, brie, roquefort y parmesano fundidos sobre masa artesanal.")
                    .tiempoEstimadoMinutos(30)
                    .preparacion("Montar con mezcla de quesos sobre la masa con salsa, hornear.")
                    .categoria(categoriaPizza)
                    .unidadMedida(unidadPorcion)
                    .imagen(imgPizza4Quesos)
                    .build();
            ArticuloManufacturado pizzaEspecial = ArticuloManufacturado.builder()
                    .denominacion("Pizza Especial")
                    .precioVenta(8800.0)
                    .descripcion("Pizza con jamón cocido, morrones y muzzarella.")
                    .tiempoEstimadoMinutos(30)
                    .preparacion("Montar con salsa, muzzarella, jamón y morrones, hornear.")
                    .categoria(categoriaPizza)
                    .unidadMedida(unidadPorcion)
                    .imagen(imgPizzaEspecial)
                    .build();
            ArticuloManufacturado pizzaFugazzetta = ArticuloManufacturado.builder()
                    .denominacion("Pizza Fugazzetta")
                    .precioVenta(8200.0)
                    .descripcion("Pizza rellena con muzzarella y cubierta de cebolla caramelizada.")
                    .tiempoEstimadoMinutos(30)
                    .preparacion("Rellenar con queso, cubrir con cebolla, hornear hasta dorar.")
                    .categoria(categoriaPizza)
                    .unidadMedida(unidadPorcion)
                    .imagen(imgPizzaFugazzetta)
                    .build();
            ArticuloManufacturado empanadasCarne = ArticuloManufacturado.builder()
                    .denominacion("Empanadas de Carne")

            List<ArticuloManufacturadoDetalle> detallesMuzzarellaCentro = new ArrayList<>();
            detallesMuzzarellaCentro.add(ArticuloManufacturadoDetalle.builder().cantidad(1.0).articuloInsumo(masaPrepizzaCentro).articuloManufacturado(pizzaMuzzarellaCentro).build());
            detallesMuzzarellaCentro.add(ArticuloManufacturadoDetalle.builder().cantidad(80.0).articuloInsumo(salsaTomateCentro).articuloManufacturado(pizzaMuzzarellaCentro).build());
            detallesMuzzarellaCentro.add(ArticuloManufacturadoDetalle.builder().cantidad(150.0).articuloInsumo(quesoMuzzarellaCentro).articuloManufacturado(pizzaMuzzarellaCentro).build());
            pizzaMuzzarellaCentro.setDetalles(detallesMuzzarellaCentro);
            articuloManufacturadoService.save(pizzaMuzzarellaCentro);

            ArticuloManufacturado empanadasCarneCentro = ArticuloManufacturado.builder()
                    .denominacion("Empanadas de Carne_Centro") // ✅ Denominación única

                    .precioVenta(500.0)
                    .descripcion("Empanadas rellenas de sabrosa carne molida condimentada.")
                    .tiempoEstimadoMinutos(25)
                    .preparacion("Preparar el relleno, rellenar las tapas y hornear.")
                    .categoria(categoriaEmpanada)
                    .unidadMedida(unidadPorcion)
                    .imagen(imagenService.save(Imagen.builder().denominacion("https://example.com/empanadascarne_centro.jpg").build())) // ✅ Imagen única
                    .sucursal(sucursalCentro)
                    .build();

            ArticuloManufacturado empanadasJyQ = ArticuloManufacturado.builder()
                    .denominacion("Empanadas de Jamón y Queso")

            List<ArticuloManufacturadoDetalle> detallesCarneCentro = new ArrayList<>();
            detallesCarneCentro.add(ArticuloManufacturadoDetalle.builder().cantidad(1.0).articuloInsumo(tapasEmpanadaCentro).articuloManufacturado(empanadasCarneCentro).build());
            detallesCarneCentro.add(ArticuloManufacturadoDetalle.builder().cantidad(60.0).articuloInsumo(carneMolidaCentro).articuloManufacturado(empanadasCarneCentro).build());
            detallesCarneCentro.add(ArticuloManufacturadoDetalle.builder().cantidad(20.0).articuloInsumo(cebollaCentro).articuloManufacturado(empanadasCarneCentro).build());
            empanadasCarneCentro.setDetalles(detallesCarneCentro);
            articuloManufacturadoService.save(empanadasCarneCentro);

            ArticuloManufacturado empanadasJyQCentro = ArticuloManufacturado.builder()
                    .denominacion("Empanadas de Jamon y Queso_Centro") // ✅ Denominación única

                    .precioVenta(550.0)
                    .descripcion("Empanadas con relleno cremoso de jamón cocido y queso.")
                    .tiempoEstimadoMinutos(25)
                    .preparacion("Rellenar las tapas con jamón y queso, cerrar y hornear.")
                    .categoria(categoriaEmpanada)
                    .unidadMedida(unidadPorcion)
                    .imagen(imagenService.save(Imagen.builder().denominacion("https://example.com/empanadasjyq_centro.jpg").build())) // ✅ Imagen única
                    .sucursal(sucursalCentro)
                    .build();

            ArticuloManufacturado lomitoSimple = ArticuloManufacturado.builder()
                    .denominacion("Lomito Simple")
                    .precioVenta(8500.0)
                    .descripcion("Lomito con carne, lechuga, tomate, jamón y queso en pan artesanal.")
                    .tiempoEstimadoMinutos(20)
                    .preparacion("Cocinar la carne, montar el lomito con vegetales, jamón y queso.")
                    .categoria(categoriaSanguche)

            List<ArticuloManufacturadoDetalle> detallesJyQCentro = new ArrayList<>();
            detallesJyQCentro.add(ArticuloManufacturadoDetalle.builder().cantidad(1.0).articuloInsumo(tapasEmpanadaCentro).articuloManufacturado(empanadasJyQCentro).build());
            detallesJyQCentro.add(ArticuloManufacturadoDetalle.builder().cantidad(40.0).articuloInsumo(jamonCocidoCentro).articuloManufacturado(empanadasJyQCentro).build());
            detallesJyQCentro.add(ArticuloManufacturadoDetalle.builder().cantidad(40.0).articuloInsumo(quesoMuzzarellaCentro).articuloManufacturado(empanadasJyQCentro).build());
            empanadasJyQCentro.setDetalles(detallesJyQCentro);
            articuloManufacturadoService.save(empanadasJyQCentro);

            // Artículos Manufacturados para Sucursal Maipú
            ArticuloManufacturado hamburguesaMaipu = ArticuloManufacturado.builder()
                    .denominacion("Hamburguesa Clásica_Maipu") // ✅ Denominación única
                    .precioVenta(1300.0)
                    .descripcion("Deliciosa hamburguesa con queso y lechuga")
                    .tiempoEstimadoMinutos(22)
                    .preparacion("Preparar la carne, cocinar, armar.")
                    .categoria(categoriaComida)

                    .unidadMedida(unidadPorcion)
                    .imagen(imagenService.save(Imagen.builder().denominacion("https://example.com/hamburguesasimple_maipu.jpg").build())) // ✅ Imagen única
                    .sucursal(sucursalMaipu)
                    .build();

            ArticuloManufacturado barrolucoSimple = ArticuloManufacturado.builder()
                    .denominacion("Barroluco Simple")
                    .precioVenta(8200.0)
                    .descripcion("Sándwich de miga tostado con carne, jamón y queso fundido.")
                    .tiempoEstimadoMinutos(18)
                    .preparacion("Montar el barroluco y tostar en plancha hasta que gratine.")
                    .categoria(categoriaSanguche)

            List<ArticuloManufacturadoDetalle> detallesHamburguesaMaipu = new ArrayList<>();
            detallesHamburguesaMaipu.add(ArticuloManufacturadoDetalle.builder().cantidad(1.0).articuloInsumo(panHamburguesaMaipu).articuloManufacturado(hamburguesaMaipu).build());
            detallesHamburguesaMaipu.add(ArticuloManufacturadoDetalle.builder().cantidad(1.0).articuloInsumo(medallonCarneMaipu).articuloManufacturado(hamburguesaMaipu).build());
            detallesHamburguesaMaipu.add(ArticuloManufacturadoDetalle.builder().cantidad(30.0).articuloInsumo(lechugaMaipu).articuloManufacturado(hamburguesaMaipu).build());
            detallesHamburguesaMaipu.add(ArticuloManufacturadoDetalle.builder().cantidad(40.0).articuloInsumo(cheddarMaipu).articuloManufacturado(hamburguesaMaipu).build());
            hamburguesaMaipu.setDetalles(detallesHamburguesaMaipu);
            articuloManufacturadoService.save(hamburguesaMaipu);

            ArticuloManufacturado pizza4QuesosMaipu = ArticuloManufacturado.builder()
                    .denominacion("Pizza Cuatro Quesos_Maipu") // ✅ Denominación única
                    .precioVenta(9800.0)
                    .descripcion("Muzzarella, brie, roquefort y parmesano fundidos sobre masa artesanal.")
                    .tiempoEstimadoMinutos(35)
                    .preparacion("Montar con mezcla de quesos sobre la masa con salsa, hornear.")
                    .categoria(categoriaPizza)

                    .unidadMedida(unidadPorcion)
                    .imagen(imagenService.save(Imagen.builder().denominacion("https://example.com/pizza4quesos_maipu.jpg").build())) // ✅ Imagen única
                    .sucursal(sucursalMaipu)
                    .build();

            List<ArticuloManufacturadoDetalle> detalles = new ArrayList<>();
            detalles.add(ArticuloManufacturadoDetalle.builder().cantidad(200.0).articuloInsumo(insumoHarina).articuloManufacturado(hamburguesa).build());
            detalles.add(ArticuloManufacturadoDetalle.builder().cantidad(50.0).articuloInsumo(insumoTomate).articuloManufacturado(hamburguesa).build());
            hamburguesa.setDetalles(detalles);
            articuloManufacturadoService.save(hamburguesa);
            List<ArticuloManufacturadoDetalle> detallesTriple = new ArrayList<>();
            detallesTriple.add(ArticuloManufacturadoDetalle.builder().cantidad(1.0).articuloInsumo(panHamburguesa).articuloManufacturado(hamburguesaTriple).build());
            detallesTriple.add(ArticuloManufacturadoDetalle.builder().cantidad(3.0).articuloInsumo(medallonCarne).articuloManufacturado(hamburguesaTriple).build());
            detallesTriple.add(ArticuloManufacturadoDetalle.builder().cantidad(90.0).articuloInsumo(cheddar).articuloManufacturado(hamburguesaTriple).build());
            detallesTriple.add(ArticuloManufacturadoDetalle.builder().cantidad(30.0).articuloInsumo(lechuga).articuloManufacturado(hamburguesaTriple).build());
            hamburguesaTriple.setDetalles(detallesTriple);
            articuloManufacturadoService.save(hamburguesaTriple);
            List<ArticuloManufacturadoDetalle> detallesPolloSimple = new ArrayList<>();
            detallesPolloSimple.add(ArticuloManufacturadoDetalle.builder().cantidad(1.0).articuloInsumo(panHamburguesa).articuloManufacturado(hamburguesaPolloSimple).build());
            detallesPolloSimple.add(ArticuloManufacturadoDetalle.builder().cantidad(1.0).articuloInsumo(medallonPollo).articuloManufacturado(hamburguesaPolloSimple).build());
            detallesPolloSimple.add(ArticuloManufacturadoDetalle.builder().cantidad(30.0).articuloInsumo(cheddar).articuloManufacturado(hamburguesaPolloSimple).build());
            detallesPolloSimple.add(ArticuloManufacturadoDetalle.builder().cantidad(20.0).articuloInsumo(lechuga).articuloManufacturado(hamburguesaPolloSimple).build());
            hamburguesaPolloSimple.setDetalles(detallesPolloSimple);
            articuloManufacturadoService.save(hamburguesaPolloSimple);
            List<ArticuloManufacturadoDetalle> detallesPolloDoble = new ArrayList<>();
            detallesPolloDoble.add(ArticuloManufacturadoDetalle.builder().cantidad(1.0).articuloInsumo(panHamburguesa).articuloManufacturado(hamburguesaPolloDoble).build());
            detallesPolloDoble.add(ArticuloManufacturadoDetalle.builder().cantidad(2.0).articuloInsumo(medallonPollo).articuloManufacturado(hamburguesaPolloDoble).build());
            detallesPolloDoble.add(ArticuloManufacturadoDetalle.builder().cantidad(60.0).articuloInsumo(cheddar).articuloManufacturado(hamburguesaPolloDoble).build());
            detallesPolloDoble.add(ArticuloManufacturadoDetalle.builder().cantidad(30.0).articuloInsumo(lechuga).articuloManufacturado(hamburguesaPolloDoble).build());
            hamburguesaPolloDoble.setDetalles(detallesPolloDoble);
            articuloManufacturadoService.save(hamburguesaPolloDoble);
            List<ArticuloManufacturadoDetalle> detallesMuzzarella = new ArrayList<>();
            detallesMuzzarella.add(ArticuloManufacturadoDetalle.builder().cantidad(1.0).articuloInsumo(masaPrepizza).articuloManufacturado(pizzaMuzzarella).build());
            detallesMuzzarella.add(ArticuloManufacturadoDetalle.builder().cantidad(80.0).articuloInsumo(salsaTomate).articuloManufacturado(pizzaMuzzarella).build());
            detallesMuzzarella.add(ArticuloManufacturadoDetalle.builder().cantidad(150.0).articuloInsumo(quesoMuzzarella).articuloManufacturado(pizzaMuzzarella).build());
            pizzaMuzzarella.setDetalles(detallesMuzzarella);
            articuloManufacturadoService.save(pizzaMuzzarella);
            List<ArticuloManufacturadoDetalle> detallesMargarita = new ArrayList<>();
            detallesMargarita.add(ArticuloManufacturadoDetalle.builder().cantidad(1.0).articuloInsumo(masaPrepizza).articuloManufacturado(pizzaMargarita).build());
            detallesMargarita.add(ArticuloManufacturadoDetalle.builder().cantidad(80.0).articuloInsumo(salsaTomate).articuloManufacturado(pizzaMargarita).build());
            detallesMargarita.add(ArticuloManufacturadoDetalle.builder().cantidad(130.0).articuloInsumo(quesoMuzzarella).articuloManufacturado(pizzaMargarita).build());
            detallesMargarita.add(ArticuloManufacturadoDetalle.builder().cantidad(40.0).articuloInsumo(insumoTomate).articuloManufacturado(pizzaMargarita).build());
            detallesMargarita.add(ArticuloManufacturadoDetalle.builder().cantidad(2.0).articuloInsumo(albahaca).articuloManufacturado(pizzaMargarita).build());
            pizzaMargarita.setDetalles(detallesMargarita);
            articuloManufacturadoService.save(pizzaMargarita);
            List<ArticuloManufacturadoDetalle> detalles4Quesos = new ArrayList<>();
            detalles4Quesos.add(ArticuloManufacturadoDetalle.builder().cantidad(1.0).articuloInsumo(masaPrepizza).articuloManufacturado(pizza4Quesos).build());
            detalles4Quesos.add(ArticuloManufacturadoDetalle.builder().cantidad(60.0).articuloInsumo(salsaTomate).articuloManufacturado(pizza4Quesos).build());
            detalles4Quesos.add(ArticuloManufacturadoDetalle.builder().cantidad(100.0).articuloInsumo(quesoMuzzarella).articuloManufacturado(pizza4Quesos).build());
            detalles4Quesos.add(ArticuloManufacturadoDetalle.builder().cantidad(40.0).articuloInsumo(quesoRoquefort).articuloManufacturado(pizza4Quesos).build());
            detalles4Quesos.add(ArticuloManufacturadoDetalle.builder().cantidad(30.0).articuloInsumo(quesoBrie).articuloManufacturado(pizza4Quesos).build());
            detalles4Quesos.add(ArticuloManufacturadoDetalle.builder().cantidad(30.0).articuloInsumo(quesoParmesano).articuloManufacturado(pizza4Quesos).build());
            pizza4Quesos.setDetalles(detalles4Quesos);
            articuloManufacturadoService.save(pizza4Quesos);
            List<ArticuloManufacturadoDetalle> detallesEspecial = new ArrayList<>();
            detallesEspecial.add(ArticuloManufacturadoDetalle.builder().cantidad(1.0).articuloInsumo(masaPrepizza).articuloManufacturado(pizzaEspecial).build());
            detallesEspecial.add(ArticuloManufacturadoDetalle.builder().cantidad(80.0).articuloInsumo(salsaTomate).articuloManufacturado(pizzaEspecial).build());
            detallesEspecial.add(ArticuloManufacturadoDetalle.builder().cantidad(140.0).articuloInsumo(quesoMuzzarella).articuloManufacturado(pizzaEspecial).build());
            detallesEspecial.add(ArticuloManufacturadoDetalle.builder().cantidad(60.0).articuloInsumo(jamonCocido).articuloManufacturado(pizzaEspecial).build());
            detallesEspecial.add(ArticuloManufacturadoDetalle.builder().cantidad(30.0).articuloInsumo(insumoTomate).articuloManufacturado(pizzaEspecial).build());
            pizzaEspecial.setDetalles(detallesEspecial);
            articuloManufacturadoService.save(pizzaEspecial);
            List<ArticuloManufacturadoDetalle> detallesFugazzetta = new ArrayList<>();
            detallesFugazzetta.add(ArticuloManufacturadoDetalle.builder().cantidad(1.0).articuloInsumo(masaPrepizza).articuloManufacturado(pizzaFugazzetta).build());
            detallesFugazzetta.add(ArticuloManufacturadoDetalle.builder().cantidad(150.0).articuloInsumo(quesoMuzzarella).articuloManufacturado(pizzaFugazzetta).build());
            detallesFugazzetta.add(ArticuloManufacturadoDetalle.builder().cantidad(60.0).articuloInsumo(cebolla).articuloManufacturado(pizzaFugazzetta).build());
            pizzaFugazzetta.setDetalles(detallesFugazzetta);
            articuloManufacturadoService.save(pizzaFugazzetta);
            List<ArticuloManufacturadoDetalle> detallesCarne = new ArrayList<>();
            detallesCarne.add(ArticuloManufacturadoDetalle.builder().cantidad(1.0).articuloInsumo(tapasEmpanada).articuloManufacturado(empanadasCarne).build());
            detallesCarne.add(ArticuloManufacturadoDetalle.builder().cantidad(60.0).articuloInsumo(carneMolida).articuloManufacturado(empanadasCarne).build());
            detallesCarne.add(ArticuloManufacturadoDetalle.builder().cantidad(20.0).articuloInsumo(cebolla).articuloManufacturado(empanadasCarne).build());
            empanadasCarne.setDetalles(detallesCarne);
            articuloManufacturadoService.save(empanadasCarne);
            List<ArticuloManufacturadoDetalle> detallesJyQ = new ArrayList<>();
            detallesJyQ.add(ArticuloManufacturadoDetalle.builder().cantidad(1.0).articuloInsumo(tapasEmpanada).articuloManufacturado(empanadasJyQ).build());
            detallesJyQ.add(ArticuloManufacturadoDetalle.builder().cantidad(40.0).articuloInsumo(jamonCocido).articuloManufacturado(empanadasJyQ).build());
            detallesJyQ.add(ArticuloManufacturadoDetalle.builder().cantidad(40.0).articuloInsumo(quesoMuzzarella).articuloManufacturado(empanadasJyQ).build());
            empanadasJyQ.setDetalles(detallesJyQ);
            articuloManufacturadoService.save(empanadasJyQ);
            List<ArticuloManufacturadoDetalle> detallesLomito = new ArrayList<>();
            detallesLomito.add(ArticuloManufacturadoDetalle.builder().cantidad(1.0).articuloInsumo(panLomito).articuloManufacturado(lomitoSimple).build());
            detallesLomito.add(ArticuloManufacturadoDetalle.builder().cantidad(1.0).articuloInsumo(bifeCarne).articuloManufacturado(lomitoSimple).build());
            detallesLomito.add(ArticuloManufacturadoDetalle.builder().cantidad(20.0).articuloInsumo(lechuga).articuloManufacturado(lomitoSimple).build());
            detallesLomito.add(ArticuloManufacturadoDetalle.builder().cantidad(30.0).articuloInsumo(insumoTomate).articuloManufacturado(lomitoSimple).build());
            detallesLomito.add(ArticuloManufacturadoDetalle.builder().cantidad(40.0).articuloInsumo(cheddar).articuloManufacturado(lomitoSimple).build());
            lomitoSimple.setDetalles(detallesLomito);
            articuloManufacturadoService.save(lomitoSimple);
            List<ArticuloManufacturadoDetalle> detallesBarroluco = new ArrayList<>();
            detallesBarroluco.add(ArticuloManufacturadoDetalle.builder().cantidad(1.0).articuloInsumo(panBarroluco).articuloManufacturado(barrolucoSimple).build());
            detallesBarroluco.add(ArticuloManufacturadoDetalle.builder().cantidad(1.0).articuloInsumo(bifeCarne).articuloManufacturado(barrolucoSimple).build());
            detallesBarroluco.add(ArticuloManufacturadoDetalle.builder().cantidad(30.0).articuloInsumo(insumoTomate).articuloManufacturado(barrolucoSimple).build());
            detallesBarroluco.add(ArticuloManufacturadoDetalle.builder().cantidad(20.0).articuloInsumo(lechuga).articuloManufacturado(barrolucoSimple).build());
            detallesBarroluco.add(ArticuloManufacturadoDetalle.builder().cantidad(40.0).articuloInsumo(jamonCocido).articuloManufacturado(barrolucoSimple).build());
            detallesBarroluco.add(ArticuloManufacturadoDetalle.builder().cantidad(40.0).articuloInsumo(cheddar).articuloManufacturado(barrolucoSimple).build());
            barrolucoSimple.setDetalles(detallesBarroluco);
            articuloManufacturadoService.save(barrolucoSimple);
            List<ArticuloManufacturadoDetalle> detalles4QuesosMaipu = new ArrayList<>();
            detalles4QuesosMaipu.add(ArticuloManufacturadoDetalle.builder().cantidad(1.0).articuloInsumo(masaPrepizzaMaipu).articuloManufacturado(pizza4QuesosMaipu).build());
            detalles4QuesosMaipu.add(ArticuloManufacturadoDetalle.builder().cantidad(60.0).articuloInsumo(salsaTomateMaipu).articuloManufacturado(pizza4QuesosMaipu).build());
            detalles4QuesosMaipu.add(ArticuloManufacturadoDetalle.builder().cantidad(100.0).articuloInsumo(quesoMuzzarellaMaipu).articuloManufacturado(pizza4QuesosMaipu).build());
            detalles4QuesosMaipu.add(ArticuloManufacturadoDetalle.builder().cantidad(40.0).articuloInsumo(quesoRoquefortMaipu).articuloManufacturado(pizza4QuesosMaipu).build());
            detalles4QuesosMaipu.add(ArticuloManufacturadoDetalle.builder().cantidad(30.0).articuloInsumo(quesoBrieMaipu).articuloManufacturado(pizza4QuesosMaipu).build());
            detalles4QuesosMaipu.add(ArticuloManufacturadoDetalle.builder().cantidad(30.0).articuloInsumo(quesoParmesanoMaipu).articuloManufacturado(pizza4QuesosMaipu).build());
            pizza4QuesosMaipu.setDetalles(detalles4QuesosMaipu);
            articuloManufacturadoService.save(pizza4QuesosMaipu);

            // Puedes seguir agregando más artículos y sus detalles, asociándolos a sucursalCentro o sucursalMaipu


            // 12. Promociones (Asociar a sucursales - ManyToMany)
            Promocion promoHappyHour = Promocion.builder()
                    .denominacion("Happy Hour de Pizzas")
                    .fechaDesde(LocalDate.of(2024, 1, 1))
                    .fechaHasta(LocalDate.of(2024, 12, 31))
                    .horaDesde(LocalTime.of(18, 0))
                    .horaHasta(LocalTime.of(20, 0))
                    .descripcionDescuento("2x1 en Pizzas seleccionadas")
                    .precioPromocional(8000.0)
                    .tipoPromocion(TipoPromocion.HAPPY_HOUR)
                    .imagen(imagenService.save(Imagen.builder().denominacion("https://example.com/promohappyhour.jpg").build())) // ✅ Imagen única
                    .articulosManufacturados(Arrays.asList(pizzaMuzzarellaCentro, pizza4QuesosMaipu))
                    .sucursales(Arrays.asList(sucursalCentro, sucursalMaipu))
                    .build();
            promocionService.save(promoHappyHour);

            Promocion promoEmpanadas = Promocion.builder()
                    .denominacion("Docena de Empanadas con Descuento")
                    .fechaDesde(LocalDate.of(2024, 6, 1))
                    .fechaHasta(LocalDate.of(2024, 8, 31))
                    .horaDesde(LocalTime.of(12, 0))
                    .horaHasta(LocalTime.of(22, 0))
                    .descripcionDescuento("12 Empanadas al precio de 10")
                    .precioPromocional(5000.0)
                    .tipoPromocion(TipoPromocion.PROMOCION_GENERAL)
                    .imagen(imagenService.save(Imagen.builder().denominacion("https://example.com/promoempanadas.jpg").build())) // ✅ Imagen única
                    .articulosManufacturados(Arrays.asList(empanadasCarneCentro, empanadasJyQCentro))
                    .sucursales(Arrays.asList(sucursalCentro))
                    .build();
            promocionService.save(promoEmpanadas);

            // 13. Usuarios y Roles
            Usuario usuarioAdmin = usuarioService.save(Usuario.builder()
                    .email("admin@buen.com")
                    .password(passwordEncoder.encode("admin123"))
                    .nombre("Admin General")
                    .rol(Rol.ADMINISTRADOR)
                    .build());
            Usuario usuarioCocinero = usuarioService.save(Usuario.builder()
                    .email("cocinero@buen.com")
                    .password(passwordEncoder.encode("cocinero123"))
                    .nombre("Juan Cocinero")
                    .rol(Rol.COCINERO)
                    .build());
            // === 9. Cargar pedidos de prueba ===
            Pedido pedido1 = Pedido.builder()
                    .cliente(cliente)
                    .estado(Estado.ENTREGADO)
                    .formaPago(FormaPago.EFECTIVO)
                    .tipoEnvio(TipoEnvio.DELIVERY)
                    .fechaPedido(LocalDate.of(2025, 3, 15))
                    .horaEstimadaFinalizacion(LocalDate.now().atTime(13, 0).toLocalTime())
                    .total(hamburguesa.getPrecioVenta() * 2)
                    .totalCosto(hamburguesa.getPrecioVenta() * 2 * 0.6)
                    .detallesPedidos(Set.of(
                            DetallePedido.builder().articuloManufacturado(hamburguesa).cantidad(2).subTotal(hamburguesa.getPrecioVenta() * 2).build()
                    ))
                    .build();
            Pedido pedido2 = Pedido.builder()
                    .cliente(cliente)
                    .estado(Estado.ENTREGADO)
                    .formaPago(FormaPago.MERCADO_PAGO)
                    .tipoEnvio(TipoEnvio.RETIRO_EN_LOCAL)
                    .fechaPedido(LocalDate.of(2025, 4, 5))
                    .horaEstimadaFinalizacion(LocalDate.now().atTime(20, 30).toLocalTime())
                    .total(pizzaMuzzarella.getPrecioVenta())
                    .totalCosto(pizzaMuzzarella.getPrecioVenta() * 0.6)
                    .detallesPedidos(Set.of(
                            DetallePedido.builder().articuloManufacturado(pizzaMuzzarella).cantidad(1).subTotal(pizzaMuzzarella.getPrecioVenta()).build()
                    ))
                    .build();
            Pedido pedido3 = Pedido.builder()
                    .cliente(cliente)
                    .estado(Estado.ENTREGADO)
                    .formaPago(FormaPago.EFECTIVO)
                    .tipoEnvio(TipoEnvio.DELIVERY)
                    .fechaPedido(LocalDate.of(2025, 7, 12))
                    .horaEstimadaFinalizacion(LocalDate.now().atTime(19, 15).toLocalTime())
                    .total(lomitoSimple.getPrecioVenta())
                    .totalCosto(lomitoSimple.getPrecioVenta() * 0.6)
                    .detallesPedidos(Set.of(
                            DetallePedido.builder().articuloManufacturado(lomitoSimple).cantidad(1).subTotal(lomitoSimple.getPrecioVenta()).build()
                    ))
                    .build();
            Pedido pedido4 = Pedido.builder()
                    .cliente(cliente)
                    .estado(Estado.ENTREGADO)
                    .formaPago(FormaPago.MERCADO_PAGO)
                    .tipoEnvio(TipoEnvio.RETIRO_EN_LOCAL)
                    .fechaPedido(LocalDate.of(2025, 8, 20))
                    .horaEstimadaFinalizacion(LocalDate.now().atTime(22, 0).toLocalTime())
                    .total(empanadasJyQ.getPrecioVenta() * 3)
                    .totalCosto(empanadasJyQ.getPrecioVenta() * 3 * 0.6)
                    .detallesPedidos(Set.of(
                            DetallePedido.builder().articuloManufacturado(empanadasJyQ).cantidad(3).subTotal(empanadasJyQ.getPrecioVenta() * 3).build()
                    ))
                    .build();
            Pedido pedido5 = Pedido.builder()
                    .cliente(cliente)
                    .estado(Estado.ENTREGADO)
                    .formaPago(FormaPago.EFECTIVO)
                    .tipoEnvio(TipoEnvio.DELIVERY)
                    .fechaPedido(LocalDate.of(2025, 9, 8))
                    .horaEstimadaFinalizacion(LocalDate.now().atTime(18, 45).toLocalTime())
                    .total(pizzaEspecial.getPrecioVenta())
                    .totalCosto(pizzaEspecial.getPrecioVenta() * 0.6)
                    .detallesPedidos(Set.of(
                            DetallePedido.builder().articuloManufacturado(pizzaEspecial).cantidad(1).subTotal(pizzaEspecial.getPrecioVenta()).build()
                    ))
                    .build();
            // Guardar pedidos
            pedido1 = pedidoService.save(pedido1);
            pedido2 = pedidoService.save(pedido2);
            pedido3 = pedidoService.save(pedido3);
            pedido4 = pedidoService.save(pedido4);
            pedido5 = pedidoService.save(pedido5);

            //Pedido

            Sucursal sucursal = Sucursal.builder()
                    .nombre("Sucursal Centro")
                    .horarioApertura(LocalTime.of(10, 0))
                    .horarioCierre(LocalTime.of(23, 59))
                    .domicilio(domicilioCliente) // o podés crear otro domicilio si querés
                    .build();

            sucursal = sucursalService.save(sucursal);

            Factura facturaPedido = Factura.builder()
                    .fechaFacturacion(LocalDate.now())
                    .formaPago(FormaPago.MERCADO_PAGO)
                    .totalVenta(1250.0)
                    .anulada(false)
                    .mpPaymentId(null)         // Dummy, poné un número real si querés simular el ID de MP
                    .mpMerchantOrderId(null)
                    .mpPreferenceId(null)
                    .mpPaymentType("credit_card") // Dummy, o "mercadopago" si querés
                    .urlPdf(null)              // O poné un link si ya lo generás en test
                    .build();

            facturaPedido = facturaService.save(facturaPedido);

            Pedido pedido = Pedido.builder()
                    .horaEstimadaFinalizacion(LocalTime.now().plusMinutes(30))
                    .total(1250.0)
                    .totalCosto(900.0)
                    .estado(Estado.EN_COCINA)
                    .tipoEnvio(TipoEnvio.DELIVERY)
                    .formaPago(FormaPago.MERCADO_PAGO)
                    .fechaPedido(LocalDate.now())
                    .cliente(cliente)
                    .domicilioEntrega(domicilioCliente)
                    .sucursal(sucursal)
                    .factura(facturaPedido) // ¡Aca se asigna la factura!
                    .anulado(false)
                    .build();

            DetallePedido detalle1a = DetallePedido.builder()
                    .cantidad(1)
                    .subTotal(1250.0)
                    .articuloManufacturado(hamburguesaMaipu)
                    .pedido(pedido)
                    .build();

            DetallePedido detalle1b = DetallePedido.builder()
                    .cantidad(2)
                    .subTotal(1000.0)
                    .articuloManufacturado(empanadasCarneCentro)
                    .pedido(pedido)
                    .build();

            pedido.setDetallesPedidos(Set.of(detalle1a, detalle1b));

            pedido.setEmpleado(null);

            facturaPedido.setPedido(pedido);

            pedidoService.save(pedido);


            Factura facturaPedido1 = Factura.builder()
                    .fechaFacturacion(LocalDate.now())
                    .formaPago(FormaPago.MERCADO_PAGO)
                    .totalVenta(1250.0)
                    .anulada(false)
                    .mpPaymentId(null)
                    .mpMerchantOrderId(null)
                    .mpPreferenceId(null)
                    .mpPaymentType("credit_card")
                    .urlPdf(null)
                    .build();

            facturaPedido1 = facturaService.save(facturaPedido1);

            Pedido pedido1 = Pedido.builder()
                    .horaEstimadaFinalizacion(LocalTime.now().plusMinutes(30))
                    .total(1250.0)
                    .totalCosto(900.0)
                    .estado(Estado.EN_COCINA)
                    .tipoEnvio(TipoEnvio.DELIVERY)
                    .formaPago(FormaPago.MERCADO_PAGO)
                    .fechaPedido(LocalDate.now())
                    .cliente(cliente)
                    .domicilioEntrega(domicilioCliente)
                    .sucursal(sucursal)
                    .factura(facturaPedido1)
                    .anulado(false)
                    .build();

            DetallePedido detalle2a = DetallePedido.builder()
                    .cantidad(2)
                    .subTotal(12350.0)
                    .articuloManufacturado(pizzaMuzzarellaCentro)
                    .pedido(pedido1)
                    .build();

            DetallePedido detalle2b = DetallePedido.builder()
                    .cantidad(1)
                    .subTotal(7500.0)
                    .articuloManufacturado(pizza4QuesosMaipu)
                    .pedido(pedido1)
                    .build();

            pedido1.setDetallesPedidos(Set.of(detalle2a, detalle2b));

            pedido.setEmpleado(null);

            facturaPedido1.setPedido(pedido1);

            pedidoService.save(pedido1);


            System.out.println("Datos de ejemplo cargados exitosamente."); // Mensaje al final
        } catch (Exception e) {
            System.err.println("********************************************************");
            System.err.println("ERROR CRÍTICO AL CARGAR DATOS DE EJEMPLO:");
            System.err.println("Mensaje: " + e.getMessage());
            System.err.println("Causa principal: " + (e.getCause() != null ? e.getCause().getMessage() : "N/A"));
            System.err.println("********************************************************");
            e.printStackTrace(); // Esto debería imprimir la pila completa en la consola

            throw e; // Re-lanzar la excepción para que el test la capture
        }
    }
}
