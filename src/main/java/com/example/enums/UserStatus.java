package com.example.enums;

public enum UserStatus {
    ACTIVE(0, "在籍中"),
    WITHDRAWN(1, "退会済み");

    private final int code;
    private final String label;

    UserStatus(int code, String label) {
        this.code = code;
        this.label = label;
    }

    public int getCode() {
        return code;
    }

    public String getLabel() {
        return label;
    }

    public static UserStatus fromCode(Integer code) {
        for (UserStatus value : values()) {
            if (value.code == (int) code) {
                return value;
            }
        }
        throw new IllegalArgumentException("不正なstatusコードです: " + code);
    }
}
