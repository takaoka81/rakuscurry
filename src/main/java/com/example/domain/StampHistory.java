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

    

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + id;
        result = prime * result + userId;
        result = prime * result + orderId;
        result = prime * result + stampCountChanges;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        StampHistory other = (StampHistory) obj;
        if (id != other.id)
            return false;
        if (userId != other.userId)
            return false;
        if (orderId != other.orderId)
            return false;
        if (stampCountChanges != other.stampCountChanges)
            return false;
        return true;
    }

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
