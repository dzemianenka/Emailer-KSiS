package com.demosocket.emailer.service.impl;

import com.demosocket.emailer.exceptions.WrongEmailException;
import com.demosocket.emailer.model.Domen;
import com.demosocket.emailer.model.Mail;
import com.demosocket.emailer.service.EmailerService;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.internet.MimeMessage;
import java.util.Arrays;
import java.util.Objects;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

@Service
public class EmailerServiceImpl implements EmailerService {

    private static final String MAIL_REGEX = "^(.+)@(.+)$";
    private static final String DOMEN_REGEX = "(\\W|^)[\\w.\\-]{0,25}@";

    @Override
    public void send(Mail mail) throws Exception {

        validateEmails(mail);
        Domen domen = getDomenFromEmail(mail.getUsername());

        Properties javaMailProperties = new Properties();
        javaMailProperties.put("mail.smtp.starttls.enable", "true");
        javaMailProperties.put("mail.smtp.auth", "true");
        javaMailProperties.put("mail.transport.protocol", "smtp");
        javaMailProperties.put("mail.debug", "true");
        javaMailProperties.put("mail.smtps.ssl.trust", "*");
        javaMailProperties.put("mail.smtp.ssl.enable", "true");

        JavaMailSenderImpl javaMailSender = new JavaMailSenderImpl();
        javaMailSender.setHost(domen.getHost());
        javaMailSender.setPort(domen.getPort());
        javaMailSender.setUsername(mail.getUsername());
        javaMailSender.setPassword(mail.getPassword());
        javaMailSender.setJavaMailProperties(javaMailProperties);

        MimeMessage mimeMessage = javaMailSender.createMimeMessage();

        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);
        mimeMessageHelper.setFrom(mail.getUsername());
        mimeMessageHelper.setReplyTo(mail.getUsername());
        mimeMessageHelper.setTo(mail.getTo());
        if (!mail.getCc().isEmpty()) {
            mimeMessageHelper.setCc(mail.getCc());
        }
        mimeMessageHelper.setSubject(mail.getSubject());
        mimeMessageHelper.setText(mail.getContent());
        if (!mail.getAttachment().isEmpty()) {
            mimeMessageHelper.addAttachment(
                    Objects.requireNonNull(mail.getAttachment().getOriginalFilename()), mail.getAttachment()
            );
        }

        javaMailSender.send(mimeMessage);
    }

    private void validateEmails(Mail mail) {
        Boolean anyWrongEmail = Stream.of(mail.getUsername(), mail.getTo(), mail.getCc())
                .filter(s -> !s.isEmpty())
                .map(this::isInvalidEmail)
                .filter(l -> l.equals(true))
                .findFirst().orElse(false);

        if (anyWrongEmail.equals(true)) {
            throw new WrongEmailException("Wrong email");
        }
    }

    private boolean isInvalidEmail(String emailString) {
        Pattern pattern = Pattern.compile(MAIL_REGEX);
        Matcher matcher = pattern.matcher(emailString);

        return !matcher.matches();
    }

    private Domen getDomenFromEmail(String username) {
        String code = username.replaceAll(DOMEN_REGEX, "");

        return Arrays.stream(Domen.values())
                .filter(d -> code.equals(d.getCode()))
                .findFirst()
                .orElseThrow(() -> new WrongEmailException("Wrong domen"));
    }

}
