package com.example.listener;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.example.domain.OrderItem;
import com.example.event.OrderRegisterEvent;
import com.example.repository.OrderItemRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class FreeCuryEventListener {
    private final OrderItemRepository orderItemRepository;

    @Order(1)
    @EventListener
    public void freeCury(OrderRegisterEvent event) {
        List<OrderItem> oi = new ArrayList<>();
        for (OrderItem orderItem : event.getOrder().getOrderItemList()) {
            if (orderItem.getOrderPrice().equals(0)) {
                orderItem.setOrderId(event.getOrder().getId());
                oi.add(orderItem);
            }
        }
        orderItemRepository.updateOrder(oi);
    }
}
