package com.example.service;

import com.example.domain.Order;

public abstract class MailServiceDecorator implements MailService {
    protected final MailService delegate;

    public MailServiceDecorator(MailService delegate) {
        this.delegate = delegate;
    }

    @Override
    public void sendMail(Order order, String to) {
        delegate.sendMail(order, to);
    }

}
