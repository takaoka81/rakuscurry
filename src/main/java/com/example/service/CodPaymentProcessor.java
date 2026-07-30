package com.example.service;

import com.example.enums.Status;

public class CodPaymentProcessor implements PaymentProcessor {
	@Override
	public int pay() {
		return Status.PAYMENT_RECEIVED.getCode();
	}
}
