package com.example.service;

public interface PaymentProcessor {
    /**
     * 支払い方法に応じてStatuコードを変換する
     * @return　Statusコード変換
     */
	int pay();

}
