package com.demosocket.emailer.service.impl;

import com.demosocket.emailer.exceptions.WrongEmailException;
import com.demosocket.emailer.model.Domen;
import com.demosocket.emailer.model.EmailAuthenticator;
import com.demosocket.emailer.model.InboxAuth;
import com.demosocket.emailer.model.InboxMail;
import com.demosocket.emailer.model.Mail;
import com.demosocket.emailer.mapper.MessageMapper;
import com.demosocket.emailer.service.EmailerService;
import com.sun.mail.imap.IMAPMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import javax.mail.Authenticator;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Store;
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

    private final MessageMapper messageMapper;

    public EmailerServiceImpl(MessageMapper messageMapper) {
        this.messageMapper = messageMapper;
    }

    @Override
    public void send(Mail mail) throws Exception {

        validateEmails(mail);
        Domen domen = getDomenFromEmail(mail.getUsername());

        Properties javaMailProperties = setSmtpProperties();

        JavaMailSenderImpl javaMailSender = createJavaMailSender(domen, mail);
        javaMailSender.setJavaMailProperties(javaMailProperties);

        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        fillMimeMessage(mimeMessage, mail);

        javaMailSender.send(mimeMessage);
    }

    @Override
    public List<InboxMail> receive(InboxAuth inboxAuth) throws Exception {

        if (isInvalidEmail(inboxAuth.getUsername())) {
            throw new WrongEmailException("Wrong email");
        }
        Domen domen = getDomenFromEmail(inboxAuth.getUsername());

        Properties properties = setImapProperties(domen);
        Authenticator auth = new EmailAuthenticator(inboxAuth);

        Store store = Session.getDefaultInstance(properties, auth).getStore();
        store.connect(domen.getImapHost(), inboxAuth.getUsername(), inboxAuth.getPassword());

        Folder inboxFolder = store.getFolder("INBOX");
        inboxFolder.open(Folder.READ_ONLY);

        Message[] message = inboxFolder.getMessages();

        return Arrays.stream(message)
                .map(mesh -> (IMAPMessage) mesh)
                .map(messageMapper::toInboxMail)
                .collect(Collectors.toList());
    }

    private Properties setSmtpProperties() {
        Properties javaMailProperties = new Properties();
        javaMailProperties.put("mail.smtp.starttls.enable", "true");
        javaMailProperties.put("mail.smtp.auth", "true");
        javaMailProperties.put("mail.transport.protocol", "smtp");
        javaMailProperties.put("mail.debug", "true");
        javaMailProperties.put("mail.smtps.ssl.trust", "*");
        javaMailProperties.put("mail.smtp.ssl.enable", "true");

        return javaMailProperties;
    }

    private Properties setImapProperties(Domen domen) {
        Properties javaMailProperties = new Properties();
        javaMailProperties.put("mail.debug", "false");
        javaMailProperties.put("mail.store.protocol", "imaps");
        javaMailProperties.put("mail.imap.ssl.enable", "true");
        javaMailProperties.put("mail.imap.port", domen.getImapPort());

        return javaMailProperties;
    }

    private JavaMailSenderImpl createJavaMailSender(Domen domen, Mail mail) {
        JavaMailSenderImpl javaMailSender = new JavaMailSenderImpl();
        javaMailSender.setHost(domen.getSmtpHost());
        javaMailSender.setPort(domen.getSmtpPort());
        javaMailSender.setUsername(mail.getUsername());
        javaMailSender.setPassword(mail.getPassword());

        return javaMailSender;
    }

    private void fillMimeMessage(MimeMessage mimeMessage, Mail mail) throws Exception {
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
    }

    private void validateEmails(Mail mail) {
        Boolean isWrongEmail = Stream.of(mail.getUsername(), mail.getTo(), mail.getCc())
                .filter(s -> !s.isEmpty())
                .map(this::isInvalidEmail)
                .filter(l -> l.equals(true))
                .findFirst()
                .orElse(false);

        if (isWrongEmail.equals(true)) {
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
