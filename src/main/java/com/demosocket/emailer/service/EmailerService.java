package com.demosocket.emailer.service;

import com.demosocket.emailer.model.InboxAuth;
import com.demosocket.emailer.model.InboxMail;
import com.demosocket.emailer.model.Mail;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface EmailerService {

    void send(Mail mail) throws Exception;

    List<InboxMail> receive(InboxAuth inboxAuth) throws Exception;

}
