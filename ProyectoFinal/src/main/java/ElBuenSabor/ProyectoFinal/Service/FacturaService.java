package ElBuenSabor.ProyectoFinal.Service;

import ElBuenSabor.ProyectoFinal.Entities.Factura;
import ElBuenSabor.ProyectoFinal.Entities.Pedido;

import java.io.ByteArrayOutputStream;
import java.util.List; // Asegúrate de tener esta importación

public interface FacturaService extends BaseService<Factura, Long>{

    ByteArrayOutputStream generarFacturaPdf(Pedido pedido) throws Exception;
}
