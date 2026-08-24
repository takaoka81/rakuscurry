package com.example.service.Processor;

import org.springframework.stereotype.Component;

import com.example.domain.Order;
import com.example.enums.OrderStatus;
import com.example.enums.PayJudge;

@Component
public class CreditCardPaymentProcessor implements PaymentProcessor {
	@Override
	public OrderStatus resolveStatus(Order order) {
		return OrderStatus.PAYMENT_RECEIVED;
	}

	@Override
	public PayJudge getPaymentMethod() {
		return PayJudge.CREDIT_CARD;
	}
}
