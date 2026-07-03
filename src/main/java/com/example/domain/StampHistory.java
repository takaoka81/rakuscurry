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
    private int id;

    /**
     * ユーザid
     */
    private int userId;

    /**
     * 注文id
     */
    private int orderId;

    /**
     * 注文idのなかのスタンプ増減数
     */
    private int stampCountChanges;

    public StampHistory() {
    }

    public StampHistory(Integer userId, Integer orderId, Integer stampCountChanges) {
        this.userId = userId;
        this.orderId = orderId;
        this.stampCountChanges = stampCountChanges;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public int getStampCountChanges() {
        return stampCountChanges;
    }

    public void setStampCountChanges(int stampCountChanges) {
        this.stampCountChanges = stampCountChanges;
    }

}
