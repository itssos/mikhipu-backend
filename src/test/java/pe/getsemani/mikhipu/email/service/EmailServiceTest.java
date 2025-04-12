package pe.getsemani.mikhipu.email.service;

import jakarta.mail.BodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Pruebas de EmailService")
class EmailServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private EmailService service;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(service, "fromAddress", "no-reply@test.com");
    }

    @Test
    @DisplayName("sendHtmlEmail envía un correo HTML correctamente")
    void sendHtmlEmail_sendsHtmlMessage() throws Exception {
        // Arrange
        MimeMessage message = new MimeMessage((jakarta.mail.Session) null);
        when(mailSender.createMimeMessage()).thenReturn(message);

        String to = "dest@example.com";
        String subject = "Asunto de prueba";
        String htmlBody = "<p>Hola mundo</p>";

        // Act
        service.sendHtmlEmail(to, subject, htmlBody);

        // Assert
        ArgumentCaptor<MimeMessage> captor = ArgumentCaptor.forClass(MimeMessage.class);
        verify(mailSender).send(captor.capture());
        MimeMessage sent = captor.getValue();

        // Verificar cabeceras
        assertThat(sent.getFrom()[0].toString()).isEqualTo("no-reply@test.com");
        assertThat(sent.getAllRecipients()[0].toString()).isEqualTo(to);
        assertThat(sent.getSubject()).isEqualTo(subject);

        // Extraer contenido multipart y buscar cualquier String que contenga el HTML
        Object content = sent.getContent();
        assertThat(content).isInstanceOf(MimeMultipart.class);
        MimeMultipart multipart = (MimeMultipart) content;

        String found = findStringPart(multipart, htmlBody);
        assertThat(found)
                .as("Debe encontrar el HTML en alguna parte del multipart")
                .isNotNull()
                .contains(htmlBody);
    }

    private String findStringPart(MimeMultipart multipart, String snippet) throws Exception {
        for (int i = 0; i < multipart.getCount(); i++) {
            BodyPart part = multipart.getBodyPart(i);
            Object partContent = part.getContent();
            if (partContent instanceof String text) {
                if (text.contains(snippet)) {
                    return text;
                }
            } else if (partContent instanceof MimeMultipart) {
                String inner = findStringPart((MimeMultipart) partContent, snippet);
                if (inner != null) {
                    return inner;
                }
            }
        }
        return null;
    }
}
