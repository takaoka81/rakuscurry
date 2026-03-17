package com.example.service;

import org.springframework.stereotype.Service;

@Service
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

}
