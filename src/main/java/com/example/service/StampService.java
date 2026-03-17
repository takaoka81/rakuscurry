package com.example.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.domain.OrderItem;

/**
 * スタンプに関連する業務処理を行います
 *
 * @author masashi.saito
 */
@Service
@Transactional
public class StampService {

    /**
     * 無料で注文可能なスタンプ数
     */
    private static final Integer FREE_STAMP_COUNT = 25;

    /**
     * 現在のスタンプ量から何杯無料で頼めるかを求める
     * 
     * @param stampCount 現在持っているスタンプの数
     * @return 無料で頼めるカレーの数
     */
    public Integer getFreeCurryCount(Integer stampCount) {
        Integer freeCount = 0;
        while (stampCount > FREE_STAMP_COUNT) {
            stampCount -= FREE_STAMP_COUNT;
            freeCount++;
        }
        return freeCount;
    }

    /**
     * スタンプカードに表示するスタンプ数を取得する
     * 
     * @param stampCount 現在持っているスタンプの数
     * @return スタンプカードに表示する分のスタンプ数
     */
    public Integer getStampOnCardCount(Integer stampCount) {
        while (stampCount > FREE_STAMP_COUNT) {
            stampCount -= FREE_STAMP_COUNT;
        }
        return stampCount;
    }

    /**
     * 注文ごとのスタンプ付与数を取得します。
     * 
     * @param orderItemList 注文情報
     * @return 注文に対するスタンプ付与数
     */
    public Integer getStampCountByOrder(List<OrderItem> orderItemList) {
        Integer stampCount = 0;
        for (OrderItem orderItem : orderItemList) {
            if (orderItem.getSize().equals("M")) {
                stampCount++;
            } else {
                stampCount = stampCount + 2;
            }
        }
        return stampCount;
    }

}
