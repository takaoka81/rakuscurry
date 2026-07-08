package com.example.service;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import com.example.domain.CartItem;

public class CartServiceTest {

    @Mock
    private CartService cartService;

    @Mock
    private CartItem cartItem;

    @Test
    void addToCart_concurrent_shouldNotExceedStock() throws Exception {
        // 在庫 = 1 の商品を2ユーザーが同時にカートに入れる
        CountDownLatch latch = new CountDownLatch(2);
        AtomicInteger successCount = new AtomicInteger(0);
        ExecutorService executor = Executors.newFixedThreadPool(2);

        for (int userId = 1; userId <= 2; userId++) {
            final int uid = userId;
            executor.submit(() -> {
                try {
                    cartService.addItemToCart(cartItem, uid);
                    successCount.incrementAndGet();
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await(5, TimeUnit.SECONDS);
        // 在庫1個なので、成功は1人だけのはず
        assertThat(successCount.get()).isEqualTo(1); // ← これが通らない
    }
}
