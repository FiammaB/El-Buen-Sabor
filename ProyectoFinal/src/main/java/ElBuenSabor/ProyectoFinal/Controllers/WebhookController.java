// ProyectoFinal/src/main/java/ElBuenSabor/ProyectoFinal/Controllers/WebhookController.java
package ElBuenSabor.ProyectoFinal.Controllers;

import ElBuenSabor.ProyectoFinal.Entities.Factura; // Importa Factura
import ElBuenSabor.ProyectoFinal.Entities.Estado; // Importa Estado
import ElBuenSabor.ProyectoFinal.Entities.Pedido;   // Importa Pedido
import ElBuenSabor.ProyectoFinal.Service.PedidoService; // Importa PedidoService
import com.mercadopago.client.payment.PaymentClient;
import com.mercadopago.resources.payment.Payment;
import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.exceptions.MPException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Value; // Para inyectar el notificationUrl

import java.time.LocalDate; // Para Factura.fechaFacturacion
import java.time.format.DateTimeFormatter; // Para parsear fechas de MP

import java.util.Map; // Para el cuerpo de la notificación de MP (Map<String, String>)

@RestController
@RequestMapping("/api/webhooks") // URL base para todos los webhooks
public class WebhookController {

    @Autowired
    private PedidoService pedidoService;

    @PostMapping("/mercadopago")
    public ResponseEntity<String> handleMercadoPagoWebhook(@RequestParam Map<String, String> params) {
        try {
            String topic = params.get("topic"); // Por ejemplo, 'payment' o 'merchant_order'
            String resourceId = params.get("id"); // El ID del recurso (ej. ID del pago)

            System.out.println("Webhook de Mercado Pago recibido:");
            System.out.println("Topic: " + topic + ", Resource ID: " + resourceId);

            if (topic == null || resourceId == null) {
                return ResponseEntity.badRequest().body("Parámetros de notificación incompletos (topic o id)");
            }

            // Dependiendo del 'topic', podríamos procesar diferentes tipos de notificaciones.
            // Para Checkout Pro, 'payment' es el más común y directo.
            if ("payment".equals(topic)) {
                // Llama a un método en el servicio de Pedido para procesar el pago.
                // El servicio se encargará de buscar el pago en MP y actualizar el pedido/factura.
                pedidoService.procesarNotificacionPagoMercadoPago(resourceId);
            } else if ("merchant_order".equals(topic)) {
                // Si también te interesan las órdenes de comerciante, aquí iría la lógica.
                // Por ahora, nos centraremos en el topic 'payment'.
                System.out.println("Notificación de 'merchant_order' recibida. Ignorando por ahora.");
            } else {
                System.out.println("Topic de notificación desconocido: " + topic);
            }

            // Mercado Pago espera un 200 OK para confirmar que recibimos la notificación.
            return ResponseEntity.ok("Notificación recibida y procesada");

        } catch (MPException | MPApiException e) {
            System.err.println("Error de Mercado Pago al procesar webhook: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al procesar notificación de Mercado Pago");
        } catch (Exception e) {
            System.err.println("Error interno del servidor al procesar webhook: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error interno del servidor");
        }
    }
}