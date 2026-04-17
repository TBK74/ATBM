package vn.edu.hcmuaf.fit.util;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

public class EmailUtils {

    private static final String HOST_NAME = "smtp.gmail.com";
    private static final int TSL_PORT = 587;
    private static final String APP_EMAIL = "23130130@st.hcmuaf.edu.vn";
    private static final String APP_PASSWORD = "xgqd uztg bbrb wtdf";

    public static void sendEmail(String toEmail, String subject, String body) {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", HOST_NAME);
        props.put("mail.smtp.port", TSL_PORT);
        props.put("mail.smtp.ssl.trust", HOST_NAME);

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(APP_EMAIL, APP_PASSWORD);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(APP_EMAIL));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            message.setSubject(subject);
            message.setContent(body, "text/html; charset=UTF-8");

            Transport.send(message);
            System.out.println("Gửi email thành công đến: " + toEmail);

        } catch (MessagingException e) {
            e.printStackTrace();
            System.out.println("Gửi email thất bại!");
        }
    }

    public static void main(String[] args) {
        sendEmail("23130130@st.hcmuaf.edu.vn", "Test OTP", "Mã test là: 123456");
    }
}