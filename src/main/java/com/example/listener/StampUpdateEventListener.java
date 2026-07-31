package com.example.listener;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.example.event.OrderRegisterEvent;
import com.example.repository.StampHistoryRepository;
import com.example.service.UserService;

@Component
public class StampUpdateEventListener {

    private final StampHistoryRepository stampHistoryRepository;
    private final UserService userService;

    StampUpdateEventListener(UserService userService, StampHistoryRepository stampHistoryRepository) {
        this.userService = userService;
        this.stampHistoryRepository = stampHistoryRepository;
    }

    @EventListener
    public void stampUpdate(OrderRegisterEvent event) {
        userService.updateStampCounts(event.getUser());
        stampHistoryRepository.insert(event.getStampHistory());
    }
}
