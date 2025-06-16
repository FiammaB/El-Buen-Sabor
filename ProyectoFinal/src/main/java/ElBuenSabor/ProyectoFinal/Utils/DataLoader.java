// ProyectoFinal/src/main/java/ElBuenSabor/ProyectoFinal/Util/DataLoader.java
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

    // ... (servicios inyectados) ...
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
            // 1. Crear País, Provincia, Localidad
            Pais pais = Pais.builder().nombre("Argentina").build();
            pais = paisService.save(pais);

            Provincia provincia = Provincia.builder().nombre("Mendoza").pais(pais).build();
            provincia = provinciaService.save(provincia);

            Localidad localidad = Localidad.builder().nombre("Maipú").provincia(provincia).build();
            localidad = localidadService.save(localidad);

            // 2. Crear IMÁGENES ÚNICAS PARA CADA ENTIDAD (O AL MENOS PARA CADA ARTÍCULO)
            Imagen imgCliente = Imagen.builder().denominacion("https://example.com/cliente.jpg").build();
            imgCliente = imagenService.save(imgCliente);

            Imagen imgHarina = Imagen.builder().denominacion("https://example.com/harina.jpg").build(); // Nueva imagen para Harina
            imgHarina = imagenService.save(imgHarina);

            Imagen imgTomate = Imagen.builder().denominacion("https://example.com/tomate.jpg").build(); // Nueva imagen para Tomate
            imgTomate = imagenService.save(imgTomate);

            Imagen imgHamburguesa = Imagen.builder().denominacion("https://example.com/hamburguesa.jpg").build(); // Nueva imagen para Hamburguesa
            imgHamburguesa = imagenService.save(imgHamburguesa);


            // 3. Crear Domicilio
            Domicilio domicilioCliente = Domicilio.builder()
                    .calle("Calle Falsa")
                    .numero(123)
                    .cp(5515)
                    .localidad(localidad)
                    .build();
            domicilioCliente = domicilioService.save(domicilioCliente);

            // 4. Crear Usuario
            Usuario usuarioCliente = Usuario.builder()
                    .auth0Id("auth0|123456789")
                    .username("cliente_test")
                    .build();
            usuarioCliente = usuarioService.save(usuarioCliente);

            // 5. Crear Cliente
            Cliente cliente = Cliente.builder()
                    .nombre("Juan")
                    .apellido("Perez")
                    .telefono("2615551234")
                    .email("juan.perez@example.com")
                    .password("hashed_password")
                    .fechaNacimiento(LocalDate.of(1990, 5, 15))
                    .imagen(imgCliente)
                    .usuario(usuarioCliente)
                    .build();

            Set<Domicilio> domiciliosCliente = new HashSet<>();
            domiciliosCliente.add(domicilioCliente);
            cliente.setDomicilios(domiciliosCliente);

            cliente = clienteService.save(cliente);

            // La línea siguiente no es necesaria ya que el Cliente es el lado dueño con CascadeType.MERGE/PERSIST.
            // Si Domicilio.clientes se mantiene en memoria para la sesión, es por la relación bidireccional,
            // pero JPA debería encargarse de la tabla de unión.
            /*
            Set<Cliente> clientesDomicilio = new HashSet<>();
            clientesDomicilio.add(cliente);
            domicilioCliente.setClientes(clientesDomicilio);
            // domicilioService.update(domicilioCliente.getId(), domicilioCliente); // Evitar un save/update separado aquí si no es estrictamente necesario
            */


            // 6. Crear Categoría y Unidad de Medida
            Categoria categoriaComida = Categoria.builder().denominacion("Comida").build();
            categoriaComida = categoriaService.save(categoriaComida);

            UnidadMedida unidadGramos = UnidadMedida.builder().denominacion("gramos").build();
            unidadGramos = unidadMedidaService.save(unidadGramos);

            UnidadMedida unidadPorcion = UnidadMedida.builder().denominacion("unidad").build();
            unidadPorcion = unidadMedidaService.save(unidadPorcion);


            // 7. Crear ArticuloInsumo
            ArticuloInsumo insumoHarina = ArticuloInsumo.builder()
                    .denominacion("Harina")
                    .precioVenta(500.0)
                    .precioCompra(300.0)
                    .stockActual(1000.0)
                    .stockMinimo(200.0)
                    .esParaElaborar(true)
                    .categoria(categoriaComida)
                    .unidadMedida(unidadGramos)
                    .imagen(imgHarina) // <<-- Asigna la nueva imagen imgHarina
                    .build();
            insumoHarina = articuloInsumoService.save(insumoHarina);

            ArticuloInsumo insumoTomate = ArticuloInsumo.builder()
                    .denominacion("Tomate")
                    .precioVenta(100.0)
                    .precioCompra(50.0)
                    .stockActual(500.0)
                    .stockMinimo(100.0)
                    .esParaElaborar(true)
                    .categoria(categoriaComida)
                    .unidadMedida(unidadGramos)
                    .imagen(imgTomate) // <<-- Asigna la nueva imagen imgTomate
                    .build();
            insumoTomate = articuloInsumoService.save(insumoTomate);

            // 8. Crear ArticuloManufacturado
            ArticuloManufacturado hamburguesa = ArticuloManufacturado.builder()
                    .denominacion("Hamburguesa Clásica")
                    .precioVenta(1250.0)
                    .descripcion("Deliciosa hamburguesa con queso y lechuga")
                    .tiempoEstimadoMinutos(20)
                    .preparacion("Preparar la carne, cocinar, armar.")
                    .categoria(categoriaComida)
                    .unidadMedida(unidadPorcion)
                    .imagen(imgHamburguesa) // <<-- Asigna la nueva imagen imgHamburguesa
                    .build();

            // Añadir detalles (ingredientes) y establecer la relación inversa
            Set<ArticuloManufacturadoDetalle> detallesHamburguesa = new HashSet<>();
            ArticuloManufacturadoDetalle detalleHarina = ArticuloManufacturadoDetalle.builder()
                    .cantidad(200.0)
                    .articuloInsumo(insumoHarina)
                    .build();
            detalleHarina.setArticuloManufacturado(hamburguesa);
            detallesHamburguesa.add(detalleHarina);

            ArticuloManufacturadoDetalle detalleTomate = ArticuloManufacturadoDetalle.builder()
                    .cantidad(50.0)
                    .articuloInsumo(insumoTomate)
                    .build();
            detalleTomate.setArticuloManufacturado(hamburguesa);
            detallesHamburguesa.add(detalleTomate);

            hamburguesa.setDetalles(detallesHamburguesa);

            hamburguesa = articuloManufacturadoService.save(hamburguesa);


            System.out.println("Datos de ejemplo cargados exitosamente.");

        } catch (Exception e) {
            System.err.println("Error al cargar datos de ejemplo: " + e.getMessage());
            e.printStackTrace();
        }
    }
}