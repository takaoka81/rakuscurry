package com.example.service;

import com.example.enums.Status;

public class CreditCardPaymentProcessor implements PaymentProcessor {
	@Override
	public int pay() {
		return Status.ORDER.getCode();
	}
}
