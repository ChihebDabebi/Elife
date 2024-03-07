package services;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

public class ServiceEmailUtil {
    public static void sendEmail(String userEmail,String header, String bodyP) {
        // Sender's email details
        String senderEmail = "oumaimaamdouni69@gmail.com";
        String senderPassword = "nxsa gtgl noya nwiw\n";

        // SMTP server details
        String host = "smtp.gmail.com";
        int port = 587;

        // Recipient's email details
        String recipientEmail = userEmail;

        // Email content
        String subject = header;
        String body = bodyP;

        // Set properties for Gmail SMTP server
        Properties properties = new Properties();
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.host", host);
        properties.put("mail.smtp.port", port);

        // Create session with authentication
        Session session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(senderEmail, senderPassword);
            }
        });

        try {
            // Create MimeMessage object
            MimeMessage message = new MimeMessage(session);

            // Set sender, recipient, subject, and body
            message.setFrom(new InternetAddress(senderEmail));
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(recipientEmail));
            message.setSubject(subject);
            message.setText(body);

            // Send the message
            Transport.send(message);

            System.out.println("Email sent successfully to: " + recipientEmail);
        } catch (MessagingException e) {
            System.out.println("Error sending email: " + e.getMessage());
        }
    }
}