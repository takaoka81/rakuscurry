package com.example.enums;

public enum Status {
    CART(0, "カート内"),
    ORDER(1, "未入金"),
    PAYMENT_RECEIVED(2, "入金済"),
    SHIPPED(3, "発送済"),
    DELIVERY_COMPLETED(4, "配送完了"),
    INVALID(9, "価格改定による無効");

    private final int code;
    private final String label;

    Status(int code, String label) {
        this.code = code;
        this.label = label;
    }

    public int getCode() {
        return code;
    }

    public String getLabel() {
        return label;
    }

    public static Status fromCode(Integer code) {
        for (Status value : values()) {
            if (value.code == (int) code) {
                return value;
            }
        }
        throw new IllegalArgumentException("不正なstatusコードです: " + code);
    }


}
