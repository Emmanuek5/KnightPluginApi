package com.obsidian.knight_api.utils;

import org.apache.commons.mail.*;

public class Mailer {

    private final String host;
    private final int port;
    private final String username;
    private final String password;

    private boolean useSsl;
    private boolean useTls;
    private String from;
    private String subject;
    private String message;
    private boolean isHtml;

    public Mailer(String host, int port, String username, String password) {
        this.host = host;
        this.port = port;
        this.username = username;
        this.password = password;
    }

    public Mailer from(String from) {
        this.from = from;
        return this;
    }

    public Mailer subject(String subject) {
        this.subject = subject;
        return this;
    }

    public Mailer useSSl(boolean useSsl) {
        this.useSsl = useSsl;
        return this;
    }

    public Mailer useTls(boolean useTls) {
        this.useTls = useTls;
        return this;
    }

    public Mailer message(String message) {
        this.message = message;
        return this;
    }

    public Mailer isHtml(boolean isHtml) {
        this.isHtml = isHtml;
        return this;
    }

    public void send(String to) throws Exception {
       if (isHtml) {
           HtmlEmail email = new HtmlEmail();
           email.setHostName(host);
           email.setSmtpPort(port);
           email.setAuthenticator(new DefaultAuthenticator(username, password));
           email.setSSLOnConnect(useSsl);
           email.setStartTLSEnabled(useTls);
           email.setFrom(from);
           email.setSubject(subject);
           email.setMsg(message);
           email.addTo(to);
           email.setHtmlMsg(message);
           email.send();
       } else {
           SimpleEmail email = new SimpleEmail();
           email.setHostName(host);
           email.setSmtpPort(port);
           email.setAuthenticator(new DefaultAuthenticator(username, password));
           email.setSSLOnConnect(useSsl);
           email.setStartTLSEnabled(useTls);
           email.setFrom(from);
           email.setSubject(subject);
           email.setMsg(message);
           email.addTo(to);
           email.send();
       }

    }

    public static void main(String[] args) {
        // Replace these values with your actual email credentials

    }


}
