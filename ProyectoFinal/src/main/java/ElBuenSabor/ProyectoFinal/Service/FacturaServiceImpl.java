package ElBuenSabor.ProyectoFinal.Service;

import ElBuenSabor.ProyectoFinal.Entities.*;
import ElBuenSabor.ProyectoFinal.Repositories.FacturaRepository;

import java.io.ByteArrayOutputStream;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // Importar Transactional

import com.itextpdf.layout.Document;

import java.time.format.DateTimeFormatter;


@Service

public class FacturaServiceImpl extends BaseServiceImpl<Factura, Long> implements FacturaService {
    public FacturaServiceImpl(FacturaRepository facturaRepository) {
        super(facturaRepository);
    }


    @Override
    @Transactional
    public Factura update(Long id, Factura updatedFactura) throws Exception {
        try {
            Factura actual = findById(id);

            actual.setFechaFacturacion(updatedFactura.getFechaFacturacion());
            actual.setMpPaymentId(updatedFactura.getMpPaymentId());
            actual.setMpMerchantOrderId(updatedFactura.getMpMerchantOrderId());
            actual.setMpPreferenceId(updatedFactura.getMpPreferenceId());
            actual.setMpPaymentType(updatedFactura.getMpPaymentType());
            actual.setFormaPago(updatedFactura.getFormaPago());
            actual.setTotalVenta(updatedFactura.getTotalVenta());

            return baseRepository.save(actual);
        } catch (Exception e) {

            throw new Exception("Error al actualizar la factura: " + e.getMessage());
        }
    }
    @Override
    public ByteArrayOutputStream generarFacturaPdf(Pedido pedido) throws Exception {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(byteArrayOutputStream);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);

        try {
            // Título de la factura
            document.add(new Paragraph("FACTURA DE VENTA EL BUEN SABOR")
                    .setTextAlignment(TextAlignment.CENTER)
                    .setFontSize(20));
            document.add(new Paragraph("\n")); // Salto de línea

            // Información del Pedido y Persona
            document.add(new Paragraph("Pedido N°: " + pedido.getId()));
            document.add(new Paragraph("Fecha: " + pedido.getFechaPedido().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))));

            Persona persona = pedido.getPersona();
            if (persona != null && persona.getUsuario() != null) {
                document.add(new Paragraph("Persona: "
                        + persona.getUsuario().getUsername() + " "
                        + persona.getApellido()));
                document.add(new Paragraph("Email: " + persona.getUsuario().getEmail()));

                if (pedido.getDomicilioEntrega() != null) {
                    document.add(new Paragraph("Dirección: "
                            + pedido.getDomicilioEntrega().getCalle() + " "
                            + pedido.getDomicilioEntrega().getNumero() + ", "
                            + pedido.getDomicilioEntrega().getLocalidad().getNombre()));
                }
            }

            document.add(new Paragraph("\n"));

            // Tabla de detalles del pedido
            // NOTA: Asegúrate que el array de floats coincida con el número de columnas (5 en este caso)
            Table table = new Table(UnitValue.createPercentArray(new float[]{4, 1, 1, 1, 1})); // Ajuste de anchos para Descripción y Cantidad
            table.setWidth(UnitValue.createPercentValue(100)); // Ancho de la tabla al 100%

            // Encabezados de la tabla - ORDEN CORREGIDO
            table.addHeaderCell(new Paragraph("Descripción").setTextAlignment(TextAlignment.CENTER)); //
            table.addHeaderCell(new Paragraph("Cant.").setTextAlignment(TextAlignment.CENTER)); //
            table.addHeaderCell(new Paragraph("P. Unit.").setTextAlignment(TextAlignment.CENTER)); //
            table.addHeaderCell(new Paragraph("Subtotal").setTextAlignment(TextAlignment.CENTER)); //



            // Filas de detalles
            double totalArticulos = 0.0;
            if (pedido.getDetallesPedidos() != null) {
                System.out.println("Detalles del pedido: " + pedido.getDetallesPedidos().size());

                for (DetallePedido detalle : pedido.getDetallesPedidos()) {
                    String descripcion = "N/A";
                    double precioUnitario = 0.0;
                    int tiempoEstimado = 0;

                    if (detalle.getPromocion() != null) {
                        // ---- CASO PROMOCIÓN ----
                        descripcion = "[PROMO] " + detalle.getPromocion().getDenominacion();
                        precioUnitario = detalle.getPromocion().getPrecioPromocional();
                        tiempoEstimado = 0; // Si tu promoción tiene un tiempo estimado, podés mostrarlo aquí

                    } else if (detalle.getArticuloManufacturado() != null) {
                        descripcion = detalle.getArticuloManufacturado().getDenominacion();
                        precioUnitario = detalle.getArticuloManufacturado().getPrecioVenta();
                        tiempoEstimado = detalle.getArticuloManufacturado().getTiempoEstimadoMinutos();

                    } else if (detalle.getArticuloInsumo() != null) {
                        descripcion = detalle.getArticuloInsumo().getDenominacion();
                        precioUnitario = detalle.getArticuloInsumo().getPrecioVenta();
                        tiempoEstimado = 0; // Insumos no suelen tener tiempo estimado
                    }

                    double subtotalDetalle = detalle.getCantidad() * precioUnitario;
                    totalArticulos += subtotalDetalle;

                    // Celdas de datos - ORDEN CORREGIDO para coincidir con los encabezados
                    table.addCell(new Paragraph(descripcion));
                    table.addCell(new Paragraph(String.valueOf(detalle.getCantidad())).setTextAlignment(TextAlignment.CENTER));
                    table.addCell(new Paragraph(String.format("%.2f", precioUnitario)).setTextAlignment(TextAlignment.RIGHT));
                    table.addCell(new Paragraph(String.format("%.2f", subtotalDetalle)).setTextAlignment(TextAlignment.RIGHT));

                }
            }
            document.add(table);
            document.add(new Paragraph("\n"));

            // Totales de la factura
            document.add(new Paragraph("Total Artículos: $" + String.format("%.2f", totalArticulos))
                    .setTextAlignment(TextAlignment.RIGHT));


            Factura factura = pedido.getFactura();
            if (factura != null) {
                document.add(new Paragraph("Total Venta (MP): $" + String.format("%.2f", factura.getTotalVenta()))
                        .setTextAlignment(TextAlignment.RIGHT));
                document.add(new Paragraph("Forma de Pago: " + factura.getFormaPago().name())
                        .setTextAlignment(TextAlignment.RIGHT));
                if (factura.getMpPaymentId() != null) {
                    document.add(new Paragraph("ID Transacción MP: " + factura.getMpPaymentId())
                            .setTextAlignment(TextAlignment.RIGHT));
                }
                if (factura.getMpPreferenceId() != null) {
                    document.add(new Paragraph("ID Preferencia MP: " + factura.getMpPreferenceId())
                            .setTextAlignment(TextAlignment.RIGHT));
                }
            }

            return byteArrayOutputStream;

        } catch (Exception e) {
            System.err.println("Error al generar el PDF de la factura: " + e.getMessage());
            e.printStackTrace();
            throw new Exception("Error al generar el PDF de la factura", e);
        } finally {
            // Asegurarse de que el documento se cierre siempre
            if (document != null) {
                document.close();
            }
        }
    }


    @Override
    public ByteArrayOutputStream generarNotaCreditoPdf(NotaCredito notaCredito) throws Exception { // <-- Asegúrate de que esta línea tenga 'throws Exception'
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        PdfWriter writer = new PdfWriter(byteArrayOutputStream);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);

        try { // <-- Este 'try' envuelve TODA la lógica de generación del PDF de la Nota de Crédito
            // Título de la Nota de Crédito
            document.add(new Paragraph("NOTA DE CRÉDITO")
                    .setTextAlignment(TextAlignment.CENTER)
                    .setFontSize(20));
            document.add(new Paragraph("\n"));

            // Información de la Nota de Crédito, Persona y Factura Original
            document.add(new Paragraph("Nota de Crédito N°: " + notaCredito.getId()));
            document.add(new Paragraph("Fecha de Emisión: " + notaCredito.getFechaEmision().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))));
            document.add(new Paragraph("Motivo: " + notaCredito.getMotivo()));

            Persona persona = notaCredito.getPersona();
            if (persona != null && persona.getUsuario() != null) {
                document.add(new Paragraph("Persona: "
                        + persona.getUsuario().getUsername() + " "
                        + persona.getApellido()));
                document.add(new Paragraph("Email: " + persona.getUsuario().getEmail()));
            }


            Factura facturaOriginal = notaCredito.getFacturaAnulada();
            if (facturaOriginal != null) {
                document.add(new Paragraph("Anula Factura N°: " + facturaOriginal.getId() + " del " + facturaOriginal.getFechaFacturacion().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))));
            }
            document.add(new Paragraph("\n"));

            // Tabla de detalles (ítems de la Nota de Crédito)
            Table table = new Table(UnitValue.createPercentArray(new float[]{1, 4, 1, 1}));
            table.setWidth(UnitValue.createPercentValue(100));

            // Encabezados de la tabla
            table.addHeaderCell(new Paragraph("Cant.").setTextAlignment(TextAlignment.CENTER));
            table.addHeaderCell(new Paragraph("Descripción").setTextAlignment(TextAlignment.CENTER));
            table.addHeaderCell(new Paragraph("P. Unit.").setTextAlignment(TextAlignment.RIGHT));
            table.addHeaderCell(new Paragraph("Subtotal").setTextAlignment(TextAlignment.RIGHT));

            double totalNotaCredito = 0.0;
            if (notaCredito.getDetalles() != null) {
                for (DetallePedido detalle : notaCredito.getDetalles()) {
                    String descripcion = "N/A";
                    double precioUnitario = 0.0;

                    if (detalle.getPromocion() != null) {
                        descripcion = "[PROMO] " + detalle.getPromocion().getDenominacion();
                        precioUnitario = detalle.getPromocion().getPrecioPromocional();
                    } else if (detalle.getArticuloManufacturado() != null) {
                        descripcion = detalle.getArticuloManufacturado().getDenominacion();
                        precioUnitario = detalle.getArticuloManufacturado().getPrecioVenta();
                    } else if (detalle.getArticuloInsumo() != null) {
                        descripcion = detalle.getArticuloInsumo().getDenominacion();
                        precioUnitario = detalle.getArticuloInsumo().getPrecioVenta();
                    }
                    double subtotalDetalle = detalle.getCantidad() * precioUnitario;
                    totalNotaCredito += subtotalDetalle;

                    table.addCell(new Paragraph(String.valueOf(detalle.getCantidad())).setTextAlignment(TextAlignment.CENTER));
                    table.addCell(new Paragraph(descripcion));
                    table.addCell(new Paragraph(String.format("%.2f", precioUnitario)).setTextAlignment(TextAlignment.RIGHT));
                    table.addCell(new Paragraph(String.format("%.2f", subtotalDetalle)).setTextAlignment(TextAlignment.RIGHT));
                }
            }
            document.add(table);
            document.add(new Paragraph("\n"));

            // Totales de la Nota de Crédito
            document.add(new Paragraph("TOTAL NOTA DE CRÉDITO: $" + String.format("%.2f", totalNotaCredito))
                    .setTextAlignment(TextAlignment.RIGHT)
                    .setFontSize(14));

        } catch (Exception e) { // <-- El catch va aquí
            System.err.println("Error al generar el PDF de la Nota de Crédito: " + e.getMessage());
            e.printStackTrace();
            throw new Exception("Error al generar el PDF de la Nota de Crédito", e);
        } finally { // <-- Y el finally va aquí
            document.close();
        }
        return byteArrayOutputStream;
    }

}


