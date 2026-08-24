package com.example.service.Processor;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.example.enums.PayJudge;

@Configuration
public class PaymentProcessorConfig {
    @Bean
    public Map<PayJudge, PaymentProcessor> paymentProcessorMap(List<PaymentProcessor> processors) {
        return processors.stream()
                .collect(Collectors.toMap(PaymentProcessor::getPaymentMethod, p -> p));
    }
}
