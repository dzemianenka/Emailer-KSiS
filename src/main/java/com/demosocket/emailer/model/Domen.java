package com.demosocket.emailer.model;

public enum Domen {
    GMAIL("gmail.com","smtp.gmail.com", 465, "imap.gmail.com", 993),
    MAIL("mail.ru","smtp.mail.ru", 465, "imap.mail.ru", 993),
    YANDEX("yandex.ru","smtp.yandex.ru", 465, "imap.yandex.ru", 993),
    RAMBLER("rambler.ru","smtp.rambler.ru", 465, "imap.rambler.ru", 993),
    YAHOO("yahoo.com","smtp.mail.yahoo.com", 465, "imap.mail.yahoo.com", 993),
    UKR("ukr.net","smtp.ukr.net", 465, "imap.ukr.net", 993);

    String code;
    String SmtpHost;
    int SmtpPort;
    String ImapHost;
    int ImapPort;

    Domen(final String code, final String SmtpHost, final int SmtpPort, final String ImapHost, final int ImapPort) {
        this.code = code;
        this.SmtpHost = SmtpHost;
        this.SmtpPort = SmtpPort;
        this.ImapHost = ImapHost;
        this.ImapPort = ImapPort;
    }

    public String getCode() {
        return code;
    }

    public String getSmtpHost() {
        return SmtpHost;
    }

    public int getSmtpPort() {
        return SmtpPort;
    }

    public String getImapHost() {
        return ImapHost;
    }

    public int getImapPort() {
        return ImapPort;
    }

}
