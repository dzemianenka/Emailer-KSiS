package com.demosocket.emailer.controller;

import com.demosocket.emailer.model.InboxAuth;
import com.demosocket.emailer.model.Mail;
import com.demosocket.emailer.service.EmailerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
public class MailController {

    final private EmailerService emailerService;

    @Autowired
    public MailController(EmailerService emailerService) {
        this.emailerService = emailerService;
    }

    @GetMapping("/")
    public String getMailForm() {
        return "index";
    }

    @PostMapping("/send")
    public String send(@ModelAttribute Mail mail) throws Exception {
        emailerService.send(mail);
        return "success";
    }

    @GetMapping("/receive")
    public String getReceive() {
        return "receive";
    }

    @PostMapping("/receive")
    public String receiveMessages(@ModelAttribute InboxAuth inboxAuth, Model model) throws Exception {
        model.addAttribute("mails", emailerService.receive(inboxAuth));
        return "receivedMessages";
    }

}
