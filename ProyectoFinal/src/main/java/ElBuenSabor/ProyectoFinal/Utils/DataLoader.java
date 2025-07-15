package ElBuenSabor.ProyectoFinal.Utils;

import ElBuenSabor.ProyectoFinal.Entities.*;
import ElBuenSabor.ProyectoFinal.Service.*;
import jakarta.transaction.Transactional;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
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
    private final PersonaService personaService;
    private final ArticuloInsumoService articuloInsumoService;
    private final ArticuloManufacturadoService articuloManufacturadoService;
    private final ArticuloManufacturadoDetalleService articuloManufacturadoDetalleService;
    private final DomicilioService domicilioService;
    private final PasswordEncoder passwordEncoder; // ✅ agregado
    private final SucursalService sucursalService;
    private final PedidoService pedidoService;
    private final FacturaService facturaService;
    private final PromocionService promocionService;

    public DataLoader(PaisService paisService,
                      ProvinciaService provinciaService,
                      LocalidadService localidadService,
                      CategoriaService categoriaService,
                      UnidadMedidaService unidadMedidaService,
                      ImagenService imagenService,
                      UsuarioService usuarioService,
                      PersonaService personaService,
                      ArticuloInsumoService articuloInsumoService,
                      ArticuloManufacturadoService articuloManufacturadoService,
                      ArticuloManufacturadoDetalleService articuloManufacturadoDetalleService,
                      DomicilioService domicilioService,
                      PasswordEncoder passwordEncoder,
                      SucursalService sucursalService,
                      PedidoService pedidoService,
                      FacturaService facturaService,
                      PromocionService promocionService) { // ✅ agregado acá también
        this.paisService = paisService;
        this.provinciaService = provinciaService;
        this.localidadService = localidadService;
        this.categoriaService = categoriaService;
        this.unidadMedidaService = unidadMedidaService;
        this.imagenService = imagenService;
        this.usuarioService = usuarioService;
        this.personaService = personaService;
        this.articuloInsumoService = articuloInsumoService;
        this.articuloManufacturadoService = articuloManufacturadoService;
        this.articuloManufacturadoDetalleService = articuloManufacturadoDetalleService;
        this.domicilioService = domicilioService;
        this.passwordEncoder = passwordEncoder; // ✅ asignación
        this.sucursalService = sucursalService;
        this.pedidoService = pedidoService;
        this.facturaService = facturaService;
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
            Imagen imgCliente = imagenService.save(Imagen.builder().denominacion("https://example.com/cliente.jpg").build());
            Imagen imgCliente1 = imagenService.save(Imagen.builder().denominacion("https://example.com/cliente.jpg").build());
            Imagen imgCliente2 = imagenService.save(Imagen.builder().denominacion("https://example.com/cliente.jpg").build());
            Imagen imgCliente3 = imagenService.save(Imagen.builder().denominacion("https://example.com/cliente.jpg").build());
            Imagen imgCliente4 = imagenService.save(Imagen.builder().denominacion("https://example.com/cliente.jpg").build());
            Imagen imgCliente5 = imagenService.save(Imagen.builder().denominacion("https://example.com/cliente.jpg").build());
            Imagen imgCliente6 = imagenService.save(Imagen.builder().denominacion("https://example.com/cliente.jpg").build());
            Imagen imgCliente7 = imagenService.save(Imagen.builder().denominacion("https://example.com/cliente.jpg").build());
            Imagen imgCliente8 = imagenService.save(Imagen.builder().denominacion("https://example.com/cliente.jpg").build());
            Imagen imgCliente9 = imagenService.save(Imagen.builder().denominacion("https://example.com/cliente.jpg").build());
            Imagen imgCliente10 = imagenService.save(Imagen.builder().denominacion("https://example.com/cliente.jpg").build());
            Imagen imgCliente11 = imagenService.save(Imagen.builder().denominacion("https://example.com/cliente.jpg").build());
            Imagen imgCliente12 = imagenService.save(Imagen.builder().denominacion("https://example.com/cliente.jpg").build());
            Imagen imgCliente13 = imagenService.save(Imagen.builder().denominacion("https://example.com/cliente.jpg").build());
            Imagen imgCliente14 = imagenService.save(Imagen.builder().denominacion("https://example.com/cliente.jpg").build());
            Imagen imgCliente15 = imagenService.save(Imagen.builder().denominacion("https://example.com/cliente.jpg").build());

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
            Imagen imgQuesoParmesano = imagenService.save(Imagen.builder().denominacion("https://res.cloudinary.com/deagcdoak/image/upload/v1750352611/ihli8hclpii1mquajqnc.jpg").build());
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
            Imagen imgPapasFritas = imagenService.save(Imagen.builder().denominacion("https://res.cloudinary.com/deagcdoak/image/upload/v1751238697/zzrbf39eez7j40wifjfz.jpg").build());
            Imagen imgPapasCheddarBacon = imagenService.save(Imagen.builder().denominacion("https://res.cloudinary.com/deagcdoak/image/upload/v1751238729/qx0z9lausf5f5nv9q98z.jpg").build());
            Imagen imgEmpanadaCuchillo = imagenService.save(Imagen.builder().denominacion("https://res.cloudinary.com/deagcdoak/image/upload/v1751238711/t4znioamg9tpn4l3bajv.jpg").build());
            Imagen imgEmpanadaHumita = imagenService.save(Imagen.builder().denominacion("https://res.cloudinary.com/deagcdoak/image/upload/v1751238684/gwoxenaymp42zsgqz3hs.jpg").build());
            Imagen imgCoca500 = imagenService.save(Imagen.builder().denominacion("https://res.cloudinary.com/deagcdoak/image/upload/v1751329981/mzvzw5rmkyrxnkhsf15k.webp").build());
            Imagen imgCoca1l = imagenService.save(Imagen.builder().denominacion("https://res.cloudinary.com/deagcdoak/image/upload/v1751329990/hximljfzsnd4vv8wug6q.webp").build());
            Imagen imgCoca15l = imagenService.save(Imagen.builder().denominacion("https://res.cloudinary.com/deagcdoak/image/upload/v1751329999/asoymdsq1gt9o3onwfr9.webp").build());
            Imagen imgPepsi500 = imagenService.save(Imagen.builder().denominacion("https://res.cloudinary.com/deagcdoak/image/upload/v1751329922/agf0iduaawosofjemu1u.jpg").build());
            Imagen imgPepsi1l = imagenService.save(Imagen.builder().denominacion("https://res.cloudinary.com/deagcdoak/image/upload/v1751329931/bsj8tsnl5mkukiqobnga.webp").build());
            Imagen imgPepsi15l = imagenService.save(Imagen.builder().denominacion("https://res.cloudinary.com/deagcdoak/image/upload/v1751329940/ja7gqbrjwvdonlcmszok.jpg").build());
            Imagen imgSprite500 = imagenService.save(Imagen.builder().denominacion("https://res.cloudinary.com/deagcdoak/image/upload/v1751329893/rado9ogkwvu6ju5ibg8h.webp").build());
            Imagen imgSprite1l = imagenService.save(Imagen.builder().denominacion("https://res.cloudinary.com/deagcdoak/image/upload/v1751329913/sndtakkmjcldenivem3u.jpg").build());
            Imagen imgSprite15l = imagenService.save(Imagen.builder().denominacion("https://res.cloudinary.com/deagcdoak/image/upload/v1751329904/pgzsdiqyh38psyhwhziy.png").build());
            Imagen imgFanta500 = imagenService.save(Imagen.builder().denominacion("https://res.cloudinary.com/deagcdoak/image/upload/v1751329950/gcusw0frai8ztbf9uqm9.webp").build());
            Imagen imgFanta1l = imagenService.save(Imagen.builder().denominacion("https://res.cloudinary.com/deagcdoak/image/upload/v1751329959/bozxr6zqukxoz7zwtqso.avif").build());
            Imagen imgFanta15l = imagenService.save(Imagen.builder().denominacion("https://res.cloudinary.com/deagcdoak/image/upload/v1751329973/wr5vmh6ikuyuml9fmytr.jpg").build());
            Imagen imgBombonHelado = imagenService.save(Imagen.builder().denominacion("https://res.cloudinary.com/deagcdoak/image/upload/v1751330011/j1tnysvpkrnsyifgi4jd.jpg").build());
            Imagen imgBombonEscoces = imagenService.save(Imagen.builder().denominacion("https://res.cloudinary.com/deagcdoak/image/upload/v1751330020/ufzgj4qcdussnz5uxtrp.jpg").build());
            Imagen imgBombonSuizo = imagenService.save(Imagen.builder().denominacion("https://res.cloudinary.com/deagcdoak/image/upload/v1751330033/idjhsxzlufnndsjnfqts.jpg").build());

            Imagen imgPromo1 = imagenService.save(Imagen.builder().denominacion("https://res.cloudinary.com/deagcdoak/image/upload/v1751379730/qwicagg3qogbmkco7qie.jpg").build());
            Imagen imgPromo2 = imagenService.save(Imagen.builder().denominacion("https://res.cloudinary.com/deagcdoak/image/upload/v1751379513/b0rsq5wdbi52ci7bqesb.jpg").build());
            Imagen imgPromo3 = imagenService.save(Imagen.builder().denominacion("https://res.cloudinary.com/deagcdoak/image/upload/v1751379760/pvkzswr2gg4hjhpae88b.jpg").build());
            Imagen imgPromo4 = imagenService.save(Imagen.builder().denominacion("https://res.cloudinary.com/deagcdoak/image/upload/v1751379748/wjdiosiva5f8vko32nju.jpg").build());
            Imagen imgPromo5 = imagenService.save(Imagen.builder().denominacion("https://res.cloudinary.com/deagcdoak/image/upload/v1751379475/yv0gf1oz7dd2stjisheu.jpg").build());


            // 3. Domicilio
            Domicilio domicilioCliente = domicilioService.save(Domicilio.builder()
                    .calle("Calle Falsa")
                    .numero(123)
                    .cp(5515)
                    .localidad(localidad)//Siempre misma localidad
                    .build());

            // 4. Usuario CLIENTE
            Usuario usuarioCliente = usuarioService.save(Usuario.builder()
                    .email("faustinovinolo@gmail.com") //Inventados (ej: persona@buen.com)
                    .password(passwordEncoder.encode("cliente123")) // Mismas contraseñas cliente123
                    .rol(Rol.CLIENTE)//Siempre rol CLIENTE
                    .username("Fiamma") //Nombre inventado
                    .build());

            // 5. Persona asociado al Usuario
            Persona persona = Persona.builder()
                    .apellido("Brizuela")//Apellido inventado
                    .telefono("2615551234")//telefono inventado
                    .fechaNacimiento(LocalDate.of(1990, 5, 15))//Nacimiento inventado
                    .imagen(imgCliente)//imagen siempre la misma de stock
                    .usuario(usuarioCliente)//relación con el usuario ya creado
                    .domicilios(List.of(domicilioCliente))//Domicilio creado individualmente para el persona
                    .build();

            personaService.save(persona);

            // Persona 1
            Domicilio domicilioCliente1 = domicilioService.save(Domicilio.builder()
                    .calle("Av. Belgrano")
                    .numero(456)
                    .cp(5515)
                    .localidad(localidad)
                    .build());

            Usuario usuarioCliente1 = usuarioService.save(Usuario.builder()
                    .email("persona1@buen.com")
                    .password(passwordEncoder.encode("cliente123"))
                    .rol(Rol.CLIENTE)
                    .username("Camila")
                    .build());

            Persona persona1 = Persona.builder()
                    .apellido("Rodríguez")
                    .telefono("2611112233")
                    .fechaNacimiento(LocalDate.of(1993, 3, 10))
                    .imagen(imgCliente1)
                    .usuario(usuarioCliente1)
                    .domicilios(List.of(domicilioCliente1))
                    .build();

            personaService.save(persona1);

// Persona 2
            Domicilio domicilioCliente2 = domicilioService.save(Domicilio.builder()
                    .calle("Calle Las Heras")
                    .numero(789)
                    .cp(5515)
                    .localidad(localidad)
                    .build());

            Usuario usuarioCliente2 = usuarioService.save(Usuario.builder()
                    .email("persona2@buen.com")
                    .password(passwordEncoder.encode("cliente123"))
                    .rol(Rol.CLIENTE)
                    .username("Lucas")
                    .build());

            Persona persona2 = Persona.builder()
                    .apellido("Fernández")
                    .telefono("2614445566")
                    .fechaNacimiento(LocalDate.of(1988, 7, 22))
                    .imagen(imgCliente2)
                    .usuario(usuarioCliente2)
                    .domicilios(List.of(domicilioCliente2))
                    .build();

            personaService.save(persona2);

            // Persona 3
            Domicilio domicilioCliente3 = domicilioService.save(Domicilio.builder()
                    .calle("San Martín")
                    .numero(321)
                    .cp(5515)
                    .localidad(localidad)
                    .build());

            Usuario usuarioCliente3 = usuarioService.save(Usuario.builder()
                    .email("persona3@buen.com")
                    .password(passwordEncoder.encode("cliente123"))
                    .rol(Rol.CLIENTE)
                    .username("Valentina")
                    .build());

            Persona persona3 = Persona.builder()
                    .apellido("Gómez")
                    .telefono("2617778899")
                    .fechaNacimiento(LocalDate.of(1995, 11, 5))
                    .imagen(imgCliente3)
                    .usuario(usuarioCliente3)
                    .domicilios(List.of(domicilioCliente3))
                    .build();

            personaService.save(persona3);

// Persona 4
            Domicilio domicilioCliente4 = domicilioService.save(Domicilio.builder()
                    .calle("Patricias Mendocinas")
                    .numero(654)
                    .cp(5515)
                    .localidad(localidad)
                    .build());

            Usuario usuarioCliente4 = usuarioService.save(Usuario.builder()
                    .email("persona4@buen.com")
                    .password(passwordEncoder.encode("cliente123"))
                    .rol(Rol.CLIENTE)
                    .username("Bruno")
                    .build());

            Persona persona4 = Persona.builder()
                    .apellido("López")
                    .telefono("2613334455")
                    .fechaNacimiento(LocalDate.of(1985, 1, 18))
                    .imagen(imgCliente4)
                    .usuario(usuarioCliente4)
                    .domicilios(List.of(domicilioCliente4))
                    .build();

            personaService.save(persona4);

            // Persona 5
            Domicilio domicilioCliente5 = domicilioService.save(Domicilio.builder()
                    .calle("Godoy Cruz")
                    .numero(88)
                    .cp(5515)
                    .localidad(localidad)
                    .build());

            Usuario usuarioCliente5 = usuarioService.save(Usuario.builder()
                    .email("persona5@buen.com")
                    .password(passwordEncoder.encode("cliente123"))
                    .rol(Rol.CLIENTE)
                    .username("Martina")
                    .build());

            Persona persona5 = Persona.builder()
                    .apellido("Fernández")
                    .telefono("2611122334")
                    .fechaNacimiento(LocalDate.of(1993, 3, 22))
                    .imagen(imgCliente5)
                    .usuario(usuarioCliente5)
                    .domicilios(List.of(domicilioCliente5))
                    .build();

            personaService.save(persona5);

// Persona 6
            Domicilio domicilioCliente6 = domicilioService.save(Domicilio.builder()
                    .calle("Maipú")
                    .numero(456)
                    .cp(5515)
                    .localidad(localidad)
                    .build());

            Usuario usuarioCliente6 = usuarioService.save(Usuario.builder()
                    .email("persona6@buen.com")
                    .password(passwordEncoder.encode("cliente123"))
                    .rol(Rol.CLIENTE)
                    .username("Ezequiel")
                    .build());

            Persona persona6 = Persona.builder()
                    .apellido("Sánchez")
                    .telefono("2616677889")
                    .fechaNacimiento(LocalDate.of(1988, 6, 10))
                    .imagen(imgCliente6)
                    .usuario(usuarioCliente6)
                    .domicilios(List.of(domicilioCliente6))
                    .build();

            personaService.save(persona6);

            // Persona 7
            Domicilio domicilioCliente7 = domicilioService.save(Domicilio.builder()
                    .calle("25 de Mayo")
                    .numero(1025)
                    .cp(5515)
                    .localidad(localidad)
                    .build());

            Usuario usuarioCliente7 = usuarioService.save(Usuario.builder()
                    .email("persona7@buen.com")
                    .password(passwordEncoder.encode("cliente123"))
                    .rol(Rol.CLIENTE)
                    .username("Valentina")
                    .build());

            Persona persona7 = Persona.builder()
                    .apellido("Moreno")
                    .telefono("2613004567")
                    .fechaNacimiento(LocalDate.of(1997, 11, 12))
                    .imagen(imgCliente7)
                    .usuario(usuarioCliente7)
                    .domicilios(List.of(domicilioCliente7))
                    .build();

            personaService.save(persona7);

// Persona 8
            Domicilio domicilioCliente8 = domicilioService.save(Domicilio.builder()
                    .calle("Tucumán")
                    .numero(731)
                    .cp(5515)
                    .localidad(localidad)
                    .build());

            Usuario usuarioCliente8 = usuarioService.save(Usuario.builder()
                    .email("persona8@buen.com")
                    .password(passwordEncoder.encode("cliente123"))
                    .rol(Rol.CLIENTE)
                    .username("Benjamín")
                    .build());

            Persona persona8 = Persona.builder()
                    .apellido("Quiroga")
                    .telefono("2619801234")
                    .fechaNacimiento(LocalDate.of(1991, 4, 27))
                    .imagen(imgCliente8)
                    .usuario(usuarioCliente8)
                    .domicilios(List.of(domicilioCliente8))
                    .build();

            personaService.save(persona8);

            // Persona 9
            Domicilio domicilioCliente9 = domicilioService.save(Domicilio.builder()
                    .calle("Av. San Martín")
                    .numero(856)
                    .cp(5515)
                    .localidad(localidad)
                    .build());

            Usuario usuarioCliente9 = usuarioService.save(Usuario.builder()
                    .email("persona9@buen.com")
                    .password(passwordEncoder.encode("cliente123"))
                    .rol(Rol.CLIENTE)
                    .username("Sofía")
                    .build());

            Persona persona9 = Persona.builder()
                    .apellido("Montoya")
                    .telefono("2613407890")
                    .fechaNacimiento(LocalDate.of(1993, 2, 18))
                    .imagen(imgCliente9)
                    .usuario(usuarioCliente9)
                    .domicilios(List.of(domicilioCliente9))
                    .build();

            personaService.save(persona9);

// Persona 10
            Domicilio domicilioCliente10 = domicilioService.save(Domicilio.builder()
                    .calle("España")
                    .numero(1290)
                    .cp(5515)
                    .localidad(localidad)
                    .build());

            Usuario usuarioCliente10 = usuarioService.save(Usuario.builder()
                    .email("persona10@buen.com")
                    .password(passwordEncoder.encode("cliente123"))
                    .rol(Rol.CLIENTE)
                    .username("Tomás")
                    .build());

            Persona persona10 = Persona.builder()
                    .apellido("Sánchez")
                    .telefono("2614109876")
                    .fechaNacimiento(LocalDate.of(1990, 10, 5))
                    .imagen(imgCliente10)
                    .usuario(usuarioCliente10)
                    .domicilios(List.of(domicilioCliente10))
                    .build();

            personaService.save(persona10);

            // Persona 11
            Domicilio domicilioCliente11 = domicilioService.save(Domicilio.builder()
                    .calle("Av. Libertador")
                    .numero(707)
                    .cp(5515)
                    .localidad(localidad)
                    .build());

            Usuario usuarioCliente11 = usuarioService.save(Usuario.builder()
                    .email("persona11@buen.com")
                    .password(passwordEncoder.encode("cliente123"))
                    .rol(Rol.CLIENTE)
                    .username("Guadalupe")
                    .build());

            Persona persona11 = Persona.builder()
                    .apellido("Carrizo")
                    .telefono("2613004567")
                    .fechaNacimiento(LocalDate.of(1988, 4, 22))
                    .imagen(imgCliente11)
                    .usuario(usuarioCliente11)
                    .domicilios(List.of(domicilioCliente11))
                    .build();

            personaService.save(persona11);

// Persona 12
            Domicilio domicilioCliente12 = domicilioService.save(Domicilio.builder()
                    .calle("Av. Godoy Cruz")
                    .numero(315)
                    .cp(5515)
                    .localidad(localidad)
                    .build());

            Usuario usuarioCliente12 = usuarioService.save(Usuario.builder()
                    .email("persona12@buen.com")
                    .password(passwordEncoder.encode("cliente123"))
                    .rol(Rol.CLIENTE)
                    .username("Luciano")
                    .build());

            Persona persona12 = Persona.builder()
                    .apellido("Nieto")
                    .telefono("2613901234")
                    .fechaNacimiento(LocalDate.of(1995, 9, 9))
                    .imagen(imgCliente12)
                    .usuario(usuarioCliente12)
                    .domicilios(List.of(domicilioCliente12))
                    .build();

            personaService.save(persona12);

// Persona 13
            Domicilio domicilioCliente13 = domicilioService.save(Domicilio.builder()
                    .calle("Ituzaingó")
                    .numero(444)
                    .cp(5515)
                    .localidad(localidad)
                    .build());

            Usuario usuarioCliente13 = usuarioService.save(Usuario.builder()
                    .email("persona13@buen.com")
                    .password(passwordEncoder.encode("cliente123"))
                    .rol(Rol.CLIENTE)
                    .username("Martina")
                    .build());

            Persona persona13 = Persona.builder()
                    .apellido("Toledo")
                    .telefono("2613809876")
                    .fechaNacimiento(LocalDate.of(1997, 6, 17))
                    .imagen(imgCliente13)
                    .usuario(usuarioCliente13)
                    .domicilios(List.of(domicilioCliente13))
                    .build();

            personaService.save(persona13);

// Persona 14
            Domicilio domicilioCliente14 = domicilioService.save(Domicilio.builder()
                    .calle("José Ingenieros")
                    .numero(672)
                    .cp(5515)
                    .localidad(localidad)
                    .build());

            Usuario usuarioCliente14 = usuarioService.save(Usuario.builder()
                    .email("persona14@buen.com")
                    .password(passwordEncoder.encode("cliente123"))
                    .rol(Rol.CLIENTE)
                    .username("Alejo")
                    .build());

            Persona persona14 = Persona.builder()
                    .apellido("Herrera")
                    .telefono("2613211122")
                    .fechaNacimiento(LocalDate.of(1989, 8, 12))
                    .imagen(imgCliente14)
                    .usuario(usuarioCliente14)
                    .domicilios(List.of(domicilioCliente14))
                    .build();

            personaService.save(persona14);


            // 6. Categoría y Unidades
            Categoria categoriaPadre= categoriaService.save(Categoria.builder().denominacion("Manufacturados").build());
            Categoria categoriaPadre1= categoriaService.save(Categoria.builder().denominacion("Insumos").build());

            Categoria categoriaPizza = categoriaService.save(Categoria.builder().denominacion("Pizza").categoriaPadre(categoriaPadre).build());
            Categoria categoriaSanguche = categoriaService.save(Categoria.builder().denominacion("Sanguche").categoriaPadre(categoriaPadre).build());
            Categoria categoriaEmpanada = categoriaService.save(Categoria.builder().denominacion("Empanada").categoriaPadre(categoriaPadre).build());
            Categoria categoriaHamburguesa = categoriaService.save(Categoria.builder().denominacion("Hamburguesa").categoriaPadre(categoriaPadre).build());
            Categoria categoriaComida = categoriaService.save(Categoria.builder().denominacion("Comida").categoriaPadre(categoriaPadre).build());
            Categoria categoriaPapasFritas = categoriaService.save(Categoria.builder().denominacion("Papas fritas").categoriaPadre(categoriaPadre).build());

            Categoria categoriaPan = categoriaService.save(Categoria.builder().denominacion("Pan").categoriaPadre(categoriaPadre1).build());
            Categoria categoriaQueso = categoriaService.save(Categoria.builder().denominacion("Queso").categoriaPadre(categoriaPadre1).build());
            Categoria categoriaVerdura = categoriaService.save(Categoria.builder().denominacion("Verdura").categoriaPadre(categoriaPadre1).build());
            Categoria categoriaCarne = categoriaService.save(Categoria.builder().denominacion("Carne").categoriaPadre(categoriaPadre1).build());
            Categoria categoriaCondimentosYHierbas = categoriaService.save(Categoria.builder().denominacion("Condimento").categoriaPadre(categoriaPadre1).build());
            Categoria categoriaSalsasYAderezos = categoriaService.save(Categoria.builder().denominacion("Aderezo y Salsas").categoriaPadre(categoriaPadre1).build());
            Categoria categoriaLacteosYDerivados = categoriaService.save(Categoria.builder().denominacion("Lácteo").categoriaPadre(categoriaPadre1).build());
            Categoria categoriaMasasYTapas = categoriaService.save(Categoria.builder().denominacion("Masa y Tapas").categoriaPadre(categoriaPadre1).build());
            Categoria categoriaConservasYOtros = categoriaService.save(Categoria.builder().denominacion("Conservas").categoriaPadre(categoriaPadre1).build());

            Categoria categoriaBebida = categoriaService.save(Categoria.builder().denominacion("Bebida").build());
            Categoria categoriaPostre = categoriaService.save(Categoria.builder().denominacion("Postre").build());
            UnidadMedida unidadGramos = unidadMedidaService.save(UnidadMedida.builder().denominacion("gramos").build());
            UnidadMedida unidadPorcion = unidadMedidaService.save(UnidadMedida.builder().denominacion("unidad").build());
            UnidadMedida unidadMililitros = unidadMedidaService.save(UnidadMedida.builder().denominacion("mililitros").build());

            // 7. Insumos
            ArticuloInsumo insumoHarina = articuloInsumoService.save(ArticuloInsumo.builder()
                    .denominacion("Harina")
                    .precioVenta(1.5)
                    .precioCompra(1.5)
                    .stockActual(5000.0)
                    .stockMinimo(500.0)
                    .esParaElaborar(true)
                    .categoria(categoriaMasasYTapas)
                    .unidadMedida(unidadGramos)
                    .imagen(imgHarina)
                    .build());

            ArticuloInsumo insumoTomate = articuloInsumoService.save(ArticuloInsumo.builder()
                    .denominacion("Tomate")
                    .precioVenta(2.0)
                    .precioCompra(2.0)
                    .stockActual(5000.0)
                    .stockMinimo(500.0)
                    .esParaElaborar(true)
                    .categoria(categoriaVerdura)
                    .unidadMedida(unidadGramos)
                    .imagen(imgTomate)
                    .build());

            ArticuloInsumo masaPrepizza = articuloInsumoService.save(ArticuloInsumo.builder()
                    .denominacion("Masa Prepizza")
                    .precioVenta(2000.0)
                    .precioCompra(2000.0)
                    .stockActual(30.0)
                    .stockMinimo(5.0)
                    .esParaElaborar(true)
                    .categoria(categoriaMasasYTapas)
                    .unidadMedida(unidadPorcion)
                    .imagen(imgPrepizza)
                    .build());

            ArticuloInsumo quesoMuzzarella = articuloInsumoService.save(ArticuloInsumo.builder()
                    .denominacion("Queso Muzzarella")
                    .precioVenta(15.0)
                    .precioCompra(15.0)
                    .stockActual(10000.0)
                    .stockMinimo(1500.0)
                    .esParaElaborar(true)
                    .categoria(categoriaQueso)
                    .unidadMedida(unidadGramos)
                    .imagen(imgQuesoMuzarella)
                    .build());

            ArticuloInsumo quesoRoquefort = articuloInsumoService.save(ArticuloInsumo.builder()
                    .denominacion("Queso Roquefort")
                    .precioVenta(20.0)
                    .precioCompra(20.0)
                    .stockActual(4000.0)
                    .stockMinimo(500.0)
                    .esParaElaborar(true)
                    .categoria(categoriaQueso)
                    .unidadMedida(unidadGramos)
                    .imagen(imgQuesoRoquefort)
                    .build());

            ArticuloInsumo quesoBrie = articuloInsumoService.save(ArticuloInsumo.builder()
                    .denominacion("Queso Brie")
                    .precioVenta(70.0)
                    .precioCompra(70.0)
                    .stockActual(3000.0)
                    .stockMinimo(500.0)
                    .esParaElaborar(true)
                    .categoria(categoriaQueso)
                    .unidadMedida(unidadGramos)
                    .imagen(imgQuesoBrie)
                    .build());

            ArticuloInsumo quesoParmesano = articuloInsumoService.save(ArticuloInsumo.builder()
                    .denominacion("Queso Parmesano")
                    .precioVenta(30.0)
                    .precioCompra(30.0)
                    .stockActual(6000.0)
                    .stockMinimo(500.0)
                    .esParaElaborar(true)
                    .categoria(categoriaQueso)
                    .unidadMedida(unidadGramos)
                    .imagen(imgQuesoParmesano)
                    .build());

            ArticuloInsumo salsaTomate = articuloInsumoService.save(ArticuloInsumo.builder()
                    .denominacion("Salsa de Tomate")
                    .precioVenta(1.0)
                    .precioCompra(1.0)
                    .stockActual(10000.0)
                    .stockMinimo(1000.0)
                    .esParaElaborar(true)
                    .categoria(categoriaSalsasYAderezos)
                    .unidadMedida(unidadMililitros)
                    .imagen(imgSalsaTomate)
                    .build());

            ArticuloInsumo cebolla = articuloInsumoService.save(ArticuloInsumo.builder()
                    .denominacion("Cebolla")
                    .precioVenta(0.4)
                    .precioCompra(0.4)
                    .stockActual(4000.0)
                    .stockMinimo(300.0)
                    .esParaElaborar(true)
                    .categoria(categoriaVerdura)
                    .unidadMedida(unidadGramos)
                    .imagen(imgCebolla)
                    .build());

            ArticuloInsumo albahaca = articuloInsumoService.save(ArticuloInsumo.builder()
                    .denominacion("Albahaca")
                    .precioVenta(3.0)
                    .precioCompra(3.0)
                    .stockActual(500.0)
                    .stockMinimo(50.0)
                    .esParaElaborar(true)
                    .categoria(categoriaCondimentosYHierbas)
                    .unidadMedida(unidadPorcion)
                    .imagen(imgAlbahaca)
                    .build());

            ArticuloInsumo jamonCocido = articuloInsumoService.save(ArticuloInsumo.builder()
                    .denominacion("Jamón Cocido")
                    .precioVenta(20.0)
                    .precioCompra(20.0)
                    .stockActual(4000.0)
                    .stockMinimo(500.0)
                    .esParaElaborar(true)
                    .categoria(categoriaCarne)
                    .unidadMedida(unidadGramos)
                    .imagen(imgJamonCocido)
                    .build());

            ArticuloInsumo panHamburguesa = articuloInsumoService.save(ArticuloInsumo.builder()
                    .denominacion("Pan de Hamburguesa")
                    .precioVenta(2000.0)
                    .precioCompra(2000.0)
                    .stockActual(40.0)
                    .stockMinimo(5.0)
                    .esParaElaborar(true)
                    .categoria(categoriaPan)
                    .unidadMedida(unidadPorcion)
                    .imagen(imgPanHamburguesa)
                    .build());

            ArticuloInsumo lechuga = articuloInsumoService.save(ArticuloInsumo.builder()
                    .denominacion("Lechuga")
                    .precioVenta(2.5)
                    .precioCompra(2.5)
                    .stockActual(4000.0)
                    .stockMinimo(500.0)
                    .esParaElaborar(true)
                    .categoria(categoriaVerdura)
                    .unidadMedida(unidadGramos)
                    .imagen(imgLechuga)
                    .build());

            ArticuloInsumo medallonCarne = articuloInsumoService.save(ArticuloInsumo.builder()
                    .denominacion("Medallón de Carne")
                    .precioVenta(2500.0)
                    .precioCompra(2500.0)
                    .stockActual(100.0)
                    .stockMinimo(10.0)
                    .esParaElaborar(true)
                    .categoria(categoriaCarne)
                    .unidadMedida(unidadPorcion)
                    .imagen(imgMedallonCarne)
                    .build());

            ArticuloInsumo medallonPollo = articuloInsumoService.save(ArticuloInsumo.builder()
                    .denominacion("Medallón de Pollo")
                    .precioVenta(2200.0)
                    .precioCompra(2200.0)
                    .stockActual(50.0)
                    .stockMinimo(5.0)
                    .esParaElaborar(true)
                    .categoria(categoriaCarne)
                    .unidadMedida(unidadPorcion)
                    .imagen(imgMedallonPollo)
                    .build());

            ArticuloInsumo cheddar = articuloInsumoService.save(ArticuloInsumo.builder()
                    .denominacion("Queso Cheddar")
                    .precioVenta(30.0)
                    .precioCompra(30.0)
                    .stockActual(4500.0)
                    .stockMinimo(500.0)
                    .esParaElaborar(true)
                    .categoria(categoriaQueso)
                    .unidadMedida(unidadGramos)
                    .imagen(imgCheddar)
                    .build());

            ArticuloInsumo tapasEmpanada = articuloInsumoService.save(ArticuloInsumo.builder()
                    .denominacion("Tapas para Empanadas")
                    .precioVenta(150.0)
                    .precioCompra(150.0)
                    .stockActual(240.0)
                    .stockMinimo(48.0)
                    .esParaElaborar(true)
                    .categoria(categoriaMasasYTapas)
                    .unidadMedida(unidadPorcion) // cada tapa se considera una unidad
                    .imagen(imgTapasEmpanada)
                    .build());

            ArticuloInsumo panLomito = articuloInsumoService.save(ArticuloInsumo.builder()
                    .denominacion("Pan para Lomito")
                    .precioVenta(2000.0)
                    .precioCompra(2000.0)
                    .stockActual(40.0)
                    .stockMinimo(5.0)
                    .esParaElaborar(true)
                    .categoria(categoriaPan)
                    .unidadMedida(unidadPorcion)
                    .imagen(imgPanLomito)
                    .build());

            ArticuloInsumo panBarroluco = articuloInsumoService.save(ArticuloInsumo.builder()
                    .denominacion("Pan de Miga Cuadrado")
                    .precioVenta(2000.0)
                    .precioCompra(2000.0)
                    .stockActual(40.0)
                    .stockMinimo(5.0)
                    .esParaElaborar(true)
                    .categoria(categoriaPan)
                    .unidadMedida(unidadPorcion)
                    .imagen(imgPanBarroluco)
                    .build());

            ArticuloInsumo carneMolida = articuloInsumoService.save(ArticuloInsumo.builder()
                    .denominacion("Carne Molida")
                    .precioVenta(9.0)
                    .precioCompra(9.0)
                    .stockActual(600.0)
                    .stockMinimo(500.0)
                    .esParaElaborar(true)
                    .categoria(categoriaCarne)
                    .unidadMedida(unidadGramos)
                    .imagen(imgCarneMolida)
                    .build());

            ArticuloInsumo bifeCarne = articuloInsumoService.save(ArticuloInsumo.builder()
                    .denominacion("Bife de Carne")
                    .precioVenta(1500.0)
                    .precioCompra(1500.0)
                    .stockActual(150.0)
                    .stockMinimo(30.0)
                    .esParaElaborar(true)
                    .categoria(categoriaCarne)
                    .unidadMedida(unidadPorcion)
                    .imagen(imgBifeCarne)
                    .build());

            ArticuloInsumo morronRojo = articuloInsumoService.save(ArticuloInsumo.builder()
                    .denominacion("Morrón Rojo")
                    .precioVenta(7.0)
                    .precioCompra(7.0)
                    .stockActual(2000.0)
                    .stockMinimo(300.0)
                    .esParaElaborar(true)
                    .categoria(categoriaVerdura)
                    .unidadMedida(unidadGramos)
                    .imagen(imagenService.save(Imagen.builder().denominacion("https://i.imgur.com/morron.jpg").build()))
                    .build());

            ArticuloInsumo huevo = articuloInsumoService.save(ArticuloInsumo.builder()
                    .denominacion("Huevo")
                    .precioVenta(300.0)
                    .precioCompra(300.0)
                    .stockActual(120.0)
                    .stockMinimo(30.0)
                    .esParaElaborar(true)
                    .categoria(categoriaConservasYOtros)
                    .unidadMedida(unidadPorcion)
                    .imagen(imagenService.save(Imagen.builder().denominacion("https://i.imgur.com/huevo.jpg").build()))
                    .build());

            ArticuloInsumo mayonesa = articuloInsumoService.save(ArticuloInsumo.builder()
                    .denominacion("Mayonesa")
                    .precioVenta(1.0)
                    .precioCompra(1.0)
                    .stockActual(5000.0)
                    .stockMinimo(500.0)
                    .esParaElaborar(true)
                    .categoria(categoriaSalsasYAderezos)
                    .unidadMedida(unidadMililitros)
                    .imagen(imagenService.save(Imagen.builder().denominacion("https://i.imgur.com/mayonesa.jpg").build()))
                    .build());

            ArticuloInsumo mostaza = articuloInsumoService.save(ArticuloInsumo.builder()
                    .denominacion("Mostaza")
                    .precioVenta(1.0)
                    .precioCompra(1.0)
                    .stockActual(5000.0)
                    .stockMinimo(500.0)
                    .esParaElaborar(true)
                    .categoria(categoriaSalsasYAderezos)
                    .unidadMedida(unidadMililitros)
                    .imagen(imagenService.save(Imagen.builder().denominacion("https://i.imgur.com/mostaza.jpg").build()))
                    .build());

            ArticuloInsumo ketchup = articuloInsumoService.save(ArticuloInsumo.builder()
                    .denominacion("Ketchup")
                    .precioVenta(1.0)
                    .precioCompra(1.0)
                    .stockActual(5000.0)
                    .stockMinimo(500.0)
                    .esParaElaborar(true)
                    .categoria(categoriaSalsasYAderezos)
                    .unidadMedida(unidadMililitros)
                    .imagen(imagenService.save(Imagen.builder().denominacion("https://i.imgur.com/ketchup.jpg").build()))
                    .build());

            ArticuloInsumo ajo = articuloInsumoService.save(ArticuloInsumo.builder()
                    .denominacion("Ajo")
                    .precioVenta(40.0)
                    .precioCompra(40.0)
                    .stockActual(2500.0)
                    .stockMinimo(250.0)
                    .esParaElaborar(true)
                    .categoria(categoriaCondimentosYHierbas)
                    .unidadMedida(unidadGramos)
                    .imagen(imagenService.save(Imagen.builder().denominacion("https://i.imgur.com/ajo.jpg").build()))
                    .build());

            ArticuloInsumo champiñones = articuloInsumoService.save(ArticuloInsumo.builder()
                    .denominacion("Champiñones")
                    .precioVenta(30.0)
                    .precioCompra(30.0)
                    .stockActual(1500.0)
                    .stockMinimo(200.0)
                    .esParaElaborar(true)
                    .categoria(categoriaVerdura)
                    .unidadMedida(unidadGramos)
                    .imagen(imagenService.save(Imagen.builder().denominacion("https://i.imgur.com/champinones.jpg").build()))
                    .build());

            ArticuloInsumo zanahoria = articuloInsumoService.save(ArticuloInsumo.builder()
                    .denominacion("Zanahoria")
                    .precioVenta(1.0)
                    .precioCompra(1.0)
                    .stockActual(3000.0)
                    .stockMinimo(350.0)
                    .esParaElaborar(true)
                    .categoria(categoriaVerdura)
                    .unidadMedida(unidadGramos)
                    .imagen(imagenService.save(Imagen.builder().denominacion("https://i.imgur.com/zanahoria.jpg").build()))
                    .build());

            ArticuloInsumo quesoProvolone = articuloInsumoService.save(ArticuloInsumo.builder()
                    .denominacion("Queso Provolone")
                    .precioVenta(32.0)
                    .precioCompra(32.0)
                    .stockActual(2500.0)
                    .stockMinimo(500.0)
                    .esParaElaborar(true)
                    .categoria(categoriaQueso)
                    .unidadMedida(unidadGramos)
                    .imagen(imagenService.save(Imagen.builder().denominacion("https://i.imgur.com/provolone.jpg").build()))
                    .build());

            ArticuloInsumo salame = articuloInsumoService.save(ArticuloInsumo.builder()
                    .denominacion("Salame")
                    .precioVenta(12.0)
                    .precioCompra(12.0)
                    .stockActual(3000.0)
                    .stockMinimo(200.0)
                    .esParaElaborar(true)
                    .categoria(categoriaCarne)
                    .unidadMedida(unidadGramos)
                    .imagen(imagenService.save(Imagen.builder().denominacion("https://i.imgur.com/salame.jpg").build()))
                    .build());

            ArticuloInsumo cremaLeche = articuloInsumoService.save(ArticuloInsumo.builder()
                    .denominacion("Crema de Leche")
                    .precioVenta(12.0)
                    .precioCompra(12.0)
                    .stockActual(3000.0)
                    .stockMinimo(400.0)
                    .esParaElaborar(true)
                    .categoria(categoriaLacteosYDerivados)
                    .unidadMedida(unidadMililitros)
                    .imagen(imagenService.save(Imagen.builder().denominacion("https://i.imgur.com/crema.jpg").build()))
                    .build());

            ArticuloInsumo oregano = articuloInsumoService.save(ArticuloInsumo.builder()
                    .denominacion("Orégano")
                    .precioVenta(35.0)
                    .precioCompra(35.0)
                    .stockActual(2500.0)
                    .stockMinimo(300.0)
                    .esParaElaborar(true)
                    .categoria(categoriaCondimentosYHierbas)
                    .unidadMedida(unidadGramos)
                    .imagen(imagenService.save(Imagen.builder().denominacion("https://i.imgur.com/oregano.jpg").build()))
                    .build());

            ArticuloInsumo aceitunas = articuloInsumoService.save(ArticuloInsumo.builder()
                    .denominacion("Aceitunas")
                    .precioVenta(8.0)
                    .precioCompra(8.0)
                    .stockActual(2500.0)
                    .stockMinimo(300.0)
                    .esParaElaborar(true)
                    .categoria(categoriaConservasYOtros)
                    .unidadMedida(unidadGramos)
                    .imagen(imagenService.save(Imagen.builder().denominacion("https://i.imgur.com/aceitunas.jpg").build()))
                    .build());

            ArticuloInsumo perejil = articuloInsumoService.save(ArticuloInsumo.builder()
                    .denominacion("Perejil")
                    .precioVenta(40.0)
                    .precioCompra(40.0)
                    .stockActual(1500.0)
                    .stockMinimo(250.0)
                    .esParaElaborar(true)
                    .categoria(categoriaCondimentosYHierbas)
                    .unidadMedida(unidadGramos)
                    .imagen(imagenService.save(Imagen.builder().denominacion("https://i.imgur.com/perejil.jpg").build()))
                    .build());

            ArticuloInsumo panceta = articuloInsumoService.save(ArticuloInsumo.builder()
                    .denominacion("Panceta")
                    .precioVenta(20.0)
                    .precioCompra(20.0)
                    .stockActual(3000.0)
                    .stockMinimo(400.0)
                    .esParaElaborar(true)
                    .categoria(categoriaCarne)
                    .unidadMedida(unidadGramos)
                    .imagen(imagenService.save(Imagen.builder().denominacion("https://i.imgur.com/panceta.jpg").build()))
                    .build());

            ArticuloInsumo arvejas = articuloInsumoService.save(ArticuloInsumo.builder()
                    .denominacion("Arvejas")
                    .precioVenta(120.0)
                    .precioCompra(90.0)
                    .stockActual(300.0)
                    .stockMinimo(50.0)
                    .esParaElaborar(true)
                    .categoria(categoriaVerdura)
                    .unidadMedida(unidadGramos)
                    .imagen(imagenService.save(Imagen.builder().denominacion("https://i.imgur.com/arvejas.jpg").build()))
                    .build());

            ArticuloInsumo ciboulette = articuloInsumoService.save(ArticuloInsumo.builder()
                    .denominacion("Ciboulette")
                    .precioVenta(3.0)
                    .precioCompra(3.0)
                    .stockActual(2000.0)
                    .stockMinimo(200.0)
                    .esParaElaborar(true)
                    .categoria(categoriaVerdura)
                    .unidadMedida(unidadGramos)
                    .imagen(imagenService.save(Imagen.builder().denominacion("https://i.imgur.com/ciboulette.jpg").build()))
                    .build());

            ArticuloInsumo ajimolido = articuloInsumoService.save(ArticuloInsumo.builder()
                    .denominacion("Ají molido")
                    .precioVenta(25.0)
                    .precioCompra(25.0)
                    .stockActual(2000.0)
                    .stockMinimo(250.0)
                    .esParaElaborar(true)
                    .categoria(categoriaCondimentosYHierbas)
                    .unidadMedida(unidadGramos)
                    .imagen(imagenService.save(Imagen.builder().denominacion("https://i.imgur.com/ajimolido.jpg").build()))
                    .build());

            ArticuloInsumo papas = articuloInsumoService.save(ArticuloInsumo.builder()
                    .denominacion("Papas")
                    .precioVenta(1.0)
                    .precioCompra(1.0)
                    .stockActual(8000.0)
                    .stockMinimo(500.0)
                    .esParaElaborar(true)
                    .categoria(categoriaVerdura)
                    .unidadMedida(unidadGramos)
                    .imagen(imagenService.save(Imagen.builder().denominacion("https://i.imgur.com/papas.jpg").build()))
                    .build());

            ArticuloInsumo choclo = articuloInsumoService.save(ArticuloInsumo.builder()
                    .denominacion("Choclo")
                    .precioVenta(5.0)
                    .precioCompra(5.0)
                    .stockActual(6000.0)
                    .stockMinimo(500.0)
                    .esParaElaborar(true)
                    .categoria(categoriaVerdura)
                    .unidadMedida(unidadGramos)
                    .imagen(imagenService.save(Imagen.builder().denominacion("https://i.imgur.com/choclo.jpg").build()))
                    .build());

            //7.1 Insumos de venta al publico
            ArticuloInsumo cocaCola = articuloInsumoService.save(ArticuloInsumo.builder()
                    .denominacion("Coca-Cola 500ml")
                    .precioVenta(1250.0)
                    .precioCompra(1250.0)
                    .stockActual(100.0)
                    .stockMinimo(20.0)
                    .esParaElaborar(false)
                    .categoria(categoriaBebida)
                    .unidadMedida(unidadPorcion)
                    .imagen(imgCoca500)
                    .build());

            ArticuloInsumo cocaCola1L = articuloInsumoService.save(ArticuloInsumo.builder()
                    .denominacion("Coca-Cola 1L")
                    .precioVenta(2000.0)
                    .precioCompra(2000.0)
                    .stockActual(80.0)
                    .stockMinimo(15.0)
                    .esParaElaborar(false)
                    .categoria(categoriaBebida)
                    .unidadMedida(unidadPorcion)
                    .imagen(imgCoca1l)
                    .build());

            ArticuloInsumo cocaCola15L = articuloInsumoService.save(ArticuloInsumo.builder()
                    .denominacion("Coca-Cola 1.5L")
                    .precioVenta(2500.0)
                    .precioCompra(2500.0)
                    .stockActual(80.0)
                    .stockMinimo(15.0)
                    .esParaElaborar(false)
                    .categoria(categoriaBebida)
                    .unidadMedida(unidadPorcion)
                    .imagen(imgCoca15l)
                    .build());

            ArticuloInsumo pepsi = articuloInsumoService.save(ArticuloInsumo.builder()
                    .denominacion("Pepsi 500ml")
                    .precioVenta(1200.0)
                    .precioCompra(1200.0)
                    .stockActual(100.0)
                    .stockMinimo(20.0)
                    .esParaElaborar(false)
                    .categoria(categoriaBebida)
                    .unidadMedida(unidadPorcion)
                    .imagen(imgPepsi500)
                    .build());
            ArticuloInsumo pepsi1L = articuloInsumoService.save(ArticuloInsumo.builder()
                    .denominacion("Pepsi 1L")
                    .precioVenta(2000.0)
                    .precioCompra(2000.0)
                    .stockActual(80.0)
                    .stockMinimo(15.0)
                    .esParaElaborar(false)
                    .categoria(categoriaBebida)
                    .unidadMedida(unidadPorcion)
                    .imagen(imgPepsi1l)
                    .build());

            ArticuloInsumo pepsi15L = articuloInsumoService.save(ArticuloInsumo.builder()
                    .denominacion("Pepsi 1.5L")
                    .precioVenta(2500.0)
                    .precioCompra(2500.0)
                    .stockActual(80.0)
                    .stockMinimo(15.0)
                    .esParaElaborar(false)
                    .categoria(categoriaBebida)
                    .unidadMedida(unidadPorcion)
                    .imagen(imgPepsi15l)
                    .build());

            ArticuloInsumo sprite = articuloInsumoService.save(ArticuloInsumo.builder()
                    .denominacion("Sprite 500ml")
                    .precioVenta(1200.0)
                    .precioCompra(1200.0)
                    .stockActual(100.0)
                    .stockMinimo(20.0)
                    .esParaElaborar(false)
                    .categoria(categoriaBebida)
                    .unidadMedida(unidadPorcion)
                    .imagen(imgSprite500)
                    .build());

            ArticuloInsumo sprite1L = articuloInsumoService.save(ArticuloInsumo.builder()
                    .denominacion("Sprite 1L")
                    .precioVenta(2100.0)
                    .precioCompra(2100.0)
                    .stockActual(80.0)
                    .stockMinimo(15.0)
                    .esParaElaborar(false)
                    .categoria(categoriaBebida)
                    .unidadMedida(unidadPorcion)
                    .imagen(imgSprite1l)
                    .build());

            ArticuloInsumo sprite15L = articuloInsumoService.save(ArticuloInsumo.builder()
                    .denominacion("Sprite 1.5L")
                    .precioVenta(2500.0)
                    .precioCompra(2500.0)
                    .stockActual(80.0)
                    .stockMinimo(15.0)
                    .esParaElaborar(false)
                    .categoria(categoriaBebida)
                    .unidadMedida(unidadPorcion)
                    .imagen(imgSprite15l)
                    .build());

            ArticuloInsumo fanta = articuloInsumoService.save(ArticuloInsumo.builder()
                    .denominacion("Fanta 500ml")
                    .precioVenta(1200.0)
                    .precioCompra(1200.0)
                    .stockActual(100.0)
                    .stockMinimo(20.0)
                    .esParaElaborar(false)
                    .categoria(categoriaBebida)
                    .unidadMedida(unidadPorcion)
                    .imagen(imgFanta500)
                    .build());

            ArticuloInsumo fanta1L = articuloInsumoService.save(ArticuloInsumo.builder()
                    .denominacion("Fanta 1L")
                    .precioVenta(2100.0)
                    .precioCompra(2100.0)
                    .stockActual(80.0)
                    .stockMinimo(15.0)
                    .esParaElaborar(false)
                    .categoria(categoriaBebida)
                    .unidadMedida(unidadPorcion)
                    .imagen(imgFanta1l)
                    .build());

            ArticuloInsumo fanta15L = articuloInsumoService.save(ArticuloInsumo.builder()
                    .denominacion("Fanta 1.5L")
                    .precioVenta(2500.0)
                    .precioCompra(2500.0)
                    .stockActual(80.0)
                    .stockMinimo(15.0)
                    .esParaElaborar(false)
                    .categoria(categoriaBebida)
                    .unidadMedida(unidadPorcion)
                    .imagen(imgFanta15l)
                    .build());

            ArticuloInsumo bombonHeladoSopelsa = articuloInsumoService.save(ArticuloInsumo.builder()
                    .denominacion("Bombón Helado Sopelsa")
                    .precioVenta(2000.0)
                    .precioCompra(2000.0)
                    .stockActual(50.0)
                    .stockMinimo(10.0)
                    .esParaElaborar(false)
                    .categoria(categoriaPostre)
                    .unidadMedida(unidadPorcion)
                    .imagen(imgBombonHelado)
                    .build());

            ArticuloInsumo bombonEscocesSopelsa = articuloInsumoService.save(ArticuloInsumo.builder()
                    .denominacion("Bombón Escocés Sopelsa")
                    .precioVenta(2000.0)
                    .precioCompra(2000.0)
                    .stockActual(50.0)
                    .stockMinimo(10.0)
                    .esParaElaborar(false)
                    .categoria(categoriaPostre)
                    .unidadMedida(unidadPorcion)
                    .imagen(imgBombonEscoces)
                    .build());

            ArticuloInsumo bombonSuizoSopelsa = articuloInsumoService.save(ArticuloInsumo.builder()
                    .denominacion("Bombón Suizo Sopelsa")
                    .precioVenta(2000.0)
                    .precioCompra(2000.0)
                    .stockActual(50.0)
                    .stockMinimo(10.0)
                    .esParaElaborar(false)
                    .categoria(categoriaPostre)
                    .unidadMedida(unidadPorcion)
                    .imagen(imgBombonSuizo)
                    .build());
//
            // 8. Artículo Manufacturado
            ArticuloManufacturado hamburguesa = ArticuloManufacturado.builder()
                    .denominacion("Hamburguesa Clásica")
                    .precioVenta(1250.0)
                    .descripcion("Deliciosa hamburguesa con queso y lechuga")
                    .tiempoEstimadoMinutos(20)
                    .preparacion("Preparar la carne, cocinar, armar.")
                    .categoria(categoriaHamburguesa)
                    .unidadMedida(unidadPorcion)
                    .imagen(imgHamburguesaSimple)
                    .build();

            ArticuloManufacturado hamburguesaTriple = ArticuloManufacturado.builder()
                    .denominacion("Hamburguesa Triple")
                    .precioVenta(13500.0)
                    .descripcion("Tres jugosos medallones de carne, queso cheddar y lechuga en pan artesanal.")
                    .tiempoEstimadoMinutos(25)
                    .preparacion("Cocinar los tres medallones, apilar con queso cheddar, armar la hamburguesa.")
                    .categoria(categoriaHamburguesa)
                    .unidadMedida(unidadPorcion)
                    .imagen(imgHamburguesaTriple)
                    .build();

            ArticuloManufacturado hamburguesaPolloSimple = ArticuloManufacturado.builder()
                    .denominacion("Hamburguesa de Pollo")
                    .precioVenta(8500.0)
                    .descripcion("Medallón de pollo crujiente con queso cheddar y lechuga fresca.")
                    .tiempoEstimadoMinutos(20)
                    .preparacion("Freír el medallón de pollo, montar con cheddar y lechuga en el pan.")
                    .categoria(categoriaHamburguesa)
                    .unidadMedida(unidadPorcion)
                    .imagen(imgHamburguesaPolloSimple)
                    .build();

            ArticuloManufacturado hamburguesaPolloDoble = ArticuloManufacturado.builder()
                    .denominacion("Hamburguesa de Pollo Doble")
                    .precioVenta(10500.0)
                    .descripcion("Doble medallón de pollo, doble cheddar, lechuga fresca y pan suave.")
                    .tiempoEstimadoMinutos(23)
                    .preparacion("Freír ambos medallones, montar con cheddar y lechuga en el pan.")
                    .categoria(categoriaHamburguesa)
                    .unidadMedida(unidadPorcion)
                    .imagen(imgHamburguesaPolloDoble)
                    .build();

            ArticuloManufacturado pizzaMuzzarella = ArticuloManufacturado.builder()
                    .denominacion("Pizza Muzzarella")
                    .precioVenta(7500.0)
                    .descripcion("Clásica pizza con abundante queso muzzarella y salsa de tomate.")
                    .tiempoEstimadoMinutos(30)
                    .preparacion("Agregar salsa y muzzarella a la masa y hornear.")
                    .categoria(categoriaPizza)
                    .unidadMedida(unidadPorcion)
                    .imagen(imgPizzaMuzzarella)
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
                    .precioVenta(500.0)
                    .descripcion("Empanadas rellenas de sabrosa carne molida condimentada.")
                    .tiempoEstimadoMinutos(25)
                    .preparacion("Preparar el relleno, rellenar las tapas y hornear.")
                    .categoria(categoriaEmpanada)
                    .unidadMedida(unidadPorcion)
                    .imagen(imgEmpanadasCarne)
                    .build();

            ArticuloManufacturado empanadasJyQ = ArticuloManufacturado.builder()
                    .denominacion("Empanadas de Jamón y Queso")
                    .precioVenta(550.0)
                    .descripcion("Empanadas con relleno cremoso de jamón cocido y queso.")
                    .tiempoEstimadoMinutos(25)
                    .preparacion("Rellenar las tapas con jamón y queso, cerrar y hornear.")
                    .categoria(categoriaEmpanada)
                    .unidadMedida(unidadPorcion)
                    .imagen(imgEmpanadasJyQ)
                    .build();

            ArticuloManufacturado lomitoSimple = ArticuloManufacturado.builder()
                    .denominacion("Lomito Simple")
                    .precioVenta(8500.0)
                    .descripcion("Lomito con carne, lechuga, tomate, jamón y queso en pan artesanal.")
                    .tiempoEstimadoMinutos(20)
                    .preparacion("Cocinar la carne, montar el lomito con vegetales, jamón y queso.")
                    .categoria(categoriaSanguche)
                    .unidadMedida(unidadPorcion)
                    .imagen(imgLomito)
                    .build();

            ArticuloManufacturado barrolucoSimple = ArticuloManufacturado.builder()
                    .denominacion("Barroluco Simple")
                    .precioVenta(8200.0)
                    .descripcion("Sándwich de miga tostado con carne, jamón y queso fundido.")
                    .tiempoEstimadoMinutos(18)
                    .preparacion("Montar el barroluco y tostar en plancha hasta que gratine.")
                    .categoria(categoriaSanguche)
                    .unidadMedida(unidadPorcion)
                    .imagen(imgBarroluco)
                    .build();

            ArticuloManufacturado papasFritas = ArticuloManufacturado.builder()
                    .denominacion("Papas Fritas")
                    .precioVenta(3200.0)
                    .descripcion("Clásicas papas fritas doradas y crujientes.")
                    .tiempoEstimadoMinutos(15)
                    .preparacion("Freír las papas en aceite caliente hasta dorar.")
                    .categoria(categoriaComida)
                    .unidadMedida(unidadPorcion)
                    .imagen(imgPapasFritas)
                    .build();

            ArticuloManufacturado papasCheddarBacon = ArticuloManufacturado.builder()
                    .denominacion("Papas con Cheddar y Bacon")
                    .precioVenta(4200.0)
                    .descripcion("Papas fritas cubiertas con queso cheddar fundido y crujiente bacon.")
                    .tiempoEstimadoMinutos(18)
                    .preparacion("Freír las papas, cubrir con cheddar derretido y bacon crujiente.")
                    .categoria(categoriaComida)
                    .unidadMedida(unidadPorcion)
                    .imagen(imgPapasCheddarBacon)
                    .build();

            ArticuloManufacturado empanadasCarneCuchillo = ArticuloManufacturado.builder()
                    .denominacion("Empanadas de Carne a Cuchillo")
                    .precioVenta(600.0)
                    .descripcion("Empanadas tradicionales rellenas con carne cortada a cuchillo.")
                    .tiempoEstimadoMinutos(30)
                    .preparacion("Preparar el relleno con carne cortada a cuchillo, rellenar las tapas y hornear.")
                    .categoria(categoriaEmpanada)
                    .unidadMedida(unidadPorcion)
                    .imagen(imgEmpanadaCuchillo)
                    .build();

            ArticuloManufacturado empanadasHumita = ArticuloManufacturado.builder()
                    .denominacion("Empanadas de Humita")
                    .precioVenta(580.0)
                    .descripcion("Empanadas con relleno suave de humita (choclo y salsa blanca).")
                    .tiempoEstimadoMinutos(25)
                    .preparacion("Preparar relleno de humita, rellenar las tapas y hornear.")
                    .categoria(categoriaEmpanada)
                    .unidadMedida(unidadPorcion)
                    .imagen(imgEmpanadaHumita)
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

            List<ArticuloManufacturadoDetalle> detallesPapasFritas = new ArrayList<>();
            detallesPapasFritas.add(ArticuloManufacturadoDetalle.builder().cantidad(250.0).articuloInsumo(papas).articuloManufacturado(papasFritas).build());

            papasFritas.setDetalles(detallesPapasFritas);
            articuloManufacturadoService.save(papasFritas);

            List<ArticuloManufacturadoDetalle> detallesPapasCheddarBacon = new ArrayList<>();
            detallesPapasCheddarBacon.add(ArticuloManufacturadoDetalle.builder().cantidad(250.0).articuloInsumo(papas).articuloManufacturado(papasCheddarBacon).build());
            detallesPapasCheddarBacon.add(ArticuloManufacturadoDetalle.builder().cantidad(80.0).articuloInsumo(cheddar).articuloManufacturado(papasCheddarBacon).build());
            detallesPapasCheddarBacon.add(ArticuloManufacturadoDetalle.builder().cantidad(50.0).articuloInsumo(panceta).articuloManufacturado(papasCheddarBacon).build());

            papasCheddarBacon.setDetalles(detallesPapasCheddarBacon);
            articuloManufacturadoService.save(papasCheddarBacon);

            List<ArticuloManufacturadoDetalle> detallesEmpCarneCuchillo = new ArrayList<>();
            detallesEmpCarneCuchillo.add(ArticuloManufacturadoDetalle.builder().cantidad(1.0).articuloInsumo(tapasEmpanada).articuloManufacturado(empanadasCarneCuchillo).build());
            detallesEmpCarneCuchillo.add(ArticuloManufacturadoDetalle.builder().cantidad(2.0).articuloInsumo(bifeCarne).articuloManufacturado(empanadasCarneCuchillo).build());
            detallesEmpCarneCuchillo.add(ArticuloManufacturadoDetalle.builder().cantidad(20.0).articuloInsumo(cebolla).articuloManufacturado(empanadasCarneCuchillo).build());

            empanadasCarneCuchillo.setDetalles(detallesEmpCarneCuchillo);
            articuloManufacturadoService.save(empanadasCarneCuchillo);

            List<ArticuloManufacturadoDetalle> detallesEmpHumita = new ArrayList<>();
            detallesEmpHumita.add(ArticuloManufacturadoDetalle.builder().cantidad(1.0).articuloInsumo(tapasEmpanada).articuloManufacturado(empanadasHumita).build());
            detallesEmpHumita.add(ArticuloManufacturadoDetalle.builder().cantidad(80.0).articuloInsumo(choclo).articuloManufacturado(empanadasHumita).build());
            detallesEmpHumita.add(ArticuloManufacturadoDetalle.builder().cantidad(30.0).articuloInsumo(cremaLeche).articuloManufacturado(empanadasHumita).build());
            detallesEmpHumita.add(ArticuloManufacturadoDetalle.builder().cantidad(10.0).articuloInsumo(quesoMuzzarella).articuloManufacturado(empanadasHumita).build());

            empanadasHumita.setDetalles(detallesEmpHumita);
            articuloManufacturadoService.save(empanadasHumita);



            //Carga de Usuarios
            Usuario usuarioAdmin = usuarioService.save(Usuario.builder()
                    .email("admin@buen.com")
                    .password(passwordEncoder.encode("admin123")) // asegurate que uses PasswordEncoder
                    .username("Admin General")
                    .rol(Rol.ADMINISTRADOR)
                    .build());
            Usuario usuarioCocinero = usuarioService.save(Usuario.builder()
                    .email("cocinero@buen.com")
                    .password(passwordEncoder.encode("cocinero123"))
                    .username("Juan Cocinero")
                    .rol(Rol.COCINERO)
                    .build());
            Usuario usuarioCajero = usuarioService.save(Usuario.builder()
                    .email("cajero@buen.com")
                    .password(passwordEncoder.encode("cajero123"))
                    .username("Carlos Cajero")
                    .rol(Rol.CAJERO)
                    .build());
            Usuario usuarioDelivery = usuarioService.save(Usuario.builder()
                    .email("delivery@buen.com")
                    .password(passwordEncoder.encode("delivery123"))
                    .username("Pepe delivery")
                    .rol(Rol.DELIVERY)
                    .build());



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
                    .formaPago(FormaPago.MERCADO_PAGO)//O EFECTIVO
                    .totalVenta(1250.0)
                    .anulada(false)
                    .mpPaymentId(123456789)//Dummy
                    .mpMerchantOrderId(987654321)//Dummy
                    .mpPreferenceId("PREF-123abc456")//Dummy
                    .mpPaymentType("credit_card") //Dummy
                    .urlPdf("https://mi-app.com/facturas/99.pdf")//Dummy
                    .build();

            facturaPedido = facturaService.save(facturaPedido);

            Pedido pedido = Pedido.builder()
                    .horaEstimadaFinalizacion(LocalTime.now().plusMinutes(30))
                    .total(1250.0)
                    .totalCosto(900.0)//Total y total costo dependiendo del detalle usado
                    .estado(Estado.EN_DELIVERY)//Variar entre: A_CONFIRMAR, PAGADO, EN_COCINA, EN_PREPARACION, LISTO, EN_DELIVERY, ENTREGADO, CANCELADO, RECHAZADO, DEVOLUCION
                    .tipoEnvio(TipoEnvio.DELIVERY)//Variar entre: DELIVERY y RETIRO_EN_LOCAL (DELIVERY solo disponible para pago con mercado_pago)
                    .formaPago(FormaPago.MERCADO_PAGO)//Variar entre: MERCADO_PAGO Y EFECTIVO
                    .fechaPedido(LocalDate.now())
                    .persona(persona)//Variar entre personas creados anteriormente
                    .domicilioEntrega(domicilioCliente)//Variar dependiendo el persona seleccionado en el campo anterior
                    .sucursal(sucursal)//Misma sucursal
                    .factura(facturaPedido) //Asignar factura
                    .anulado(false)
                    .build();

            DetallePedido detalle1a = DetallePedido.builder()
                    .cantidad(1)
                    .subTotal(1250.0)
                    .articuloManufacturado(hamburguesa)
                    .pedido(pedido)
                    .build();

            DetallePedido detalle1b = DetallePedido.builder()
                    .cantidad(2)
                    .subTotal(1000.0)
                    .articuloManufacturado(empanadasCarne)
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
                    .mpPaymentId(43523452)
                    .mpMerchantOrderId(23545234)
                    .mpPreferenceId("MPREF-13-87812339")
                    .mpPaymentType("credit_card")
                    .urlPdf(null)
                    .build();

            facturaPedido1 = facturaService.save(facturaPedido1);

            Pedido pedido1 = Pedido.builder()
                    .horaEstimadaFinalizacion(LocalTime.now().plusMinutes(30))
                    .total(1250.0)
                    .totalCosto(900.0)
                    .estado(Estado.EN_DELIVERY)
                    .tipoEnvio(TipoEnvio.DELIVERY)
                    .formaPago(FormaPago.MERCADO_PAGO)
                    .fechaPedido(LocalDate.now())
                    .persona(persona)
                    .domicilioEntrega(domicilioCliente)
                    .sucursal(sucursal)
                    .factura(facturaPedido1)
                    .anulado(false)
                    .build();

            DetallePedido detalle2a = DetallePedido.builder()
                    .cantidad(2)
                    .subTotal(12350.0)
                    .articuloManufacturado(hamburguesaPolloSimple)
                    .pedido(pedido1)
                    .build();

            DetallePedido detalle2b = DetallePedido.builder()
                    .cantidad(1)
                    .subTotal(7500.0)
                    .articuloManufacturado(pizzaMuzzarella)
                    .pedido(pedido1)
                    .build();

            DetallePedido detalle2c = DetallePedido.builder()
                    .cantidad(1)
                    .subTotal(2500.0)
                    .articuloInsumo(albahaca)
                    .pedido(pedido1)
                    .build();

            pedido1.setDetallesPedidos(Set.of(detalle2a, detalle2b, detalle2c));

            pedido.setEmpleado(null);

            facturaPedido1.setPedido(pedido1);

            pedidoService.save(pedido1);

            // Pedido 1 - Hamburguesa Clásica + Coca-Cola 500ml

// 1. Crear la factura
            Factura factura1 = Factura.builder()
                    .fechaFacturacion(LocalDate.now())
                    .formaPago(FormaPago.EFECTIVO)
                    .totalVenta(2050.0) // 1250 (hamburguesa) + 800 (coca)
                    .anulada(false)
                    .mpPaymentId(8373032)
                    .mpMerchantOrderId(16568309)
                    .mpPreferenceId("MPREF-13-87812339")
                    .mpPaymentType("credit_card")
                    .urlPdf("https://mi-app.com/facturas/1.pdf")
                    .build();

            factura1 = facturaService.save(factura1);

// 2. Crear el pedido
            Pedido pedido1a = Pedido.builder()
                    .horaEstimadaFinalizacion(LocalTime.now().plusMinutes(30))
                    .total(2050.0)
                    .totalCosto(1400.0) // Ejemplo: 900 (hamburguesa) + 500 (coca)
                    .estado(Estado.PAGADO)
                    .tipoEnvio(TipoEnvio.RETIRO_EN_LOCAL)
                    .formaPago(FormaPago.EFECTIVO)
                    .fechaPedido(LocalDate.now())
                    .persona(persona)
                    .domicilioEntrega(null) // No se necesita para retiro
                    .sucursal(sucursal)
                    .factura(factura1)
                    .anulado(false)
                    .build();

// 3. Crear los detalles
            DetallePedido detalle11a = DetallePedido.builder()
                    .cantidad(1)
                    .subTotal(1250.0)
                    .articuloManufacturado(hamburguesa)
                    .pedido(pedido1a)
                    .build();

            DetallePedido detalle11b = DetallePedido.builder()
                    .cantidad(1)
                    .subTotal(800.0)
                    .articuloInsumo(cocaCola)
                    .pedido(pedido1a)
                    .build();

// 4. Asignar detalles y guardar
            pedido1.setDetallesPedidos(Set.of(detalle11a, detalle11b));
            pedido1.setEmpleado(null);
            factura1.setPedido(pedido1a);
            pedidoService.save(pedido1a);

            // Pedido 2 - Empanadas de Carne a Cuchillo + Sprite 500ml

// 1. Crear la factura
            Factura factura2 = Factura.builder()
                    .fechaFacturacion(LocalDate.now())
                    .formaPago(FormaPago.MERCADO_PAGO)
                    .totalVenta(1400.0) // 600 (empanadas) + 800 (sprite)
                    .anulada(false)
                    .mpPaymentId(987654321)
                    .mpMerchantOrderId(54321)
                    .mpPreferenceId("pref_002")
                    .mpPaymentType("credit_card")
                    .urlPdf("https://mi-app.com/facturas/2.pdf")
                    .build();

            factura2 = facturaService.save(factura2);

// 2. Crear el pedido
            Pedido pedido2 = Pedido.builder()
                    .horaEstimadaFinalizacion(LocalTime.now().plusMinutes(25))
                    .total(1400.0)
                    .totalCosto(1050.0) // 550 (empanadas) + 500 (sprite)
                    .estado(Estado.PAGADO)
                    .tipoEnvio(TipoEnvio.DELIVERY)
                    .formaPago(FormaPago.MERCADO_PAGO)
                    .fechaPedido(LocalDate.now())
                    .persona(persona2) // ← Cambiar al segundo persona
                    .domicilioEntrega(persona2.getDomicilios().stream().findFirst().orElse(null))
                    .sucursal(sucursal)
                    .factura(factura2)
                    .anulado(false)
                    .build();

// 3. Crear los detalles
            DetallePedido detalle22a = DetallePedido.builder()
                    .cantidad(1)
                    .subTotal(600.0)
                    .articuloManufacturado(empanadasCarneCuchillo)
                    .pedido(pedido2)
                    .build();

            DetallePedido detalle22b = DetallePedido.builder()
                    .cantidad(1)
                    .subTotal(800.0)
                    .articuloInsumo(sprite)
                    .pedido(pedido2)
                    .build();

// 4. Asignar detalles y guardar
            pedido2.setDetallesPedidos(Set.of(detalle22a, detalle22b));
            pedido2.setEmpleado(null);
            factura2.setPedido(pedido2);
            pedidoService.save(pedido2);


            // Pedido 3 - Papas con Cheddar y Bacon + Pepsi 1L

// 1. Crear la factura
            Factura factura3 = Factura.builder()
                    .fechaFacturacion(LocalDate.now())
                    .formaPago(FormaPago.EFECTIVO)
                    .totalVenta(5100.0) // 4200 (papas cheddar bacon) + 900 (pepsi 1L)
                    .anulada(false)
                    .mpPaymentId(90330386)
                    .mpMerchantOrderId(213129011)
                    .mpPreferenceId("MPREF-12-87812339")
                    .mpPaymentType("credit_card")
                    .urlPdf("https://mi-app.com/facturas/3.pdf")
                    .build();

            factura3 = facturaService.save(factura3);

// 2. Crear el pedido
            Pedido pedido3 = Pedido.builder()
                    .horaEstimadaFinalizacion(LocalTime.now().plusMinutes(20))
                    .total(5100.0)
                    .totalCosto(3500.0) // 2600 (papas cheddar bacon) + 900 (pepsi 1L)
                    .estado(Estado.PAGADO)
                    .tipoEnvio(TipoEnvio.DELIVERY)
                    .formaPago(FormaPago.EFECTIVO)
                    .fechaPedido(LocalDate.now())
                    .persona(persona3) // ← Tercer persona
                    .domicilioEntrega(persona3.getDomicilios().stream().findFirst().orElse(null))
                    .sucursal(sucursal)
                    .factura(factura3)
                    .anulado(false)
                    .build();

// 3. Crear los detalles
            DetallePedido detalle33a = DetallePedido.builder()
                    .cantidad(1)
                    .subTotal(4200.0)
                    .articuloManufacturado(papasCheddarBacon)
                    .pedido(pedido3)
                    .build();

            DetallePedido detalle33b = DetallePedido.builder()
                    .cantidad(1)
                    .subTotal(900.0)
                    .articuloInsumo(pepsi1L)
                    .pedido(pedido3)
                    .build();

// 4. Asignar detalles y guardar
            pedido3.setDetallesPedidos(Set.of(detalle33a, detalle33b));
            pedido3.setEmpleado(null);
            factura3.setPedido(pedido3);
            pedidoService.save(pedido3);


// Pedido 4 - Empanadas JyQ (2 unidades) + Coca-Cola 500ml

// 1. Crear la factura
            Factura factura4 = Factura.builder()
                    .fechaFacturacion(LocalDate.now())
                    .formaPago(FormaPago.EFECTIVO)
                    .totalVenta(1900.0) // 2 * 550 (empanadas JyQ) + 800 (coca 500ml)
                    .anulada(false)
                    .mpPaymentId(121233331)
                    .mpMerchantOrderId(201203010)
                    .mpPreferenceId("MPREF-11-87812339")
                    .mpPaymentType("credit_card")
                    .urlPdf("https://mi-app.com/facturas/4.pdf")
                    .build();

            factura4 = facturaService.save(factura4);

// 2. Crear el pedido
            Pedido pedido4 = Pedido.builder()
                    .horaEstimadaFinalizacion(LocalTime.now().plusMinutes(20))
                    .total(1900.0)
                    .totalCosto(1300.0) // 2 * 400 (empanadas JyQ) + 500 (coca 500ml)
                    .estado(Estado.PAGADO)
                    .tipoEnvio(TipoEnvio.DELIVERY)
                    .formaPago(FormaPago.EFECTIVO)
                    .fechaPedido(LocalDate.now())
                    .persona(persona4) // ← Cuarto persona
                    .domicilioEntrega(persona4.getDomicilios().stream().findFirst().orElse(null))
                    .sucursal(sucursal)
                    .factura(factura4)
                    .anulado(false)
                    .build();

// 3. Crear los detalles
            DetallePedido detalle4a = DetallePedido.builder()
                    .cantidad(2)
                    .subTotal(1100.0)
                    .articuloManufacturado(empanadasJyQ)
                    .pedido(pedido4)
                    .build();

            DetallePedido detalle4b = DetallePedido.builder()
                    .cantidad(1)
                    .subTotal(800.0)
                    .articuloInsumo(cocaCola)
                    .pedido(pedido4)
                    .build();

// 4. Asignar detalles y guardar
            pedido4.setDetallesPedidos(Set.of(detalle4a, detalle4b));
            pedido4.setEmpleado(null);
            factura4.setPedido(pedido4);
            pedidoService.save(pedido4);


            // Pedido 5 - Papas con Cheddar y Bacon + Sprite 1L

// 1. Crear la factura
            Factura factura5 = Factura.builder()
                    .fechaFacturacion(LocalDate.now())
                    .formaPago(FormaPago.MERCADO_PAGO)
                    .totalVenta(5200.0) // 4200 (papas cheddar y bacon) + 1000 (sprite 1L)
                    .anulada(false)
                    .mpPaymentId(30343305)
                    .mpMerchantOrderId(400342335)
                    .mpPreferenceId("pref_005")
                    .mpPaymentType("credit_card")
                    .urlPdf("https://mi-app.com/facturas/5.pdf")
                    .build();

            factura5 = facturaService.save(factura5);

// 2. Crear el pedido
            Pedido pedido5 = Pedido.builder()
                    .horaEstimadaFinalizacion(LocalTime.now().plusMinutes(20))
                    .total(5200.0)
                    .totalCosto(3700.0) // 2700 (papas cheddar y bacon) + 1000 (sprite 1L)
                    .estado(Estado.PAGADO)
                    .tipoEnvio(TipoEnvio.RETIRO_EN_LOCAL)
                    .formaPago(FormaPago.MERCADO_PAGO)
                    .fechaPedido(LocalDate.now())
                    .persona(persona5) // ← Quinto persona
                    .domicilioEntrega(null)
                    .sucursal(sucursal)
                    .factura(factura5)
                    .anulado(false)
                    .build();

// 3. Crear los detalles
            DetallePedido detalle5a = DetallePedido.builder()
                    .cantidad(1)
                    .subTotal(4200.0)
                    .articuloManufacturado(papasCheddarBacon)
                    .pedido(pedido5)
                    .build();

            DetallePedido detalle5b = DetallePedido.builder()
                    .cantidad(1)
                    .subTotal(1000.0)
                    .articuloInsumo(sprite1L)
                    .pedido(pedido5)
                    .build();

// 4. Asignar detalles y guardar
            pedido5.setDetallesPedidos(Set.of(detalle5a, detalle5b));
            pedido5.setEmpleado(null);
            factura5.setPedido(pedido5);
            pedidoService.save(pedido5);


            // Pedido 6 - Empanadas de Humita x2 + Coca-Cola 1.5L

// 1. Crear la factura
            Factura factura6 = Factura.builder()
                    .fechaFacturacion(LocalDate.now())
                    .formaPago(FormaPago.MERCADO_PAGO)
                    .totalVenta(1960.0) // 580 * 2 + 800 (Coca 1.5L)
                    .anulada(false)
                    .mpPaymentId(303434306)
                    .mpMerchantOrderId(400434346)
                    .mpPreferenceId("pref_006")
                    .mpPaymentType("credit_card")
                    .urlPdf("https://mi-app.com/facturas/6.pdf")
                    .build();

            factura6 = facturaService.save(factura6);

// 2. Crear el pedido
            Pedido pedido6 = Pedido.builder()
                    .horaEstimadaFinalizacion(LocalTime.now().plusMinutes(25))
                    .total(1960.0)
                    .totalCosto(1300.0) // 2x empanadas humita (2 * 580 costo estimado ~ 800) + coca 1.5L (500)
                    .estado(Estado.PAGADO)
                    .tipoEnvio(TipoEnvio.RETIRO_EN_LOCAL)
                    .formaPago(FormaPago.MERCADO_PAGO)
                    .fechaPedido(LocalDate.now())
                    .persona(persona6) // ← Sexto persona
                    .domicilioEntrega(null)
                    .sucursal(sucursal)
                    .factura(factura6)
                    .anulado(false)
                    .build();

// 3. Crear los detalles
            DetallePedido detalle6a = DetallePedido.builder()
                    .cantidad(2)
                    .subTotal(1160.0)
                    .articuloManufacturado(empanadasHumita)
                    .pedido(pedido6)
                    .build();

            DetallePedido detalle6b = DetallePedido.builder()
                    .cantidad(1)
                    .subTotal(800.0)
                    .articuloInsumo(cocaCola15L)
                    .pedido(pedido6)
                    .build();

// 4. Asignar detalles y guardar
            pedido6.setDetallesPedidos(Set.of(detalle6a, detalle6b));
            pedido6.setEmpleado(null);
            factura6.setPedido(pedido6);
            pedidoService.save(pedido6);


// Crear la factura
            Factura factura7 = Factura.builder()
                    .fechaFacturacion(LocalDate.now())
                    .formaPago(FormaPago.EFECTIVO)
                    .totalVenta(1800.0)
                    .anulada(false)
                    .mpPaymentId(234134865)
                    .mpMerchantOrderId(234563798)
                    .mpPreferenceId("MPREF-11-87812339")
                    .mpPaymentType("credit_card")
                    .urlPdf("https://mi-app.com/facturas/107.pdf")
                    .build();

            factura7 = facturaService.save(factura7);

// Crear el pedido
            Pedido pedido7 = Pedido.builder()
                    .horaEstimadaFinalizacion(LocalTime.now().plusMinutes(25))
                    .total(1800.0)
                    .totalCosto(1000.0)
                    .estado(Estado.ENTREGADO)
                    .tipoEnvio(TipoEnvio.RETIRO_EN_LOCAL)
                    .formaPago(FormaPago.EFECTIVO)
                    .fechaPedido(LocalDate.now())
                    .persona(persona)
                    .domicilioEntrega(null) // Retiro en local
                    .sucursal(sucursal)
                    .factura(factura7)
                    .anulado(false)
                    .build();

// Crear los detalles
            DetallePedido detalle7a = DetallePedido.builder()
                    .cantidad(3)
                    .subTotal(1800.0) // 3 * 600
                    .articuloManufacturado(empanadasCarneCuchillo)
                    .pedido(pedido7)
                    .build();

// Asignar detalles al pedido
            pedido7.setDetallesPedidos(Set.of(detalle7a));

// Asignar empleado si aplica
            pedido7.setEmpleado(null);

// Relacionar la factura con el pedido
            factura7.setPedido(pedido7);

// Guardar el pedido
            pedidoService.save(pedido7);


// Crear la factura
            Factura factura8 = Factura.builder()
                    .fechaFacturacion(LocalDate.now())
                    .formaPago(FormaPago.MERCADO_PAGO)
                    .totalVenta(6200.0)
                    .anulada(false)
                    .mpPaymentId(3080012)
                    .mpMerchantOrderId(7800123)
                    .mpPreferenceId("PREF8")
                    .mpPaymentType("credit_card")
                    .urlPdf("https://mi-app.com/facturas/108.pdf")
                    .build();

            factura8 = facturaService.save(factura8);

// Crear el pedido
            Pedido pedido8 = Pedido.builder()
                    .horaEstimadaFinalizacion(LocalTime.now().plusMinutes(18))
                    .total(6200.0)
                    .totalCosto(3700.0)
                    .estado(Estado.ENTREGADO)
                    .tipoEnvio(TipoEnvio.DELIVERY)
                    .formaPago(FormaPago.MERCADO_PAGO)
                    .fechaPedido(LocalDate.now())
                    .persona(persona2)
                    .domicilioEntrega(persona2.getDomicilios().stream().findFirst().orElse(null))
                    .sucursal(sucursal)
                    .factura(factura8)
                    .anulado(false)
                    .build();

// Crear los detalles
            DetallePedido detalle8a = DetallePedido.builder()
                    .cantidad(1)
                    .subTotal(4200.0)
                    .articuloManufacturado(papasCheddarBacon)
                    .pedido(pedido8)
                    .build();

            DetallePedido detalle8b = DetallePedido.builder()
                    .cantidad(1)
                    .subTotal(2000.0)
                    .articuloInsumo(fanta15L)
                    .pedido(pedido8)
                    .build();

// Asignar detalles al pedido
            pedido8.setDetallesPedidos(Set.of(detalle8a, detalle8b));

// Relacionar la factura con el pedido
            factura8.setPedido(pedido8);

// Guardar el pedido
            pedidoService.save(pedido8);


            // Crear la factura
            Factura factura9 = Factura.builder()
                    .fechaFacturacion(LocalDate.now())
                    .formaPago(FormaPago.EFECTIVO)
                    .totalVenta(1700.0)
                    .anulada(false)
                    .mpPaymentId(23777227)
                    .mpMerchantOrderId(324111423)
                    .mpPreferenceId("MPREF-16-87812339")
                    .mpPaymentType("credit_card")
                    .urlPdf("https://mi-app.com/facturas/109.pdf")
                    .build();

            factura9 = facturaService.save(factura9);

// Crear el pedido
            Pedido pedido9 = Pedido.builder()
                    .horaEstimadaFinalizacion(LocalTime.now().plusMinutes(25))
                    .total(1700.0)
                    .totalCosto(1100.0)
                    .estado(Estado.EN_COCINA)
                    .tipoEnvio(TipoEnvio.RETIRO_EN_LOCAL)
                    .formaPago(FormaPago.EFECTIVO)
                    .fechaPedido(LocalDate.now())
                    .persona(persona3)
                    .domicilioEntrega(null)
                    .sucursal(sucursal)
                    .factura(factura9)
                    .anulado(false)
                    .build();

// Crear los detalles
            DetallePedido detalle9a = DetallePedido.builder()
                    .cantidad(2)
                    .subTotal(1160.0) // 2 x 580.0
                    .articuloManufacturado(empanadasHumita)
                    .pedido(pedido9)
                    .build();

            DetallePedido detalle9b = DetallePedido.builder()
                    .cantidad(1)
                    .subTotal(540.0)
                    .articuloInsumo(sprite1L)
                    .pedido(pedido9)
                    .build();

// Asignar detalles al pedido
            pedido9.setDetallesPedidos(Set.of(detalle9a, detalle9b));

// Relacionar la factura con el pedido
            factura9.setPedido(pedido9);

// Guardar el pedido
            pedidoService.save(pedido9);


            // Crear la factura
            Factura factura10 = Factura.builder()
                    .fechaFacturacion(LocalDate.now())
                    .formaPago(FormaPago.MERCADO_PAGO)
                    .totalVenta(5600.0)
                    .anulada(false)
                    .mpPaymentId(100010)
                    .mpMerchantOrderId(500010)
                    .mpPreferenceId("MPREF010")
                    .mpPaymentType("credit_card")
                    .urlPdf("https://mi-app.com/facturas/110.pdf")
                    .build();

            factura10 = facturaService.save(factura10);

// Crear el pedido
            Pedido pedido10 = Pedido.builder()
                    .horaEstimadaFinalizacion(LocalTime.now().plusMinutes(18))
                    .total(5600.0)
                    .totalCosto(3600.0)
                    .estado(Estado.EN_PREPARACION)
                    .tipoEnvio(TipoEnvio.DELIVERY)
                    .formaPago(FormaPago.MERCADO_PAGO)
                    .fechaPedido(LocalDate.now())
                    .persona(persona4)
                    .domicilioEntrega(persona4.getDomicilios().stream().findFirst().orElse(null))
                    .sucursal(sucursal)
                    .factura(factura10)
                    .anulado(false)
                    .build();

// Crear los detalles
            DetallePedido detalle10a = DetallePedido.builder()
                    .cantidad(1)
                    .subTotal(4200.0)
                    .articuloManufacturado(papasCheddarBacon)
                    .pedido(pedido10)
                    .build();

            DetallePedido detalle10b = DetallePedido.builder()
                    .cantidad(1)
                    .subTotal(1400.0)
                    .articuloInsumo(fanta15L)
                    .pedido(pedido10)
                    .build();

// Asignar detalles al pedido
            pedido10.setDetallesPedidos(Set.of(detalle10a, detalle10b));

// Relacionar la factura con el pedido
            factura10.setPedido(pedido10);

// Guardar el pedido
            pedidoService.save(pedido10);


            // Crear la factura
            Factura factura11 = Factura.builder()
                    .fechaFacturacion(LocalDate.now())
                    .formaPago(FormaPago.EFECTIVO)
                    .totalVenta(9500.0)
                    .anulada(false)
                    .mpPaymentId(11342566)
                    .mpMerchantOrderId(64646112)
                    .mpPreferenceId("MPREF-17-87812339")
                    .mpPaymentType("credit_card")
                    .urlPdf("https://mi-app.com/facturas/111.pdf")
                    .build();

            factura11 = facturaService.save(factura11);

// Crear el pedido
            Pedido pedido11 = Pedido.builder()
                    .horaEstimadaFinalizacion(LocalTime.now().plusMinutes(20))
                    .total(9500.0)
                    .totalCosto(6100.0)
                    .estado(Estado.ENTREGADO)
                    .tipoEnvio(TipoEnvio.RETIRO_EN_LOCAL)
                    .formaPago(FormaPago.EFECTIVO)
                    .fechaPedido(LocalDate.now())
                    .persona(persona5)
                    .domicilioEntrega(null)
                    .sucursal(sucursal)
                    .factura(factura11)
                    .anulado(false)
                    .build();

// Crear los detalles
            DetallePedido detalle111a = DetallePedido.builder()
                    .cantidad(1)
                    .subTotal(8500.0)
                    .articuloManufacturado(lomitoSimple)
                    .pedido(pedido11)
                    .build();

            DetallePedido detalle111b = DetallePedido.builder()
                    .cantidad(1)
                    .subTotal(1000.0)
                    .articuloInsumo(cocaCola1L)
                    .pedido(pedido11)
                    .build();

// Asignar detalles al pedido
            pedido11.setDetallesPedidos(Set.of(detalle111a, detalle111b));

// Relacionar la factura con el pedido
            factura11.setPedido(pedido11);

// Guardar el pedido
            pedidoService.save(pedido11);


            // FACTURA
            Factura facturaPedido12 = Factura.builder()
                    .fechaFacturacion(LocalDate.now())
                    .formaPago(FormaPago.EFECTIVO)
                    .totalVenta(1200.0)
                    .anulada(false)
                    .mpPaymentId(9999823)
                    .mpMerchantOrderId(99992382)
                    .mpPreferenceId("MPREF-18-87812339")
                    .mpPaymentType("credit_card")
                    .urlPdf("https://mi-app.com/facturas/112.pdf")
                    .build();

            facturaPedido12 = facturaService.save(facturaPedido12);

// PEDIDO
            Pedido pedido12 = Pedido.builder()
                    .horaEstimadaFinalizacion(LocalTime.now().plusMinutes(20))
                    .total(1200.0)
                    .totalCosto(850.0)
                    .estado(Estado.ENTREGADO)
                    .tipoEnvio(TipoEnvio.RETIRO_EN_LOCAL)
                    .formaPago(FormaPago.EFECTIVO)
                    .fechaPedido(LocalDate.now())
                    .persona(persona2) // Usar persona existente
                    .domicilioEntrega(null) // Su domicilio
                    .sucursal(sucursal)
                    .factura(facturaPedido12)
                    .anulado(false)
                    .build();

// DETALLES
            DetallePedido detalle12a = DetallePedido.builder()
                    .cantidad(1)
                    .subTotal(600.0)
                    .articuloManufacturado(empanadasCarneCuchillo)
                    .pedido(pedido12)
                    .build();

            DetallePedido detalle12b = DetallePedido.builder()
                    .cantidad(1)
                    .subTotal(600.0)
                    .articuloInsumo(cocaCola1L)
                    .pedido(pedido12)
                    .build();

            pedido12.setDetallesPedidos(Set.of(detalle12a, detalle12b));
            pedido12.setEmpleado(null); // No asignado
            facturaPedido12.setPedido(pedido12);

// GUARDAR PEDIDO
            pedidoService.save(pedido12);


            // FACTURA
            Factura facturaPedido13 = Factura.builder()
                    .fechaFacturacion(LocalDate.now())
                    .formaPago(FormaPago.MERCADO_PAGO)
                    .totalVenta(9800.0)
                    .anulada(false)
                    .mpPaymentId(94837218)
                    .mpMerchantOrderId(2738119)
                    .mpPreferenceId("MPREF-13-87281239")
                    .mpPaymentType("credit_card")
                    .urlPdf("https://mi-app.com/facturas/113.pdf")
                    .build();

            facturaPedido13 = facturaService.save(facturaPedido13);

// PEDIDO
            Pedido pedido13 = Pedido.builder()
                    .horaEstimadaFinalizacion(LocalTime.now().plusMinutes(30))
                    .total(9800.0)
                    .totalCosto(7150.0)
                    .estado(Estado.EN_COCINA)
                    .tipoEnvio(TipoEnvio.DELIVERY)
                    .formaPago(FormaPago.MERCADO_PAGO)
                    .fechaPedido(LocalDate.now())
                    .persona(persona3)
                    .domicilioEntrega(domicilioCliente3)
                    .sucursal(sucursal)
                    .factura(facturaPedido13)
                    .anulado(false)
                    .build();

// DETALLES
            DetallePedido detalle13a = DetallePedido.builder()
                    .cantidad(1)
                    .subTotal(5600.0)
                    .articuloManufacturado(lomitoSimple)
                    .pedido(pedido13)
                    .build();

            DetallePedido detalle13b = DetallePedido.builder()
                    .cantidad(1)
                    .subTotal(4200.0)
                    .articuloManufacturado(papasCheddarBacon)
                    .pedido(pedido13)
                    .build();

            pedido13.setDetallesPedidos(Set.of(detalle13a, detalle13b));
            pedido13.setEmpleado(null);
            facturaPedido13.setPedido(pedido13);

// GUARDAR PEDIDO
            pedidoService.save(pedido13);


// FACTURA
            Factura facturaPedido14 = Factura.builder()
                    .fechaFacturacion(LocalDate.now())
                    .formaPago(FormaPago.MERCADO_PAGO)
                    .totalVenta(1800.0)
                    .anulada(false)
                    .mpPaymentId(94837219)
                    .mpMerchantOrderId(2738120)
                    .mpPreferenceId("MPREF-14-87281240")
                    .mpPaymentType("credit_card")
                    .urlPdf("https://mi-app.com/facturas/114.pdf")
                    .build();

            facturaPedido14 = facturaService.save(facturaPedido14);

// PEDIDO
            Pedido pedido14 = Pedido.builder()
                    .horaEstimadaFinalizacion(LocalTime.now().plusMinutes(25))
                    .total(1800.0)
                    .totalCosto(1240.0)
                    .estado(Estado.ENTREGADO)
                    .tipoEnvio(TipoEnvio.RETIRO_EN_LOCAL)
                    .formaPago(FormaPago.MERCADO_PAGO)
                    .fechaPedido(LocalDate.now())
                    .persona(persona4)
                    .domicilioEntrega(null)
                    .sucursal(sucursal)
                    .factura(facturaPedido14)
                    .anulado(false)
                    .build();

// DETALLES
            DetallePedido detalle14a = DetallePedido.builder()
                    .cantidad(3)
                    .subTotal(1800.0)
                    .articuloManufacturado(empanadasHumita)
                    .pedido(pedido14)
                    .build();

            pedido14.setDetallesPedidos(Set.of(detalle14a));
            pedido14.setEmpleado(null);
            facturaPedido14.setPedido(pedido14);

// GUARDAR PEDIDO
            pedidoService.save(pedido14);


            // FACTURA
            Factura facturaPedido15 = Factura.builder()
                    .fechaFacturacion(LocalDate.now())
                    .formaPago(FormaPago.MERCADO_PAGO)
                    .totalVenta(8200.0)
                    .anulada(false)
                    .mpPaymentId(94837220)
                    .mpMerchantOrderId(2738121)
                    .mpPreferenceId("MPREF-15-87281241")
                    .mpPaymentType("credit_card")
                    .urlPdf("https://mi-app.com/facturas/115.pdf")
                    .build();

            facturaPedido15 = facturaService.save(facturaPedido15);

// PEDIDO
            Pedido pedido15 = Pedido.builder()
                    .horaEstimadaFinalizacion(LocalTime.now().plusMinutes(20))
                    .total(8200.0)
                    .totalCosto(6100.0)
                    .estado(Estado.EN_PREPARACION)
                    .tipoEnvio(TipoEnvio.RETIRO_EN_LOCAL)
                    .formaPago(FormaPago.MERCADO_PAGO)
                    .fechaPedido(LocalDate.now())
                    .persona(persona5)
                    .domicilioEntrega(null)
                    .sucursal(sucursal)
                    .factura(facturaPedido15)
                    .anulado(false)
                    .build();

// DETALLES
            DetallePedido detalle15a = DetallePedido.builder()
                    .cantidad(1)
                    .subTotal(8200.0)
                    .articuloManufacturado(barrolucoSimple)
                    .pedido(pedido15)
                    .build();

            pedido15.setDetallesPedidos(Set.of(detalle15a));
            pedido15.setEmpleado(null);
            facturaPedido15.setPedido(pedido15);

// GUARDAR PEDIDO
            pedidoService.save(pedido15);


// FACTURA
            Factura facturaPedido16 = Factura.builder()
                    .fechaFacturacion(LocalDate.now())
                    .formaPago(FormaPago.MERCADO_PAGO)
                    .totalVenta(5800.0)
                    .anulada(false)
                    .mpPaymentId(94837221)
                    .mpMerchantOrderId(2738122)
                    .mpPreferenceId("MPREF-16-87281242")
                    .mpPaymentType("debit_card")
                    .urlPdf("https://mi-app.com/facturas/116.pdf")
                    .build();

            facturaPedido16 = facturaService.save(facturaPedido16);

// PEDIDO
            Pedido pedido16 = Pedido.builder()
                    .horaEstimadaFinalizacion(LocalTime.now().plusMinutes(30))
                    .total(5800.0)
                    .totalCosto(4300.0)
                    .estado(Estado.A_CONFIRMAR)
                    .tipoEnvio(TipoEnvio.DELIVERY)
                    .formaPago(FormaPago.MERCADO_PAGO)
                    .fechaPedido(LocalDate.now())
                    .persona(persona6)
                    .domicilioEntrega(domicilioCliente6)
                    .sucursal(sucursal)
                    .factura(facturaPedido16)
                    .anulado(false)
                    .build();

// DETALLES
            DetallePedido detalle16a = DetallePedido.builder()
                    .cantidad(10)
                    .subTotal(5800.0)
                    .articuloManufacturado(empanadasHumita)
                    .pedido(pedido16)
                    .build();

            pedido16.setDetallesPedidos(Set.of(detalle16a));
            pedido16.setEmpleado(null);
            facturaPedido16.setPedido(pedido16);

// GUARDAR PEDIDO
            pedidoService.save(pedido16);


            // FACTURA
            Factura facturaPedido17 = Factura.builder()
                    .fechaFacturacion(LocalDate.now())
                    .formaPago(FormaPago.MERCADO_PAGO)
                    .totalVenta(12600.0)
                    .anulada(false)
                    .mpPaymentId(94837222)
                    .mpMerchantOrderId(2738123)
                    .mpPreferenceId("MPREF-17-87281243")
                    .mpPaymentType("credit_card")
                    .urlPdf("https://mi-app.com/facturas/117.pdf")
                    .build();

            facturaPedido17 = facturaService.save(facturaPedido17);

// PEDIDO
            Pedido pedido17 = Pedido.builder()
                    .horaEstimadaFinalizacion(LocalTime.now().plusMinutes(25))
                    .total(12600.0)
                    .totalCosto(9200.0)
                    .estado(Estado.CANCELADO)
                    .tipoEnvio(TipoEnvio.DELIVERY)
                    .formaPago(FormaPago.MERCADO_PAGO)
                    .fechaPedido(LocalDate.now())
                    .persona(persona7)
                    .domicilioEntrega(domicilioCliente7)
                    .sucursal(sucursal)
                    .factura(facturaPedido17)
                    .anulado(false)
                    .build();

// DETALLES
            DetallePedido detalle17a = DetallePedido.builder()
                    .cantidad(2)
                    .subTotal(8400.0)
                    .articuloManufacturado(lomitoSimple)
                    .pedido(pedido17)
                    .build();

            DetallePedido detalle17b = DetallePedido.builder()
                    .cantidad(1)
                    .subTotal(4200.0)
                    .articuloManufacturado(papasCheddarBacon)
                    .pedido(pedido17)
                    .build();

            pedido17.setDetallesPedidos(Set.of(detalle17a, detalle17b));
            pedido17.setEmpleado(null);
            facturaPedido17.setPedido(pedido17);

// GUARDAR PEDIDO
            pedidoService.save(pedido17);


            // FACTURA
            Factura facturaPedido18 = Factura.builder()
                    .fechaFacturacion(LocalDate.now())
                    .formaPago(FormaPago.MERCADO_PAGO)
                    .totalVenta(17600.0)
                    .anulada(false)
                    .mpPaymentId(94837223)
                    .mpMerchantOrderId(2738124)
                    .mpPreferenceId("MPREF-18-87281244")
                    .mpPaymentType("credit_card")
                    .urlPdf("https://mi-app.com/facturas/118.pdf")
                    .build();

            facturaPedido18 = facturaService.save(facturaPedido18);

// PEDIDO
            Pedido pedido18 = Pedido.builder()
                    .horaEstimadaFinalizacion(LocalTime.now().plusMinutes(30))
                    .total(17600.0)
                    .totalCosto(12600.0)
                    .estado(Estado.EN_DELIVERY)
                    .tipoEnvio(TipoEnvio.DELIVERY)
                    .formaPago(FormaPago.MERCADO_PAGO)
                    .fechaPedido(LocalDate.now())
                    .persona(persona8)
                    .domicilioEntrega(domicilioCliente8)
                    .sucursal(sucursal)
                    .factura(facturaPedido18)
                    .anulado(false)
                    .build();

// DETALLES
            DetallePedido detalle18a = DetallePedido.builder()
                    .cantidad(2)
                    .subTotal(11000.0)
                    .articuloManufacturado(hamburguesaTriple)
                    .pedido(pedido18)
                    .build();

            DetallePedido detalle18b = DetallePedido.builder()
                    .cantidad(2)
                    .subTotal(6600.0)
                    .articuloManufacturado(papasFritas)
                    .pedido(pedido18)
                    .build();

            pedido18.setDetallesPedidos(Set.of(detalle18a, detalle18b));
            pedido18.setEmpleado(null);
            facturaPedido18.setPedido(pedido18);

// GUARDAR PEDIDO
            pedidoService.save(pedido18);


// FACTURA
            Factura facturaPedido19 = Factura.builder()
                    .fechaFacturacion(LocalDate.now())
                    .formaPago(FormaPago.MERCADO_PAGO)
                    .totalVenta(18200.0)
                    .anulada(false)
                    .mpPaymentId(94837224)
                    .mpMerchantOrderId(2738125)
                    .mpPreferenceId("MPREF-19-87281245")
                    .mpPaymentType("credit_card")
                    .urlPdf("https://mi-app.com/facturas/119.pdf")
                    .build();

            facturaPedido19 = facturaService.save(facturaPedido19);

// PEDIDO
            Pedido pedido19 = Pedido.builder()
                    .horaEstimadaFinalizacion(LocalTime.now().plusMinutes(35))
                    .total(18200.0)
                    .totalCosto(12900.0)
                    .estado(Estado.CANCELADO)
                    .tipoEnvio(TipoEnvio.DELIVERY)
                    .formaPago(FormaPago.MERCADO_PAGO)
                    .fechaPedido(LocalDate.now())
                    .persona(persona10)
                    .domicilioEntrega(domicilioCliente10)
                    .sucursal(sucursal)
                    .factura(facturaPedido19)
                    .anulado(false)
                    .build();

// DETALLES
            DetallePedido detalle19a = DetallePedido.builder()
                    .cantidad(1)
                    .subTotal(8500.0)
                    .articuloManufacturado(lomitoSimple)
                    .pedido(pedido19)
                    .build();

            DetallePedido detalle19b = DetallePedido.builder()
                    .cantidad(1)
                    .subTotal(8200.0)
                    .articuloManufacturado(barrolucoSimple)
                    .pedido(pedido19)
                    .build();

            DetallePedido detalle19c = DetallePedido.builder()
                    .cantidad(1)
                    .subTotal(1500.0)
                    .articuloInsumo(cocaCola)
                    .pedido(pedido19)
                    .build();

            pedido19.setDetallesPedidos(Set.of(detalle19a, detalle19b, detalle19c));
            pedido19.setEmpleado(null);
            facturaPedido19.setPedido(pedido19);

// GUARDAR PEDIDO
            pedidoService.save(pedido19);


// FACTURA
            Factura facturaPedido20 = Factura.builder()
                    .fechaFacturacion(LocalDate.now())
                    .formaPago(FormaPago.MERCADO_PAGO)
                    .totalVenta(11030.0)
                    .anulada(false)
                    .mpPaymentId(94837225)
                    .mpMerchantOrderId(2738126)
                    .mpPreferenceId("MPREF-20-87281246")
                    .mpPaymentType("credit_card")
                    .urlPdf("https://mi-app.com/facturas/120.pdf")
                    .build();

            facturaPedido20 = facturaService.save(facturaPedido20);

// PEDIDO
            Pedido pedido20 = Pedido.builder()
                    .horaEstimadaFinalizacion(LocalTime.now().plusMinutes(30))
                    .total(11030.0)
                    .totalCosto(7450.0)
                    .estado(Estado.EN_COCINA)
                    .tipoEnvio(TipoEnvio.RETIRO_EN_LOCAL)
                    .formaPago(FormaPago.MERCADO_PAGO)
                    .fechaPedido(LocalDate.now())
                    .persona(persona11)
                    .domicilioEntrega(null)
                    .sucursal(sucursal)
                    .factura(facturaPedido20)
                    .anulado(false)
                    .build();

// DETALLES
            DetallePedido detalle20a = DetallePedido.builder()
                    .cantidad(2)
                    .subTotal(6400.0)
                    .articuloManufacturado(empanadasCarneCuchillo)
                    .pedido(pedido20)
                    .build();

            DetallePedido detalle20b = DetallePedido.builder()
                    .cantidad(1)
                    .subTotal(4200.0)
                    .articuloManufacturado(papasCheddarBacon)
                    .pedido(pedido20)
                    .build();

            DetallePedido detalle20c = DetallePedido.builder()
                    .cantidad(1)
                    .subTotal(800.0)
                    .articuloInsumo(sprite)
                    .pedido(pedido20)
                    .build();

            pedido20.setDetallesPedidos(Set.of(detalle20a, detalle20b, detalle20c));
            pedido20.setEmpleado(null);
            facturaPedido20.setPedido(pedido20);

// GUARDAR PEDIDO
            pedidoService.save(pedido20);


            // FACTURA
            Factura facturaPedido202 = Factura.builder()
                    .fechaFacturacion(LocalDate.now())
                    .formaPago(FormaPago.MERCADO_PAGO)
                    .totalVenta(12300.0)
                    .anulada(false)
                    .mpPaymentId(587934120)
                    .mpMerchantOrderId(782145600)
                    .mpPreferenceId("PREF-z9y8x7w6-20")
                    .mpPaymentType("credit_card")
                    .urlPdf("https://mi-app.com/facturas/20.pdf")
                    .build();

            facturaPedido202 = facturaService.save(facturaPedido202);

// PEDIDO
            Pedido pedido202 = Pedido.builder()
                    .horaEstimadaFinalizacion(LocalTime.now().plusMinutes(28))
                    .total(12300.0)
                    .totalCosto(8600.0)
                    .estado(Estado.LISTO)
                    .tipoEnvio(TipoEnvio.DELIVERY)
                    .formaPago(FormaPago.MERCADO_PAGO)
                    .fechaPedido(LocalDate.now())
                    .persona(persona) // Usa persona previamente cargado
                    .domicilioEntrega(domicilioCliente)
                    .sucursal(sucursal)
                    .factura(facturaPedido202)
                    .anulado(false)
                    .build();

// DETALLES DEL PEDIDO
            DetallePedido detalle202a = DetallePedido.builder()
                    .cantidad(1)
                    .subTotal(6000.0)
                    .articuloManufacturado(empanadasCarneCuchillo)
                    .pedido(pedido202)
                    .build();

            DetallePedido detalle202b = DetallePedido.builder()
                    .cantidad(1)
                    .subTotal(5000.0)
                    .articuloManufacturado(empanadasJyQ)
                    .pedido(pedido202)
                    .build();

            DetallePedido detalle202c = DetallePedido.builder()
                    .cantidad(1)
                    .subTotal(1300.0)
                    .articuloInsumo(mostaza)
                    .pedido(pedido202)
                    .build();

            pedido202.setDetallesPedidos(Set.of(detalle202a, detalle202b, detalle202c));

// Relación bidireccional
            facturaPedido20.setPedido(pedido202);
            pedido202.setEmpleado(null);

            pedidoService.save(pedido202);


// FACTURA
            Factura facturaPedido21 = Factura.builder()
                    .fechaFacturacion(LocalDate.now())
                    .formaPago(FormaPago.MERCADO_PAGO)
                    .totalVenta(10500.0)
                    .anulada(false)
                    .mpPaymentId(687245139)
                    .mpMerchantOrderId(888456710)
                    .mpPreferenceId("PREF-a1b2c3d4-21")
                    .mpPaymentType("debit_card")
                    .urlPdf("https://mi-app.com/facturas/21.pdf")
                    .build();

            facturaPedido21 = facturaService.save(facturaPedido21);

// PEDIDO
            Pedido pedido21 = Pedido.builder()
                    .horaEstimadaFinalizacion(LocalTime.now().plusMinutes(22))
                    .total(10500.0)
                    .totalCosto(7200.0)
                    .estado(Estado.A_CONFIRMAR)
                    .tipoEnvio(TipoEnvio.RETIRO_EN_LOCAL)
                    .formaPago(FormaPago.MERCADO_PAGO)
                    .fechaPedido(LocalDate.now())
                    .persona(persona) // Usa persona previamente cargado
                    .domicilioEntrega(null)
                    .sucursal(sucursal)
                    .factura(facturaPedido21)
                    .anulado(false)
                    .build();

// DETALLES DEL PEDIDO
            DetallePedido detalle21a = DetallePedido.builder()
                    .cantidad(1)
                    .subTotal(5800.0)
                    .articuloManufacturado(empanadasHumita)
                    .pedido(pedido21)
                    .build();

            DetallePedido detalle21b = DetallePedido.builder()
                    .cantidad(1)
                    .subTotal(3500.0)
                    .articuloInsumo(cocaCola)
                    .pedido(pedido21)
                    .build();

            DetallePedido detalle21c = DetallePedido.builder()
                    .cantidad(1)
                    .subTotal(1200.0)
                    .articuloInsumo(bombonSuizoSopelsa)
                    .pedido(pedido21)
                    .build();

            pedido21.setDetallesPedidos(Set.of(detalle21a, detalle21b, detalle21c));

// Relación bidireccional
            facturaPedido21.setPedido(pedido21);
            pedido21.setEmpleado(null);

            pedidoService.save(pedido21);


// FACTURA
            Factura facturaPedido22 = facturaService.save(Factura.builder()
                    .fechaFacturacion(LocalDate.now())
                    .formaPago(FormaPago.MERCADO_PAGO)
                    .totalVenta(8800.0) // 1 Hamburguesa Clásica (1250) + 1 CocaCola 1.5L (1500) + 2 Empanadas JyQ (550*2)
                    .anulada(false)
                    .mpPaymentId(565564789)
                    .mpMerchantOrderId(198764321)
                    .mpPreferenceId("PREF-22abc456")
                    .mpPaymentType("credit_card")
                    .urlPdf("https://mi-app.com/facturas/22.pdf")
                    .build());

            // PEDIDO
            Pedido pedido22 = Pedido.builder()
                    .horaEstimadaFinalizacion(LocalTime.now().plusMinutes(40))
                    .total(8800.0)
                    .totalCosto(5700.0) // 500 (costo hamburguesa) + 1000 (cocaCola1.5L) + 2*600 (JyQ insumo aprox)
                    .estado(Estado.PAGADO)
                    .tipoEnvio(TipoEnvio.DELIVERY)
                    .formaPago(FormaPago.MERCADO_PAGO)
                    .fechaPedido(LocalDate.now())
                    .persona(persona10) // por ejemplo, décimo persona cargado
                    .domicilioEntrega(persona10.getDomicilios().iterator().next())
                    .sucursal(sucursal)
                    .factura(facturaPedido22)
                    .anulado(false)
                    .build();

// DETALLES
            DetallePedido detalle1 = DetallePedido.builder()
                    .cantidad(1)
                    .subTotal(hamburguesa.getPrecioVenta() * 1) // 1250.0
                    .articuloManufacturado(hamburguesa)
                    .pedido(pedido22)
                    .build();

            DetallePedido detalle2 = DetallePedido.builder()
                    .cantidad(1)
                    .subTotal(cocaCola15L.getPrecioVenta() * 1) // 1500.0
                    .articuloInsumo(cocaCola15L)
                    .pedido(pedido22)
                    .build();

            DetallePedido detalle3 = DetallePedido.builder()
                    .cantidad(2)
                    .subTotal(empanadasJyQ.getPrecioVenta() * 2) // 550 * 2 = 1100.0
                    .articuloManufacturado(empanadasJyQ)
                    .pedido(pedido22)
                    .build();

            pedido22.setDetallesPedidos(Set.of(detalle1, detalle2, detalle3));
            pedido22.setEmpleado(null); // Opcionalmente se puede dejar null

// Relación inversa
            facturaPedido22.setPedido(pedido22);

// Guardar el pedido (esto guarda en cascada la factura si está bien mapeada)
            pedidoService.save(pedido22);


// 1. FACTURA
            Factura facturaPedido23 = facturaService.save(Factura.builder()
                    .fechaFacturacion(LocalDate.now())
                    .formaPago(FormaPago.MERCADO_PAGO)
                    .totalVenta(11600.0) // 1 Hamburguesa de Pollo Doble (10500.0) + 1 Pepsi 500ml (800.0) + 1 Bombón Escocés (1300.0)
                    .anulada(false)
                    .mpPaymentId(565564790)
                    .mpMerchantOrderId(198764322)
                    .mpPreferenceId("PREF-23abc456")
                    .mpPaymentType("credit_card")
                    .urlPdf("https://mi-app.com/facturas/23.pdf")
                    .build());

// 2. PEDIDO
            Pedido pedido23 = Pedido.builder()
                    .horaEstimadaFinalizacion(LocalTime.now().plusMinutes(35))
                    .total(11600.0)
                    .totalCosto(8650.0) // 6500 (pollo doble) + 500 (pepsi) + 850 (bombón escocés)
                    .estado(Estado.EN_DELIVERY)
                    .tipoEnvio(TipoEnvio.DELIVERY)
                    .formaPago(FormaPago.MERCADO_PAGO)
                    .fechaPedido(LocalDate.now())
                    .persona(persona3) // Persona previamente creado
                    .domicilioEntrega(persona3.getDomicilios().iterator().next()) // Primer domicilio asociado
                    .sucursal(sucursal)
                    .factura(facturaPedido23)
                    .anulado(false)
                    .build();

// 3. DETALLES
            DetallePedido detalle23a = DetallePedido.builder()
                    .cantidad(1)
                    .subTotal(hamburguesaPolloDoble.getPrecioVenta() * 1) // 10500.0
                    .articuloManufacturado(hamburguesaPolloDoble)
                    .pedido(pedido23)
                    .build();

            DetallePedido detalle23b = DetallePedido.builder()
                    .cantidad(1)
                    .subTotal(pepsi.getPrecioVenta() * 1) // 800.0
                    .articuloInsumo(pepsi)
                    .pedido(pedido23)
                    .build();

            DetallePedido detalle23c = DetallePedido.builder()
                    .cantidad(1)
                    .subTotal(bombonEscocesSopelsa.getPrecioVenta() * 1) // 1300.0
                    .articuloInsumo(bombonEscocesSopelsa)
                    .pedido(pedido23)
                    .build();

// 4. RELACIONES Y GUARDADO
            pedido23.setDetallesPedidos(Set.of(detalle23a, detalle23b, detalle23c));
            pedido23.setEmpleado(null); // No asignado
            facturaPedido23.setPedido(pedido23);
            pedidoService.save(pedido23);


            // 1. FACTURA
            Factura facturaPedido24 = facturaService.save(Factura.builder()
                    .fechaFacturacion(LocalDate.now())
                    .formaPago(FormaPago.EFECTIVO)
                    .totalVenta(12800.0) // 1 Pizza Margarita (8000.0) + 1 Fanta 1.5L (1500.0) + 2 Bombón Helado Sopelsa (2 x 1200.0)
                    .anulada(false)
                    .mpPaymentId(112233445)
                    .mpMerchantOrderId(998877665)
                    .mpPreferenceId("PREF-24abc789")
                    .mpPaymentType("cash")
                    .urlPdf("https://mi-app.com/facturas/24.pdf")
                    .build());

// 2. PEDIDO
            Pedido pedido24 = Pedido.builder()
                    .horaEstimadaFinalizacion(LocalTime.now().plusMinutes(45))
                    .total(12800.0)
                    .totalCosto(9700.0) // 6000 (pizza) + 1000 (fanta) + 2 x 850 (bombón helado)
                    .estado(Estado.PAGADO)
                    .tipoEnvio(TipoEnvio.RETIRO_EN_LOCAL)
                    .formaPago(FormaPago.EFECTIVO)
                    .fechaPedido(LocalDate.now())
                    .persona(persona4)
                    .domicilioEntrega(null)
                    .sucursal(sucursal)
                    .factura(facturaPedido24)
                    .anulado(false)
                    .build();

// 3. DETALLES
            DetallePedido detalle24a = DetallePedido.builder()
                    .cantidad(1)
                    .subTotal(pizzaMargarita.getPrecioVenta() * 1) // 8000.0
                    .articuloManufacturado(pizzaMargarita)
                    .pedido(pedido24)
                    .build();

            DetallePedido detalle24b = DetallePedido.builder()
                    .cantidad(1)
                    .subTotal(fanta15L.getPrecioVenta() * 1) // 1500.0
                    .articuloInsumo(fanta15L)
                    .pedido(pedido24)
                    .build();

            DetallePedido detalle24c = DetallePedido.builder()
                    .cantidad(2)
                    .subTotal(bombonHeladoSopelsa.getPrecioVenta() * 2) // 2 x 1200.0 = 2400.0
                    .articuloInsumo(bombonHeladoSopelsa)
                    .pedido(pedido24)
                    .build();

// 4. RELACIONES Y GUARDADO
            pedido24.setDetallesPedidos(Set.of(detalle24a, detalle24b, detalle24c));
            pedido24.setEmpleado(null);
            facturaPedido24.setPedido(pedido24);
            pedidoService.save(pedido24);


// 1. FACTURA
            Factura facturaPedido25 = facturaService.save(Factura.builder()
                    .fechaFacturacion(LocalDate.now())
                    .formaPago(FormaPago.MERCADO_PAGO)
                    .totalVenta(13300.0) // 1 Lomito Simple (8500.0) + 1 Sprite 1L (1200.0) + 3 Empanadas de Humita (3 x 580.0)
                    .anulada(false)
                    .mpPaymentId(192837465)
                    .mpMerchantOrderId(564738291)
                    .mpPreferenceId("PREF-25xyz321")
                    .mpPaymentType("credit_card")
                    .urlPdf("https://mi-app.com/facturas/25.pdf")
                    .build());

// 2. PEDIDO
            Pedido pedido25 = Pedido.builder()
                    .horaEstimadaFinalizacion(LocalTime.now().plusMinutes(35))
                    .total(13300.0)
                    .totalCosto(9800.0) // 6200 (lomito) + 750 (sprite1L) + 3 x 950 (empanadas humita)
                    .estado(Estado.LISTO)
                    .tipoEnvio(TipoEnvio.DELIVERY)
                    .formaPago(FormaPago.MERCADO_PAGO)
                    .fechaPedido(LocalDate.now())
                    .persona(persona5)
                    .domicilioEntrega(persona5.getDomicilios().iterator().next())
                    .sucursal(sucursal)
                    .factura(facturaPedido25)
                    .anulado(false)
                    .build();

// 3. DETALLES
            DetallePedido detalle25a = DetallePedido.builder()
                    .cantidad(1)
                    .subTotal(lomitoSimple.getPrecioVenta()) // 8500.0
                    .articuloManufacturado(lomitoSimple)
                    .pedido(pedido25)
                    .build();

            DetallePedido detalle25b = DetallePedido.builder()
                    .cantidad(1)
                    .subTotal(sprite1L.getPrecioVenta()) // 1200.0
                    .articuloInsumo(sprite1L)
                    .pedido(pedido25)
                    .build();

            DetallePedido detalle25c = DetallePedido.builder()
                    .cantidad(3)
                    .subTotal(empanadasHumita.getPrecioVenta() * 3) // 3 x 580.0 = 1740.0
                    .articuloManufacturado(empanadasHumita)
                    .pedido(pedido25)
                    .build();

// 4. RELACIONES Y GUARDADO
            pedido25.setDetallesPedidos(Set.of(detalle25a, detalle25b, detalle25c));
            pedido25.setEmpleado(null);
            facturaPedido25.setPedido(pedido25);
            pedidoService.save(pedido25);


// 1. FACTURA
            Factura facturaPedido26 = facturaService.save(Factura.builder()
                    .fechaFacturacion(LocalDate.now())
                    .formaPago(FormaPago.EFECTIVO)
                    .totalVenta(11050.0) // 1 Pizza Especial (8800) + 1 Bombón Escocés Sopelsa (1300) + 1 Sprite 500ml (800)
                    .anulada(false)
                    .mpPaymentId(293847561)
                    .mpMerchantOrderId(675849302)
                    .mpPreferenceId("PREF-26xyz321")
                    .mpPaymentType("cash")
                    .urlPdf("https://mi-app.com/facturas/26.pdf")
                    .build());

// 2. PEDIDO
            Pedido pedido26 = Pedido.builder()
                    .horaEstimadaFinalizacion(LocalTime.now().plusMinutes(40))
                    .total(11050.0)
                    .totalCosto(7850.0) // 5900 (pizza especial) + 850 (bombón escocés) + 1100 (sprite 500ml)
                    .estado(Estado.EN_COCINA)
                    .tipoEnvio(TipoEnvio.RETIRO_EN_LOCAL)
                    .formaPago(FormaPago.EFECTIVO)
                    .fechaPedido(LocalDate.now())
                    .persona(persona6)
                    .domicilioEntrega(null)
                    .sucursal(sucursal)
                    .factura(facturaPedido26)
                    .anulado(false)
                    .build();

// 3. DETALLES
            DetallePedido detalle26a = DetallePedido.builder()
                    .cantidad(1)
                    .subTotal(pizzaEspecial.getPrecioVenta()) // 8800.0
                    .articuloManufacturado(pizzaEspecial)
                    .pedido(pedido26)
                    .build();

            DetallePedido detalle26b = DetallePedido.builder()
                    .cantidad(1)
                    .subTotal(bombonEscocesSopelsa.getPrecioVenta()) // 1300.0
                    .articuloInsumo(bombonEscocesSopelsa)
                    .pedido(pedido26)
                    .build();

            DetallePedido detalle26c = DetallePedido.builder()
                    .cantidad(1)
                    .subTotal(sprite.getPrecioVenta()) // 800.0
                    .articuloInsumo(sprite)
                    .pedido(pedido26)
                    .build();

// 4. RELACIONES Y GUARDADO
            pedido26.setDetallesPedidos(Set.of(detalle26a, detalle26b, detalle26c));
            pedido26.setEmpleado(null);
            facturaPedido26.setPedido(pedido26);
            pedidoService.save(pedido26);


// 1. FACTURA
            Factura facturaPedido27 = facturaService.save(Factura.builder()
                    .fechaFacturacion(LocalDate.now())
                    .formaPago(FormaPago.MERCADO_PAGO)
                    .totalVenta(11800.0) // 2 Lomito Simple (2×8500 = 17000) + 1 Fanta 500ml (800) - Descuento ficticio = 11800
                    .anulada(false)
                    .mpPaymentId(192837465)
                    .mpMerchantOrderId(786452301)
                    .mpPreferenceId("PREF-27abc987")
                    .mpPaymentType("credit_card")
                    .urlPdf("https://mi-app.com/facturas/27.pdf")
                    .build());

// 2. PEDIDO
            Pedido pedido27 = Pedido.builder()
                    .horaEstimadaFinalizacion(LocalTime.now().plusMinutes(45))
                    .total(11800.0)
                    .totalCosto(8650.0) // (2 × 6000 del lomito) + 650 fanta
                    .estado(Estado.A_CONFIRMAR)
                    .tipoEnvio(TipoEnvio.DELIVERY)
                    .formaPago(FormaPago.MERCADO_PAGO)
                    .fechaPedido(LocalDate.now())
                    .persona(persona12)
                    .domicilioEntrega(persona12.getDomicilios().iterator().next())
                    .sucursal(sucursal)
                    .factura(facturaPedido27)
                    .anulado(false)
                    .build();

// 3. DETALLES
            DetallePedido detalle27a = DetallePedido.builder()
                    .cantidad(2)
                    .subTotal(lomitoSimple.getPrecioVenta() * 2) // 8500 * 2 = 17000
                    .articuloManufacturado(lomitoSimple)
                    .pedido(pedido27)
                    .build();

            DetallePedido detalle27b = DetallePedido.builder()
                    .cantidad(1)
                    .subTotal(fanta.getPrecioVenta()) // 800.0
                    .articuloInsumo(fanta)
                    .pedido(pedido27)
                    .build();

// 4. RELACIONES Y GUARDADO
            pedido27.setDetallesPedidos(Set.of(detalle27a, detalle27b));
            pedido27.setEmpleado(null);
            facturaPedido27.setPedido(pedido27);
            pedidoService.save(pedido27);


            // Factura
            Factura factura28 = facturaService.save(Factura.builder()
                    .fechaFacturacion(LocalDate.now())
                    .formaPago(FormaPago.EFECTIVO)
                    .totalVenta(8700.0)
                    .anulada(false)
                    .mpPaymentId(987654325)
                    .mpMerchantOrderId(123456785)
                    .mpPreferenceId("PREF-828abc456")
                    .mpPaymentType("cash")
                    .urlPdf("https://mi-app.com/facturas/128.pdf")
                    .build());

// Pedido
            Pedido pedido28 = Pedido.builder()
                    .horaEstimadaFinalizacion(LocalTime.now().plusMinutes(35))
                    .total(8700.0)
                    .totalCosto(6200.0)
                    .estado(Estado.LISTO)
                    .tipoEnvio(TipoEnvio.RETIRO_EN_LOCAL)
                    .formaPago(FormaPago.EFECTIVO)
                    .fechaPedido(LocalDate.now())
                    .persona(persona5)
                    .domicilioEntrega(null)
                    .sucursal(sucursal)
                    .factura(factura28)
                    .anulado(false)
                    .build();

// Detalles
            DetallePedido d28a = DetallePedido.builder()
                    .cantidad(1)
                    .subTotal(hamburguesaPolloSimple.getPrecioVenta() * 1)
                    .articuloManufacturado(hamburguesaPolloSimple)
                    .pedido(pedido28)
                    .build();

            DetallePedido d28b = DetallePedido.builder()
                    .cantidad(1)
                    .subTotal(pepsi1L.getPrecioVenta() * 1)
                    .articuloInsumo(pepsi1L)
                    .pedido(pedido28)
                    .build();

            pedido28.setDetallesPedidos(Set.of(d28a, d28b));
            factura28.setPedido(pedido28);
            pedido28.setEmpleado(null);
            pedidoService.save(pedido28);


            Factura factura29 = facturaService.save(Factura.builder()
                    .fechaFacturacion(LocalDate.now())
                    .formaPago(FormaPago.MERCADO_PAGO)
                    .totalVenta(17300.0)
                    .anulada(false)
                    .mpPaymentId(876543210)
                    .mpMerchantOrderId(210987654)
                    .mpPreferenceId("PREF-829def789")
                    .mpPaymentType("credit_card")
                    .urlPdf("https://mi-app.com/facturas/129.pdf")
                    .build());

            Pedido pedido29 = Pedido.builder()
                    .horaEstimadaFinalizacion(LocalTime.now().plusMinutes(50))
                    .total(17300.0)
                    .totalCosto(12900.0)
                    .estado(Estado.ENTREGADO)
                    .tipoEnvio(TipoEnvio.DELIVERY)
                    .formaPago(FormaPago.MERCADO_PAGO)
                    .fechaPedido(LocalDate.now())
                    .persona(persona9)
                    .domicilioEntrega(domicilioCliente9)
                    .sucursal(sucursal)
                    .factura(factura29)
                    .anulado(false)
                    .build();

            DetallePedido d29a = DetallePedido.builder()
                    .cantidad(2)
                    .subTotal(pizzaMargarita.getPrecioVenta() * 2)
                    .articuloManufacturado(pizzaMargarita)
                    .pedido(pedido29)
                    .build();

            DetallePedido d29b = DetallePedido.builder()
                    .cantidad(1)
                    .subTotal(fanta15L.getPrecioVenta())
                    .articuloInsumo(fanta15L)
                    .pedido(pedido29)
                    .build();

            pedido29.setDetallesPedidos(Set.of(d29a, d29b));
            factura29.setPedido(pedido29);
            pedido29.setEmpleado(null);
            pedidoService.save(pedido29);


            Factura factura30 = facturaService.save(Factura.builder()
                    .fechaFacturacion(LocalDate.now())
                    .formaPago(FormaPago.MERCADO_PAGO)
                    .totalVenta(11500.0)
                    .anulada(false)
                    .mpPaymentId(112233445)
                    .mpMerchantOrderId(998877665)
                    .mpPreferenceId("PREF-830ghi123")
                    .mpPaymentType("debit_card")
                    .urlPdf("https://mi-app.com/facturas/130.pdf")
                    .build());

            Pedido pedido30 = Pedido.builder()
                    .horaEstimadaFinalizacion(LocalTime.now().plusMinutes(40))
                    .total(11500.0)
                    .totalCosto(8000.0)
                    .estado(Estado.EN_PREPARACION)
                    .tipoEnvio(TipoEnvio.DELIVERY)
                    .formaPago(FormaPago.MERCADO_PAGO)
                    .fechaPedido(LocalDate.now())
                    .persona(persona13)
                    .domicilioEntrega(domicilioCliente13)
                    .sucursal(sucursal)
                    .factura(factura30)
                    .anulado(false)
                    .build();

            DetallePedido d30a = DetallePedido.builder()
                    .cantidad(1)
                    .subTotal(lomitoSimple.getPrecioVenta())
                    .articuloManufacturado(lomitoSimple)
                    .pedido(pedido30)
                    .build();

            DetallePedido d30b = DetallePedido.builder()
                    .cantidad(2)
                    .subTotal(bombonHeladoSopelsa.getPrecioVenta() * 2)
                    .articuloInsumo(bombonHeladoSopelsa)
                    .pedido(pedido30)
                    .build();

            pedido30.setDetallesPedidos(Set.of(d30a, d30b));
            factura30.setPedido(pedido30);
            pedido30.setEmpleado(null);
            pedidoService.save(pedido30);


            //PROMOCIONES

            Promocion promoBurgerPizza = Promocion.builder()
                    .denominacion("Promo Locura Hamburguesas")
                    .fechaDesde(LocalDate.now())
                    .fechaHasta(LocalDate.now().plusDays(10))
                    .horaDesde(LocalTime.of(18, 0))
                    .horaHasta(LocalTime.of(22, 0))
                    .descripcionDescuento("2x1 en Hamburguesa Clásica")
                    .precioPromocional(9500.0) // O el precio que prefieras
                    .tipoPromocion(TipoPromocion.HAPPY_HOUR)
                    .imagen(imgPromo1)
                    .articulosManufacturados(
                            List.of(hamburguesa, hamburguesa)
                    )
                    .sucursales(List.of(sucursal))
                    .build();

            promocionService.save(promoBurgerPizza);

            Promocion pizzaNight = Promocion.builder()
                    .denominacion("Pizza Night")
                    .fechaDesde(LocalDate.now())
                    .fechaHasta(LocalDate.now().plusDays(14))
                    .horaDesde(LocalTime.of(20, 0))
                    .horaHasta(LocalTime.of(23, 0))
                    .descripcionDescuento("¡Llevate 2 pizzas a precio especial solo por la noche! Aplica a Muzzarella")
                    .precioPromocional(13900.0) // Precio por 2
                    .tipoPromocion(TipoPromocion.PROMOCION_GENERAL)
                    .imagen(imgPromo2)
                    .articulosManufacturados(List.of(pizzaMuzzarella, pizzaMuzzarella))
                    .sucursales(List.of(sucursal))
                    .build();
            promocionService.save(pizzaNight);

            Promocion empanadaFest = Promocion.builder()
                    .denominacion("Empanada Fest")
                    .fechaDesde(LocalDate.now())
                    .fechaHasta(LocalDate.now().plusWeeks(1))
                    .horaDesde(LocalTime.of(12, 0))
                    .horaHasta(LocalTime.of(15, 0))
                    .descripcionDescuento("¡Promo mediodía! 12 empanadas surtidas por precio especial.")
                    .precioPromocional(6200.0)
                    .tipoPromocion(TipoPromocion.PROMOCION_GENERAL)
                    .imagen(imgPromo3)
                    .articulosManufacturados(List.of(empanadasCarne, empanadasJyQ, empanadasCarneCuchillo, empanadasHumita, empanadasCarne, empanadasJyQ, empanadasCarneCuchillo, empanadasHumita, empanadasCarne, empanadasJyQ, empanadasCarneCuchillo, empanadasHumita))
                    .sucursales(List.of(sucursal))
                    .build();

            promocionService.save(empanadaFest);

            Promocion sandwichYpapas = Promocion.builder()
                    .denominacion("Sándwich + Papas")
                    .fechaDesde(LocalDate.now())
                    .fechaHasta(LocalDate.now().plusDays(10))
                    .horaDesde(LocalTime.of(19, 0))
                    .horaHasta(LocalTime.of(23, 0))
                    .descripcionDescuento("Pedí un lomito y llevate papas clásicas al 50%.")
                    .precioPromocional(9999.0) // Precio combo sugerido
                    .tipoPromocion(TipoPromocion.HAPPY_HOUR)
                    .imagen(imgPromo4)
                    .articulosManufacturados(List.of(lomitoSimple, papasFritas))
                    .sucursales(List.of(sucursal))
                    .build();

            promocionService.save(sandwichYpapas);

            Promocion megaBurgerLovers = Promocion.builder()
                    .denominacion("Mega Burger Lovers")
                    .fechaDesde(LocalDate.now())
                    .fechaHasta(LocalDate.now().plusDays(7))
                    .horaDesde(LocalTime.of(11, 0))
                    .horaHasta(LocalTime.of(23, 0))
                    .descripcionDescuento("2 hamburguesas clasicas + Papas Cheddar Bacon por solo $18.900.")
                    .precioPromocional(18900.0)
                    .tipoPromocion(TipoPromocion.PROMOCION_GENERAL)
                    .imagen(imgPromo5)
                    .articulosManufacturados(List.of(
                            hamburguesa, hamburguesa, papasCheddarBacon
                    ))
                    .sucursales(List.of(sucursal))
                    .build();

            promocionService.save(megaBurgerLovers);

        } catch (Exception e) {
            System.err.println("Error al cargar datos de ejemplo: " + e.getMessage());
            e.printStackTrace();
        }
    }
}