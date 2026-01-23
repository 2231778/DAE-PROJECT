package pt.ipleiria.estg.dei.ei.daeproject.academics.ejbs;
import jakarta.ejb.Stateless;
import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import java.util.Properties;


@Stateless
public class EmailBean {
    public void sendResetEmail(String to, String token) {
        String resetLink = "http://localhost:3000/auth/reset-password?token=" + token;

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "sandbox.smtp.mailtrap.io");
        props.put("mail.smtp.port", "2525");

        Session session = Session.getInstance(props, new jakarta.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                // Substitui pelos teus dados do Mailtrap
                return new PasswordAuthentication("ade762a36ba1ac", "22a2727c2ab14f");
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress("support@academics.pt"));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
            message.setSubject("🔒 Reset your Academic Repository password");

            String htmlBody =
                    "<div style='font-family: sans-serif; background-color: #f4f4f7; padding: 40px; color: #333;'>" +
                            "<div style='max-width: 500px; margin: 0 auto; background: white; border-radius: 8px; overflow: hidden; box-shadow: 0 4px 10px rgba(0,0,0,0.05);'>" +
                            "<div style='background-color: #0f172a; padding: 20px; text-align: center;'>" +
                            "<h1 style='color: white; margin: 0; font-size: 20px;'>Academic Repository</h1>" +
                            "</div>" +
                            "<div style='padding: 30px; text-align: center;'>" +
                            "<h2 style='color: #1e293b; margin-top: 0;'>Password Reset</h2>" +
                            "<p style='color: #64748b; line-height: 1.6;'>You requested to reset your password. Click the button below to choose a new one. This link is valid for <strong>30 minutes</strong>.</p>" +
                            "<a href='" + resetLink + "' style='display: inline-block; background-color: #2563eb; color: white; padding: 12px 25px; margin: 20px 0; border-radius: 6px; text-decoration: none; font-weight: bold;'>Reset Password</a>" +
                            "<p style='font-size: 12px; color: #94a3b8; margin-top: 20px;'>If you didn't request this, you can safely ignore this email.</p>" +
                            "</div>" +
                            "<div style='background-color: #f8fafc; padding: 15px; text-align: center; border-top: 1px solid #e2e8f0; font-size: 11px; color: #94a3b8;'>" +
                            "&copy; 2026 Academic Repository. All rights reserved." +
                            "</div>" +
                            "</div>" +
                            "</div>";

            message.setContent(htmlBody, "text/html; charset=UTF-8");

            Transport.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}