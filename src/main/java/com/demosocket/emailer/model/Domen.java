package com.demosocket.emailer.model;

public enum Domen {
    GMAIL("gmail.com","smtp.gmail.com", 465),
    MAIL("mail.ru","smtp.mail.ru", 465),
    YANDEX("yandex.ru","smtp.yandex.ru", 465),
    RAMBLER("rambler.ru","smtp.rambler.ru", 465),
    YAHOO("yahoo.com","smtp.mail.yahoo.com", 465),
    UKR("ukr.net","smtp.ukr.net", 465);

    String code;
    String host;
    int port;

    Domen(String code, String host, int port) {
        this.code = code;
        this.host = host;
        this.port = port;
    }

    public String getCode() {
        return code;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

}
