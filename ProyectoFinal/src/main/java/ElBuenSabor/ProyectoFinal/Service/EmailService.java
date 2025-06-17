package ElBuenSabor.ProyectoFinal.Service;

import java.io.ByteArrayOutputStream; // Necesario para adjuntos

public interface EmailService {
    /**
     * Envía un correo electrónico.
     * @param to Dirección de correo del destinatario.
     * @param subject Asunto del correo.
     * @param text Cuerpo del correo (texto plano o HTML simple).
     * @param attachmentBytes Opcional: bytes del archivo adjunto (ej. PDF).
     * @param attachmentFilename Opcional: nombre del archivo adjunto (ej. "factura.pdf").
     * @throws Exception Si ocurre un error al enviar el correo.
     */
    void sendEmail(String to, String subject, String text, ByteArrayOutputStream attachmentBytes, String attachmentFilename) throws Exception;
}
