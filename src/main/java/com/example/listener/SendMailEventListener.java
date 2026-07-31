package com.example.listener;

import java.util.List;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.example.domain.Order;
import com.example.event.OrderRegisterEvent;
import com.example.repository.OrderRepository;
import com.example.service.MailService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class SendMailEventListener {
    private final OrderRepository orderRepository;
    private final MailService mailService;

    @org.springframework.core.annotation.Order(2)
    @EventListener
    public void sendMail(OrderRegisterEvent event) {
        Order order = event.getOrder();
        List<Order> loaded = orderRepository.orderLoad(order.getId());
        if (loaded != null && !loaded.isEmpty()) {
            order = loaded.get(0);
        }
        mailService.sendMail(order, event.getUser().getEmail());
    }
}
