package ElBuenSabor.ProyectoFinal.Controllers; // Asegúrate que este sea el paquete correcto de tus controladores

import ElBuenSabor.ProyectoFinal.DTO.PedidoCreateDTO; // Importa tu DTO de entrada
import ElBuenSabor.ProyectoFinal.Entities.ArticuloInsumo;
import ElBuenSabor.ProyectoFinal.Entities.ArticuloManufacturado;
import ElBuenSabor.ProyectoFinal.Entities.DetallePedido;
import ElBuenSabor.ProyectoFinal.Entities.Pedido;
import ElBuenSabor.ProyectoFinal.Service.PedidoService; // Importa la interfaz de tu servicio

import com.mercadopago.MercadoPagoConfig;
import com.mercadopago.client.preference.PreferenceClient;
import com.mercadopago.client.preference.PreferenceRequest;
import com.mercadopago.client.preference.PreferenceItemRequest;
import com.mercadopago.client.preference.PreferenceBackUrlsRequest;
import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.exceptions.MPException;
import com.mercadopago.resources.preference.Preference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.annotation.PostConstruct;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/mercadoPago")
@CrossOrigin(origins = "http://localhost:5173") // Asegúrate que este es el puerto de tu frontend React
public class MPController { // Renombrado a MPController si así lo tienes en tu proyecto

    @Value("${mercadopago.access.token}") // Inyecta el token desde application.properties
    private String accessToken;

    @Autowired
    private PedidoService pedidoService; // Inyecta tu servicio de pedido

    @PostConstruct
    public void init() {
        MercadoPagoConfig.setAccessToken(accessToken);
        System.out.println("Mercado Pago SDK inicializado con Access Token.");
    }

    @PostMapping("/crear-preferencia")
    public ResponseEntity<?> crearPreferencia(@RequestBody PedidoCreateDTO pedidoDTO) {
        try {
            // 1. Guardar el pedido en la base de datos usando el servicio
            // *** CORRECCIÓN CLAVE AQUÍ: Llamar a crearPedidoEntidad() ***
            // Este método devuelve la entidad Pedido, no el DTO de respuesta.
            Pedido pedidoPersistido = pedidoService.crearPedidoEntidad(pedidoDTO);

            // Validar que el pedido se haya guardado correctamente y tenga un ID
            if (pedidoPersistido == null || pedidoPersistido.getId() == null || pedidoPersistido.getId() == 0L) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al persistir el pedido en la base de datos, ID no generado.");
            }

            // 2. Configurar el Access Token de Mercado Pago (ya hecho en @PostConstruct)

            // 3. Crear la lista de ítems para la preferencia de Mercado Pago
            List<PreferenceItemRequest> items = new ArrayList<>();
            // Usamos los detalles de la entidad Pedido persistida, que ya tienen los objetos Articulo relacionados
            Set<DetallePedido> detalles = pedidoPersistido.getDetallesPedidos();

            if (detalles == null || detalles.isEmpty()) {
                return ResponseEntity.badRequest().body("El pedido persistido no tiene detalles válidos para Mercado Pago.");
            }

            for (DetallePedido detalle : detalles) {
                String itemId = null;
                String itemTitle = null;
                BigDecimal itemPrice = null;

                // Determinar si el detalle es de un ArticuloManufacturado o un ArticuloInsumo
                // Aquí usamos los objetos ya cargados en la entidad Pedido persistida
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
                    if (ai.getId() != null && ai.getDenominacion() != null && ai.getPrecioCompra() != null && detalle.getCantidad() != null) {
                        itemId = String.valueOf(ai.getId());
                        itemTitle = ai.getDenominacion();
                        itemPrice = BigDecimal.valueOf(ai.getPrecioCompra());
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

            // 4. Configurar las URLs de redirección después del pago
            PreferenceBackUrlsRequest backUrls = PreferenceBackUrlsRequest.builder()
                    .success("http://localhost:5173/pago-exitoso")
                    .pending("http://localhost:5173/pago-pendiente")
                    .failure("http://localhost:5173/pago-fallido")
                    .build();

            // 5. Construir la solicitud de preferencia
            PreferenceRequest preferenceRequest = PreferenceRequest.builder()
                    .items(items)
                    .backUrls(backUrls)
                    .notificationUrl("https://tu-dominio.com/webhook/mercadopago") // ¡REEMPLAZA CON TU WEBHOOK REAL!
                    .externalReference(String.valueOf(pedidoPersistido.getId())) // <--- Usa el ID del pedido PERSISTIDO
                    .build();

            // 6. Crear la preferencia utilizando el cliente de Mercado Pago
            PreferenceClient client = new PreferenceClient();
            Preference preference = client.create(preferenceRequest);

            // 7. Devolver el ID de la preferencia al frontend
            return ResponseEntity.ok(preference.getId());

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