package ElBuenSabor.ProyectoFinal.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.MessagingException;
import jakarta.activation.DataSource;
import jakarta.mail.util.ByteArrayDataSource;

import java.io.ByteArrayOutputStream;

@Service
public class EmailServiceImpl implements EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${mail.from.address}")
    private String fromAddress;

    @Override
    public void sendEmail(String to, String subject, String text, ByteArrayOutputStream attachmentBytes, String attachmentFilename) throws Exception {
        MimeMessage message = mailSender.createMimeMessage();
        try {
            boolean tieneAdjunto = attachmentBytes != null && attachmentFilename != null;

            MimeMessageHelper helper = new MimeMessageHelper(message, tieneAdjunto);
            helper.setFrom(fromAddress);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(text, true); // true habilita HTML

            if (tieneAdjunto) {
                DataSource dataSource = new ByteArrayDataSource(attachmentBytes.toByteArray(), "application/pdf");
                helper.addAttachment(attachmentFilename, dataSource);
            }

            mailSender.send(message);
            System.out.println("Correo enviado exitosamente a " + to);
        } catch (MessagingException e) {
            System.err.println("Error al enviar el correo a " + to + ": " + e.getMessage());
            e.printStackTrace();
            throw new Exception("Error al enviar correo", e);
        } catch (Exception e) {
            System.err.println("Error inesperado al preparar o enviar el correo a " + to + ": " + e.getMessage());
            e.printStackTrace();
            throw new Exception("Error inesperado en servicio de correo", e);
        }
    }
}
