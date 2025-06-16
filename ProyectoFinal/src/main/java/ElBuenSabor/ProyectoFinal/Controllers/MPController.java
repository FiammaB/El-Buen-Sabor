package ElBuenSabor.ProyectoFinal.Controllers;



import ElBuenSabor.ProyectoFinal.Entities.ArticuloInsumo; // Importa ArticuloInsumo
import ElBuenSabor.ProyectoFinal.Entities.ArticuloManufacturado; // Importa ArticuloManufacturado
import ElBuenSabor.ProyectoFinal.Entities.DetallePedido; // Importa DetallePedido
import ElBuenSabor.ProyectoFinal.Entities.Pedido; // Importa Pedido
import ElBuenSabor.ProyectoFinal.Entities.Estado; // Importa Estado
import ElBuenSabor.ProyectoFinal.DTO.PedidoCreateDTO; // Importa PedidoCreateDTO
import ElBuenSabor.ProyectoFinal.Service.PedidoService; // Importa PedidoService
import com.mercadopago.MercadoPagoConfig;
import com.mercadopago.client.preference.PreferenceClient;
import com.mercadopago.client.preference.PreferenceItemRequest;
import com.mercadopago.client.preference.PreferenceRequest;
import com.mercadopago.client.preference.PreferenceBackUrlsRequest; // Importa PreferenceBackUrlsRequest
import com.mercadopago.resources.preference.Preference;
import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.exceptions.MPException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.annotation.PostConstruct; // Importa PostConstruct
import java.math.BigDecimal; // Importa BigDecimal
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set; // Importa Set

@RestController
@RequestMapping("/api/mercadoPago") // Endpoint para las operaciones de Mercado Pago
@CrossOrigin(origins = "http://localhost:5173") // Asegúrate que este es el puerto de tu frontend React
public class MPController {

    @Value("${mercadopago.access.token}")
    private String accessToken;

    @Value("${mercadopago.back-urls.success}")
    private String successUrl;

    @Value("${mercadopago.back-urls.pending}")
    private String pendingUrl;

    @Value("${mercadopago.back-urls.failure}")
    private String failureUrl;

    @Value("${mercadopago.notification.url}")
    private String notificationUrl;

    @Autowired
    private PedidoService pedidoService;

    @PostConstruct
    public void init() {
        MercadoPagoConfig.setAccessToken(accessToken);
        System.out.println("Mercado Pago SDK inicializado con Access Token.");
    }

    @PostMapping("/crear-preferencia")
    public ResponseEntity<?> crearPreferencia(@RequestBody PedidoCreateDTO pedidoDTO) {
        try {
            // 1. Guardar el pedido en la base de datos con un estado inicial (ej. A_CONFIRMAR)
            // La lógica para mapear DTO a entidad y resolver relaciones está en el servicio.
            // Creamos un nuevo método en PedidoService para esto, ya que este pedido es
            // "pre-pago" y puede tener un estado diferente al de un pedido normal guardado por el controller.
            Pedido pedidoPersistido = pedidoService.crearPedidoPreferenciaMP(pedidoDTO);

            if (pedidoPersistido == null || pedidoPersistido.getId() == null || pedidoPersistido.getId() == 0L) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al persistir el pedido en la base de datos, ID no generado.");
            }

            // 2. Crear la lista de ítems para la preferencia de Mercado Pago
            List<PreferenceItemRequest> items = new ArrayList<>();
            Set<DetallePedido> detalles = pedidoPersistido.getDetallesPedidos();

            if (detalles == null || detalles.isEmpty()) {
                return ResponseEntity.badRequest().body("El pedido persistido no tiene detalles válidos para Mercado Pago.");
            }

            for (DetallePedido detalle : detalles) {
                String itemId = null;
                String itemTitle = null;
                BigDecimal itemPrice = null;

                if (detalle.getArticuloManufacturado() != null) {
                    ArticuloManufacturado am = detalle.getArticuloManufacturado();
                    if (am.getId() != null && am.getDenominacion() != null && am.getPrecioVenta() != null && detalle.getCantidad() != null) {
                        itemId = String.valueOf(am.getId());
                        itemTitle = am.getDenominacion();
                        itemPrice = BigDecimal.valueOf(am.getPrecioVenta());
                    } else {
                        System.err.println("Advertencia: Datos incompletos para ArticuloManufacturado en detalle de pedido ID: " + detalle.getId());
                        continue;
                    }
                } else if (detalle.getArticuloInsumo() != null) {
                    ArticuloInsumo ai = detalle.getArticuloInsumo();
                    // *** CORRECCIÓN: Usar getPrecioVenta() para ArticuloInsumo si se vende ***
                    if (ai.getId() != null && ai.getDenominacion() != null && ai.getPrecioVenta() != null && detalle.getCantidad() != null) {
                        itemId = String.valueOf(ai.getId());
                        itemTitle = ai.getDenominacion();
                        itemPrice = BigDecimal.valueOf(ai.getPrecioVenta()); // Usar precioVenta del insumo
                    } else {
                        System.err.println("Advertencia: Datos incompletos para ArticuloInsumo en detalle de pedido ID: " + detalle.getId());
                        continue;
                    }
                } else {
                    System.err.println("Advertencia: Detalle de pedido sin ArticuloManufacturado ni ArticuloInsumo asociado. Se omite. Detalle ID: " + detalle.getId());
                    continue;
                }

                if (detalle.getCantidad() <= 0) {
                    System.err.println("Advertencia: Cantidad inválida (" + detalle.getCantidad() + ") para el item " + itemTitle + ". Se omite.");
                    continue;
                }

                PreferenceItemRequest item = PreferenceItemRequest.builder()
                        .id(itemId)
                        .title(itemTitle)
                        .quantity(detalle.getCantidad())
                        .unitPrice(itemPrice)
                        .currencyId("ARS")
                        .build();
                items.add(item);
            }

            if (items.isEmpty()) {
                return ResponseEntity.badRequest().body("Ningún item válido encontrado en los detalles del pedido para crear la preferencia. Verifica los datos de los artículos en el pedido.");
            }

            // 3. Configurar las URLs de redirección y el webhook
            PreferenceBackUrlsRequest backUrls = PreferenceBackUrlsRequest.builder()
                    .success(successUrl)
                    .pending(pendingUrl)
                    .failure(failureUrl)
                    .build();

            // 4. Construir la solicitud de preferencia
            PreferenceRequest preferenceRequest = PreferenceRequest.builder()
                    .items(items)
                    .backUrls(backUrls)
                    .notificationUrl(notificationUrl) // Usamos la URL del webhook de properties
                    .externalReference(String.valueOf(pedidoPersistido.getId())) // Usa el ID del pedido PERSISTIDO
                    .build();

            // 5. Crear la preferencia utilizando el cliente de Mercado Pago
            PreferenceClient client = new PreferenceClient();
            Preference preference = client.create(preferenceRequest);

            // 6. Devolver el ID de la preferencia y el init_point al frontend
            // El frontend usará el init_point para redirigir al usuario
            return ResponseEntity.ok(Map.of("preferenceId", preference.getId(), "initPoint", preference.getInitPoint()));

        } catch (MPException | MPApiException e) {
            System.err.println("Error de Mercado Pago al generar preferencia: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al generar preferencia de Mercado Pago: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Error inesperado en el servidor al procesar el pedido: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error inesperado en el servidor: " + e.getMessage());
        }
    }
}
