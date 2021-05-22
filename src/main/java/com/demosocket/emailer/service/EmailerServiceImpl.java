package com.demosocket.emailer.service;

import com.demosocket.emailer.model.Mail;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.Objects;
import java.util.Properties;

@Service
public class EmailerServiceImpl implements EmailerService {

    @Override
    public void send(Mail mail) {
        String username = mail.getUsername();
        String password = mail.getPassword();
        String to = mail.getRecipient();

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", mail.getHost());
        props.put("mail.smtp.port", mail.getPort());

        // Get the Session object
        Session session = Session.getInstance(props,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(username, password);
                    }
                });

        try {
            // creates message part
            MimeBodyPart messageBodyPart = new MimeBodyPart();
            messageBodyPart.setContent(mail.getContent(), "text/html");
            // creates multi-part
            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(messageBodyPart);
            addFileToMultipart(mail.getAttachment(), multipart);

            // creates a new e-mail message
            Message msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress(username));
            InternetAddress[] toAddresses = { new InternetAddress(to) };
            msg.setRecipients(Message.RecipientType.TO, toAddresses);
            msg.setSubject(mail.getSubject());
            msg.setSentDate(new Date());
            msg.setContent(multipart);

            // sends the e-mail
            Transport.send(msg);
        } catch (MessagingException | IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    private void addFileToMultipart(MultipartFile attachment, Multipart multipart) throws IOException {
        File convFile = new File(Objects.requireNonNull(attachment.getOriginalFilename()));
        convFile.createNewFile();
        FileOutputStream fos = new FileOutputStream(convFile);
        fos.write(attachment.getBytes());
        fos.close();

        if (convFile.isFile()) {
            try {
                MimeBodyPart messageBodyPart = new MimeBodyPart();
                messageBodyPart.attachFile(convFile);
                multipart.addBodyPart(messageBodyPart);
            } catch (MessagingException e) {
                e.printStackTrace();
            }
        }

        convFile.deleteOnExit();
    }
}
