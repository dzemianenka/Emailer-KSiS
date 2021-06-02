package com.demosocket.emailer.mapper;

import com.demosocket.emailer.model.InboxMail;
import com.sun.mail.imap.IMAPMessage;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.stream.Collectors;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;

@Component
public class MessageMapper {

    public InboxMail toInboxMail(IMAPMessage message) {
        try {
            InboxMail inboxMail = new InboxMail();
            inboxMail.setFrom(getFrom(message));
            inboxMail.setDate(getDate(message));
            inboxMail.setSubject(getSubject(message));

            return inboxMail;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    private String getFrom(IMAPMessage message) throws MessagingException {
        return Arrays.stream(message.getFrom())
                .map(a -> (InternetAddress) a)
                .map(InternetAddress::getAddress)
                .collect(Collectors.joining(", "));
    }

    private LocalDateTime getDate(IMAPMessage message) throws MessagingException {
        return message.getSentDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

    private String getSubject(IMAPMessage message) {
        try {
            return message.getSubject();
        } catch (MessagingException e) {
            return "N/A";
        }
    }

}
