package com.example.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import com.example.domain.OrderItem;

class StampServiceTest {

    private final StampService stampService = new StampService();

    @Nested
    @DisplayName("getFreeCurryCountのテスト")
    class GetFreeCurryCountTest {
        @Test
        @DisplayName("スタンプ数が25個なら1杯無料")
        void testGetFreeCurryCount_ShouldReturnOne() {
            assertEquals(1, stampService.getFreeCurryCount(25));
        }

        @Test
        @DisplayName("スタンプ数が50個なら2杯無料")
        void testGetFreeCurryCount_ShouldReturnTwo() {
            assertEquals(2, stampService.getFreeCurryCount(50));
        }

        @Test
        @DisplayName("スタンプ数が24個なら0杯無料")
        void testGetFreeCurryCount_ShouldReturnZero() {
            assertEquals(0, stampService.getFreeCurryCount(24));
        }
    }

    @Nested
    @DisplayName("getStampOnCardCountのテスト")
    class GetStampOnCardCountTest {
        @Test
        @DisplayName("スタンプ数が26個ならカード表示は1個")
        void testGetStampOnCardCount_ShouldReturnRemainder() {
            assertEquals(1, stampService.getStampOnCardCount(26));
        }

        @Test
        @DisplayName("スタンプ数が25個ならカード表示は0個")
        void testGetStampOnCardCount_ShouldReturnZero() {
            assertEquals(0, stampService.getStampOnCardCount(25));
        }
    }

    @Nested
    @DisplayName("getStampCountByOrderのテスト")
    class GetStampCountByOrderTest {

        @Test
        @DisplayName("空のリストの場合は0を返す")
        void testGetStampCountByOrder_EmptyList() {
            assertEquals(0, stampService.getStampCountByOrder(Collections.emptyList()));
        }

        @Test
        @DisplayName("サイズM(1点)とサイズL(2点)の混在パターンの計算")
        void testGetStampCountByOrder_MixedSizes() {
            List<OrderItem> items = Arrays.asList(
                    createItem(false, "M", 2),
                    createItem(false, "L", 1));
            assertEquals(4, stampService.getStampCountByOrder(items));
        }

        @Test
        @DisplayName("無料注文が含まれる場合はスタンプが25個減算される")
        void testGetStampCountByOrder_WithFreeItem() {
            List<OrderItem> items = Arrays.asList(
                    createItem(true, "M", 1),
                    createItem(false, "L", 1));
            assertEquals(-23, stampService.getStampCountByOrder(items));
        }
    }

    /**
     * テスト用のOrderItemオブジェクトを生成する補助メソッド
     */
    private OrderItem createItem(boolean isFree, String size, int quantity) {
        OrderItem item = new OrderItem();
        item.setFree(isFree);
        item.setSize(size);
        item.setQuantity(quantity);
        return item;
    }
}
