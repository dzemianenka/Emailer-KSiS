package com.demosocket.emailer.model;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;

public class EmailAuthenticator extends Authenticator {

    private final String username;
    private final String password;

    public EmailAuthenticator (final InboxAuth inboxAuth) {
        this.username = inboxAuth.getUsername();
        this.password = inboxAuth.getPassword();
    }

    public PasswordAuthentication getPasswordAuthentication() {
        return new PasswordAuthentication(username, password);
    }

}
