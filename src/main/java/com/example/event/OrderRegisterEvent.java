package com.example.event;

import com.example.domain.Order;
import com.example.domain.StampHistory;
import com.example.domain.User;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class OrderRegisterEvent {
    private final Order order;
    private final User user;
    private final StampHistory stampHistory;
}
