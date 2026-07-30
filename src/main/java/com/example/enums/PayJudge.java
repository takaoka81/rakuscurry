package com.example.enums;

public enum PayJudge {
	COD(1, "代金引換"),
	CREDIT_CARD(2, "クレジットカード支払い");

	private final int code;
	private final String label;

	PayJudge(int code, String label) {
		this.code = code;
		this.label = label;
	}

	public int getCode() {
		return code;
	}

	public String getLabel() {
		return label;
	}

	public static PayJudge fromCode(Integer code) {
		for (PayJudge value : values()) {
			if (value.code == (int) code) {
				return value;
			}
		}
		throw new IllegalArgumentException("不正なstatusコードです: " + code);
	}
}
