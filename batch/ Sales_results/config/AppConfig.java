package com.example.batch.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;
import java.net.InetSocketAddress;
import java.net.Proxy;

@Configuration
public class AppConfig {

    @Bean
    public RestTemplate restTemplate() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(5000); // 接続待ち 5秒
        factory.setReadTimeout(5000);    // データ受信待ち 5秒
        
        // プロキシサーバーの設定
        Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("44.254.52.215", 8080)); 
        factory.setProxy(proxy);
        
        return new RestTemplate(factory);
    }
}
