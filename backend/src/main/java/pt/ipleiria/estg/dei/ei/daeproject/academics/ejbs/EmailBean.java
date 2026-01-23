package pt.ipleiria.estg.dei.ei.daeproject.academics.ejbs;
import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;
import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import pt.ipleiria.estg.dei.ei.daeproject.academics.entities.Publication;
import pt.ipleiria.estg.dei.ei.daeproject.academics.entities.Tag;
import pt.ipleiria.estg.dei.ei.daeproject.academics.entities.User;

import java.util.List;
import java.util.Properties;


@Stateless
public class EmailBean {
    @EJB
    private UserBean userBean;
    @EJB
    private PublicationBean publicationBean;

    public void sendResetEmail(String to, String token) {
        String resetLink = "http://localhost:3000/auth/reset-password?token=" + token;

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "sandbox.smtp.mailtrap.io");
        props.put("mail.smtp.port", "2525");

        Session session = Session.getInstance(props, new jakarta.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {

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

    public void sendNewsEmail(Integer publicationId) {

        Publication publication = publicationBean.find(publicationId);
        if (publication == null || publication.getTags().isEmpty()) return;

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "sandbox.smtp.mailtrap.io");
        props.put("mail.smtp.port", "2525");

        Session session = Session.getInstance(props, new jakarta.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {

                return new PasswordAuthentication("402c1cfc253896", "855c63d2ebe2e5");
            }
        });

        for (User user : userBean.findAll()) {

            // Match subscribed tags
            if (!matchesUserTags(user, publication)) continue;

            try {
                Message message = new MimeMessage(session);
                message.setFrom(new InternetAddress("support@academics.pt"));
                message.setRecipients(
                        Message.RecipientType.TO,
                        InternetAddress.parse(user.getEmail())
                );

                message.setSubject("🆕 New publication in your interests");

                String htmlBody =
                        "<div style='font-family:sans-serif;background:#f4f4f7;padding:40px'>" +
                                "<div style='max-width:500px;margin:auto;background:white;border-radius:8px'>" +

                                "<div style='background:#0f172a;padding:20px;text-align:center'>" +
                                "<h1 style='color:white;margin:0'>Academic Repository</h1>" +
                                "</div>" +

                                "<div style='padding:30px;text-align:center'>" +
                                "<h2>Hello " + user.getName() + " 👋</h2>" +
                                "<p>A new publication matching your interests was published:</p>" +
                                "<h3 style='color:#2563eb'>" + publication.getTitle() + "</h3>" +
                                "<p>" + publication.getDescription() + "</p>" +
                                "</div>" +

                                "<div style='background:#f8fafc;padding:15px;text-align:center;font-size:11px'>" +
                                "&copy; 2026 Academic Repository" +
                                "</div>" +

                                "</div></div>";

                message.setContent(htmlBody, "text/html; charset=UTF-8");

                Transport.send(message);

            } catch (MessagingException e) {
                e.printStackTrace();
            }
        }
    }

    public void sendUpdateEmail(Integer publicationId) {

        Publication publication = publicationBean.find(publicationId);
        if (publication == null || publication.getTags().isEmpty()) return;

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "sandbox.smtp.mailtrap.io");
        props.put("mail.smtp.port", "2525");

        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(
                        "ade762a36ba1ac",
                        "22a2727c2ab14f"
                );
            }
        });

        for (User user : userBean.findAll()) {

            // Skip users whose tags don't match
            if (!matchesUserTags(user, publication)) continue;

            // Optional: skip the author of the publication
            // if (user.equals(publication.getAuthor())) continue;

            try {
                Message message = new MimeMessage(session);
                message.setFrom(new InternetAddress("support@academics.pt"));
                message.setRecipients(
                        Message.RecipientType.TO,
                        InternetAddress.parse(user.getEmail())
                );

                message.setSubject("✏️ Publication updated");

                String htmlBody =
                        "<div style='font-family:sans-serif;background:#f4f4f7;padding:40px'>" +
                                "<div style='max-width:500px;margin:auto;background:white;border-radius:8px'>" +

                                "<div style='background:#0f172a;padding:20px;text-align:center'>" +
                                "<h1 style='color:white;margin:0'>Academic Repository</h1>" +
                                "</div>" +

                                "<div style='padding:30px;text-align:center'>" +
                                "<h2>Hello " + user.getName() + " 👋</h2>" +
                                "<p>A publication matching your interests has been updated:</p>" +
                                "<h3 style='color:#2563eb'>" + publication.getTitle() + "</h3>" +
                                "<p>" + publication.getDescription() + "</p>" +
                                "</div>" +

                                "<div style='background:#f8fafc;padding:15px;text-align:center;font-size:11px'>" +
                                "&copy; 2026 Academic Repository" +
                                "</div>" +

                                "</div></div>";

                message.setContent(htmlBody, "text/html; charset=UTF-8");

                Transport.send(message);

            } catch (MessagingException e) {
                e.printStackTrace();
            }
        }
    }




    private boolean matchesUserTags(User user, Publication publication) {
        return user.getTags().stream()
                .anyMatch(publication.getTags()::contains);
    }

}