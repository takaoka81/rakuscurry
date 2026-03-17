package com.example.domain;

/**
 * stamp_historyテーブルのdomainクラス
 *
 * @author masashi.saito
 */
public class StampHistory {

    /**
     * id
     */
    private Integer id;

    /**
     * ユーザid
     */
    private Integer userId;

    /**
     * 注文id
     */
    private Integer orderId;

    /**
     * 注文idのなかのスタンプ増減数
     */
    private Integer stampCountChenges;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    public Integer getStampCountChenges() {
        return stampCountChenges;
    }

    public void setStampCountChenges(Integer stampCountChenges) {
        this.stampCountChenges = stampCountChenges;
    }

}
