package com.example.service;

import com.example.domain.Order;

public interface MailService {
    void sendMail(Order order, String to);
}
