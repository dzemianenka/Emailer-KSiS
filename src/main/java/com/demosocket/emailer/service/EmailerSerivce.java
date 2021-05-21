package com.demosocket.emailer.service;

import com.demosocket.emailer.model.Mail;
import org.springframework.stereotype.Service;

@Service
public interface EmailerSerivce {

    void send(Mail mail);
}
