package com.example.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.domain.Order;

public class LoggingMailServiceDecorator extends MailServiceDecorator {
    private static final Logger logger = LoggerFactory.getLogger(LoggingMailServiceDecorator.class);

    public LoggingMailServiceDecorator(MailService delegate) {
        super(delegate);
    }

    @Override
    public void sendMail(Order order, String to) {
        try {
            delegate.sendMail(order, to);
            logger.info("メール送信成功: " + to);
        } catch (Exception e) {
            logger.error("メール送信失敗: " + to, e);
            throw e; // 例外を再スローして呼び出し元に通知
        }
    }

}
