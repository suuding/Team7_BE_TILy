package com.example.tily._core.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.net.InetSocketAddress;
import java.net.Proxy;

@Configuration
public class ProxyConfig {

    @Value("krmp-proxy.9rum.cc")
    private String proxyHost;

    @Value("3128")
    private int proxyPort;

    @Bean
    @Profile("deploy")
    public RestTemplate restTemplateWithProxy() {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyHost, proxyPort));
        requestFactory.setProxy(proxy);
        requestFactory.setConnectTimeout(6000);
        requestFactory.setReadTimeout(6000);
        return new RestTemplate(requestFactory);
    }
}
