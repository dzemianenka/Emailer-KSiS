package com.demosocket.emailer.service;

import com.demosocket.emailer.model.Mail;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.internet.MimeMessage;
import java.util.Objects;

@Service
public class MailService implements EmailerSerivce {

    final private JavaMailSender javaMailSender;

    public MailService(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    @Override
    public void send(Mail mail) {
        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);

            mimeMessageHelper.setTo(mail.getRecipient());
            mimeMessageHelper.setSubject(mail.getSubject());
            mimeMessageHelper.setText(mail.getContent());

            mimeMessageHelper.addAttachment(Objects.requireNonNull(mail.getAttachment().getOriginalFilename()), mail.getAttachment());

            javaMailSender.send(mimeMessage);

        } catch (Exception e) {
            System.out.println("Something bad");
            e.printStackTrace();
        }
    }
}
