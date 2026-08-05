package com.example.service;

import com.example.domain.Order;

public class RetryMailServiceDecorator implements MailService {
    private final MailService delegate;
    private final int maxRetries;

    public RetryMailServiceDecorator(MailService delegate, int maxRetries) {
        this.delegate = delegate;
        this.maxRetries = maxRetries;
    }

    @Override
    public void sendMail(Order order, String to) {
        int attempt = 0;
        while (attempt < maxRetries) {
            try {
                delegate.sendMail(order, to);
                return; // 成功した場合は終了
            } catch (Exception e) {
                attempt++;
                if (attempt >= maxRetries) {
                    throw e; // 最大試行回数に達した場合は例外を再スロー
                }
            }
        }
    }

}
