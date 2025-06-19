package ElBuenSabor.ProyectoFinal.Utils;

import ElBuenSabor.ProyectoFinal.Entities.*;
import ElBuenSabor.ProyectoFinal.Service.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

@Component
public class DataLoader implements CommandLineRunner {

    @Value("${app.data.loader.enabled:false}")
    private boolean dataLoaderEnabled;

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
    private final PromocionService promocionService;
    private final SucursalService sucursalService;
    private final DetallePedidoService detallePedidoService;
    private final PedidoService pedidoService;

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
                      DomicilioService domicilioService,
                      PromocionService promocionService,
                      SucursalService sucursalService,
                      DetallePedidoService detallePedidoService,
                      PedidoService pedidoService) {
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
        this.promocionService = promocionService;
        this.sucursalService = sucursalService;
        this.detallePedidoService = detallePedidoService;
        this.pedidoService = pedidoService;
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        if (!dataLoaderEnabled) {
            System.out.println("DataLoader está deshabilitado. Saltando la carga de datos.");
            return;
        }

        System.out.println("Cargando datos de ejemplo...");

        try {
            Pais pais = paisService.findAll().stream()
                    .filter(p -> p.getNombre().equalsIgnoreCase("Argentina"))
                    .findFirst()
                    .orElse(null);
            if (pais == null) {
                pais = paisService.save(Pais.builder().nombre("Argentina").build());
            }

            Provincia provincia = provinciaService.findAll().stream()
                    .filter(p -> p.getNombre().equalsIgnoreCase("Mendoza"))
                    .findFirst()
                    .orElse(null);
            if (provincia == null) {
                provincia = provinciaService.save(Provincia.builder().nombre("Mendoza").pais(pais).build());
            }

            Localidad localidad = localidadService.findAll().stream()
                    .filter(l -> l.getNombre().equalsIgnoreCase("Maipú"))
                    .findFirst()
                    .orElse(null);
            if (localidad == null) {
                localidad = localidadService.save(Localidad.builder().nombre("Maipú").provincia(provincia).build());
            }

            Categoria categoria = categoriaService.findAll().stream()
                    .filter(c -> c.getDenominacion().equalsIgnoreCase("Comida"))
                    .findFirst()
                    .orElse(null);
            if (categoria == null) {
                categoria = categoriaService.save(Categoria.builder().denominacion("Comida").build());
            }

            UnidadMedida unidad = unidadMedidaService.findAll().stream()
                    .filter(u -> u.getDenominacion().equalsIgnoreCase("unidad"))
                    .findFirst()
                    .orElse(null);
            if (unidad == null) {
                unidad = unidadMedidaService.save(UnidadMedida.builder().denominacion("unidad").build());
            }

            Imagen img1 = imagenService.save(Imagen.builder().denominacion("https://example.com/img1.jpg").build());
            Imagen img2 = imagenService.save(Imagen.builder().denominacion("https://example.com/img2.jpg").build());
            Imagen img3 = imagenService.save(Imagen.builder().denominacion("https://example.com/img3.jpg").build());

            ArticuloManufacturado a1 = articuloManufacturadoService.save(ArticuloManufacturado.builder()
                    .denominacion("Hamburguesa Clásica")
                    .precioVenta(1500.0)
                    .descripcion("Hamburguesa con lechuga y tomate")
                    .tiempoEstimadoMinutos(20)
                    .preparacion("Cocinar y armar")
                    .categoria(categoria)
                    .unidadMedida(unidad)
                    .imagen(img1)
                    .build());

            ArticuloManufacturado a2 = articuloManufacturadoService.save(ArticuloManufacturado.builder()
                    .denominacion("Pizza Margarita")
                    .precioVenta(2000.0)
                    .descripcion("Pizza con queso y albahaca")
                    .tiempoEstimadoMinutos(30)
                    .preparacion("Hornear")
                    .categoria(categoria)
                    .unidadMedida(unidad)
                    .imagen(img2)
                    .build());

            ArticuloManufacturado a3 = articuloManufacturadoService.save(ArticuloManufacturado.builder()
                    .denominacion("Ensalada César")
                    .precioVenta(1200.0)
                    .descripcion("Ensalada con pollo y crutones")
                    .tiempoEstimadoMinutos(15)
                    .preparacion("Mezclar ingredientes")
                    .categoria(categoria)
                    .unidadMedida(unidad)
                    .imagen(img3)
                    .build());

            Promocion promo = Promocion.builder()
                    .denominacion("Happy Hour Promocional")
                    .fechaDesde(LocalDate.now())
                    .fechaHasta(LocalDate.now().plusWeeks(2))
                    .horaDesde(LocalTime.of(18, 0))
                    .horaHasta(LocalTime.of(21, 0))
                    .precioPromocional(1000.0)
                    .tipoPromocion(TipoPromocion.HAPPY_HOUR)
                    .articulosManufacturados(List.of(a1, a2))
                    .build();

            promocionService.save(promo);

            Cliente cliente;
            List<Cliente> clientes = clienteService.findAll();
            if (clientes.isEmpty()) {
                cliente = clienteService.save(Cliente.builder()
                        .nombre("Laura")
                        .apellido("Pelayes")
                        .email("laura@example.com")
                        .telefono("2612345678")
                        .fechaNacimiento(LocalDate.of(1990, 1, 1))
                        .build());
            } else {
                cliente = clientes.get(0);
            }

            Sucursal sucursal;
            List<Sucursal> sucursales = sucursalService.findAll();
            if (sucursales.isEmpty()) {
                sucursal = sucursalService.save(Sucursal.builder()
                        .nombre("Sucursal Central")
                        .build());
            } else {
                sucursal = sucursales.get(0);
            }

            Pedido pedido = pedidoService.save(Pedido.builder()
                    .horaEstimadaFinalizacion(LocalTime.of(20, 0))
                    .total(0.0)
                    .totalCosto(3000.0)
                    .estado(Estado.ENTREGADO)
                    .tipoEnvio(TipoEnvio.DELIVERY)
                    .formaPago(FormaPago.EFECTIVO)
                    .fechaPedido(LocalDate.now())
                    .cliente(cliente)
                    .sucursal(sucursal)
                    .build());

            DetallePedido dp1 = new DetallePedido();
            dp1.setCantidad(3);
            dp1.setArticuloManufacturado(a1);
            dp1.setPedido(pedido);
            detallePedidoService.save(dp1);

            DetallePedido dp2 = new DetallePedido();
            dp2.setCantidad(5);
            dp2.setArticuloManufacturado(a2);
            dp2.setPedido(pedido);
            detallePedidoService.save(dp2);

            System.out.println("Datos cargados correctamente.");
        } catch (Exception e) {
            System.err.println("Error al cargar datos de ejemplo: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
