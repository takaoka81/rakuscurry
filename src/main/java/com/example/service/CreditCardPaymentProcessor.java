package com.example.service;

import com.example.domain.Order;
import com.example.enums.PayJudge;
import com.example.enums.Status;

public class CreditCardPaymentProcessor implements PaymentProcessor {
	@Override
	public int pay() {
		return Status.ORDER.getCode();
	}
}
