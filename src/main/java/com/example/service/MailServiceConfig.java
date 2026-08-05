package com.example.service;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.MailSender;

import com.example.repository.OrderRepository;

@Configuration

public class MailServiceConfig {
    @Bean
    public MailService mailService(OrderRepository orderRepository, MailSender sender) {
        return new LoggingMailServiceDecorator(
                new RetryMailServiceDecorator(new SimpleMailService(orderRepository, sender), 3));
    }
}
