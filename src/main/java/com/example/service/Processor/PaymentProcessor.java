package com.example.service.Processor;

import com.example.domain.Order;
import com.example.enums.OrderStatus;
import com.example.enums.PayJudge;

public interface PaymentProcessor {
    /**
     * 支払い方法に応じてStatuコードを変換する
     * 
     * @return Statusコード変換
     */
    PayJudge getPaymentMethod();

    OrderStatus resolveStatus(Order order);

}
