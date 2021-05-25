package com.demosocket.emailer.service;

import com.demosocket.emailer.controller.MailController;
import com.demosocket.emailer.exceptions.WrongEmailException;
import com.demosocket.emailer.model.Mail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class EmailerServiceImpl implements EmailerService {

    Logger logger = LoggerFactory.getLogger(MailController.class);
    private static final String MAIL_REGEX = "^(.+)@(.+)$";

    @Override
    public void send(Mail mail) {

        validateEmails(mail);
        Session session = getSession(mail);

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
            msg.setFrom(new InternetAddress(mail.getUsername()));
            InternetAddress[] toAddress = { new InternetAddress(mail.getRecipient()) };
            InternetAddress[] ccAddress = { new InternetAddress(mail.getCc()) };
            msg.setRecipients(Message.RecipientType.TO, toAddress);
            msg.setRecipients(Message.RecipientType.CC, ccAddress);
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

    private void validateEmails(Mail mail) {
        if (isInvalidEmail(mail.getUsername()) || isInvalidEmail(mail.getCc())) {
            throw new WrongEmailException("Email is incorrect");
        }
    }

    private boolean isInvalidEmail(String emailString) {
        Pattern pattern = Pattern.compile(MAIL_REGEX);
        Matcher matcher = pattern.matcher(emailString);
        return !matcher.matches();
    }

    private Session getSession(Mail mail) {
        return Session.getInstance(getPropertiesFromMail(mail),
                new Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(mail.getUsername(), mail.getPassword());
                    }
                });
    }

    private Properties getPropertiesFromMail(Mail mail) {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", mail.getHost());
        props.put("mail.smtp.port", mail.getPort());

        return props;
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
