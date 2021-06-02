package com.demosocket.emailer.model;

import java.time.LocalDateTime;

public class InboxMail {

    private String from;
    private LocalDateTime date;
    private String subject;

    public InboxMail() {
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

}
